package org.wenzhe.filewatcher.edd.dsl

import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

import org.wenzhe.filewatcher.dsl.CodeExecutor
import org.wenzhe.filewatcher.dsl.ConditionFilter
import org.wenzhe.filewatcher.dsl.FileWatcherDslContext
import org.wenzhe.filewatcher.dsl.UpdateType

def builder = new FileWatcherDslContext()
builder.with {
  
  version "1.0"

  start recursively watch "E:/wenzhe/folder1" \
  filter include extension (
    "md", "txt", "doc", "docx", "png", "jpg", "jpeg"
  ) filter exclude folder name equalsTo "target", "bin", ".settings" \
  filter exclude file name matches "\\d{4}\\.\\d?\\d\\.\\d?\\d" \
  filter include file name startsWith "wenzhe", "java" \
  filter exclude folder name cases insensitive endsWith "test", "DSL" \
  filter exclude folder path contains "src/test", "src/main/resources" \
  filter exclude when { updatedFile, updatedType ->
    println "when file $updatedFile $updatedType"
    true;
  } \
  on file modified {
    execute "E:/wenzhe/script/update_blog.bat"
  } \
  on file and folder created { handler ->
    handler execute { updatedFile, updatedType ->
      
      def now = LocalDateTime.now()
      println "file $updatedFile upload to cloud on $now"
    }
  }
}

CodeExecutor exec1 = builder.getWatchers().get(0).getHandlers().get(1).getExecSeq().getExecutors().get(0)
Path path = Paths.get("src", "java")
exec1.getCode().call(path, UpdateType.MODIFIED)

ConditionFilter whenCondition = builder.getWatchers().get(0).getFilters().last().getFilterCondition();
println whenCondition.getCondition().call(path, UpdateType.MODIFIED)
