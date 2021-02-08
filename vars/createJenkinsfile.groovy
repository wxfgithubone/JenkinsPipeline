def call() {
	pipelineJob('testpipeline'){
        definition {
            cps {
                script(readFileFromWorkspace("cicdjobs/jenkinsfiles/jenkinsfile_${ProjectType}.groovy"))
            }
        }
        //authenticationToken('11739db8e2f84262f9c8e52adce4f08675')
        parameters {
            stringParam("ProjectType","${ProjectType}")
            choiceParam("Environment",['prod','test'])
        }
    }
}
