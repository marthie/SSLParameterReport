package de.thiemann.ssl.report.server;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ServerController {

	private Injector injector = null;

	public ServerController() {
		injector = Guice.createInjector(new ReportServerModul());
	}

	public void startServer() {
		
		HandlerList list = createHandlerList();
		
		Server server = new Server(8080);
		server.setHandler(list);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HandlerList createHandlerList() {
		HandlerList list = new HandlerList();
		
		// servlet
		ReportServlet reportServlet = injector.getInstance(ReportServlet.class);
		
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/service");
		
		servletContext.addServlet(new ServletHolder(reportServlet), "/sslReport");
		
		list.addHandler(servletContext);
		
		// static content
		ResourceHandler resApp = new ResourceHandler();
		resApp.setBaseResource(Resource.newClassPathResource("/sslReportApp"));
		//resApp.setDirectoriesListed(true);
		resApp.setWelcomeFiles(new String[] {"index.html"});
		
		list.addHandler(resApp);
		
		
		return list;
	}
	
}
