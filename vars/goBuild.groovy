#!/usr/bin/env groovy

def call(harborAddress,imageName,imageTag) {
    pipeline {
        stage("build") {
            sh "echo ${harborAddress}"
            withDockerRegistry([credentialsId: 'jenkina-habor-credential', url: "${harborAddress}"]) {
                def customImage = docker.build("${imageName}:${imageTag}", ".")
                customImage.push()
                customImage.push('latest')
            }
        }
    }
}