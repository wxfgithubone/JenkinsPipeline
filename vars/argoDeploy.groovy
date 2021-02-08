#!groovy

def call(argoAddress,applicationName,imageTag,argocdToken,pythonRunMethod,projectType){
    pipeline{
        stage("deploy") {
            script {
                switch ("${projectType}") {
                    case "python":
                        // 如果项目中不存在pipeline.yaml这个文件，启动命令默认为python server.py,否则要求研发在项目中添加该文件，并且以json格式写入指令，格式如下
                        // webserver:
                        //     command:
                        // task:
                        //     command: 

                        withEnv(["ARGOCD_SERVER=${argoAddress}","ARGOCD_AUTH_TOKEN=${argocdToken}","USER=argocd"]) {
                            echo "Start Deploy ${imageTag}"
                            sh "argocd app set ${applicationName} --parameter image.tag=${imageTag} --grpc-web --insecure"

                            // 判断用户是否在初始化的时候勾选了python项目的执行任务(webserver,task,beat)，不勾选默认发布webserver
                            if ( "${pythonRunMethod}" ) {
                                method = "${pythonRunMethod}"
                            } else {
                                method = "webserver,"
                            }

                            // 判断项目仓库是否存在pipeline.yaml，如果不存在，设定默认python命令
                            def destFile = fileExists "${WORKSPACE}/pipeline.yaml"
                            // 循环用户指定的任务去set每个任务的运行指令
                            if ( destFile ) {
                                sh "cat ${WORKSPACE}/pipeline.yaml"
                                def data = readYaml file: "${WORKSPACE}/pipeline.yaml"
                                "${method}".split(',').each {
                                    // 取pipeline.yaml文件设定的任务指令赋值给
                                    if ( data."${it}" ) {
                                        command = data."${it}".command
                                    }

                                    // running = "${command}".split(' ')
                                    // sh "echo ${running}"
                                    // sh "argocd app set ${applicationName} --parameter ${it}.command=${running} --grpc-web --insecure"
                                    "${command}".split(' ').eachWithIndex { item, index ->
                                        sh "argocd app set ${applicationName} --parameter ${it}.command[${index}]=${item} --grpc-web --insecure"
                                    }
                                }
                            } else {
                                sh "echo pipeline.yaml not exists"
                                command = "python server.py"
                                sh "echo ${method}"
                                "${method}".split(',').each {
                                    sh "echo ${it}"
                                    "${command}".split(' ').eachWithIndex { item, index ->
                                        sh "argocd app set ${applicationName} --parameter ${it}.command[${index}]=${item} --grpc-web --insecure"
                                    }
                                }
                            }
                            sh "argocd app sync ${applicationName} --grpc-web --insecure"
                            sh "argocd app wait --timeout 300 ${applicationName} --insecure --grpc-web"
                        }
                        break
                    default:
                        withEnv(["ARGOCD_SERVER=${argoAddress}","ARGOCD_AUTH_TOKEN=${argocdToken}","USER=argocd"]) {
                            echo "Start Deploy ${imageTag}"
                            sh "argocd app set ${applicationName} --parameter image.tag=${imageTag} --grpc-web --insecure"
                            sh "argocd app sync ${applicationName} --grpc-web --insecure"
                            sh "argocd app wait --timeout 300 ${applicationName} --insecure --grpc-web"
                        }
                        break
                }
            }
        }
    }
}