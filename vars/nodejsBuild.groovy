#!/usr/bin/env groovy

def call(harborAddress,compiler_version,imageName,imageTag) {
    pipeline {
        def harbor = harborAddress.split('/')[2]
        stage("build") {
            withDockerRegistry([credentialsId: 'jenkina-habor-credential', url: "${harborAddress}"]) {
               withDockerContainer("${harbor}/wekube/node:${compiler_version}") {
                    sh "sh buildops.sh"
                }
                script {
                    def customImage = docker.build("${imageName}:${imageTag}", ".")
                    customImage.push()
                    customImage.push('latest')
                }
            }
        }
    }
}