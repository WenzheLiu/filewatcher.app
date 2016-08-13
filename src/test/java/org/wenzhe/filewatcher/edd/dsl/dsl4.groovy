package org.wenzhe.filewatcher.edd.dsl

class DslContextWrapper3 {
  
  static interface UpdateHandler {
    
  }
  
  static class Executor {
  
  }
  
  static class ScriptExecutor extends Executor {
    String scriptPath
    ScriptExecutor(String scriptPath) {
      this.scriptPath = scriptPath
    }
  }

  static class ExecutorSequence {
    def executor = []
    
    def executeScript(path) {
      println "execute script $path"
      executor += new ScriptExecutor(path)
      this
    }
  }
  
  static enum FileType {
    FILE, FOLDER, FILE_AND_FOLDER
  }
  
  static enum UpdateType {
    CREATED, MODIFIED, DELETED, 
    UPDATED // include all
  }
  
  static class Handler {
    Watcher watcher
    FileType handledFileType
    ExecutorSequence execSeq = new ExecutorSequence()
    UpdateType updateType
    
    def Handler(Watcher watcher, FileType handledFileType) {
      this.watcher = watcher
      this.handledFileType = handledFileType
    }
    
    private Watcher execute(closure) {
      closure.delegate = execSeq
      closure()
      return watcher
    }
    
    def updated(closure) {
      updateType = UpdateType.UPDATED
      println updateType
      return execute(closure)
    }
    
    def modified(closure) {
      updateType = UpdateType.MODIFIED
      println updateType
      return execute(closure)
    }
    
    def created(closure) {
      updateType = UpdateType.CREATED
      println updateType
      return execute(closure)
    }
    
    def deleted(closure) {
      updateType = UpdateType.DELETED
      println updateType
      return execute(closure)
    }
  }
  
  static class Watcher {
    boolean start
    String watchedFile
    
    def handlers = []
    
    def watch(path) {
      println "watch $path"
      watchedFile = path
      this
    }
    
    def on(FileType fileType) {
      println "on $fileType"
      def handler = new Handler(this, fileType)
      handlers += handler
      return handler
    }
  }
  
  static class Builder {
    
    def watchers = []
    
    def to
    def file = FileType.FILE
    def folder = FileType.FOLDER
    def fileAndFolder = FileType.FILE_AND_FOLDER
    
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


def builder = new DslContextWrapper3.Builder()
builder.with {
  start to watch "E:/wenzhe/file1.md" on file modified {
    executeScript "E:/wenzhe/script/update_blog.bat"
  }
}

