package de.thiemann.ssl.report.model;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

public class Report {

	private String id;
	private String host;
	private int port;
	private InetAddress ip;
	private Set<Integer> supportedSSLVersions;
	private boolean compress;
	private Map<Integer, Set<Integer>> supportedCipherSuite;
	private Map<Integer, Set<Certificate>> serverCert;

	public Report(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		this.id = UUID.randomUUID().toString();
	}

	public Report(InetAddress ip, int port) {
		super();
		this.port = port;
		this.ip = ip;
		this.host = ip.getHostName();
		this.id = UUID.randomUUID().toString();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public Set<Integer> getSupportedSSLVersions() {
		return supportedSSLVersions;
	}

	public void setSupportedSSLVersions(Set<Integer> supportedSSLVersions) {
		this.supportedSSLVersions = supportedSSLVersions;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public Map<Integer, Set<Integer>> getSupportedCipherSuite() {
		return supportedCipherSuite;
	}

	public void setSupportedCipherSuite(Map<Integer, Set<Integer>> supportedCipherSuite) {
		this.supportedCipherSuite = supportedCipherSuite;
	}

	public Map<Integer, Set<Certificate>> getServerCert() {
		return serverCert;
	}

	public void setServerCert(Map<Integer, Set<Certificate>> serverCert) {
		this.serverCert = serverCert;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
