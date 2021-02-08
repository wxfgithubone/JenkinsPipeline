import groovy.json.JsonSlurper
import groovy.json.JsonOutput

//例子：
// {
//     "code": 1,
//     "data": [
//         {
//             "affinity": "true",
//             "apiServer": "null",
//             "apollo": "true",
//             "apolloEnv": "PRO",
//             "apolloIDC": "default",
//         }
//     ]
// }

def call(){
    try {
        def artifactsUrl = "http://wefocus-api.devops.net/api/wefocus/v1/get_project_parameters_info?jobname=${JOB_NAME}"
        def artifactsObjectRaw = ["curl", "-s", "-k", "--url", "${artifactsUrl}"].execute().text
        def jsonSlurper = new JsonSlurper()
        def artifactsJsonObject = jsonSlurper.parseText(artifactsObjectRaw)['data'][0]
        // 序列化json，否则jenkinsfile调用会报错
        print artifactsJsonObject
        return JsonOutput.toJson(artifactsJsonObject)
    } catch (Exception e) {
        print "There was a problem fetching the artifacts"
    }
}

获取每个服务的所有信息，列表内每个服务为一个map
@NonCPS
def getAllEnvParameters(department,env){
    try {
        List<String> artifacts = new ArrayList<String>()
        def artifactsUrl = "http://wefocus-api.devops.net/api/wefocus/v1/get_department_env_parameters_info?department=${department}&env=${env}"      
        def artifactsObjectRaw = ["curl", "-s", "-k", "--url", "${artifactsUrl}"].execute().text
        def jsonSlurper = new JsonSlurper()
        def artifactsJsonObject = jsonSlurper.parseText(artifactsObjectRaw)
        def dataArray = artifactsJsonObject.data
        for (item in dataArray) {
            artifacts.add(item)
        }
        return JsonOutput.toJson(artifacts)
        // return dataArray
    } catch (Exception e) {
        print "There was a problem fetching the artifacts"
    }
}

def getAllEnvAPP(department,env) {
    try {
        List<String> artifacts = new ArrayList<String>()
        def artifactsUrl = "http://wefocus-api.devops.net/api/wefocus/v1/get_department_env_parameters_info?department=${department}&env=${env}"          
        def artifactsObjectRaw = ["curl", "-s", "-k", "--url", "${artifactsUrl}"].execute().text
        def jsonSlurper = new JsonSlurper()
        def artifactsJsonObject = jsonSlurper.parseText(artifactsObjectRaw)
        def dataArray = artifactsJsonObject.data
        for(item in dataArray){
            artifacts.add(item.applicationName)
        }
        return artifacts
    } catch (Exception e) {
        print "There was a problem fetching the artifacts"
    }
}