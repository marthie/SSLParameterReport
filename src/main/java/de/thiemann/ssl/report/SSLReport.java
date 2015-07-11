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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import de.thiemann.ssl.report.console.ConsoleController;
import de.thiemann.ssl.report.server.ServerController;
import de.thiemann.ssl.report.util.Util;

public class SSLReport {

	public static void main(String[] args) throws Exception {
		OptionParser optParser = new OptionParser() {
			{
				acceptsAll(Util.asList("?", "h", "help"), "Show help message");
				accepts("server", "start server for self services");
			}
		};

		OptionSpec<String> optWebName = optParser
				.acceptsAll(Util.asList("wn", "webName"),
						"create report for the webName").withRequiredArg()
				.ofType(String.class);
		OptionSpec<Integer> optPort = optParser
				.acceptsAll(Util.asList("p", "port"),
						"use the given port in place of port 443 for report creation")
				.withRequiredArg().ofType(Integer.class);

		OptionSet os = optParser.parse(args);

		if (os.has("?")) {
			optParser.printHelpOn(System.out);
			System.exit(1);
		}

		if (os.has(optWebName)) {
			String webName = os.valueOf(optWebName);
			int port = 443;

			if (os.has(optPort)) {
				port = os.valueOf(optPort);

				if (port <= 0 || port > 65535) {
					throw new Error("Wrong port! Port range is [0;65535]");
				}
			}
			
			ConsoleController controller = new ConsoleController();
			controller.outputReport(webName, port);
		} else if (os.has("server")) {
			new ServerController().startServer();
		}
	}
}
