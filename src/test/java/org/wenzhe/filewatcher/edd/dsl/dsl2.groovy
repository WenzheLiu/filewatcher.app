package org.wenzhe.filewatcher.edd.dsl

class DslContextWrapper1 {
  static class Builder {
    
    def to = ""
    
    def start(to) {
      println "start to"
      this
    }
    
    def watch(path) {
      println "watch $path"
      this
    }
    
    def on(closure) {
      closure()
    }
  }
}

def builder = new DslContextWrapper1.Builder()
builder.with {
  start to watch "E:/wenzhe/file1.md" on {
    println "on watch"
  }
}
