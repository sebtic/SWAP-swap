package org.projectsforge.swap.proxy.persistence.data;

import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "org.projectsforge.swap.proxy.persistence.data.Data")
public class Data {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private URL url;

  public Data() {
    this.id = null;
    this.url = null;
  }

  public Data(final Long id, final URL url) {
    this.id = id;
    this.url = url;
  }

  public Long getId() {
    return id;
  }

  public URL getUrl() {
    return url;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public void SetUrl(final URL url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "[" + id + ':' + url + ']';
  }
}
