package org.wenzhe.filewatcher.edd.dsl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.wenzhe.filewatcher.dsl.FileWatcherDslContext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.SneakyThrows;
import lombok.val;

/**handling multiple files
 * 
 * @author liuwenzhe2008@gmail.com
 *
 */
public class FileWatcherDslRunner2 {
  
  @SneakyThrows
  private static FileWatcherDslContext parse(Path dslPath) {
    val context = new FileWatcherDslContext();
    val binding = new Binding();
    binding.setProperty("context", context);
    val configuration = new CompilerConfiguration();
    val dslText = new String(Files.readAllBytes(dslPath), "UTF8");
    val groovyCode = String.format("context.with {%s}", dslText);
    val dslScript = new GroovyShell(binding, configuration).parse(groovyCode);
    dslScript.run();
    return context;
  }

  public static void main(String[] arags) throws CompilationFailedException, InstantiationException, IllegalAccessException, IOException {
    val dslDir = Paths.get("src/test/resources/dsl");
    try (val fileStream = Files.walk(dslDir, 1)) {
      fileStream.filter(Files::isRegularFile)
      .filter(path -> path.toString().endsWith(".fw"))
      .map(FileWatcherDslRunner2::parse)
      .collect(Collectors.toList());
    }
  }
}
