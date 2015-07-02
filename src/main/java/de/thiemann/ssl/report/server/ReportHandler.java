package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.output.ReportJsonOutput;

public class ReportHandler extends AbstractHandler {
	
	private ReportBuilder builder;
	private ReportJsonOutput output;

	public ReportHandler(ReportBuilder builder, ReportJsonOutput output) {
		super();
		this.builder = builder;
		this.output = output;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		
	}

}
