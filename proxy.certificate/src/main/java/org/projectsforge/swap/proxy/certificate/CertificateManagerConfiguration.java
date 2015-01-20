/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.proxy.certificate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bouncycastle.openssl.PEMReader;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.projectsforge.utils.path.JRegExPathMatcher;
import org.projectsforge.utils.path.URISearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class CertificateManagerConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@Configuration
class CertificateManagerConfiguration {

	private static Logger logger = LoggerFactory
			.getLogger(CertificateManagerConfiguration.class);

	/** The environment. */
	@Autowired
	Environment environment;

	@Autowired
	URISearcher uriSearcher;

	/**
	 * Certificate manager.
	 * 
	 * @return the certificate manager
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 */
	@Bean(name = "proxy.cagen.certManager")
	CertificateManager certificateManager() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		File trustKeystoreFile = new File(
				EnvironmentPropertyHolder.configurationDirectory.get(),
				CertificatePropertyHolder.trustKeystoreName.get());
		if (!trustKeystoreFile.exists()) {
			logger.info("Missing trust keystore. Creating it...");
			// inexisting keystore, we import standard ca certificates
			final JRegExPathMatcher<Set<URL>> certMatcher = new JRegExPathMatcher<Set<URL>>(
					new jregex.Pattern(".*/cacerts/.*\\.crt$"),
					new TreeSet<URL>(new Comparator<URL>() {
						@Override
						public int compare(final URL o1, final URL o2) {
							return o1.toExternalForm().compareTo(
									o2.toExternalForm());
						}
					}));
			uriSearcher.search(certMatcher);

			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(null, CertificatePropertyHolder.trustKeystorePassword
					.get().toCharArray());

			for (URL url : certMatcher.getMatchedPaths()) {
				try (PEMReader reader = new PEMReader(new InputStreamReader(
						url.openStream()))) {
					X509Certificate cert = (X509Certificate) reader
							.readObject();
					keystore.setCertificateEntry(cert.getSubjectX500Principal()
							.getName(), cert);
					logger.debug("Populating trust keystore with {}", cert
							.getSubjectX500Principal().getName());
				}
			}
			try (FileOutputStream fos = new FileOutputStream(trustKeystoreFile)) {
				keystore.store(fos,
						CertificatePropertyHolder.trustKeystorePassword.get()
								.toCharArray());
			}
		}

		return new CertificateManager();
	}

	/**
	 * Trusted certification agency manager.
	 * 
	 * @return the trusted certification agency manager
	 */
	@Bean
	TrustedCertificationAgencyManager trustedCertificationAgencyManager() {
		final TrustedCertificationAgencyManager tcam = new TrustedCertificationAgencyManager();
		tcam.setAlias("ca");
		tcam.setCertificatePrincipal("CN=Smart Web Accessiblity Proxy (trusted host),O=Smart Web Accessiblity Proxy (trusted host),OU=Smart Web Accessibility Proxy (trusted hosts)");
		tcam.setDataFileName("trustedca.dat");
		tcam.setPassword("a dummy password");
		tcam.setPemFileName("trustedca.pem");
		tcam.setSignatureAlgorithm("SHA256WithRSAEncryption");
		tcam.setValidityDays(365);

		return tcam;
	}

	/**
	 * Untrusted certification agency manager.
	 * 
	 * @return the untrusted certification agency manager
	 */
	@Bean
	UntrustedCertificationAgencyManager untrustedCertificationAgencyManager() {
		final UntrustedCertificationAgencyManager ucam = new UntrustedCertificationAgencyManager();
		ucam.setAlias("ca");
		ucam.setCertificatePrincipal("CN=Smart Web Accessiblity Proxy (untrusted host),O=Smart Web Accessiblity Proxy (untrusted host),OU=Smart Web Accessibility Proxy (untrusted hosts)");
		ucam.setDataFileName("untrustedca.dat");
		ucam.setPassword("a dummy password");
		ucam.setSignatureAlgorithm("SHA256WithRSAEncryption");
		ucam.setValidityDays(365);

		return ucam;
	}
}
