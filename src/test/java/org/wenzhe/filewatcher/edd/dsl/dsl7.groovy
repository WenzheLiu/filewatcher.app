package org.wenzhe.filewatcher.edd.dsl

import java.time.DayOfWeek
import java.time.LocalDateTime


class DslContextWrapper7 {
  
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
  
  static enum FilterType {
    INCLUDE, EXCLUDE
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
  
  static class FilterCondition {
    
  }
  
  static class ExtensionFilter extends FilterCondition {
    def extensions
    
    def ExtensionFilter(extensions) {
      this.extensions = extensions
    }
  }
  
  static class CodeFilter extends FilterCondition {
    Closure code
    
    def CodeFilter(Closure code) {
      this.code = code
    }
  }
  
  static class Filter {
    Watcher watcher
    FilterType filterType
    FilterCondition filterCondition
    
    def Filter(Watcher watcher, FilterType filterType) {
      this.watcher = watcher
      this.filterType = filterType;
    }
    
    def extension(extensions) {
      println("extension $extensions")
      filterCondition = new ExtensionFilter(extensions)
      return watcher
    }
    
    def code(Closure code) {
      println("code filter")
      filterCondition = new CodeFilter(code)
      return watcher
    }
  }
  
  static class Watcher {
    boolean start
    String watchedFile
    
    def handlers = []
    def filters = []
    
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
    
    def filter(FilterType filterType) {
      println "filter $filterType"
      def filter = new Filter(this, filterType)
      filters += filter
      return filter
    }
  }
  
  static class Builder {
    
    def watchers = []
    
    def to
    def file = FileType.FILE
    def folder = FileType.FOLDER
    def fileAndFolder = FileType.FILE_AND_FOLDER
    def include = FilterType.INCLUDE
    def exclude = FilterType.EXCLUDE
    
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


def builder = new DslContextWrapper7.Builder()
builder.with {

  def isWorkTime = {dateTime ->
    DayOfWeek dayOfWeek = dateTime.getDayOfWeek()
    int hour = dateTime.getHour()
    return hour >= 9 && hour < 18 &&
      [DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
       DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
      ].contains(dayOfWeek)
  }
  
  start to watch "E:/wenzhe/folder1" filter include extension ([
    ".md", ".txt", ".doc", ".docx", ".png", ".jpg", ".jpeg"
  ]) filter exclude code { updatedFile, updatedType ->
    isWorkTime(LocalDateTime.now())
  } on file modified {
    execute "E:/wenzhe/script/update_blog.bat"
  }
}
// execute the code
println builder.watchers[0].filters[1].filterCondition.code("E:/wenzhe/file1.md", "modified")
