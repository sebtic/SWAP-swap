package org.projectsforge.swap.core.environment.impl;

import javax.annotation.PreDestroy;
import org.projectsforge.utils.visitor.Visitor;
import org.springframework.stereotype.Component;

@Component
public class VisitorStatisticsSaver {

  @PreDestroy
  public void destroy() {
    Visitor.saveStatistics();
  }

}
