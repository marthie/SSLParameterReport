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
import de.thiemann.ssl.report.exceptions.*;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.cache.ReportCache;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public String getSSlReports(@Valid @RequestBody SSLReportRequest request) throws LookupException, ReportOutputException, NoReportException {
        log.info("Request for {}", request.toString());

        InetAddress[] ips = lookUp.getAllByName(request.getHost());

        List<InetAddress> ipsList = Arrays.stream(ips).collect(Collectors.toList());

        String jsonOutput = null;
        if (ipsList != null && ipsList.size() > 0) {
            jsonOutput = getReports(ipsList, Integer.valueOf(request.getPort()));
        }

        return jsonOutput;

    }


    public String getReports(List<InetAddress> ipsList, Integer port) throws ReportOutputException, NoReportException {

        if (log.isDebugEnabled()) {
            log.debug("Start creating reports for ip addresses: {}", ipsList.stream().map(inetAddress -> inetAddress.toString()).collect(Collectors.joining("; ")));
        }

        // get all cached reports
        List<Report> cachedReportList = new ArrayList<Report>();
        for (int i = 0; i < ipsList.size(); i++) {
            InetAddress ip = ipsList.get(i);

            if (reportCache.isReportCached(ip)) {
                Report cachedReport = reportCache.getCachedReport(ip);
                cachedReportList.add(cachedReport);
                ipsList.remove(i);
            }
        }

        // generate the rest
        List<Report> generatedReportList = builder.generateReports(ipsList, port);

        if (generatedReportList != null && !generatedReportList.isEmpty()) {
            reportCache.storeReport(generatedReportList);
        }

        // join cached and generated reports
        List<Report> resultReportList = null;

        if (cachedReportList != null && !cachedReportList.isEmpty()) {
            resultReportList = cachedReportList;
        }

        if (resultReportList != null && generatedReportList != null && !generatedReportList.isEmpty()) {
            resultReportList.addAll(generatedReportList);
        }

        if (resultReportList == null || resultReportList.isEmpty()) {
            resultReportList = generatedReportList;
        }

        if(resultReportList == null || resultReportList.isEmpty()) {
            String ipsStringList =  ipsList.stream().map(inetAddress -> inetAddress.toString()).collect(Collectors.joining("; "));
            throw new NoReportException(String.format("Could not generate any report for the hosts: %s", ipsStringList));
        }

        if (log.isDebugEnabled()) {
            log.debug("End creating reports for ip addresses: {}", ipsList.stream().map(inetAddress -> inetAddress.toString()).collect(Collectors.joining("; ")));
        }

        // transfer to JSON
        String reportOutput = output.outputReports(resultReportList);

        return reportOutput;
    }

    @ExceptionHandler(value = {LookupException.class, ReportOutputException.class, NoReportException.class})
    public ResponseEntity<Object> handleExceptions(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e);
        return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
    }
}
