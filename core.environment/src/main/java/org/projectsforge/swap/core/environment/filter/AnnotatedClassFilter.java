package org.projectsforge.swap.core.environment.filter;

/**
 * The Interface AnnotatedClassFilter.
 * 
 * @author Sébastien Aupetit
 */
public interface AnnotatedClassFilter {

  /**
   * Filter annotated class.
   * 
   * @param annotatedClass the annotated class
   * @return true, if successful
   */
  boolean filterAnnotatedClass(Class<?> annotatedClass);
}
