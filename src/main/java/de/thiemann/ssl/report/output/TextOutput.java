package de.thiemann.ssl.report.output;

/*
 * ----------------------------------------------------------------------
 * Copyright (c) 2012  Thomas Pornin <pornin@bolet.org>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ----------------------------------------------------------------------
 */

import java.util.Collection;
import java.util.Set;

import de.thiemann.ssl.report.model.Certificate;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.util.CipherSuiteUtil;
import de.thiemann.ssl.report.util.SSLVersions;

public class TextOutput extends AbstractOutput {

	private static String NL = System.getProperty("line.separator");
	
	@Override
	public String outputReportCollection(Collection<Report> reportCollection) {
		StringBuffer sb = new StringBuffer();
		
		for (Report report : reportCollection) {
			sb.append(NL);
			sb.append(outputReport(report));
		}
		
		return sb.toString();
	}

	public String outputReport(Report report) {
		StringBuffer sb = new StringBuffer();
		
		if(report.supportedSSLVersions == null)
			return new String();

		if (report.supportedSSLVersions.size() == 0) {
			sb.append(NL).append("No SSL/TLS server at " + report.ip.toString());
			return sb.toString();
		}

		sb.append(NL)
				.append("**************************** SSL Parameter Report *****************************");
		sb.append(NL)
				.append(NL)
				.append("----------------------------- Common Information -------------------------------");

		// output common values
		sb.append(NL)
				.append("Report created on: ")
				.append(String.format("%1$tF %1$tT", System.currentTimeMillis()));
		sb.append(NL).append("Web-Name: ").append(report.webName);
		sb.append(NL).append("IP-Address: ").append(report.ip.toString());
		sb.append(NL).append("Port: ").append(report.port);

		sb.append(NL)
				.append(NL)
				.append("--------------------------------- Protocol -------------------------------------");

		sb.append(NL).append("Supported protocol versions:");
		for (int version : report.supportedSSLVersions) {
			sb.append(" ").append(versionString(version));
		}

		sb.append(NL).append("Deflate compression: ")
				.append((report.compress ? "YES" : "no"));

		sb.append(NL)
				.append(NL)
				.append("-------------------------- Supported Cipher Suites -----------------------------");

		for (int version : report.supportedSSLVersions) {

			if (version == SSLVersions.SSLv2.getIntVersion()) {
				sb.append(NL)
						.append(NL)
						.append("  ")
						.append(versionString(SSLVersions.SSLv2.getIntVersion()));

				for (int cipherSuite : report.supportedCipherSuite.get(version)) {
					sb.append(NL)
							.append("     ")
							.append(CipherSuiteUtil
									.cipherSuiteStringV2(cipherSuite));
				}
			} else {
				sb.append(NL).append(NL).append("  ")
						.append(versionString(version));
				for (int c : report.supportedCipherSuite.get(version)) {
					sb.append(NL).append("      ")
							.append(CipherSuiteUtil.cipherSuiteString(c));
				}
			}

		}

		sb.append(NL)
				.append(NL)
				.append("---------------------------- Server certificates -------------------------------");
		if (report.serverCert.size() == 0) {
			sb.append(NL).append("No server certificate!");
		} else {

			Set<Certificate> serverCertificates = null;
			SSLVersions[] versions = SSLVersions.values();
			for (int i = versions.length - 1; i >= 0; i--) {
				serverCertificates = report.serverCert.get(versions[i]
						.getIntVersion());

				if (serverCertificates != null)
					break;
			}

			if (serverCertificates != null) {
				for (Certificate serverCertificate : serverCertificates) {

					sb.append(NL).append("  ")
							.append(serverCertificate.certificateReport());
				}
			} else
				sb.append(NL).append("No server certificate!");
		}

		return sb.toString();
	}

}
