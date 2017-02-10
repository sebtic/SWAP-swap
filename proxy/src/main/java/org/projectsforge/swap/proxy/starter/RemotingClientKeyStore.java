package org.projectsforge.swap.proxy.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.security.auth.x500.X500Principal;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Client side remoting key store manager<br>
 * This component manages 2 stores : 
 * <ul>
 *  <li>key store : private key and certificate chain for authentication</li>
 *  <li>trust store : swap server CA for verification of the server authenticity</li>
 * </lu>
 * 
 * @author Vincent Rouillé
 */
@Component
public class RemotingClientKeyStore {
  
  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  /** The logger **/
  private static final Log logger = LogFactory.getLog(RemotingClientKeyStore.class);
  
  /** Boolean that tells if the server certificate isn't set and is still requested */
  private boolean serverCertRequired;
  
  /** Boolean that tells if the client certificate signature isn't done and is still requested */
  private boolean clientCertRequired;
  
  /** The client public key */
  private PrivateKey privateKey;
  
  /** The client certificate */
  private X509Certificate certificate;
  
  /** Path to the trust store */
  private String trustStorePath;
  
  /** Path to the key store**/
  private String keyStorePath;

  /** The environment. */
  @Autowired
  private Environment environment;

  /**
   * Initilise this key stores manager
   */
  @PostConstruct
  public void init() {
    try {  
      File keystoreFile = getStoreFile(".key");
      File truststoreFile = getStoreFile(".trust");
      keyStorePath = keystoreFile.getAbsolutePath();
      trustStorePath = truststoreFile.getAbsolutePath();
      
      X509Certificate keyServerCert = null;
      X509Certificate trustServerCert = null;
      
      // Load the keystore
      clientCertRequired = true;
      if (!keystoreFile.exists()) {
        createKeyStore();
      } else {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        // Load the key store
        keystore.load(new FileInputStream(getKeyStorePath()), getKeyStorePassword().toCharArray());
        
        // Load the private key and it's certificate chain
        privateKey = (PrivateKey) keystore.getKey(getKeyAlias(), getKeyPassword().toCharArray());
        Certificate[] certificateChain = keystore.getCertificateChain(getKeyAlias());
        boolean certificateChainValid = certificateChain != null
            && certificateChain.length > 0
            && certificateChain[0] instanceof X509Certificate;
        if (privateKey == null || !certificateChainValid) {
          createKeyStore();
        } else {
          certificate = (X509Certificate) certificateChain[0];
          if(certificateChain.length > 1) {
            keyServerCert = (X509Certificate) certificateChain[1];
            clientCertRequired = false;
          }
        }
      }
      
      // Load the trust store
      serverCertRequired = true;
      if (truststoreFile.exists()) {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(getTrustStorePath()), getTrustStorePassword().toCharArray());
        trustServerCert = (X509Certificate) keystore.getCertificate(getCertAlias());
        X509Certificate myCert = (X509Certificate) keystore.getCertificate(getKeyAlias());
        clientCertRequired = myCert == null || !myCert.equals(certificate);
        if(trustServerCert != null)
          serverCertRequired = false;          
      } else {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        logger.info("Invalid trust keystore. Creating a new empty one ...");
        keystore.load(null, getTrustStorePassword().toCharArray());
        try (FileOutputStream output = new FileOutputStream(getTrustStorePath())) {
          keystore.store(output, getTrustStorePassword().toCharArray());
          logger.debug("Trust store created");
        }
      }
      
      if(!isClientCertRequired() && !isServerCertRequired()) {
        // Check if certificateChain[1] and trustStoreCertificate matches
        serverCertRequired = keyServerCert == null || !keyServerCert.equals(trustServerCert);
        clientCertRequired = serverCertRequired;
      }
      
      if(isServerCertRequired()) {
        if (environment.getAllowUserInteraction()) {
          JOptionPane
              .showMessageDialog(
                  null,
                  "The trusted CA certificate of the remoting server is not registered\nRemoting will not work until this issue is fixed",
                  "Server remoting certificate is missing",
                  JOptionPane.WARNING_MESSAGE);
          // Asking to open server certificate
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Open the server remoting certificate");
          if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileInputStream in = new FileInputStream(file);
            setServerCertificate(in);
            in.close();
          }
        }
      }
      if(isClientCertRequired()) {
        Certificate pub = getCertificate();
        
        if (environment.getAllowUserInteraction()) {
          JOptionPane
              .showMessageDialog(
                  null,
                  "Your client remoting keys aren't signed yet\nRemoting will not work until this issue is fixed",
                  "Client remoting key not signed",
                  JOptionPane.WARNING_MESSAGE);
          // Asking to save the public key
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Save your public key for signature");
          if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileOutputStream keyfos = new FileOutputStream(file);
            keyfos.write(pub.getEncoded());
            keyfos.close();
          }
          
          // Asking to open the signed certificate
          fileChooser.setDialogTitle("Open the signed public key");
          if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileInputStream in = new FileInputStream(file);
            setClientCertificate(in);
            in.close();
          }
        }          
      }
    } catch(Exception e) {
      logger.error("Cannot initialize remoting stores", e);
      environment.stop();
    }
  }
  
  /**
   * Return a file for remoting security uses
   * 
   * @param suffix Suffix to append to the end of the filename
   * @return
   */
  private File getStoreFile(String suffix) {
    return new File(
        EnvironmentPropertyHolder.configurationDirectory.get(),
        RemotingPropertyHolder.serverKeyStoreName.get() + suffix);
  }
  
  /**
   * Creates a new key store.
   * <ol>
   *    <li>Generate a new key pair (2048bits)</li>
   *    <li>From this keypair, generate a certificate (365days validity)</li>
   *    <li>Then store the privatekey & the certificate chain in the KeyStore</li>
   * </ol>
   * 
   * @throws IOException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws CertificateException
   * @throws InvalidKeyException
   * @throws IllegalStateException
   * @throws NoSuchProviderException
   * @throws SignatureException
   */
  private void createKeyStore() throws IOException, KeyStoreException,
      NoSuchAlgorithmException, CertificateException, InvalidKeyException, IllegalStateException, NoSuchProviderException, SignatureException {
    KeyStore keystore = KeyStore.getInstance("JKS");
    
    logger.error("Missing remoting keystore. Creating a new one ...");
    final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048);
    final KeyPair keypair = keyGen.generateKeyPair();

    logger
        .debug("Self signing for storing, signature of the key by the server is still required...");

    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.DAY_OF_YEAR, 365);

    final Date startDate = Calendar.getInstance().getTime();
    final Date expiryDate = expiry.getTime();

    final X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
    final X500Principal dnName = new X500Principal("CN=swapclient");

    certGen.setSerialNumber(BigInteger.ONE);
    certGen.setIssuerDN(dnName);
    certGen.setNotBefore(startDate);
    certGen.setNotAfter(expiryDate);
    certGen.setSubjectDN(dnName); // note: same as issuer
    certGen.setPublicKey(keypair.getPublic());
    certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

    final X509Certificate certificate = certGen.generate(keypair.getPrivate(), BouncyCastleProvider.PROVIDER_NAME);

    logger.debug("Creation of a certificate for the CA done");

    keystore.load(null, getKeyStorePassword().toCharArray());
    final Certificate[] certChain = new Certificate[1];
    certChain[0] = certificate;
    keystore.setKeyEntry(getKeyAlias(), keypair.getPrivate(),
        getKeyPassword().toCharArray(), certChain);

    try (FileOutputStream output = new FileOutputStream(getKeyStorePath())) {
      keystore.store(output, getKeyStorePassword().toCharArray());
      logger.debug("Private key saved");
    }

    this.clientCertRequired = true;
    this.certificate = certificate;
    this.privateKey = keypair.getPrivate();
  }

  /**
   * Returns true if the server CA certificate isn't valid
   * 
   * @return
   */
  public boolean isServerCertRequired() {
    return serverCertRequired;
  }

  /**
   * Returns true if the client certificate isn't signed
   * 
   * @return
   */
  public boolean isClientCertRequired() {
    return clientCertRequired;
  }
  
  /**
   * Returns the current client certificate.<br>
   * This certificate could be signed or not.
   * 
   * @return
   */
  public Certificate getCertificate() {
    return this.certificate;
  }

  /**
   * Set the server CA certificate
   * 
   * @param in
   * @throws CertificateException
   * @throws KeyStoreException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public void setServerCertificate(InputStream in) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
    final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    Collection<? extends Certificate> certificates = certFactory.generateCertificates(in);
    for (Certificate certificate : certificates) {
      // Save the certificate
      final KeyStore keystore = KeyStore.getInstance("JKS");
      logger.info("Invalid trust keystore. Creating a new one ...");
      keystore.load(new FileInputStream(getTrustStorePath()), getTrustStorePassword().toCharArray());
      keystore.setCertificateEntry(getCertAlias(), certificate);
      try (FileOutputStream output = new FileOutputStream(getTrustStorePath())) {
        keystore.store(output, getTrustStorePassword().toCharArray());
        serverCertRequired = false;
        logger.debug("Trust store saved");
      }
      break;
    }
  }
  
  /**
   * Set the client signed certificate
   * 
   * @param in The client signed certificate
   * @throws CertificateException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void setClientCertificate(InputStream in) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, FileNotFoundException, IOException {
    final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    Collection<? extends Certificate> certificates = certFactory.generateCertificates(in);
    final Certificate[] certChain = certificates.toArray(new Certificate[certificates.size()]);
    if(certificates.size() != 2) {
      throw new IllegalArgumentException("The load stream doesn't contains the whole certificate chain");
    }
      
    // Update the Key store
    {
      final KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(new FileInputStream(getKeyStorePath()), getKeyStorePassword().toCharArray());
      keystore.setKeyEntry(getKeyAlias(), privateKey, getKeyPassword().toCharArray(), certChain);
      keystore.store(new FileOutputStream(getKeyStorePath()), getKeyStorePassword().toCharArray());
    }
    
    // Update the Trust store
    {
      final KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(new FileInputStream(getTrustStorePath()), getTrustStorePassword().toCharArray());
      keystore.setCertificateEntry(getKeyAlias(), certChain[0]);
      try (FileOutputStream output = new FileOutputStream(getTrustStorePath())) {
        keystore.store(output, getTrustStorePassword().toCharArray());
        logger.debug("Trust store saved");
      }
    }
    clientCertRequired = false;
  }
  
  /**
   * Returns the alias of the private key in the key store
   * 
   * @return
   */
  public String getKeyAlias() {
    return "key";
  }

  /**
   * Returns the alias of the server CA certificate in the trust store
   * 
   * @return
   */
  private String getCertAlias() {
    return "ca";
  }

  /**
   * Returns the password used to encrypt private key in the key store
   * 
   * @return
   */
  private String getKeyPassword() {
    return getKeyStorePassword();
  }
  
  /**
   * Returns the password of the key store
   *
   * @return
   */
  public String getKeyStorePassword() {
    return RemotingPropertyHolder.serverKeyStorePassword.get();
  }

  /**
   * Returns the password of the trust store
   * 
   * @return
   */
  public String getTrustStorePassword() {
    return getKeyStorePassword();
  }

  /**
   * Returns the path to the trust store
   * 
   * @return
   */
  public String getTrustStorePath() {
    return trustStorePath;
  }

  /**
   * Returns the path to the key store
   * 
   * @return
   */
  public String getKeyStorePath() {
    return keyStorePath;
  }
}
