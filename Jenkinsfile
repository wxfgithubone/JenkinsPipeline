#!groovy
@Library('devops-sharelibrary@release-v2') _

// 获取已发布版本号列表
def jobParameters = getParameters()
def paramsJson = readJSON text: jobParameters
def rollbackVersion = getV1SortingTags.getV1SortingTags("${paramsJson.harborAddress}","${paramsJson.department}","${paramsJson.serviceName}","${paramsJson.environment}")
// rollbackVersion.plus(0,"Select Version")
println paramsJson
println rollbackVersion
pipeline {
    agent {
        node {
            label "${paramsJson.cluster}"  //节点选择标签，使用k8s集群唯一标识
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '9'))
        disableConcurrentBuilds()
    }
    parameters {
        choice(choices: ['deploy','rollback'], description: '请选择发版或者回滚', name: 'Operation')
        choice(choices: rollbackVersion, description: '选择版本进行回滚', name: 'RollBackVersion')
    }
    stages {
        stage('Checkout Code') {
            when {
               expression {"${params.Operation}" == 'deploy'}
            }
            steps {
                script {
                    // 第一次构建如果没有获取到git版本，直接编译master分支
                    if ("${VERSION}" == "!No Git repository configured in SCM configuration" ) {
                        VERSION = "master"
                    } else {
                        VERSION = "${params.VERSION}"
                    }
                    sh "echo ${VERSION}"
                    sh "echo starting fetchCode"
                    checkout([$class: 'GitSCM',
                            branches: [[name: "${VERSION}"]],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [
                                [
                                    $class: 'CloneOption', 
                                    depth: 1, 
                                    reference: '', 
                                    shallow: true, 
                                    timeout: 60
                                ],
                                [
                                    $class: 'SubmoduleOption', 
                                    disableSubmodules: false, 
                                    parentCredentials: true, 
                                    recursiveSubmodules: true, 
                                    reference: '', 
                                    trackingSubmodules: false
                                ]
                            ], // 
                            gitTool: 'Default',
                            submoduleCfg: [],
                            userRemoteConfigs: [[credentialsId:"${paramsJson.department}",url: "${paramsJson.projectRepo}"]]
                            ])
                }
            }
        }
        stage ('Prepare Variables') {
            steps {
                script {
                    if ( "${paramsJson.department}" == "fintech") {
                        env.MavenManageFile =  'd397138f-15f8-4836-a5e3-e2440e2750f7'  // 由于fintech部门项目是C端纳米开发的，需要引用C端的maven file。
                    } else {
                        env.MavenManageFile = '02e95d2b-6bb6-457d-943f-13c63095e500'  // devops默认maven file
                    }
                    gitProject = "${paramsJson.projectRepo}".split(':|/|\\.')[3]
                    if ("${paramsJson.module}") {
                        env.projectModule = "${paramsJson.module}"
                    } else {
                        env.projectModule = "${gitProject}"
                    }
                    env.harbor = "${paramsJson.harborAddress}".split('/')[2]
                    GitCommitID = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    env.imageName = "${env.harbor}/${paramsJson.department}/${paramsJson.serviceName}-${paramsJson.environment}" 
                    env.imageTag = "${BUILD_NUMBER}-${GitCommitID}"
                    sh "echo ${env.imageName}"
                    ArgocdTokens = [
                        "argocd.devops.net": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1NzQzMzMwNDYsImlzcyI6ImFyZ29jZCIsIm5iZiI6MTU3NDMzMzA0Niwic3ViIjoiYWRtaW4ifQ.4t6tLkFYlfRHXSyEl3mnaJnveaiGRIZ6G_v53v_ivr8",
                        "argocd-nonprod.devops.net": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1NzI5NDA1NDIsImlzcyI6ImFyZ29jZCIsIm5iZiI6MTU3Mjk0MDU0Miwic3ViIjoiYWRtaW4ifQ.zeH2iu1hY5bNSYvwf71Mcp--eIKtVRkaboyVnV_ixWc"
                    ]
                    env.ArgocdToken = ArgocdTokens["${paramsJson.argoAddress}"]

                    if ( "${paramsJson.pythonRunMethod}") {
                        env.PythonRunMethod = "${paramsJson.pythonRunMethod}"
                    } else {
                        env.PythonRunMethod = ""
                    }
                }
            }
        }
        stage('Create Dockerfile') {
            when {
               expression {"${params.Operation}" == 'deploy'}
            }
            steps {
                // 以模块路径作为参数，将Dockerfile生成到这里
                createDockerfile("${paramsJson.projectType}","${paramsJson.module}","${paramsJson.packageName}","${paramsJson.environment}","${paramsJson.goRootDir}","${paramsJson.projectRepo}","${env.harbor}","${paramsJson.compiler_version}")
            }
        }
        stage("Load Build Java Image"){
            when {
               expression { "${params.Operation}" == 'deploy' && ("${paramsJson.projectType}" == 'java' || "${paramsJson.projectType}" == 'zip') }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    mavenbuild("${paramsJson.harborAddress}","${paramsJson.module}","${env.imageName}","${env.imageTag}")
                }
            }
        }
        stage("Load Build Node Image"){
            when {
               expression {"${paramsJson.projectType}" == 'node' && "${params.Operation}" == 'deploy' }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    nodeBuild("${paramsJson.harborAddress}","${paramsJson.compiler_version}","${env.imageName}","${env.imageTag}")
                }
            } 
        }
        stage("Load Build GO Image"){
            when {
               expression {"${paramsJson.projectType}" == 'go' && "${params.Operation}" == 'deploy' }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    goBuild("${paramsJson.harborAddress}","${env.imageName}","${env.imageTag}")
                }
            } 
        }
        stage("Load Build Python Image"){
            when {
               expression {"${paramsJson.projectType}" == 'python' && "${params.Operation}" == 'deploy' }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    pythonBuild("${paramsJson.harborAddress}","${env.imageName}","${env.imageTag}")
                }
            } 
        }
        stage("Load Build H5 Image"){
            when {
               expression {"${paramsJson.projectType}" == 'h5' && "${params.Operation}" == 'deploy' }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    h5Build("${paramsJson.harborAddress}","${env.imageName}","${env.imageTag}")
                }
            } 
        }
        stage("Load Build NodeJS Image"){
            when {
               expression {"${paramsJson.projectType}" == 'nodejs' && "${params.Operation}" == 'deploy' }
            }
            steps{
               script {
                   // 参数 1 模块名称 2 gitcommit号 3 仓库地址 4 环境
                    nodejsBuild("${paramsJson.harborAddress}","${paramsJson.compiler_version}","${env.imageName}","${env.imageTag}")
                }
            } 
        }
        stage("Load Argocd Deploy"){
            when {
               expression {"${params.Operation}" == 'deploy'}
            }
            steps{
                script {
                    argoDeploy("${paramsJson.argoAddress}","${paramsJson.applicationName}","${env.imageTag}","${env.ArgocdToken}","${env.PythonRunMethod}","${paramsJson.projectType}")
                }
            }
        }
        stage("Load Rollback"){
            when {
               expression {"${params.Operation}" == 'rollback'}
            }
            steps{
                script {
                    env.imageTag = "${params.RollBackVersion}".split('\t')[0] // 分割tag版本和时间，取tag
                    argoDeploy("${paramsJson.argoAddress}","${paramsJson.applicationName}","${env.imageTag}","${env.ArgocdToken}","${env.PythonRunMethod}","${paramsJson.projectType}")
                }
            }
        }
    }
     post {
        always {
            // clean workspace
            deleteDir()
        }
    }
}

