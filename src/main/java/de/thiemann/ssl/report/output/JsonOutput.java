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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

public class JsonOutput extends AbstractOutput {

	@Override
	public String outputReportCollection(Collection<Report> reportCollection) {
		List<Object> jsonReportList = new ArrayList<Object>();
		for (Report report : reportCollection) {
			jsonReportList.add(transferToJSONObject(report));
		}

		return transferToString(jsonReportList);
	}

	@Override
	public String outputReport(Report report) {
		Map<String, Object> jsonReport = transferToJSONObject(report);

		return transferToString(jsonReport);
	}
	
	private String transferToString(Object jsonObject) {
		if (jsonObject != null) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				String jsonString = mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(jsonObject);
				return jsonString;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "{ }";
	}

	private Map<String, Object> transferToJSONObject(Report report) {
		if (report.supportedSSLVersions.size() == 0)
			return null;

		Map<String, Object> jsonReport = new HashMap<String, Object>();

		// output common values
		jsonReport.put("createdOn",
				String.format("%1$tF %1$tT", System.currentTimeMillis()));
		jsonReport.put("host", report.host);
		jsonReport.put("ipAddress", report.ip.toString());
		jsonReport.put("port", Integer.toString(report.port));

		// protocol
		List<String> supportedVersions = new ArrayList<String>();
		for (int version : report.supportedSSLVersions) {
			supportedVersions.add(versionString(version));
		}
		jsonReport.put("supportedSSLVersions", supportedVersions);
		jsonReport.put("compress", Boolean.toString(report.compress));

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
		jsonReport.put("cipherSuites", cipherSuites);

		Map<String, Object> certificates = new HashMap<String, Object>();
		if (report.serverCert != null && report.serverCert.size() != 0) {
			for (Integer version : report.serverCert.keySet()) {
				Set<Certificate> versionCertificates = report.serverCert
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
		if (!cert.isProcessed)
			cert.processCertificateBytes();

		Map<String, Object> jsonCert = new HashMap<String, Object>();
		jsonCert.put("order", cert.order);

		if (cert instanceof SSLv2Certificate) {
			SSLv2Certificate sslv2Cert = (SSLv2Certificate) cert;

			jsonCert.put("fingerprint", sslv2Cert.hash);
			jsonCert.put("certificate-order", sslv2Cert.order);
			jsonCert.put("subject", sslv2Cert.name);

			return jsonCert;
		} else if (cert instanceof CertificateV3) {
			CertificateV3 v3Cert = (CertificateV3) cert;

			jsonCert.put("version", v3Cert.certificateVersion);
			jsonCert.put("subjectName", v3Cert.subjectName);
			jsonCert.put("alternativeNames", v3Cert.alternativeNames);
			jsonCert.put("notBefore",
					String.format("%1$tF %1$tT", v3Cert.notBefore));
			jsonCert.put("notAfter",
					String.format("%1$tF %1$tT", v3Cert.notAfter));
			jsonCert.put("pubKeyName", v3Cert.pubKeyInfo.pubKeyAlgorithm);
			jsonCert.put("pubKeySize", v3Cert.pubKeyInfo.pubKeySize);
			jsonCert.put("issuerName", v3Cert.issuerName);
			jsonCert.put("signatureAlgorithm", v3Cert.signatureAlgorithm);
			jsonCert.put("fingerprint", v3Cert.fingerprint);
			jsonCert.put("crlDistributionPoints", v3Cert.crlDistributionPoints);

			return jsonCert;
		}

		return null;
	}

}
