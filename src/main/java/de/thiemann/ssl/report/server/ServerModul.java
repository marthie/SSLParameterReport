package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import com.google.inject.AbstractModule;

import de.thiemann.ssl.report.output.JsonOutput;
import de.thiemann.ssl.report.output.Output;

public class ServerModul extends AbstractModule {

	@Override
	protected void configure() {
		bind(Output.class).to(JsonOutput.class);
	}

}
