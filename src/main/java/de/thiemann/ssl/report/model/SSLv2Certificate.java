package de.thiemann.ssl.report.model;

public class SSLv2Certificate extends Certificate {
	
	public String name;
	public String hash;
	
	public SSLv2Certificate(String name, String hash) {
		super();
		this.name = name;
		this.hash = hash;
	}

	@Override
	public String certificateReport() {
		return hash + ": " + name;
	}

}
