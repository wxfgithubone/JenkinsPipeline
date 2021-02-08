#!/usr/bin/env groovy

// 参数 1 模块名称 2 gitcommit号 3 仓库地址
def call(String Department,String ProjectType,String ArgoAddr,String ArgoAppName,String HelmChartValueName,String KubernetesAPIServer,String K8sNamespace,String SourceRepo,String ArgocdToken) {
    pipeline {
        stage("Dynamic Generate Helm Value") {
            script {
                withEnv(["ARGOCD_SERVER=${ArgoAddr}","ARGOCD_AUTH_TOKEN=${ArgocdToken}","USER=argocd"]) {
                    // sh "export USER=argocd"
                    sh "argocd app create ${ArgoAppName} \
                        --values ${HelmChartValueName}-values.yaml \
                        --repo ${SourceRepo} \
                        --revision HEAD \
                        --dest-server ${KubernetesAPIServer} \
                        --dest-namespace ${K8sNamespace} \
                        --project ${Department} \
                        --path ${ProjectType} \
                        --upsert \
                        --grpc-web \
                        --insecure"
                }
            }
        }
    }
}
