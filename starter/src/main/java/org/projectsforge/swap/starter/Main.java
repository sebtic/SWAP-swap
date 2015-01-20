package org.projectsforge.swap.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

  public static void main(final String[] args) throws MalformedURLException,
      ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, FileNotFoundException {

    final MainParameters mainParameters = new MainParameters();

    final JCommander jc = new JCommander(mainParameters);
    try {
      jc.parse(args);
    } catch (final ParameterException e) {
      jc.usage();
      return;
    }
    if (mainParameters.jars.isEmpty() && mainParameters.dirs.isEmpty()) {
      jc.usage();
      return;
    }

    if (mainParameters.output != null) {
      final PrintStream out = new PrintStream(mainParameters.output);
      System.setOut(out);
      System.setErr(out);
    }

    final List<URL> urls = new ArrayList<>();

    for (final String directory : mainParameters.jars) {
      final File dir = new File(directory).getAbsoluteFile();
      if (dir.isDirectory()) {
        for (final File file : dir.listFiles()) {
          if (file.getName().toLowerCase().endsWith(".jar")) {
            urls.add(file.toURI().toURL());
          }
        }
      }
    }

    for (final String directory : mainParameters.dirs) {
      final File dir = new File(directory).getAbsoluteFile();
      if (dir.isDirectory()) {
        urls.add(dir.toURI().toURL());
      }
    }

    Collections.sort(urls, new Comparator<URL>() {

      @Override
      public int compare(final URL o1, final URL o2) {
        return o1.toString().compareTo(o2.toString());
      }
    });

    final ClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]));
    Thread.currentThread().setContextClassLoader(cl);
    final Class<?> c = cl.loadClass(mainParameters.mainClass);
    final Method m = c.getMethod("main", String[].class);
    m.invoke(null, (Object) mainParameters.parameters);
  }
}
