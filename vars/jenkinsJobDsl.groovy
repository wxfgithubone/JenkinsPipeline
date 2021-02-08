def createJob(jenkinsJobName,gitProject) {
    jobDsl scriptText: """ 
        pipelineJob("${jenkinsJobName}"){
            definition {
                cpsScm { 
                    scm { 
                        git { 
                            remote { url("git@git.devops.net:devops/pipeline.git")
                                    credentials('devopsGit') } 
                            branches('release-v2') 
                            scriptPath('Jenkinsfile') 
                            extensions { }  // required as otherwise it may try to tag the repo, which you may not want 
                        }
                    }
                }
            }
            parameters {
                gitParameterDefinition {
                    name("VERSION")
                    type("branch")
                    defaultValue("master")
                    description("Please select branch,tag or commit id to for building")
                    branch("")
                    branchFilter("origin/(.*)")
                    tagFilter(".*")
                    sortMode("NONE")
                    selectedValue("TOP")
                    useRepository(".*${gitProject}")
                    quickFilterEnabled(true)
                }
            }
        }
    """,
    removedJobAction: 'DELETE'
} 
//removedJobAction: 'DELETE'
def createFolder(jenkinsFolder) {
    jobDsl scriptText: """ 
        folder("${jenkinsFolder}")
    """
}

def createView(department) {
    jobDsl scriptText: """ 
        listView("${department}") {
            jobs {
                regex(/${department}.+/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    """
}