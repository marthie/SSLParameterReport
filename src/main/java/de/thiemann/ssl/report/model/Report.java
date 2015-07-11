package de.thiemann.ssl.report.model;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public class Report {

	public String webName;
	public int port;
	public InetAddress ip;
	public Set<Integer> supportedSSLVersions;
	public boolean compress;
	public Map<Integer, Set<Integer>> supportedCipherSuite;
	public Map<Integer, Set<Certificate>> serverCert;

	public Report(String webName, int port) {
		super();
		this.webName = webName;
		this.port = port;
	}

	public Report(InetAddress ip, int port) {
		super();
		this.port = port;
		this.ip = ip;
		this.webName = ip.getHostName();
	}

}
