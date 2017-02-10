package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Describe a selection made in a webpage
 * 
 * @author Vincent Rouillé
 */
@Entity(name = "org.projectsforge.swap.expertinterface.ZoneSelection")
public class ZoneSelection implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;  

  private String startContainerXpath;
  private String endContainerXpath;
  private int startOffset;
  private int endOffset;
  
  public boolean prepare() {
    return true;
  }
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStartContainerXpath() {
    return startContainerXpath;
  }

  public void setStartContainerXpath(String startContainerXpath) {
    this.startContainerXpath = startContainerXpath;
  }

  public String getEndContainerXpath() {
    return endContainerXpath;
  }

  public void setEndContainerXpath(String endContainerXpath) {
    this.endContainerXpath = endContainerXpath;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public void setStartOffset(int startOffset) {
    this.startOffset = startOffset;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }
  
}
