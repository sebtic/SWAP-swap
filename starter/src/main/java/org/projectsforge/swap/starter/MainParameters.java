package org.projectsforge.swap.starter;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

public class MainParameters {

  @Parameter(description = "The parameters transmitted to the started program.")
  String[] parameters;

  @Parameter(names = "-jars", description = "The directories where the jar files are stored.")
  List<String> jars = new ArrayList<>();

  @Parameter(names = "-dirs", description = "The directories included in the classpath.")
  List<String> dirs = new ArrayList<>();

  @Parameter(names = "-main-class", required = true,
      description = "The name of the main class to start.")
  String mainClass;

  @Parameter(names = "-output",
      description = "If specified, redirect the standard and error output to the given file.")
  String output;

}
