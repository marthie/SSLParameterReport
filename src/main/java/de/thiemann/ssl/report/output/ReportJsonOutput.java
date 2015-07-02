package de.thiemann.ssl.report.output;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import de.thiemann.ssl.report.model.Report;
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
					cipherSuitesByVersion.add(CipherSuiteUtil.cipherSuiteStringV2(cipherSuite));
				}
				
				cipherSuites.put(versionString(SSLVersions.SSLv2.getIntVersion()), cipherSuitesByVersion);
			} else {
				for (int c : report.supportedCipherSuite.get(version)) {
					cipherSuitesByVersion.add(CipherSuiteUtil.cipherSuiteString(c));
				}
				
				cipherSuites.put(versionString(version), cipherSuitesByVersion);
			}

		}
		jsonStructure.put("cipherSuites", cipherSuites);
		
		Map<String, Object> certificates = new HashMap<String, Object>();
		if(report.serverCert != null && report.serverCert.size() != 0) {
			
		}

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonStructure);
			return jsonString;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
