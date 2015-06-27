package de.thiemann.ssl.report.util;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public enum SSLVersions {
	
	SSLv2(0x0200),
	SSLv3(0x0300),
	TLS_1_0(0x0301),
	TLS_1_1(0x0302),
	TLS_1_2(0x0303);
	
	private int version;

	private SSLVersions(int version) {
		this.version = version;
	}

	public int getIntVersion() {
		return version;
	}
}
