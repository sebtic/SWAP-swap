package org.projectsforge.swap.server.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.swing.JOptionPane;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage remoting key stores
 * This class manages 3 different key stores
 *  - the keyStore with the private server key and it certificate
 *  - the trustStore that contains the certificate authority
 *  - the crl store for certificate revocation
 * TODO : Add some more checks to detect invalid CA (data validity, ...)
 * 
 * @author Vincent Rouillé
 */
public class RemotingServerKeyStore {
  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }
  
  /** The logger. */
  private static Logger logger = LoggerFactory.getLogger(RemotingServerKeyStore.class);
  
  /** The Private Key linked with the CA */
  private PrivateKey privateKey;
  
  /** The Certificate linked to the private key */
  private X509Certificate certificate;
  
  /** Path to the trust store */
  private String trustStorePath;
  
  /** Path to the key store */
  private String keyStorePath;
  
  /** Path to the crl store */
  private String crlStorePath;

  /** The serial number for certificate generation. */
  private BigInteger serialNumber = BigInteger.ONE;

  private BigInteger crlNumber = BigInteger.ONE;
  
  /**
   * Initialize the key store at the best possible state
   * This method MUST be called before used this class.
   */
  public void initStores() throws Exception {
    File keystoreFile = getStoreFile(".key");
    File truststoreFile = getStoreFile(".trust");
    File crlstoreFile = getStoreFile(".crl");
    keyStorePath = keystoreFile.getAbsolutePath();
    trustStorePath = truststoreFile.getAbsolutePath();
    crlStorePath = crlstoreFile.getAbsolutePath();
    boolean justGenerated = true;
    
    // Load the keystore
    if (!keystoreFile.exists()) {
      createKeyStore();
    } else {
      final KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(new FileInputStream(getKeyStorePath()), getKeyStorePassword().toCharArray());
      privateKey = (PrivateKey) keystore.getKey(getKeyAlias(), getKeyPassword().toCharArray());
      Certificate[] certificateChain = keystore.getCertificateChain(getKeyAlias());
      boolean certificateChainValid = certificateChain != null
          && certificateChain.length > 0
          && certificateChain[0] instanceof X509Certificate;
      if (privateKey == null || !certificateChainValid) {
        if (isAllowedUserInteraction()) {
          JOptionPane
          .showMessageDialog(
              null,
              "The trusted remoting CA certificate can not be loaded. A new one will be generated.\nAll signed certificates will have to be regenerated.",
              "Remoting CA certificate can not be loaded",
              JOptionPane.WARNING_MESSAGE);
        }
        createKeyStore();
      } else {
        certificate = (X509Certificate) certificateChain[0];
        loadSN();
        justGenerated = false;
      }
    }
    
    // Check the truststore
    if (!truststoreFile.exists() || justGenerated) {
      createTrustStore();
    } else {
      final KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(new FileInputStream(getTrustStorePath()), getTrustStorePassword().toCharArray());
      X509Certificate cert = (X509Certificate) keystore.getCertificate(getCertAlias());
      if(cert == null) {
        createTrustStore();
      }
    }
    
    if(!crlstoreFile.exists() || justGenerated)
    {
      createCrl();
    }
  }

  /**
   * Returns true if user interaction are allowed, false otherwise.
   * This method is used in the initStore function to check if we can warn 
   * the user directly with a dialog message.
   * 
   * @return
   */
  protected boolean isAllowedUserInteraction() {
    return true;
  }

  /**
   * Return a file for remoting security uses
   * 
   * @param suffix Suffix to append to the end of the filename
   * @return
   */
  protected File getStoreFile(String suffix) {
    return new File(
        EnvironmentPropertyHolder.configurationDirectory.get(),
        ServerPropertyHolder.httpsKeyStoreName.get() + suffix);
  }

  /**
   * Create the key store.
   * <ol>
   *    <li>Generate a new key pair (2048bits)</li>
   *    <li>From this keypair, generate a certificate (365days validity)</li>
   *    <li>Then store the privatekey & the certificate chain in the KeyStore</li>
   * </ol>
   * 
   * @throws Exception
   */
  private void createKeyStore()
      throws Exception {
    final KeyStore keystore = KeyStore.getInstance("JKS");
    logger.error("Missing server keystore. Creating a new one ...");
    logger
        .debug("Creating a certificate for the CA (2048 bits, 365 days validity)...");
 
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.DAY_OF_YEAR, 365);
 
    final Date startDate = Calendar.getInstance().getTime();
    final Date expiryDate = expiry.getTime();
 
    final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048);
    final KeyPair keypair = keyGen.generateKeyPair();
 
    final X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
    final X500Principal dnName = new X500Principal("CN=" + ServerPropertyHolder.httpsHost.get());
 
    serialNumber = BigInteger.ONE;
    certGen.setSerialNumber(serialNumber);
    certGen.setIssuerDN(dnName);
    certGen.setNotBefore(startDate);
    certGen.setNotAfter(expiryDate);
    certGen.setSubjectDN(dnName); // note: same as issuer
    certGen.setPublicKey(keypair.getPublic());
    certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
 
    final X509Certificate certificate = certGen.generate(
        keypair.getPrivate(), BouncyCastleProvider.PROVIDER_NAME);
 
    logger.debug("Creation of a certificate for the CA done");
 
    keystore.load(null, getKeyStorePassword()
        .toCharArray());
    final Certificate[] certChain = new Certificate[1];
    certChain[0] = certificate;
    keystore.setKeyEntry(getKeyAlias(), keypair.getPrivate(),
        getKeyPassword().toCharArray(),
        certChain);
    try (FileOutputStream output = new FileOutputStream(getKeyStorePath())) {
      keystore.store(output, getKeyStorePassword().toCharArray());
      logger.debug("Private key saved");
    }
    saveSN();
    this.certificate = certificate;
    this.privateKey = keypair.getPrivate();
  }

  /**
   * Create a new trust store.<br>
   * The trust is filled with the CA certificate
   * 
   * @throws Exception
   */
  private void createTrustStore()
      throws Exception {
    final KeyStore keystore = KeyStore.getInstance("JKS");
    logger.info("Invalid trust keystore. Creating a new one ...");
    keystore.load(null, getTrustStorePassword().toCharArray());
    keystore.setCertificateEntry(getCertAlias(), certificate);
    try (FileOutputStream output = new FileOutputStream(getTrustStorePath())) {
      keystore.store(output, getTrustStorePassword().toCharArray());
      logger.debug("Trust store saved");
    }
  }
  
  /**
   * Create the crl store
   * 
   * @throws Exception
   */
  private void createCrl()
      throws Exception {
    X509V2CRLGenerator crlGen = getCrlGenerator();
    crlNumber = BigInteger.ONE;
    crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlNumber));
    generateAndSaveCrl(crlGen);
    saveSN();
  }

  /**
   * Generate the crl and save it
   * 
   * @param crlGen
   * @throws CRLException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws SignatureException
   * @throws InvalidKeyException
   * @throws IOException
   * @throws FileNotFoundException
   */
  private void generateAndSaveCrl(X509V2CRLGenerator crlGen) throws CRLException,
      NoSuchProviderException, NoSuchAlgorithmException, SignatureException,
      InvalidKeyException, IOException, FileNotFoundException {
    X509CRL x509crl = crlGen.generate(privateKey, BouncyCastleProvider.PROVIDER_NAME);
    try (FileOutputStream output = new FileOutputStream(getCrlPath())) {
      output.write(x509crl.getEncoded());
      logger.debug("Crl store saved");
    }
  }

  /**
   * Return a crl generator prepared for the 
   * @return
   * @throws CertificateParsingException
   */
  private X509V2CRLGenerator getCrlGenerator() throws CertificateParsingException {
    X509V2CRLGenerator   crlGen = new X509V2CRLGenerator();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.DAY_OF_YEAR, 365);
    final Date startDate = Calendar.getInstance().getTime();
    final Date expiryDate = expiry.getTime();
    crlGen.setIssuerDN(certificate.getSubjectX500Principal());
    crlGen.setThisUpdate(startDate);
    crlGen.setNextUpdate(expiryDate);
    crlGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
    crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(certificate));
    return crlGen;
  }

  /**
   * Load the serialNumber value from the filesystem
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void loadSN() throws IOException, ClassNotFoundException {
    serialNumber = null;
    crlNumber = null;
    try {
      try (final FileInputStream fis = new FileInputStream(getStoreFile(".dat"))) {
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
          serialNumber = (BigInteger) ois.readObject();
          crlNumber = (BigInteger) ois.readObject();
          ois.close();
        }
      }
    } catch(Exception e) {
      if(serialNumber == null)
        serialNumber = BigInteger.ONE;
      if(crlNumber == null)
        crlNumber = BigInteger.ONE;
    }
  }

  /**
   * Save the serialNumber value to the filesystem
   * 
   * @throws IOException
   */
  private void saveSN() throws IOException {
    try (FileOutputStream fos = new FileOutputStream(getStoreFile(".dat"))) {

      try (final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        oos.writeObject(serialNumber);
        oos.writeObject(crlNumber);
        oos.close();
      }
    } catch (final IOException e) {
      logger.error("Can not save SN", e);
      throw e;
    }    
  }

  /**
   * Returns the next serial number to use for signing a certificate. Increment
   * the current serial number by one and update save it to the file system
   * 
   * @return
   * @throws IOException
   */
  private BigInteger getNextSerialNumber() throws IOException {
    serialNumber = serialNumber.add(BigInteger.ONE);
    saveSN();
    return serialNumber;
  }

  /**
   * Returns the next serial number to use for signing a certificate. Increment
   * the current serial number by one and update save it to the file system
   * 
   * @return
   * @throws IOException
   */
  private BigInteger getNextCrlNumber() throws IOException {
    crlNumber  = crlNumber.add(BigInteger.ONE);
    saveSN();
    return serialNumber;
  }
  
  /**
   * Load a certificate from an input stream
   * 
   * @param inCert
   * @return
   * @throws CertificateException
   */
  private X509Certificate loadCertificate(InputStream inCert)
      throws CertificateException {
    final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    Collection<? extends Certificate> certificates = certFactory.generateCertificates(inCert);
    for (Certificate certificate : certificates) {
      return (X509Certificate) certificate;
    }
    throw new IllegalStateException("Unable to load a valid certificate in the input");
  }
  
  /**
   * Sign a user generated certificate
   * 
   * @param inCert The certificate to decode from the stream and sign
   * @param outCerts The certificate signed and encoded to the stream
   * @throws Exception
   */
  public void signPublicKey(InputStream inCert, OutputStream outCerts) throws Exception {
    X509Certificate certToSign = loadCertificate(inCert);
    X509Certificate signedCertificate = signCertificate(certToSign);
    outCerts.write(signedCertificate.getEncoded());
    outCerts.write(certificate.getEncoded());
  }
  
  /**
   * Revoke a certificate
   * 
   * @param certToRevoke
   * @throws Exception
   */
  public void revokeCertificate(InputStream inCert) throws Exception
  {
    X509Certificate certToRevoke = loadCertificate(inCert);
    revokeCertificate(certToRevoke);
  }
  
  /**
   * Revoke a certificate
   * 
   * @param certToRevoke
   * @throws Exception
   */
  public void revokeCertificate(X509Certificate certToRevoke) throws Exception
  {
    final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    X509CRL crl = (X509CRL) certFactory.generateCRL(new FileInputStream(getCrlPath()));
    X509V2CRLGenerator crlGen = getCrlGenerator();
    
    // Add all the previously revoked certificates
    crlGen.addCRL(crl);
    
    // Revoke the requested certificate
    crlGen.addCRLEntry(certToRevoke.getSerialNumber(), new Date(), CRLReason.privilegeWithdrawn);
    
    // Increment the crl number
    crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(getNextCrlNumber()));
    
    // Save the modified crl
    generateAndSaveCrl(crlGen);
  }
  
  /**
   * Sign the given certificate and returns the signed one
   * 
   * @param certToSign
   * @return
   * @throws InvalidKeyException
   * @throws IllegalStateException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws SignatureException
   * @throws KeyStoreException
   * @throws CertificateException
   * @throws IOException
   */
  public X509Certificate signCertificate(X509Certificate certToSign) throws InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, KeyStoreException, CertificateException, IOException {
    final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
    final X500Principal subjectName = certToSign.getIssuerX500Principal();

    certGen.setSerialNumber(getNextSerialNumber());
    certGen.setIssuerDN(certificate.getSubjectX500Principal());
    certGen.setNotBefore(certificate.getNotBefore());
    certGen.setNotAfter(certificate.getNotAfter());
    certGen.setSubjectDN(subjectName);
    certGen.setPublicKey(certToSign.getPublicKey());
    certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

    certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
        new AuthorityKeyIdentifierStructure(certificate));

    certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
        new SubjectKeyIdentifierStructure(certToSign.getPublicKey()));

    certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));

    final PolicyQualifierInfo policyQualifierInfo = new PolicyQualifierInfo("Trusted client remoting certificate for SWAP");
    final PolicyInformation pi = new PolicyInformation(new DERObjectIdentifier(
        "1.3.6.1.5.5.7.2.2"), new DERSequence(policyQualifierInfo));
    final DERSequence seq = new DERSequence(pi);
    certGen.addExtension(X509Extensions.CertificatePolicies, false, seq);

    X509Certificate signedCert = certGen.generate(privateKey, BouncyCastleProvider.PROVIDER_NAME);
    return signedCert;
  }

  /**
   * Returns the key for in store encryption
   * 
   * @return
   */
  private String getKeyPassword() {
    return getKeyStorePassword();
  }

  /**
   * Return the CA certificate
   * 
   * @return
   */
  public Certificate getCertificate() {
    return certificate;
  }

  /**
   * Returns the alias where the key is stored in the key store
   * 
   * @return
   */
  public String getKeyAlias() {
    return "key";
  }

  /**
   * Returns the alias where the CA certificate is stored in the trust store
   * 
   * @return
   */
  private String getCertAlias() {
    return "ca";
  }
  
  /**
   * Returns the password to open the key store
   * 
   * @return
   */
  public String getKeyStorePassword() {
    return ServerPropertyHolder.httpsKeystorePassword.get();
  }

  /**
   * Returns the password to open the trust store
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

  /**
   * Returns the path to the crl store
   * 
   * @return
   */
  public String getCrlPath() {
    return crlStorePath;
  }
}
