package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import de.thiemann.ssl.report.build.NSLookUp;
import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.JsonOutput;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.util.IOUtil;

public class SSLReportServlet extends HttpServlet {

	private static final long serialVersionUID = 7892544796659986902L;

	private ReportBuilder builder;
	private Output output;
	private NSLookUp lookUp;

	@Inject
	public SSLReportServlet(ReportBuilder builder, JsonOutput output, NSLookUp lookUp) {
		super();
		this.builder = builder;
		this.output = output;
		this.lookUp = lookUp;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> inputArguments = getArguments(request.getInputStream());

		if (inputArguments != null) {

			String webName = null;
			Integer port = new Integer(443);

			if (inputArguments.containsKey("host")) {
				webName = inputArguments.get("host").toString();
			}

			if (inputArguments.containsKey("port")) {
				port = new Integer(inputArguments.get("port").toString());
			}

			if (webName != null && !webName.isEmpty()) {
				InetAddress[] ips = lookUp.getAllByName(webName);
				
				String jsonOutput = "{ }";
				
				if(ips != null) {
					if(ips.length == 1) {
						Report report = builder.generateReport(ips[0], port);
						jsonOutput = output.outputReport(report);
					} else if(ips.length > 0) {
						List<Report> reportList = builder.generateMultipleReport(ips, port);
						jsonOutput = output.outputReportCollection(reportList);
					}
				}

				response.setContentType("application/json;charset=utf-8");
				response.setStatus(HttpServletResponse.SC_OK);
				OutputStream os = response.getOutputStream();
				os.write(jsonOutput.getBytes(Charset.forName("UTF-8")));
			}
		}
	}

	public Map<String, String> getArguments(InputStream is) {
		try {
			byte[] postBytes = IOUtil.readFully(is);
			Map<String, String> inputArguments = new HashMap<String, String>();
			
			String args = new String(postBytes);
			
			String[] splitedArgs = args.split("&");
			
			if(splitedArgs.length > 0) {
				for (String arg : splitedArgs) {
					String[] keyValue = arg.split("=");
					
					if(keyValue.length == 2) {
						inputArguments.put(keyValue[0], keyValue[1]);
					}
				}
				
				if(inputArguments.size() > 0)
					return inputArguments;
			}

			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
