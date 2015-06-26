package de.thiemann.ssl.report;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public class Report {
	
	String webName;
	int port;
	InetSocketAddress isa;
	Set<Integer> supportedSSLVersions;
	boolean compress;
	Map<Integer, Set<Integer>> supportedCipherSuite;
	Map<Integer, Set<String>> serverCert;

	public Report(String webName, int port) {
		super();
		this.webName = webName;
		this.port = port;
	}

}
