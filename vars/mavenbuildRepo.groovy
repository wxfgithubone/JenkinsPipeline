#!/usr/bin/env groovy

def call() {
    pipeline {
        stage("build") {
            withDockerRegistry([credentialsId: 'jenkina-habor-credential', url: "https://${Harbor}"]) {
                withDockerContainer([image: 'wcr.devops.net/wekube/maven:3.5.4-jdk-8']) {
                    withMaven(maven: 'maven-3.5.4', mavenSettingsConfig: 'cterminal-Settings') {
                        sh "mvn  -U -B -T 4 -pl ./${env.CompileModule}  -am -DskipTests=true  clean package"
                    }
                }
                script {
                    def customImage = docker.build("${env.RepoAddr}/${env.ImageName}:${env.ImageTag}", "./${env.CompileModule}")
                    customImage.push()
                    customImage.push('latest')
                }
            }
        }
    }
}