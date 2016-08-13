package org.wenzhe.filewatcher.edd.dsl

class DslContextWrapper2 {
  
  static interface UpdateHandler {
    
  }
  
  static class ScriptExecutor extends UpdateHandler {
    String scriptPath
    ScriptExecutor(String scriptPath) {
      this.scriptPath = scriptPath
    }
  }
  
  static class Watcher {
    boolean start
    String watchedFile
    def updateHandlers = []
    
    def watch(path) {
      println "watch $path"
      watchedFile = path
      this
    }
    
    def onFileUpdated(closure) {
      closure.delegate = this
      closure()
      this
    }
    
    def executeScript(path) {
      println "execute $path"
      updateHandlers += new ScriptExecutor(path)
      this
    }
  }
  
  static class Builder {
    
    def watchers = []
    
    def to
    
    def start(to) {
      println "start to"
      def w = new Watcher()
      w.start = true
      watchers += w
      return w
    }
    
    def stop(to) {
      println "stop to"
      def w = new Watcher()
      w.start = false
      watchers += w
      return w
    }
  }

}

def builder = new DslContextWrapper2.Builder()
builder.with {
  start to watch "E:/wenzhe/file1.md" onFileUpdated {
    println "on file updated"
    executeScript "E:/wenzhe/script/update_blog.bat"
  }
}

