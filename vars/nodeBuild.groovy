#!/usr/bin/env groovy

def call(harborAddress,compiler_version,imageName,imageTag) {
    pipeline {
        def harbor = harborAddress.split('/')[2]
        stage("build") {
            withDockerRegistry([credentialsId: 'jenkina-habor-credential', url: "${harborAddress}"]) {
               withDockerContainer("${harbor}/wekube/node:${compiler_version}") {
                //    withNPM(npmrcConfig: 'devops_npm_repository') {
                    sh "sh buildops.sh"
                    // sh "npm install"
                    // sh "npm run dll"
                    // sh "npm run build"
                //    }
                }
                // nodejs(configId: 'devops_npm_repository', nodeJSInstallationName: "${compiler_version}") {
                //     // some block
                //     sh "sh buildops.sh"
                // }
                script {
                    def customImage = docker.build("${imageName}:${imageTag}", ".")
                    customImage.push()
                    customImage.push('latest')
                }
            }
        }
    }
}