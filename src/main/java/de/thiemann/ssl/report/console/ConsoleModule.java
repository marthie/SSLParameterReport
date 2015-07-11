package de.thiemann.ssl.report.console;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import com.google.inject.AbstractModule;

import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.output.TextOutput;

public class ConsoleModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Output.class).to(TextOutput.class);		
	}

}
