package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ServerController {

	private Injector injector = null;

	public ServerController() {
		injector = Guice.createInjector(new ReportServerModul());
	}

	public void startServer() {
		
		Handler reportHandler = createRportHandler();
		
		Server server = new Server(8080);
		server.setHandler(reportHandler);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler createRportHandler() {
		ReportServlet reportServlet = injector.getInstance(ReportServlet.class);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		
		context.addServlet(new ServletHolder(reportServlet), "/sslReport");
		
		return context;
	}
}
