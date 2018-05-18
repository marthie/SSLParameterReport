package de.thiemann.ssl.report.output;

/*

The MIT License (MIT)

Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thiemann.ssl.report.model.Certificate;
import de.thiemann.ssl.report.model.CertificateV3;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.model.SSLv2Certificate;
import de.thiemann.ssl.report.util.CipherSuiteUtil;
import de.thiemann.ssl.report.util.SSLVersions;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class JsonOutput extends AbstractOutput {

	private Logger log = LoggerFactory.getLogger(JsonOutput.class);

	@Override
	public String outputReportCollection(Collection<Report> reportCollection) {
		List<Map<String, Object>> jsonReportList = new ArrayList<Map<String, Object>>();

		for (Report report : reportCollection) {
			Map<String, Object> jsonObjectMap = transferToJSONObject(report);
			jsonReportList.add(jsonObjectMap);
		}

		String jsonString = transferToString(jsonReportList);

		return jsonString;
	}

	@Override
	public String outputReport(Report report) {
		Map<String, Object> jsonReport = transferToJSONObject(report);

		String jsonString = transferToString(jsonReport);

		return jsonString;
	}
	
	private String transferToString(Object jsonObject) {
		if (jsonObject != null) {
			ObjectMapper mapper = new ObjectMapper();

			String jsonString = null;
			try {
				jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			} catch (JsonProcessingException e) {
				log.error("Exception while transfaering object to JSON.", e);
			}
			return jsonString;
		}

		return "{ }";
	}

	private Map<String, Object> transferToJSONObject(Report report) {
		if (report.getSupportedSSLVersions().size() == 0) {
			return null;
		}

		Map<String, Object> jsonReport = new HashMap<String, Object>();

		// output common values
		jsonReport.put("createdOn",
				String.format("%1$tF %1$tT", System.currentTimeMillis()));
		jsonReport.put("host", report.getHost());
		jsonReport.put("ipAddress", report.getIp().toString());
		jsonReport.put("port", Integer.toString(report.getPort()));
		jsonReport.put("reportId", report.getId());

		// protocol
		List<String> supportedVersions = new ArrayList<String>();
		for (int version : report.getSupportedSSLVersions()) {
			supportedVersions.add(versionString(version));
		}
		jsonReport.put("supportedSSLVersions", supportedVersions);
		jsonReport.put("compress", Boolean.toString(report.isCompress()));

		// cipher suites
		Map<String, Object> cipherSuites = new HashMap<String, Object>();
		for (int version : report.getSupportedSSLVersions()) {

			List<String> cipherSuitesByVersion = new ArrayList<String>();
			if (version == SSLVersions.SSLv2.getIntVersion()) {
				for (int cipherSuite : report.getSupportedCipherSuite().get(version)) {
					cipherSuitesByVersion.add(CipherSuiteUtil
							.cipherSuiteStringV2(cipherSuite));
				}

				cipherSuites.put(
						versionString(SSLVersions.SSLv2.getIntVersion()),
						cipherSuitesByVersion);
			} else {
				for (int c : report.getSupportedCipherSuite().get(version)) {
					cipherSuitesByVersion.add(CipherSuiteUtil
							.cipherSuiteString(c));
				}

				cipherSuites.put(versionString(version), cipherSuitesByVersion);
			}

		}
		jsonReport.put("cipherSuites", cipherSuites);

		// certificates
		Map<String, Object> certificates = new HashMap<String, Object>();
		if (report.getServerCert() != null && report.getServerCert() .size() != 0) {
			for (Integer version : report.getServerCert() .keySet()) {
				Set<Certificate> versionCertificates = report.getServerCert()
						.get(version);

				List<Map<String, Object>> transferedCertificates = new ArrayList<Map<String, Object>>();
				for (Certificate certificate : versionCertificates) {
					transferedCertificates
							.add(transferToJSONObject(certificate));
				}

				certificates
						.put(versionString(version), transferedCertificates);
			}
		}

		jsonReport.put("certificates", certificates);

		return jsonReport;
	}

	private Map<String, Object> transferToJSONObject(Certificate cert) {
		if (!cert.isProcessed()) {
			cert.processCertificateBytes();
		}

		Map<String, Object> jsonCert = new HashMap<String, Object>();
		jsonCert.put("order", cert.getOrder());
		jsonCert.put("reportCertId", cert.getId());

		if (cert instanceof SSLv2Certificate) {
			SSLv2Certificate sslv2Cert = (SSLv2Certificate) cert;

			jsonCert.put("fingerprint", sslv2Cert.getHash());
			jsonCert.put("certificate-order", sslv2Cert.getOrder());
			jsonCert.put("subject", sslv2Cert.getName());


			return jsonCert;
		} else if (cert instanceof CertificateV3) {
			CertificateV3 v3Cert = (CertificateV3) cert;

			jsonCert.put("version", v3Cert.getCertificateVersion());
			jsonCert.put("subjectName", v3Cert.getSubjectName());
			jsonCert.put("alternativeNames", processAlternativeNames(v3Cert.getAlternativeNames()));
			jsonCert.put("notBefore",
					String.format("%1$tF %1$tT", v3Cert.getNotBefore()));
			jsonCert.put("notAfter",
					String.format("%1$tF %1$tT", v3Cert.getNotAfter()));
			jsonCert.put("pubKeyName", v3Cert.getPubKeyInfo().getPubKeyAlgorithm());
			jsonCert.put("pubKeySize", v3Cert.getPubKeyInfo().getPubKeySize());
			jsonCert.put("issuerName", v3Cert.getIssuerName());
			jsonCert.put("signatureAlgorithm", v3Cert.getSignatureAlgorithm());
			jsonCert.put("fingerprint", v3Cert.getFingerprint());
			jsonCert.put("crlDistributionPoints", v3Cert.getCrlDistributionPoints());

			return jsonCert;
		}

		return null;
	}
	
	private List<String> processAlternativeNames(List<String> alternativeNames) {
		if(alternativeNames == null) {
			return null;
		}
		
		List<String> lines = new ArrayList<String>();
		
		StringBuffer lineBuffer = new StringBuffer();
		Iterator<String> iterator = alternativeNames.iterator();
		while(iterator.hasNext()) {
			String alternativeName = iterator.next();
			
			lineBuffer.append(alternativeName);
			
			if(iterator.hasNext()) {
				lineBuffer.append(", ");
			}
			
			if(lineBuffer.length() > 80) {
				lines.add(lineBuffer.toString());
				lineBuffer.setLength(0);
			}
		}
		
		return lines;
	}

}
