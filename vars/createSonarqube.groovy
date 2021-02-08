#!/usr/bin/env groovy

def call(String CompileModule,String JobName,String Branch,String Languange) {
    Date date = new Date()
    String dateTime = date.format("yyyy-MM-dd_HH-mm-ss")
    def template = new org.file.GStingTemplateToFile()
    if ("${CompileModule}") {
        def sonarqueb = libraryResource 'sonarqube/sonar-project.properties'
        def binding = [
            projectVersion: dateTime,
            module: "${CompileModule}",
            project_name: "${JobName}".split('/').last(),
            branch: "${Branch}".replaceAll('/','_'),  //该名称，在普通pipeline中必须在主jenkinsfile中定义变量获取
            module_name: "${CompileModule}".replaceAll('/','_')
        ]
        def output = template.renderTemplate(sonarqueb.trim(), binding)
        pipeline {
            stage("Write sonarqube") {
                script {
                    sh "echo"
                    writeFile file: "${env.WORKSPACE}/sonar-project.properties", text: "${output}"
                }
            }
        }
    } else {
        def sonarqueb = libraryResource 'sonarqube/sonar-project-nomodule.properties'
        def binding = [
            projectVersion: dateTime,
            project_name: "${JobName}".split('/').last(),
            branch: "${Branch}".replaceAll('/','_'),  //该名称，在普通pipeline中必须在主jenkinsfile中定义变量获取
            language: "${Languange}"
        ]
        def output = template.renderTemplate(sonarqueb.trim(), binding)
        pipeline {
            stage("Write sonarqube") {
                script {
                    sh "echo"
                    writeFile file: "${env.WORKSPACE}/sonar-project.properties", text: "${output}"
                }
            }
        }
    }
}