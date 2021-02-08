#!/usr/bin/env groovy

def call(projectType,module,packageName,environment,goRootDir,projectRepo,harbor,compiler_version) {
    def gitProject = "${projectRepo}".split(':|/|\\.')[4]
    def template = new org.file.GStingTemplateToFile()
    sh "echo ${projectType}"
    type = "${projectType}"
    if ( "${module}" ) {
        toFile = "${module}/Dockerfile"
    } else {
        toFile = "./Dockerfile"
    }
    if ( "${projectType}" == "go" ) {
        toFile = "./Dockerfile"
    }
    println "Dockerfile输出于"
    println toFile
    if ( type == 'java') {
        def dockerfile = libraryResource 'dockerfiles/javaDockerfile'
        def binding = [
            harbor: "${harbor}",
            app_name : "${packageName}"
        ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: toFile, text: "${output}"
                }
            }
        }
    } else if (type == 'zip'){
        def dockerfile = libraryResource 'dockerfiles/zipDockerfile'
        def binding = [
                app_name : "${packageName}",
                projectenv: "${environment}"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: toFile, text: "${output}"
                }
            }
        }
    } else if (type == 'go'){
        def dockerfile = libraryResource 'dockerfiles/goDockerfile'
        def binding = [
                git_project : "${gitProject}",
                git_module: "${module}",
                go_root_dir : "${goRootDir}",
                projectenv: "${environment}"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: toFile, text: "${output}"
                }
            }
        }
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: './Dockerfile', text: "${output}"
                }
            }
        }
    } else if (type == 'node'){
        def dockerfile = libraryResource 'dockerfiles/nodeDockerfile'
        def binding = [
                creator : "wangxingmin@devops.net",
                harbor: "${harbor}",
                projectpackage: "${packageName}"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: './Dockerfile', text: "${output}"
                }
            }
        }
    } else if (type == 'python'){
        def dockerfile = libraryResource 'dockerfiles/pythonDockerfile'
        def binding = [
                creator : "wangxingmin@devops.net",
                harbor: "${harbor}",
                pythonVersion: "${compiler_version}",
                projectenv: "${environment}"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: './Dockerfile', text: "${output}"
                }
            }
        }
    } else if (type == 'h5'){
        def dockerfile = libraryResource 'dockerfiles/h5Dockerfile'
        def binding = [
                creator : "wangxingmin@devops.net"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: './Dockerfile', text: "${output}"
                }
            }
        }
    } else if (type == 'nodejs'){
        def dockerfile = libraryResource 'dockerfiles/nodejsDockerfile'
        def binding = [
                harbor: "${harbor}",
                nodejsVersion: "${compiler_version}",
                creator : "wangxingmin@devops.net"
            ]
        def output = template.renderTemplate(dockerfile.trim(), binding)
        pipeline {
            stage("Write Dockerfile") {
                script {
                    sh "echo"
                    writeFile file: './Dockerfile', text: "${output}"
                }
            }
        }
    } 
    else {
        println "It's impossible for you to see this message"
    }
}