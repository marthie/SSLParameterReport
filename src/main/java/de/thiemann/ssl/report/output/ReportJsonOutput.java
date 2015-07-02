package de.thiemann.ssl.report.output;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import de.thiemann.ssl.report.model.Certificate;
import de.thiemann.ssl.report.model.CertificateV3;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.model.SSLv2Certificate;
import de.thiemann.ssl.report.util.CipherSuiteUtil;
import de.thiemann.ssl.report.util.SSLVersions;

public class ReportJsonOutput extends AbstractReportOutput {

	@Override
	public String outputReport(Report report) {
		if (report.supportedSSLVersions.size() == 0)
			return "{ }";

		Map<String, Object> jsonStructure = new HashMap<String, Object>();

		// output common values
		jsonStructure.put("createdOn",
				String.format("%1$tF %1$tT", System.currentTimeMillis()));
		jsonStructure.put("webName", report.webName);
		jsonStructure.put("ipAddress", report.isa.toString());
		jsonStructure.put("port", Integer.toString(report.port));

		// protocol
		List<String> supportedVersions = new ArrayList<String>();
		for (int version : report.supportedSSLVersions) {
			supportedVersions.add(versionString(version));
		}
		jsonStructure.put("supportedSSLVersions", supportedVersions);
		jsonStructure.put("compress", Boolean.toString(report.compress));

		// cipher suites
		Map<String, Object> cipherSuites = new HashMap<String, Object>();
		for (int version : report.supportedSSLVersions) {

			List<String> cipherSuitesByVersion = new ArrayList<String>();
			if (version == SSLVersions.SSLv2.getIntVersion()) {
				for (int cipherSuite : report.supportedCipherSuite.get(version)) {
					cipherSuitesByVersion.add(CipherSuiteUtil
							.cipherSuiteStringV2(cipherSuite));
				}

				cipherSuites.put(
						versionString(SSLVersions.SSLv2.getIntVersion()),
						cipherSuitesByVersion);
			} else {
				for (int c : report.supportedCipherSuite.get(version)) {
					cipherSuitesByVersion.add(CipherSuiteUtil
							.cipherSuiteString(c));
				}

				cipherSuites.put(versionString(version), cipherSuitesByVersion);
			}

		}
		jsonStructure.put("cipherSuites", cipherSuites);

		Map<String, Object> certificates = new HashMap<String, Object>();
		if (report.serverCert != null && report.serverCert.size() != 0) {
			for (Integer version : report.serverCert.keySet()) {
				Set<Certificate> versionCertificates = report.serverCert
						.get(version);

				List<Map<String,Object>> transferedCertificates = new ArrayList<Map<String,Object>>();
				for (Certificate certificate : versionCertificates) {
					transferedCertificates.add(transferToJSONObject(certificate));
				}
				
				certificates.put(versionString(version), transferedCertificates);
			}
		}
		
		jsonStructure.put("certificates", certificates);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonString = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(jsonStructure);
			return jsonString;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Map<String, Object> transferToJSONObject(Certificate cert) {
		if(!cert.isProcessed)
			cert.processCertificateBytes();
		
		if (cert instanceof SSLv2Certificate) {
			SSLv2Certificate sslv2Cert = (SSLv2Certificate) cert;

			Map<String, Object> jsonCert = new HashMap<String, Object>();
			jsonCert.put("fingerprint", sslv2Cert.hash);
			jsonCert.put("certificate-order", sslv2Cert.order);
			jsonCert.put("subject", sslv2Cert.name);

			return jsonCert;
		} else if (cert instanceof CertificateV3) {
			CertificateV3 v3Cert = (CertificateV3) cert;

			Map<String, Object> jsonCert = new HashMap<String, Object>();
			jsonCert.put("version", v3Cert.certificateVersion);
			jsonCert.put("subjectName", v3Cert.subjectName);
			jsonCert.put("alternativeNames", v3Cert.alternativeNames);
			jsonCert.put("notBefore", v3Cert.notBefore);
			jsonCert.put("notAfter", v3Cert.notAfter);
			jsonCert.put("pubKeyName", v3Cert.pubKeyInfo.pubKeyAlgorithm);
			jsonCert.put("pubKeySize", v3Cert.pubKeyInfo.pubKeySize);
			jsonCert.put("issuerName", v3Cert.issuerName);
			jsonCert.put("signatureAlgorithm", v3Cert.signatureAlgorithm);
			jsonCert.put("crlDistributionPoints", v3Cert.crlDistributionPoints);

			return jsonCert;
		}

		return null;
	}

}
