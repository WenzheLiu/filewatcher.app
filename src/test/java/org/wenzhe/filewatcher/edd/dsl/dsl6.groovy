package org.wenzhe.filewatcher.edd.dsl

import java.time.LocalDateTime

class DslContextWrapper5 {
  
  static interface UpdateHandler {
    
  }
  
  static class Executor {
    String workingDirectory
    String stdout
    String stderr
    
    def setWorkingDirectory(workDir) {
      println "workdir: $workDir"
      this.workingDirectory = workDir
    }
    def setStdout(stdout) {
      println "stdout: $stdout"
      this.stdout = stdout
    }
    def setStderr(stderr) {
      println "stderr: $stderr"
      this.stderr = stderr
    }
  }
  
  static class ScriptExecutor extends Executor {
    String scriptPath

    ScriptExecutor(String scriptPath) {
      this.scriptPath = scriptPath
    }
  }
  
  static class CodeExecutor extends Executor {
    def code

    CodeExecutor(code) {
      this.code = code
    }
  }

  static class ExecutorSequence {
    def executor = []
    
    def execute(String script, workingDirectory="", stdout="", stderr="") {
      println "execute script $script"
      def scriptExecutor = new ScriptExecutor(script)
      scriptExecutor.setWorkingDirectory(workingDirectory)
      scriptExecutor.setStdout(stdout);
      scriptExecutor.setStderr(stderr);
      executor += scriptExecutor
      this
    }
    
    def execute(Closure code, workingDirectory="", stdout="", stderr="") {
      println "execute code"
      def codeExecutor = new CodeExecutor(code)
      codeExecutor.setWorkingDirectory(workingDirectory)
      codeExecutor.setStdout(stdout);
      codeExecutor.setStderr(stderr);
      executor += codeExecutor
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


def builder = new DslContextWrapper5.Builder()
builder.with {
  
  start to watch "E:/wenzhe/file1.md" on file modified {
    
    execute script = "E:/wenzhe/script/update_blog.bat",
      workingDirectory = "E:/wenzhe/script",
      stdout = "E:/wenzhe/output/file1_update_blog_stdout.log",
      stderr = "E:/wenzhe/output/file1_update_blog_stderr.log"
      
    execute "E:/wenzhe/script/send_email.bat"
    
  } on file modified {
  
    execute "E:/wenzhe/script/upload_to_cloud.bat"
    
    execute { updatedFile, updatedType ->
      
      def now = LocalDateTime.now()
      println "file $updatedFile upload to cloud on $now"
    }
  } on file created {
  
    execute code = { updatedFile, updatedType ->
      
      println "file $updatedFile $updatedType"
      if (updatedFile.endsWith(".md")) {
        println "this is a md file"
      } else {
        println "this is not a md file"
      }
    }, stdout = "E:/wenzhe/output/file1_create_stdout.log"
  }
}
// execute the code
def executor = builder.watchers[0].handlers[1].execSeq.executor[1]
executor.code("E:/wenzhe/file1234.md", "modified")
executor = builder.watchers[0].handlers[2].execSeq.executor[0]
executor.code("E:/wenzhe/file5678.md", "created")
