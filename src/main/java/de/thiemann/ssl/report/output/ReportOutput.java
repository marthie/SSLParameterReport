package de.thiemann.ssl.report.output;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.util.Collection;

import de.thiemann.ssl.report.model.Report;

public interface ReportOutput {

	public String outputReport(Report report);
	
	public String outputReportCollection(Collection<Report> reportCollection);
}
