#!/usr/bin/env groovy

def call(harborAddress,module,imageName,imageTag) {
    def harbor = harborAddress.split('/')[2]
    pipeline {
        stage("build") {
            withDockerRegistry([credentialsId: 'jenkina-habor-credential', url: "${harborAddress}"]) {
                withDockerContainer([image: "${harbor}/wekube/maven:3.5.4-jdk-8"]) {
                    if ( "${module}" == "monitor-api" ) {
                        sh "mvn -U clean package -pl com.devops.monitor:api -amd spring-boot:repackage  -DskipTests=true"
                    } else if ( "${module}" == "monitor-alert" ) {
                        sh "mvn -U clean package -pl com.devops.monitor:alert -amd spring-boot:repackage  -DskipTests=true"
                    } else {
                        sh "mvn  -U -B -T 4 -pl ./${module}  -am -DskipTests=true -Dmaven.test.skip=true -DsmeEnv=true  clean  package"
                    }
                }
                script {
                    def customImage = docker.build("${imageName}:${imageTag}", "./${module}")
                    customImage.push()
                    customImage.push('latest')
                }
            }
        }
    }
}
