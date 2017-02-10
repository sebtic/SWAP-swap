package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity(name = "org.projectsforge.swap.expertinterface.ZoneTable")
@DiscriminatorValue("table")
public class ZoneTable extends Zone implements Serializable {
  private static final long serialVersionUID = 1L;

  private String subtype;

  @JsonGetter
  public String getType() {
    return "table";
  }

  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  @Override
  protected boolean prepareSubs() throws Exception {
    // Allow only one optional subzone 
    List<SubZone> list = getSubzones();

    if(list.size() > 1) {
      return false;
    }
    
    if(list.size() > 0) {
      SubZone subZone = list.get(0);
      subZone.setPage(getPage());
      return subZone.prepare();
    }

    return true;
  }
}
