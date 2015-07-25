package de.thiemann.ssl.report.server.cache;

import java.net.InetAddress;
import java.util.List;

import de.thiemann.ssl.report.model.Report;

public interface ReportCache {

	public Report getCachedReport(InetAddress ipAddress);
	
	public boolean isReportCached(InetAddress ipAddress);
	
	public void storeReport(Report report);
	
	public void storeReport(List<Report> reportList);
}
