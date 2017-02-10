package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity(name = "org.projectsforge.swap.expertinterface.ZoneLink")
@DiscriminatorValue("link")
public class ZoneLink extends Zone implements Serializable {
  private static final long serialVersionUID = 1L;

  private String href;

  @JsonGetter
  public String getType() {
    return "link";
  }
  
  @Override
  public boolean prepare() throws Exception {
    return !href.isEmpty() && super.prepare();
  }
  
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }
}
