import groovy.json.JsonSlurper
import groovy.json.*
import java.text.MessageFormat


def jenkinsInit(department,cluster,clusterCode,environment,sourceRepo,apiServer,projectType,compiler_version,projectRepo,module,serviceName,packageName,port,jvm,healthCheckUrl,ingress,ingressInternalType,ingressInternalHosts,ingressPublicType,ingressPublicHosts,ingressExternalType,ingressExternalHosts,ingressPrivateType,ingressPrivateHosts,k8sService,k8sServiceName,affinity,dedicated,replicaset,k8sCpuRequest,k8sMemoryRequest,k8sCpuLimit,k8sMemoryLimit,apollo,apolloMetaServer,apolloEnv,apolloIDC,harborAddress,argoAddress,jenkinsFolder,jenkinsJobName,helmChartValueName,applicationName,k8sNamespace,goRootDir,healthCheck,timeZone,pythonRunMethod) {
    def value = [:]
    value["department"] = department
    value["environment"] = environment
    value["serviceName"] = serviceName
    value["projectType"] = projectType
    value["jenkinsJobName"] = jenkinsJobName
    value["sourceRepo"] = sourceRepo
    value["apiServer"] = apiServer
    value["compiler_version"] = compiler_version
    value["projectRepo"] = projectRepo
    value["module"] = module
    value["cluster"] = cluster
    value["clusterCode"] = clusterCode

    value["packageName"] = packageName
    value["port"] = port
    value["jvm"] = jvm
    value["healthCheckUrl"] = healthCheckUrl
    value["ingress"] = ingress
    value["ingressInternalType"] = ingressInternalType
    value["ingressInternalHosts"] = ingressInternalHosts
    value["ingressPublicType"] = ingressPublicType
    value["ingressPublicHosts"] = ingressPublicHosts
    value["ingressExternalType"] = ingressExternalType
    value["ingressExternalHosts"] = ingressExternalHosts
    value["ingressPrivateType"] = ingressPrivateType
    value["ingressPrivateHosts"] = ingressPrivateHosts
    
    value["k8sService"] = k8sService
    value["k8sServiceName"] = k8sServiceName
    value["affinity"] = affinity
    value["dedicated"] = dedicated
    value["replicaset"] = replicaset
    value["k8sCpuRequest"] = k8sCpuRequest
    value["k8sMemoryRequest"] = k8sMemoryRequest
    value["k8sCpuLimit"] = k8sCpuLimit

    value["k8sMemoryLimit"] = k8sMemoryLimit
    value["apollo"] = apollo
    value["apolloMetaServer"] = apolloMetaServer
    value["apolloEnv"] = apolloEnv
    value["apolloIDC"] = apolloIDC
    value["harborAddress"] = harborAddress
    value["argoAddress"] = argoAddress
    value["jenkinsFolder"] = jenkinsFolder
    value["helmChartValueName"] = helmChartValueName

    value["applicationName"] = applicationName
    value["k8sNamespace"] = k8sNamespace
    value["goRootDir"] = goRootDir
    value["healthCheck"] = healthCheck
    value['timeZone'] = timeZone
    value['pythonRunMethod'] = pythonRunMethod
    def params = [:]
    params["department"] = department
    params["environment"] = environment
    params["applicationName"] = applicationName
    params["projectType"] = projectType
    params["jenkinsJobName"] = jenkinsJobName
    params["value"] = value

    params_map = new JsonBuilder(params).toPrettyString()
    println params_map
    def project_info_url = "http://wefocus-api.devops.net/api/wefocus/v1/set_project_parameters"
    def http = new URL("${project_info_url}").openConnection() as HttpURLConnection
    // String username = "admin"
    // String password = "adminadminadmin"
    // String userpassword = username + ":" + password
    // String encoded = userpassword.bytes.encodeBase64().toString()

    http.setRequestMethod('POST')
    http.setDoOutput(true)
    http.setRequestProperty("Accept", 'application/json')
    http.setRequestProperty("Content-Type", 'application/json')
    // http.setRequestProperty("Authorization", "Basic " + encoded);
    http.outputStream.write(params_map.getBytes("UTF-8"))
    http.connect()
    println http.responseCode
    if (http.responseCode ==  200) {
        println "成功写入数据"
    } else {
        println(MessageFormat.format("写入失败,状态码：{0}", http.responseCode))
    }
}