package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity(name = "org.projectsforge.swap.expertinterface.ZoneForm")
@DiscriminatorValue("form")
public class ZoneForm extends Zone implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonGetter
  public String getType() {
    return "form";
  }
  

  @Override
  protected boolean prepareSubs() throws Exception {
    // All subzones must be valid
    for (SubZone subzone : getSubzones()) {
      subzone.setPage(getPage());
      if(!subzone.prepare())
        return false;
    }

    return true;
  }
}
