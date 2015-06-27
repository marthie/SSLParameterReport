package de.thiemann.ssl.report.util;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public enum ASN1PublicKeyIds {
	
	// From RFC3279
	
	RSA("1.2.840.113549.1.1.1"),
	DSA("1.2.840.10040.4.1"),
	Diffie_Hellman("1.2.840.10046.2.1"),
	KEA("2.16.840.1.101.2.1.1.22"),
	ECDH("1.2.840.10045.2.1");

	private String oid;
	
	private ASN1PublicKeyIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}
}
