#!/usr/bin/env groovy
package org.file;

def renderTemplate(templateContent, binding) {
    def engine = new groovy.text.GStringTemplateEngine()
    def template = engine.createTemplate(templateContent).make(binding)
    def templateString = template.toString()
    engine = null
    template = null
    println templateString
    return templateString
}