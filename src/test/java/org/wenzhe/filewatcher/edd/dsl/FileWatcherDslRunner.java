package org.wenzhe.filewatcher.edd.dsl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.wenzhe.filewatcher.dsl.FileWatcherDslContext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.val;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
public class FileWatcherDslRunner {

  public static void main(String[] arags) throws CompilationFailedException, InstantiationException, IllegalAccessException, IOException {
    val context = new FileWatcherDslContext();
    val binding = new Binding();
    binding.setProperty("context", context);
    val configuration = new CompilerConfiguration();
    val dslText = new String(Files.readAllBytes(
        Paths.get("src/test/resources/dsl/test1.fw")),
        "UTF8");
    val groovyCode = String.format("context.with {%s}", dslText);
    val dslScript = new GroovyShell(binding, configuration).parse(groovyCode);
    try {
      dslScript.run();
    } catch (Throwable e) {
      System.err.println(e.getMessage());
    }
  }
}
