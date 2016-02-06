package de.thiemann.ssl.report.server.service;

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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thiemann.ssl.report.build.Lookup;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.server.cache.ReportCache;
import de.thiemann.ssl.report.server.dto.SSLReportRequest;

@Path("/sslReport")
public class SSLReportService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private ReportBuilder builder;
	@Inject
	private Output output;
	@Inject
	private Lookup lookUp;
	@Inject
	private ReportCache reportCache;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String getSSlReports(SSLReportRequest request) {
		
		if(request == null) {
			log.error("Request may not be null!");
			return "{ }";
		}
			
		if(request.host == null || request.host.isEmpty()) {
			log.error("Host name may not be empty or null!");
			return "{ }";
		}

		String jsonOutput = null;

		if (request.port == null) {
			request.port = new Integer(443);
		}
		
		log.info("Request for {}", request.toString());

		InetAddress[] ips = lookUp.getAllByName(request.host);

		if (ips != null) {
			if (ips.length == 1) {
				InetAddress ipAddress = ips[0];
				jsonOutput = getSingleReport(ipAddress, request.port);
			} else if (ips.length > 0) {
				jsonOutput = getMultipleReports(ips, request.port);
			}
		}
		
		if (jsonOutput == null || jsonOutput.isEmpty())
			jsonOutput = "{ }";
		
		log.trace(jsonOutput);

		return jsonOutput;

	}

	public String getSingleReport(InetAddress ipAddress, Integer port) {
		Report report = null;
		if (reportCache.isReportCached(ipAddress)) {
			log.debug("Get request {}:{}  from cache", ipAddress.toString(), port);
			report = reportCache.getCachedReport(ipAddress);
		} else {
			log.debug("No matches in cache found for request {}:{}", ipAddress.toString(), port);
			report = builder.generateReport(ipAddress, port);

			if (report != null) {
				reportCache.storeReport(report);
				log.debug("Add report for request {}:{} to cache", ipAddress.toString(), port);
			}
		}

		return output.outputReport(report);
	}

	public String getMultipleReports(InetAddress[] ips, Integer port) {
		List<InetAddress> ipList = new ArrayList<InetAddress>(
				Arrays.asList(ips));
		
		if(log.isDebugEnabled())
			log.debug("Start creating reports for {} IP Addresses", ipList.size());

		List<Report> cachedReportList = null;
		for (int i = 0; i < ipList.size(); i++) {
			InetAddress ip = ipList.get(i);

			if (reportCache.isReportCached(ip)) {
				if (cachedReportList == null)
					cachedReportList = new ArrayList<Report>();

				cachedReportList.add(reportCache.getCachedReport(ip));
				ipList.remove(i);
			}
		}

		List<Report> generatedReportList = builder.generateMultipleReport(
				ipList, port);
		if (generatedReportList != null && !generatedReportList.isEmpty())
			reportCache.storeReport(generatedReportList);

		List<Report> reportList = null;
		if (cachedReportList != null && !cachedReportList.isEmpty())
			reportList = cachedReportList;

		if (reportList == null || reportList.isEmpty())
			reportList = generatedReportList;
		else {
			reportList.addAll(generatedReportList);
		}

		return output.outputReportCollection(reportList);
	}
}
