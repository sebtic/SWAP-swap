package org.projectsforge.swap.core.expertinterface;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Java entity for HTML page storage in an efficient way
 *
 * @author Vincent Rouillé
 */
@Entity(name = "org.projectsforge.swap.expertinterface.Page")
public class Page implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Id computed by SHA1 hashing of url and html sha1(url + sha1(html))
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  /**
   * Url of the webpage
   */
  private String url;

  /**
   * Date at which this page was stored
   */
  private Date time = new Date();

  /**
   * Content of the page
   */
  @Lob
  @Column(nullable = false)
  private String html;

  /**
   * Is the content of the page saved in anonymous mode
   */
  private boolean anonymous = false;

  public String getHtml() {
    return html;
  }

  public String getId() {
    return id;
  }

  public Date getTime() {
    return time;
  }

  // Getters and setters
  public String getUrl() {
    return url;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  /**
   * Prepare this entity for saving.
   *
   * html is hashed once to prevent the almost impossible : url1 + html1 = url2
   * + html2 with url1 != url2
   *
   * @return True if this Page is ready for being saved, false otherwise
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public Boolean prepare() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    if (url.isEmpty()) {
      return false;
    }
    if (html.isEmpty()) {
      return false;
    }

    MessageDigest sha1Global = MessageDigest.getInstance("SHA-1");
    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
    sha1Global.reset();
    sha1Global.update(url.getBytes("UTF-8"));
    sha1.reset();
    sha1Global.update(sha1.digest(html.getBytes("UTF-8")));
    byte[] result = sha1Global.digest();

    StringBuilder sb = new StringBuilder();
    for (byte b : result) {
      sb.append(String.format("%02x", b & 0xff));
    }

    id = sb.toString();

    return true;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
