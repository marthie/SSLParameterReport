package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.inject.Inject;

import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.ReportJsonOutput;
import de.thiemann.ssl.report.output.ReportOutput;

public class ReportHandler extends AbstractHandler {
	
	private ReportBuilder builder;
	private ReportOutput output;

	@Inject
	public ReportHandler(ReportBuilder builder, ReportJsonOutput output) {
		super();
		this.builder = builder;
		this.output = output;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		Report report = builder.generateReport("www.googel.de", 443);
		String jsonOutput = output.outputReport(report);
		
		response.setContentType("text/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		OutputStream os = response.getOutputStream();
		os.write(jsonOutput.getBytes(Charset.forName("UTF-8")));
		
	}

}
