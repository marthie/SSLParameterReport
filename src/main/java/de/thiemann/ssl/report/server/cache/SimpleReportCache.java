
package de.thiemann.ssl.report.server.cache;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Singleton;

import de.thiemann.ssl.report.model.Report;

@Singleton
public class SimpleReportCache implements ReportCache {
	
	private Map<String, Report> cache = new ConcurrentHashMap<String, Report>();

	@Override
	public Report getCachedReport(InetAddress ipAddress) {
		if(ipAddress != null)
			return cache.get(ipAddress.toString());
		return null;
	}

	@Override
	public void storeReport(Report report) {
		if(report != null && report.ip != null) {
			cache.put(report.ip.getHostAddress(), report);
		}
	}

	@Override
	public boolean isReportCached(InetAddress ipAddress) {
		if(ipAddress != null)
			return cache.containsKey(ipAddress.getHostAddress());
		return false;
	}

	@Override
	public void storeReport(List<Report> reportList) {
		for (Report report : reportList) {
			storeReport(report);
		}
	}

}
