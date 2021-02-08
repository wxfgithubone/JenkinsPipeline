def call(String JobName) {
	if (jenkins.model.Jenkins.instance.getItem("${JobName}") != null) {
        return true
    } else {
        return false
    }
}
