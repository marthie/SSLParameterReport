package de.thiemann.ssl.report.console;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import com.google.inject.AbstractModule;

import de.thiemann.ssl.report.output.ReportOutput;
import de.thiemann.ssl.report.output.ReportTextOutput;

public class ReportConsoleModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ReportOutput.class).to(ReportTextOutput.class);		
	}

}
