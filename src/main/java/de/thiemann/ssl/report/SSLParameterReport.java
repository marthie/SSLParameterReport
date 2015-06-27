package de.thiemann.ssl.report;

/*
 * Command-line tool to test a SSL/TLS parameter on a server.
 * =====================================================================
 *
 * This application connects to the provided SSL/TLS server (by name and
 * port) and extracts the following information:
 * - supported versions (SSL 2.0, SSL 3.0, TLS 1.0 to 1.2)
 * - support of Deflate compression
 * - list of supported cipher suites (for each protocol version)
 *
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

import java.io.IOException;

import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.ReportConsoleOutput;

public class SSLParameterReport {

	static void usage() {
		System.err.println("usage: SSLParameterReport servername [ port ]");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args.length > 2) {
			usage();
		}

		String webName = args[0];
		int port = 443;

		if (args.length == 2) {
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				usage();
			}
			if (port <= 0 || port > 65535) {
				usage();
			}
		}

		ReportBuilder builder = new ReportBuilder();
		Report report = builder.generateReport(webName, port);
		
		ReportConsoleOutput rco = new ReportConsoleOutput();
		rco.consoleOutput(report);
	}
}
