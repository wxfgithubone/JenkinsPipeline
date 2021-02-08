#!/usr/bin/env groovy

def call() {
    pipeline {
        stage('SonarQube analysis') {
            def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'

            echo "starting codeAnalyze......"
            script {
                echo "starting script......"
                withSonarQubeEnv('SonarQube-Public') {
                   sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }
    }
}