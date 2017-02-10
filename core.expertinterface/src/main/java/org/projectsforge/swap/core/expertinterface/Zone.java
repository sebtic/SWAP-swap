package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * Default zone implementation
 * 
 * @author Vincent Rouillé
 */
@Entity(name = "org.projectsforge.swap.expertinterface.Zone")
@DiscriminatorColumn(name="type")
@DiscriminatorValue("default")
public class Zone extends SubZone implements Serializable {
  private static final long serialVersionUID = 1L;

  /** List of subzones */
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<SubZone> subzones = new ArrayList<SubZone>();
  
  /**
   * Is the content of the zone saved in anonymous mode
   */
  private boolean anonymous = false;
  
  @Override
  public String toString() {
    return "[" + getId() + '(' + getLabel() + "):" + getUrl() + ']';
  }
 
  /**
   * Called client side before send the zone to the server to checks
   * it's validity
   * 
   * @return True if the zone is valid, false otherwise
   * @throws Exception
   */
  @Override
  public boolean prepare() throws Exception {
    if(!super.prepare()) {
      return false;
    }
      
    return prepareSubs();
  }
  
  /**
   * Called by prepare to allow simple check of subzones by sub classes
   * Default zone doesn't allow any subzone
   * 
   * @return True if the subzones are valid, false otherwise
   * @throws Exception
   */
  protected boolean prepareSubs() throws Exception {
    subzones.clear();
      
    return true;
  }

  public List<SubZone> getSubzones() {
    return subzones;
  }

  public void setSubzones(List<SubZone> subzones) {
    this.subzones = subzones;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }
  
}
