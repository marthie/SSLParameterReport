package de.thiemann.ssl.report.console;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.thiemann.ssl.report.build.NSLookUp;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.Output;

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

public class ConsoleController {

	private ReportBuilder builder;

	private Output output;

	private NSLookUp lookup;

	public ConsoleController() {
		super();
		Injector injector = Guice.createInjector(new ConsoleModule());
		this.builder = injector.getInstance(ReportBuilder.class);
		this.output = injector.getInstance(Output.class);
		this.lookup = injector.getInstance(NSLookUp.class);
	}

	public void outputReport(String host, int port) {
		InetAddress[] ips = lookup.getAllByName(host);

		if (ips != null) {
			String consoleOutput = null;
			if (ips.length == 1) {
				Report report = builder.generateReport(ips[0], port);

				consoleOutput = output.outputReport(report);

			} else if (ips.length > 0) {
				List<Report> reportList = builder.generateMultipleReport(ips,
						port);

				consoleOutput = output.outputReportCollection(reportList);
			}

			if (consoleOutput != null && !consoleOutput.isEmpty()) {
				PrintWriter pw = new PrintWriter(System.out);
				pw.write(consoleOutput);
				pw.close();
			}
		}
	}

}
