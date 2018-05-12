package de.thiemann.ssl.report.service;

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

import de.thiemann.ssl.report.build.Lookup;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.cache.ReportCache;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class SSLReportService {

    private Logger log = LoggerFactory.getLogger(SSLReportService.class);

    @Autowired
    protected ReportBuilder builder;

    @Autowired
    private Output output;

    @Autowired
    private Lookup lookUp;

    @Autowired
    private ReportCache reportCache;

    @RequestMapping(method = RequestMethod.POST, path = "/service/sslReport", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSSlReports(@RequestBody SSLReportRequest request) {

        if (request == null) {
            log.error("Host name may not be empty or null!");
            return "{ }";
        }

        if (request.getHost() == null || request.getHost().isEmpty()) {
            log.error("Host name may not be empty or null!");
            return "{ }";
        }

        String jsonOutput = null;

        Integer iPort = null;
        if (request.getPort() == null) {
            iPort = new Integer(443);
        } else {
            iPort = new Integer(request.getPort());
        }

        log.info("Request for {}", request.toString());

        InetAddress[] ips = lookUp.getAllByName(request.getHost());

        if (ips != null) {
            if (ips.length == 1) {
                InetAddress ipAddress = ips[0];
                jsonOutput = getSingleReport(ipAddress, iPort);
            } else if (ips.length > 0) {
                jsonOutput = getMultipleReports(ips, iPort);
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

        if (log.isDebugEnabled()) {
            log.debug("Start creating reports for {} IP Addresses", ipList.size());
        }

        List<Report> cachedReportList = null;
        for (int i = 0; i < ipList.size(); i++) {
            InetAddress ip = ipList.get(i);

            if (reportCache.isReportCached(ip)) {
                if (cachedReportList == null) {
                    cachedReportList = new ArrayList<Report>();
                }

                cachedReportList.add(reportCache.getCachedReport(ip));
                ipList.remove(i);
            }
        }

        List<Report> generatedReportList = builder.generateMultipleReport(
                ipList, port);

        if (generatedReportList != null && !generatedReportList.isEmpty()) {
            reportCache.storeReport(generatedReportList);
        }

        List<Report> reportList = null;

        if (cachedReportList != null && !cachedReportList.isEmpty()) {
            reportList = cachedReportList;
        }

        if (reportList == null || reportList.isEmpty()) {
            reportList = generatedReportList;
        } else if (generatedReportList != null && !generatedReportList.isEmpty()) {
            reportList.addAll(generatedReportList);
        }

        String reportOutput = output.outputReportCollection(reportList);

        return reportOutput;
    }
}
