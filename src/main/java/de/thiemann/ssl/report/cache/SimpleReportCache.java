package de.thiemann.ssl.report.cache;

/*

The MIT License (MIT)

Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

import de.thiemann.ssl.report.model.Report;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
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
