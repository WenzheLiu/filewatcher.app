package org.wenzhe.filewatcher.app;

import static org.wenzhe.filewatcher.dsl.FileWatcherDslContext.file;
import static org.wenzhe.filewatcher.dsl.FileWatcherDslContext.include;
import static org.wenzhe.filewatcher.dsl.FileWatcherDslContext.name;
import static org.wenzhe.filewatcher.dsl.FileWatcherDslContext.to;

import org.wenzhe.filewatcher.FileWatcherExecutor;

import rx.functions.Action1;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
public class DslWatcher {

  public static void watch(String dslPath, Action1<String> onUpdateDsl) {
    FileWatcherExecutor.execute(context -> context
    
      .start(to).watch(dslPath)
      .filter(include).file(name).extension("fw")
      .on(file).modified(onUpdateDsl)
      .on(file).deleted(onUpdateDsl)
    );
  }
}
