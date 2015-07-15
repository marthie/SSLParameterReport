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
		injector = Guice.createInjector(new ServerModul());
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
		SSLReportServlet reportServlet = injector.getInstance(SSLReportServlet.class);
		
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
