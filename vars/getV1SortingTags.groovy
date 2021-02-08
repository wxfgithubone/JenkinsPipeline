import groovy.json.JsonSlurper

def getV0SortingTags(harbor,department,imageName,env) {
    try {
            // List<String> artifacts = new ArrayList<String>()
            def artifacts = []
            def artifactsUrl = "https://wcr.devops.net/api/repositories/capital%2Fcapital-bill-impl/tags"
            def artifactsObjectRaw = ["curl", "-s", "-k","-u","harbor:oV0yA9Bau5QoUTYu", "--url", "${artifactsUrl}"].execute().text
            def jsonSlurper = new JsonSlurper()
            def artifactsJsonObject = jsonSlurper.parseText(artifactsObjectRaw)
            def dataArray = artifactsJsonObject
            for (item in dataArray){
                if (item.name.split('-')[-1] == "test") {
                    artifacts.add(item.name)
                }
            }
            artifacts.sort{a,b ->
                def n1 = a.split('-')[1] as Integer
                def n2 = b.split('-')[1] as Integer
                if (n1 < n2) {
                    return 1
                } else {
                    return -1
                }
            }
            return artifacts

        } catch (Exception e) {
            print e
    }
}

@NonCPS  // DefaultGroovyMethods.sort overloads are not supported in CPS-transformed mode yet https://issues.jenkins-ci.org/browse/JENKINS-44924?page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel&showAll=true
def getV1SortingTags(String harbor,String department,String appName,String env) {
    def artifacts = []
    try {
        // List<String> artifacts = new ArrayList<String>()
        def artifactsUrl = "${harbor}/api/repositories/${department}%2F${appName}-${env}/tags"
        def artifactsObjectRaw = ["curl", "-s", "-k","-u","sea-deploy:uYKYKbKPNh3bcI2A", "--url", "${artifactsUrl}"].execute().text
        def jsonSlurper = new JsonSlurper()
        def artifactsJsonObject = jsonSlurper.parseText(artifactsObjectRaw)
        def dataArray = artifactsJsonObject
        for (item in dataArray){
            if (item.name != "latest") {
                artifacts.add(item.name + '\t' + item.created)
            }
        }
        artifacts.sort{a,b ->
            def n1 = a.split('-')[0] as Integer
            def n2 = b.split('-')[0] as Integer
            if (n1 < n2) {
                return 1
            } else {
                return -1
            }
        }
        artifacts.add(0,"Please Select Version")
        return artifacts

    } catch (Exception e) {
        artifacts.add(0,"Please Select Version")
        return artifacts
    }
}