package de.thiemann.ssl.report.model;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public class SSLv2Certificate extends Certificate {
	
	public String name;
	public String hash;
	
	public SSLv2Certificate(int i, String name, String hash) {
		this.order = i;
		this.name = name;
		this.hash = hash;
	}

	@Override
	public String certificateReport() {
		return hash + ": " + name;
	}

}
