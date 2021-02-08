if (!"${ProjectModule}") {
    Module = "${GitProject}"
    CompileModule = ""
} else {
    Module = "${ProjectModule}".replaceAll('/','-')
    CompileModule = "${ProjectModule}"

}
// pipelineJob("${Department}-${GitGroup}-${ProjectEnv}/${Department}-${ClusterCode}-${ProjectEnv}-${AppName}"){
pipelineJob("${env.JenkinsJobName}"){
    definition {
        cpsScm { 
            scm { 
                git { 
                    remote { url("git@git.devops.net:devops/pipeline.git")
                             credentials('devops') } 
                    branches('master') 
                    scriptPath('Jenkinsfile') 
                    extensions { }  // required as otherwise it may try to tag the repo, which you may not want 
                }
            }
        }
    }
    parameters {
        // gitParam('BRANCH') {
        //     description('Revision commit SHA')
        //     type('BRANCH')
        //     branch('master')
        // }
        stringParam("Department","${Department}")
        stringParam("ProjectType","${ProjectType}")
        choiceParam("ProjectEnv",["${ProjectEnv}"])
        stringParam("Module","${Module}")
		stringParam("CompileModule","${CompileModule}")
		stringParam("GitGroup","${GitGroup}")
		stringParam("GitProject","${GitProject}")
        stringParam("ProjectPackage","${ProjectPackage}")
        stringParam("AppName","${AppName}")
        stringParam("ArgoAppName","${Department}-${ClusterCode}-${ProjectEnv}-${AppName}")
        stringParam("ProjectTypeVersion","${ProjectTypeVersion}")
        stringParam("Harbor","${Harbor}")
        stringParam("ArgoAddr","${ArgoAddr}")
        stringParam("SonarScan","${SonarScan}")
        if ("${ProjectType}" == 'go') {
            stringParam("GoRootDir","${GoRootDir}")
        }
    }
}