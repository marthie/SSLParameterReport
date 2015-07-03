package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Inject;

import de.thiemann.ssl.report.build.ReportBuilder;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.output.ReportJsonOutput;
import de.thiemann.ssl.report.output.ReportOutput;
import de.thiemann.ssl.report.util.IOUtil;

public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 7892544796659986902L;

	private ReportBuilder builder;
	private ReportOutput output;

	@Inject
	public ReportServlet(ReportBuilder builder, ReportJsonOutput output) {
		super();
		this.builder = builder;
		this.output = output;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, Object> jsonInput = getArguments(request.getInputStream());

		if (jsonInput != null) {

			String webName = null;
			Integer port = new Integer(443);

			if (jsonInput.containsKey("webName")) {
				webName = jsonInput.get("webName").toString();
			}

			if (jsonInput.containsKey("port")) {
				port = new Integer(jsonInput.get("port").toString());
			}

			if (webName != null && !webName.isEmpty()) {
				Report report = builder.generateReport(webName, port);
				String jsonOutput = output.outputReport(report);

				response.setContentType("application/json;charset=utf-8");
				response.setStatus(HttpServletResponse.SC_OK);
				OutputStream os = response.getOutputStream();
				os.write(jsonOutput.getBytes(Charset.forName("UTF-8")));
			}
		}
	}

	public Map<String, Object> getArguments(InputStream is) {
		try {
			byte[] postBytes = IOUtil.readFully(is);

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> jsonInput = mapper.readValue(postBytes,
					Map.class);

			if (jsonInput.containsKey("webName"))
				return jsonInput;

			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
