package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity(name = "org.projectsforge.swap.expertinterface.ZoneImage")
@DiscriminatorValue("image")
public class ZoneImage extends Zone implements Serializable {
  private static final long serialVersionUID = 1L;

  private String src;
  
  @JsonGetter
  public String getType() {
    return "image";
  }
  
  @Override
  public boolean prepare() throws Exception {
    return !src.isEmpty() && super.prepare();
  }
  
  public String getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = src;
  }
}
