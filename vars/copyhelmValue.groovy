#!/usr/bin/env groovy

// 参数 1 模块名称 2 gitcommit号 3 仓库地址


def call(department,environment,projectType,harborAddress,projectModule,projectPort,apollo,apolloEnv,apolloMetaServer,ingress,healthCheck,healthCheckUrl,JVM,helmChartValueName,k8sAffinity,k8sAffinityKVPairs,k8sService,k8sServiceName,k8sCpuLimit,k8sMemoryLimit,k8sCpuRequest,k8sMemoryRequest,ingressInternalType,ingressPublicType,ingressExternalType,ingressInternalHosts,ingressPublicHosts,ingressExternalHosts,ingressPrivateType,ingressPrivateHosts,apolloIDC,serviceName,timeZone,pythonRunMethod,toReplicaset) {
    def template = new org.file.GetNumberFromString()
    def harbor = "${harborAddress}".split('/')[2]
    def templateFile = "${projectType}" + "/" + "template-values.yaml"
    pipeline {
        stage("Dynamic Generate Helm Value") {
            // 读取chart git仓库的模板文件
            def data = readYaml file: "${templateFile}"
            // 设置ingress的域名和接口
            sh "echo internal"
            if ("${ingressInternalHosts}") {
                "${ingressInternalHosts}".trim().split(",").each {
                    hosts = it.split(":")[0]
                    List<String> paths = Arrays.asList(it.split(":")[1].split("\\s*;\\s*"))
                    println hosts
                    println paths
                    data.ingress.internalType.enabled = "${ingressInternalType }".toBoolean()
                    data.ingress.internalType.hostsAndPaths[hosts] = paths
                }
            }
            sh "echo public"
            if ("${ingressPublicHosts}") {
                "${ingressPublicHosts}".trim().split(",").each {
                    hosts = it.split(":")[0]
                    List<String> paths = Arrays.asList(it.split(":")[1].split("\\s*;\\s*"))
                    println hosts
                    println paths
                    data.ingress.publicType.enabled =  "${ingressPublicType}".toBoolean()
                    data.ingress.publicType.hostsAndPaths[hosts] = paths
                }
            }
            sh "echo external"
            if ("${ingressExternalHosts}" && "${ingressExternalHosts}" != "null" ) {
                "${ingressExternalHosts}".trim().split(",").each {
                    hosts = it.split(":")[0]
                    List<String> paths = Arrays.asList(it.split(":")[1].split("\\s*;\\s*"))
                    println hosts
                    println paths
                    data.ingress.externalType.enabled =  "${ingressExternalType}".toBoolean()
                    data.ingress.externalType.hostsAndPaths[hosts] = paths
                }
            }
            sh "echo private ${ingressPrivateHosts}"
            if ("${ingressPrivateHosts}" && "${ingressPrivateHosts}" != "null") {
                echo "in ingressPrivateHosts"
                "${ingressPrivateHosts}".trim().split(",").each {
                    hosts = it.split(":")[0]
                    List<String> paths = Arrays.asList(it.split(":")[1].split("\\s*;\\s*"))
                    println hosts
                    println paths
                    data.ingress.privateType.enabled =  "${ingressPrivateType}".toBoolean()
                    data.ingress.privateType.hostsAndPaths[hosts] = paths
                }
            }
            sh "echo kv "
            // 设置亲和性
            "${k8sAffinityKVPairs}".split(',').each { item ->
                k = item.split(':')[0]
                v = item.split(':')[1]
                data.affinity."${k}" = "${v}"
            }
            // 设置各种变量
            // 镜像地址
            // def harbo.r = "${harborAddress}".split('/')[2]
            data.image.repository = "${harbor}/${department}/${serviceName}-${environment}"
            data.image.tag = "latest"
            data.replicaCount = "${toReplicaset}".toInteger()
            if ( "${projectPort}" ) {
                data.service.internalPort = "${projectPort}".toInteger()
            }
            
            data.ingress.enabled = "${ingress}".toBoolean()
            // data.affinity.enabled = "${k8sAffinity}".toBoolean()
            // data.affinity.department = "${Department}"
            // data.affinity.dedicated = "${k8sAffinityDedicated}"
            data.service.enabled = "${k8sService}".toBoolean()
            data.service.serviceName = "${k8sServiceName}"
            data.resources.requests.cpu = "${k8sCpuRequest}"
            data.resources.requests.memory = "${k8sMemoryRequest}"
            data.resources.limits.memory = "${k8sMemoryLimit}"
            data.resources.limits.cpu = "${k8sCpuLimit}"
            data.livenessProbe.enabled = "${healthCheck}".toBoolean()
            data.readinessProbe.enabled = "${healthCheck}".toBoolean()
            data.healthCheck = "${healthCheckUrl}"
            if ("${apollo}" == "true") {
                data.apollo.enabled = "${apollo}".toBoolean()
                data.apollo.apolloEnv = "${apolloEnv}"
                data.apollo.apolloMeta = "${apolloMetaServer}"
            }else{
                data.apollo.enabled = "${apollo}".toBoolean()
            }
            sh "echo ${apolloIDC}"
            if ("${apolloIDC}") {
                data.apollo.apolloIDC = "${apolloIDC}"
            }
            // data.apollo.apolloIDC = "${apolloIDC}"

            data.timezone = timeZone
            sh "echo ${projectType}"
            switch ("${projectType}") {
                case "java":
                    data.java.jvmOpts = "-Xmx${JVM} -Xms${JVM}"
                    data.java.bootOpts = ""
                    data.resources.requests.memory = "${JVM}"
                    if ( "${environment}" == "prod" ) {
                        def jvmMemory = template.extractInts("${JVM}")[0]
                        def jvmUnit = template.extractString("${JVM}")[0]
                        def limitMemory = jvmMemory * 2
                        data.resources.limits.memory = "${limitMemory}" + "${jvmUnit}"
                    }
                    break
                case "zip":
                    data.java.jvmOpts = "-Xmx${JVM} -Xms${JVM}"
                    data.java.bootOpts = ""
                    data.resources.requests.memory = "${JVM}"
                    break
                case "node":
                    data.service.internalPort = 80
                    break
                case "python":
                    sh "echo ${pythonRunMethod}"
                    if ( "${pythonRunMethod}" ) {
                        def methods = "${pythonRunMethod}".split(',')
                        sh  "echo ${methods}"
                        "${pythonRunMethod}".split(',').each { run ->
                            sh "echo ${run}"
                            data."${run}".enabled = true
                        }
                    }
                    break
            }
            // 删除已有values文件
            println data
            try {
                sh "rm ${projectType}/${helmChartValueName}-values.yaml"
            } catch(Exception e1) {
                println "file is not exists"
            }
            // 创建或重新创建新values文件
            writeYaml file:"${projectType}/${helmChartValueName}-values.yaml", data:data
        }
    }
}