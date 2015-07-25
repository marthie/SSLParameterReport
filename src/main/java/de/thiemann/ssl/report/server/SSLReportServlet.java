package de.thiemann.ssl.report.server;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import de.thiemann.ssl.report.build.Lookup;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.JsonOutput;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.server.cache.ReportCache;
import de.thiemann.ssl.report.util.IOUtil;

public class SSLReportServlet extends HttpServlet {

	private static final long serialVersionUID = 7892544796659986902L;

	private ReportBuilder builder;
	private Output output;
	private Lookup lookUp;
	private ReportCache reportCache;

	@Inject
	public SSLReportServlet(ReportBuilder builder, JsonOutput output,
			Lookup lookUp, ReportCache reportCache) {
		super();
		this.builder = builder;
		this.output = output;
		this.lookUp = lookUp;
		this.reportCache = reportCache;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> arguments = getArguments(request.getInputStream());
		ServerCommand cmd = getCommand(arguments);
		
		if (cmd instanceof NewCommand) {
			NewCommand newCmd = (NewCommand) cmd;
			InetAddress[] ips = lookUp.getAllByName(newCmd.host);

			String jsonOutput = null;

			if (ips != null) {
				if (ips.length == 1) {
					InetAddress ipAddress = ips[0];
					jsonOutput = getSingleReport(ipAddress, newCmd.port);
				} else if (ips.length > 0) {
					jsonOutput = getMultipleReports(ips, newCmd.port);
				}
			}

			if (jsonOutput == null || jsonOutput.isEmpty())
				jsonOutput = "{ }";

			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			OutputStream os = response.getOutputStream();
			os.write(jsonOutput.getBytes(Charset.forName("UTF-8")));
		}
	}

	private ServerCommand getCommand(Map<String, String> arguments) {
		ServerCommand cmd = null;

		if (arguments != null) {

			if (arguments.containsKey("cmd")) {
				String argCmd = arguments.get("cmd");

				if ("new".equals(argCmd))
					cmd = new NewCommand();
			}

			if (cmd instanceof NewCommand) {
				NewCommand newCmd = (NewCommand) cmd;

				if (arguments.containsKey("host")) {
					newCmd.host = arguments.get("host").toString();
				}

				if (arguments.containsKey("port")) {
					newCmd.port = new Integer(arguments.get("port").toString());
				} else
					newCmd.port = new Integer(443);
			}
		}
		
		return cmd;
	}

	public String getSingleReport(InetAddress ipAddress, Integer port) {
		Report report = null;
		if (reportCache.isReportCached(ipAddress)) {
			report = reportCache.getCachedReport(ipAddress);
		} else {
			report = builder.generateReport(ipAddress, port);

			if (report != null)
				reportCache.storeReport(report);
		}

		return output.outputReport(report);
	}

	public String getMultipleReports(InetAddress[] ips, Integer port) {
		List<InetAddress> ipList = new ArrayList<InetAddress>(
				Arrays.asList(ips));

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

	public Map<String, String> getArguments(InputStream is) {
		try {
			byte[] postBytes = IOUtil.readFully(is);
			Map<String, String> arguments = new HashMap<String, String>();

			String args = new String(postBytes);

			String[] splitedArgs = args.split("&");

			if (splitedArgs.length > 0) {
				for (String arg : splitedArgs) {
					String[] keyValue = arg.split("=");

					if (keyValue.length == 2) {
						String key = keyValue[0], value = keyValue[1];
						
						if((key != null && !key.isEmpty()) && (value != null && !value.isEmpty()))
							arguments.put(key, value);
					}
				}

				if (arguments.size() > 0)
					return arguments;
			}

			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
