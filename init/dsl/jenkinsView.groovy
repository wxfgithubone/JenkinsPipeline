listView("${params.Department}") {
    jobs {
        regex(/${params.Department}.+/)
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}