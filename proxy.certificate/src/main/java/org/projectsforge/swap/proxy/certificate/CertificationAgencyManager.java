/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id: CertificationAgencyManager.java 17
 * 2010-03-10 08:39:15Z sebtic $
 */
package org.projectsforge.swap.proxy.certificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that manages a CA and the issuing of certificate based on the CA.
 * 
 * @author Sébastien Aupetit
 */
public class CertificationAgencyManager {
  // TODO rework all certificate package to use database and simplify things

  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(CertificationAgencyManager.class);

  /** The password. */
  private String password;

  /** The alias. */
  private String alias;

  /** The data file name. */
  private String dataFileName;

  /** The pem file name. */
  private String pemFileName;

  /** The signature algorithm. */
  private String signatureAlgorithm;

  /** The certificate principal. */
  private String certificatePrincipal;

  /** The validity days. */
  private int validityDays;

  /** The serial number. */
  private BigInteger serialNumber = null;

  /** The certificate. */
  private X509Certificate certificate;

  /** The private key. */
  private PrivateKey privateKey;

  /** The issued serial number. */
  private BigInteger issuedSerialNumber = null;

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The shared secure random. */
  @Autowired
  private SharedSecureRandom sharedSecureRandom;

  /**
   * Check certificate validity.
   * 
   * @throws InvalidKeyException the invalid key exception
   * @throws CertificateException the certificate exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws NoSuchProviderException the no such provider exception
   * @throws SignatureException the signature exception
   */
  protected void checkCertificateValidity() throws InvalidKeyException, CertificateException,
      NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    certificate.checkValidity();
    certificate.verify(certificate.getPublicKey());
  }

  /**
   * Creates a certificate signed by the CA.
   * 
   * @param certificateTarget the certificate target
   * @param certPassword the cert password
   * @param policyInformation the policy information
   * @return the key store
   * @throws CertificateManagerException the certificate manager exception
   */
  KeyStore createCertifitate(final CertificateTarget certificateTarget, final String certPassword,
      final String policyInformation) throws CertificateManagerException {
    try {
      logger.info("Creating a certificate for {}", certificateTarget);
      final KeyStore store = KeyStore.getInstance("JKS");

      store.load(null, null);

      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024, sharedSecureRandom.getSecureRandom());
      final KeyPair keypair = keyGen.generateKeyPair();

      final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
      final X500Principal subjectName = new X500Principal("CN=" + certificateTarget.getHostname()
          + ",O=" + certificateTarget.getHostname() + ",OU=" + certificateTarget.getHostname());

      certGen.setSerialNumber(getNextSerialNumber());
      certGen.setIssuerDN(certificate.getSubjectX500Principal());
      certGen.setNotBefore(certificate.getNotBefore());
      certGen.setNotAfter(certificate.getNotAfter());
      certGen.setSubjectDN(subjectName);
      certGen.setPublicKey(keypair.getPublic());
      certGen.setSignatureAlgorithm(signatureAlgorithm);

      certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
          new AuthorityKeyIdentifierStructure(certificate));

      certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
          new SubjectKeyIdentifierStructure(keypair.getPublic()));

      certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));

      final PolicyQualifierInfo policyQualifierInfo = new PolicyQualifierInfo(policyInformation);
      final PolicyInformation pi = new PolicyInformation(new DERObjectIdentifier(
          "1.3.6.1.5.5.7.2.2"), new DERSequence(policyQualifierInfo));
      final DERSequence seq = new DERSequence(pi);
      certGen.addExtension(X509Extensions.CertificatePolicies, false, seq);

      final X509Certificate cert = certGen.generate(privateKey, "BC");

      final X509Certificate[] chain = new X509Certificate[] { cert, certificate };

      store.setKeyEntry("proxiedhost", keypair.getPrivate(), certPassword.toCharArray(), chain);

      /*final PEMWriter pemWrt = new PEMWriter(new OutputStreamWriter(
          new FileOutputStream(new File(EnvironmentPropertyHolder.configurationDirectory.get(),
              issuedSerialNumber + pemFileName))));
      pemWrt.writeObject(chain[0]);
      pemWrt.close();*/

      logger.debug("Creation of a certificate for {} done", certificateTarget);

      return store;
    } catch (NoSuchAlgorithmException | CertificateException | IOException | InvalidKeyException
        | IllegalStateException | NoSuchProviderException | SignatureException | KeyStoreException e) {
      throw new CertificateManagerException(e);
    }
  }

  /**
   * Generate the CA certificate.
   * 
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateEncodingException the certificate encoding exception
   * @throws InvalidKeyException the invalid key exception
   * @throws IllegalStateException the illegal state exception
   * @throws NoSuchProviderException the no such provider exception
   * @throws SignatureException the signature exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void generateCA() throws NoSuchAlgorithmException, CertificateEncodingException,
      InvalidKeyException, IllegalStateException, NoSuchProviderException, SignatureException,
      IOException {

    logger.debug("Creating a certificate for the CA");

    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.DAY_OF_YEAR, validityDays);

    final Date startDate = Calendar.getInstance().getTime();
    final Date expiryDate = expiry.getTime();

    if (serialNumber == null) {
      serialNumber = BigInteger.ZERO;
    }
    serialNumber = serialNumber.add(BigInteger.ONE);

    final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024, sharedSecureRandom.getSecureRandom());
    final KeyPair keypair = keyGen.generateKeyPair();
    privateKey = keypair.getPrivate();

    final X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
    final X500Principal dnName = new X500Principal(certificatePrincipal);

    certGen.setSerialNumber(serialNumber);
    certGen.setIssuerDN(dnName);
    certGen.setNotBefore(startDate);
    certGen.setNotAfter(expiryDate);
    certGen.setSubjectDN(dnName); // note: same as issuer
    certGen.setPublicKey(keypair.getPublic());
    certGen.setSignatureAlgorithm(signatureAlgorithm);

    certificate = certGen.generate(keypair.getPrivate(), "BC");

    logger.debug("Creation of a certificate for the CA done");
  }

  /**
   * Gets the next serial number to issue a new signed certificate.
   * 
   * @return the next serial number
   * @throws KeyStoreException the key store exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateException the certificate exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public BigInteger getNextSerialNumber() throws KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException {
    if (issuedSerialNumber == null) {
      issuedSerialNumber = BigInteger.ZERO;
    }
    issuedSerialNumber = issuedSerialNumber.add(BigInteger.ONE);

    saveCA();

    return issuedSerialNumber;
  }

  /**
   * Gets the pem file name of the CA.
   * 
   * @return the pem file name
   */
  public String getPemFileName() {
    return pemFileName;
  }

  /**
   * Load the CA and miscelaneous data.
   * 
   * @throws KeyStoreException the key store exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateException the certificate exception
   * @throws UnrecoverableKeyException the unrecoverable key exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException the class not found exception
   */
  protected void loadCA() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
      UnrecoverableKeyException, IOException, ClassNotFoundException {
    try (final FileInputStream fis = new FileInputStream(new File(
        EnvironmentPropertyHolder.configurationDirectory.get(), dataFileName))) {

      try (ObjectInputStream ois = new ObjectInputStream(fis)) {

        serialNumber = (BigInteger) ois.readObject();
        issuedSerialNumber = (BigInteger) ois.readObject();
        final byte[] keystorebyte = (byte[]) ois.readObject();
        ois.close();

        final ByteArrayInputStream bais = new ByteArrayInputStream(keystorebyte);

        final KeyStore store = KeyStore.getInstance("JKS");
        store.load(bais, password.toCharArray());
        privateKey = (PrivateKey) store.getKey(alias, password.toCharArray());
        certificate = (X509Certificate) store.getCertificate(alias);
      }
    }
  }

  /**
   * Save the CA and miscelaneous data.
   * 
   * @throws KeyStoreException the key store exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateException the certificate exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void saveCA() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
      IOException {
    try (FileOutputStream fos = new FileOutputStream(new File(
        EnvironmentPropertyHolder.configurationDirectory.get(), dataFileName))) {

      try (final ObjectOutputStream oos = new ObjectOutputStream(fos)) {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final KeyStore store = KeyStore.getInstance("JKS");
        store.load(null, null);
        store.setKeyEntry(alias, privateKey, password.toCharArray(),
            new X509Certificate[] { certificate });
        store.store(baos, password.toCharArray());

        oos.writeObject(serialNumber);
        oos.writeObject(issuedSerialNumber);
        oos.writeObject(baos.toByteArray());
        oos.close();

        if (pemFileName != null) {
          try (final PEMWriter pemWrt = new PEMWriter(new OutputStreamWriter(new FileOutputStream(
              new File(EnvironmentPropertyHolder.configurationDirectory.get(), pemFileName))))) {
            pemWrt.writeObject(certificate);
          }
        }
      }
    } catch (final IOException e) {
      logger.error("Can not save CA", e);
      throw e;
    }
  }

  /**
   * Sets the alias.
   * 
   * @param alias the new alias
   */
  public void setAlias(final String alias) {
    this.alias = alias;
  }

  /**
   * Sets the certificate principal.
   * 
   * @param certificatePrincipal the new certificate principal
   */
  public void setCertificatePrincipal(final String certificatePrincipal) {
    this.certificatePrincipal = certificatePrincipal;
  }

  /**
   * Sets the data file name.
   * 
   * @param dataFileName the new data file name
   */
  public void setDataFileName(final String dataFileName) {
    this.dataFileName = dataFileName;
  }

  /**
   * Sets the password.
   * 
   * @param password the new password
   */
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * Sets the pem file name.
   * 
   * @param pemFileName the new pem file name
   */
  public void setPemFileName(final String pemFileName) {
    this.pemFileName = pemFileName;
  }

  /**
   * Sets the signature algorithm.
   * 
   * @param signatureAlgorithm the new signature algorithm
   */
  public void setSignatureAlgorithm(final String signatureAlgorithm) {
    this.signatureAlgorithm = signatureAlgorithm;
  }

  /**
   * Sets the validity days.
   * 
   * @param validityDays the new validity days
   */
  public void setValidityDays(final int validityDays) {
    this.validityDays = validityDays;
  }

}
