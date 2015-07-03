package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import com.google.inject.AbstractModule;

import de.thiemann.ssl.report.output.ReportJsonOutput;
import de.thiemann.ssl.report.output.ReportOutput;

public class ReportServerModul extends AbstractModule{

	@Override
	protected void configure() {
		bind(ReportOutput.class).to(ReportJsonOutput.class);
	}

}
