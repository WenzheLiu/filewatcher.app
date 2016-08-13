package org.wenzhe.filewatcher.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.wenzhe.filewatcher.FileWatchEvent;
import org.wenzhe.filewatcher.FileWatcherExecutor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import rx.Observable;
import rx.Subscription;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
@RequiredArgsConstructor
public class Console {
  
  @Getter
  private final String path;
  
  Subscription subscription;

  @SneakyThrows
  public static void main(String[] args) {
    val console = new Console(args.length > 0 ? args[0] : ".");
    DslWatcher.watch(console.getPath(), updatedFile -> console.restart());
    
    console.start();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      do {
        System.out.println("Input 'exit' to exit: ");
      } while (!"exit".equalsIgnoreCase(br.readLine()));
    }
    console.stop();
  }
  
  public void start() {
    subscription = Observable.just(path)
        .map(strPath -> Paths.get(strPath).toAbsolutePath())
        .flatMap(FileWatcherDslRunner::getDslContexts)
        .flatMap(FileWatcherExecutor::run)
        .subscribe(this::onNext, this::onError);
  }
  
  private void stop() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
  
  private void restart() {
    stop();
    start();
  }

  private void onNext(FileWatchEvent e) {
    
  }
  
  private void onError(Throwable e) {
    System.err.println(e.getMessage());
  }
}
