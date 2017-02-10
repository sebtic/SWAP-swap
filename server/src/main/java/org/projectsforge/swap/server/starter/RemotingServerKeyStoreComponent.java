package org.projectsforge.swap.server.starter;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;

import org.projectsforge.swap.core.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Autowireable component that gives access to remoting key store management
 * 
 * @author Vincent Rouillé
 */
@Component
public class RemotingServerKeyStoreComponent extends RemotingServerKeyStore {
  /** The logger */
  private static Logger logger = LoggerFactory.getLogger(RemotingServerKeyStoreComponent.class);
  
  /** The environment. */
  @Autowired
  private Environment environment;
  
  /**
   * Initialize the key store at the best possible state
   * Automatically called by Spring
   */
  @PostConstruct
  public void init() {
    try {
      super.initStores();
      File file = getStoreFile(".cert");
      try (FileOutputStream out = new FileOutputStream(file)) {
        out.write(getCertificate().getEncoded());
        logger.info("Server certificate writed at : " + file.getAbsolutePath());
      }
    } catch(Exception e) {
      logger.error("Can not generate the trusted remoting CA", e);
      environment.stop();
    }
  }
  
  /**
   * Returns true if user interaction are allowed, false otherwise.
   * This method is used in the initStore function to check if we can warn 
   * the user directly with a dialog message.
   * 
   * @return
   */
  @Override
  protected boolean isAllowedUserInteraction() {
    return environment.getAllowUserInteraction();
  }
}
