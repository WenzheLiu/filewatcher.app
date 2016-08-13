package org.wenzhe.filewatcher.edd.dsl



class DslContextWrapper8 {
  
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
    String[] extensions
    
    def ExtensionFilter(String[] extensions) {
      this.extensions = extensions
    }
  }
  
  static class CodeFilter extends FilterCondition {
    Closure code
    
    def CodeFilter(Closure code) {
      this.code = code
    }
  }
  
  static class FolderFilter extends FilterCondition {
    String[] folders
    
    def FolderFilter(String[] folders) {
      this.folders = folders;
    }
    
  }
  
  static class EqualFilter extends FilterCondition {
    String[] values
    boolean ignoreCase
    
    def EqualFilter(String[] values, boolean ignoreCase) {
      this.values = values;
      this.ignoreCase = ignoreCase
    }
  }
  
  static class StartFilter extends FilterCondition {
    String[] values
    boolean ignoreCase
    
    def StartFilter(String[] values, boolean ignoreCase) {
      this.values = values;
      this.ignoreCase = ignoreCase
    }
  }
  
  static class EndFilter extends FilterCondition {
    String[] values
    boolean ignoreCase
    
    def EndFilter(String[] values, boolean ignoreCase) {
      this.values = values;
      this.ignoreCase = ignoreCase
    }
  }
  
  static class ContainFilter extends FilterCondition {
    String[] values
    boolean ignoreCase
    
    def ContainFilter(String[] values, boolean ignoreCase) {
      this.values = values;
      this.ignoreCase = ignoreCase
    }
  }
  
  static class MatchFilter extends FilterCondition {
    String value
    boolean ignoreCase
    
    def MatchFilter(String value, boolean ignoreCase) {
      this.value = value;
      this.ignoreCase = ignoreCase
    }
  }
  
  static enum NamePath {
    NAME, PATH
  }
  
  static class Filter {
    Watcher watcher
    FilterType filterType
    FilterCondition filterCondition
    FileType fileType = FileType.FILE_AND_FOLDER
    NamePath nameOrPath = NamePath.NAME
    boolean ignoreCase = false
    
    def Filter(Watcher watcher, FilterType filterType) {
      this.watcher = watcher
      this.filterType = filterType;
    }
    
    def by(FileType fileType) {
      println("by $fileType")
      this.fileType = fileType
      return this
    }
    
    def by(NamePath nameOrPath) {
      println("by $nameOrPath")
      this.nameOrPath = nameOrPath
      return this
    }
    
    def folder(NamePath nameOrPath) {
      return by(FileType.FOLDER).by(nameOrPath)
    }
    
    def file(NamePath nameOrPath) {
      return by(FileType.FILE).by(nameOrPath)
    }
    
    def cases(boolean ignoreCase) {
      println("cases " + ignoreCase ? "insensitive" : "sensitive")
      this.ignoreCase = ignoreCase
      return this
    }
    
    def extension(String... extensions) {
      println("extension $extensions")
      filterCondition = new ExtensionFilter(extensions)
      return watcher
    }
    
    def code(Closure code) {
      println("code filter")
      filterCondition = new CodeFilter(code)
      return watcher
    }
    
    def equalsTo(String... values) {
      println("equals to $values")
      filterCondition = new EqualFilter(values, ignoreCase)
      return watcher
    }
    
    def startsWith(String... values) {
      println("starts with $values")
      filterCondition = new StartFilter(values, ignoreCase)
      return watcher
    }
    
    def endsWith(String... values) {
      println("ends with $values")
      filterCondition = new EndFilter(values, ignoreCase)
      return watcher
    }
    
    def contains(String... values) {
      println("contains $values")
      filterCondition = new ContainFilter(values, ignoreCase)
      return watcher
    }
    
    def matches(String value) {
      println("matches $value")
      filterCondition = new MatchFilter(value, ignoreCase)
      return watcher
    }
  }
  
  static class Watcher {
    boolean start
    boolean recursively
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
      def ft = new Filter(this, filterType)
      filters += ft
      return ft
    }
  }
  
  static class Builder {
    
    def watchers = []
    
    def recursively = true
    def to = !recursively
    
    def file = FileType.FILE
    def folder = FileType.FOLDER
    def fileAndFolder = FileType.FILE_AND_FOLDER
    def include = FilterType.INCLUDE
    def exclude = FilterType.EXCLUDE
    def name = NamePath.NAME
    def path = NamePath.PATH
    
    def sensitive = false
    def insensitive = !sensitive
    
    def start(recursively) {
      if (recursively) {
        print "recursively "
      }
      println "start to"
      def w = new Watcher()
      w.recursively = recursively
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


def builder = new DslContextWrapper8.Builder()
builder.with {

  start recursively watch "E:/wenzhe/folder1" \
  filter include extension (
    "md", "txt", "doc", "docx", "png", "jpg", "jpeg"
  ) filter exclude folder name equalsTo "target", "bin", ".settings" \
  filter exclude file name matches "\\d{4}\\.\\d?\\d\\.\\d?\\d" \
  filter include file name startsWith "wenzhe", "java" \
  filter exclude folder name cases insensitive endsWith "test", "DSL" \
  filter exclude folder path contains "src/test", "src/main/resources" \
  on file modified {
    execute "E:/wenzhe/script/update_blog.bat"
  }
}
