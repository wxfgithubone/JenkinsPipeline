def call(String sendContents, String sendSummary,String sendBody,String sendTo) {
  // Default values
  emailext body: "${sendBody}", recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']], subject: "${sendContents}", to: "${sendTo}"
}