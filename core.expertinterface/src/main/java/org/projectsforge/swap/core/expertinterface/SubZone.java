package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Base class for all zones
 * 
 * @author Vincent Rouillé
 */
@Entity(name = "org.projectsforge.swap.expertinterface.SubZone")
@DiscriminatorColumn(name="type")
@DiscriminatorValue("subzone")
public class SubZone implements Serializable {
  private static final long serialVersionUID = 1L;

  /** Identifier of the zone */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /** Url of the zone */
  @Column(updatable=false, nullable=false)
  private String url = null;
  
  /** Url to the zone */
  @Column(nullable=false)
  private String label = null;
  
  /** Xpath to the zone element */
  @Column(nullable=false)
  private String xpath = null;

  /** Snapshot of the page at the time of the zone creation */
  @ManyToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private Page page = new Page();

  /** Snapshot of the content of the zone at the time of the zone creation */
  @Lob
  @Column(nullable=false)
  private String content = null;
  
  /** Time at which this zone was created */
  @Column(updatable=false, nullable=false)
  private Date timeCreated = new Date();
  
  /** Time at which this zone was removed */
  private Date timeRemoved = null;
    
  /** Selection related to this zone */
  @OneToOne(optional=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private ZoneSelection ref = new ZoneSelection();
  
  public boolean prepare() throws Exception {
    if(getXpath().isEmpty() || getUrl().isEmpty() || getContent().isEmpty()) {
      return false;
    }
    
    this.getRef().setId(getId());
    
    if(!getPage().prepare() || !getRef().prepare()) {
      return false;
    }
    
    return true;
  }
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getXpath() {
    return xpath;
  }

  public void setXpath(String xpath) {
    this.xpath = xpath;
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Date timeCreated) {
    this.timeCreated = timeCreated;
  }

  public Date getTimeRemoved() {
    return timeRemoved;
  }

  public void setTimeRemoved(Date timeRemoved) {
    this.timeRemoved = timeRemoved;
  }

  public ZoneSelection getRef() {
    return ref;
  }

  public void setRef(ZoneSelection ref) {
    this.ref = ref;
  }
}
