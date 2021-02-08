// 该方法为取一个字符串中的数字、非数字
package org.file;
@NonCPS
def extractInts( String input ) {
  input.findAll( /\d+/ )*.toInteger()
}

@NonCPS
def extractString( String input ) {
  input.findAll( /\D+/)*.toString()
}