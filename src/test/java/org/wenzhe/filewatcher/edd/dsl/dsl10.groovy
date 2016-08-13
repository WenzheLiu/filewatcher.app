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

  start to watch "E:/wenzhe/aa" \
  filter include extension (
    "txt"
  ) filter exclude file name contains "123" \
  on file modified { updatedFile ->
    def now = java.time.LocalDateTime.now()
    println "file $updatedFile upload to cloud on $now"
  }
  
}
CodeExecutor exec1 = builder.getWatchers().get(0).getHandlers().get(1).getExecSeq().getExecutors().get(0)
Path path = Paths.get("src", "java")
exec1.getCode().call(path, UpdateType.MODIFIED)

ConditionFilter whenCondition = builder.getWatchers().get(0).getFilters().last().getFilterCondition();
println whenCondition.getCondition().call(path, UpdateType.MODIFIED)
