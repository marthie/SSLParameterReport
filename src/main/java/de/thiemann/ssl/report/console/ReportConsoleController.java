package de.thiemann.ssl.report.console;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.thiemann.ssl.report.build.NSLookUp;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.ReportOutput;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public class ReportConsoleController {

	private ReportBuilder builder;

	private ReportOutput output;
	
	private NSLookUp lookup;

	public ReportConsoleController() {
		super();
		Injector injector = Guice.createInjector(new ReportConsoleModule());
		this.builder = injector.getInstance(ReportBuilder.class);
		this.output = injector.getInstance(ReportOutput.class);
		this.lookup = injector.getInstance(NSLookUp.class);
	}

	public void outputReport(String webName, int port) {
		InetAddress[] ips = lookup.getAllByName(webName);

		if (ips != null) {
			String consoleOutput = null;
			if (ips.length == 1) {
				Report report = builder.generateReport(ips[0], port);
				
				consoleOutput = output.outputReport(report);
				
				
			} else if (ips.length > 0) {
				List<Report> reportList = builder.generateMultipleReport(ips, port);
				
				consoleOutput = output.outputReportCollection(reportList);
			}
			
			if(consoleOutput != null && !consoleOutput.isEmpty()) {
				PrintWriter pw = new PrintWriter(System.out);
				pw.write(consoleOutput);
				pw.close();
			}
		}
	}

}
