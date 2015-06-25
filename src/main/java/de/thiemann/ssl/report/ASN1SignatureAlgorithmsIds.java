package de.thiemann.ssl.report;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public enum ASN1SignatureAlgorithmsIds {
	
	// From RFC3279
	
	MD2("1.2.840.113549.2.2"),
	MD5("1.2.840.113549.2.5"),
	SHA1("1.3.14.3.2.26"),
	MD2WithRSAEncryption("1.2.840.113549.1.1.2"),
	MD5WithRSAEncryption("1.2.840.113549.1.1.4"),
	SHA1WithRSAEncryption("1.2.840.113549.1.1.5"),
	DSAWithSHA1("1.2.840.10040.4.3"),
	ECDSAWithSHA1("1.2.840.10045.4.1"),
	
	//From RFC4055
	SHA224("2.16.840.1.101.3.4.2.4"),
	SHA256("2.16.840.1.101.3.4.2.1"),
	SHA384("2.16.840.1.101.3.4.2.2"),
	SHA512("2.16.840.1.101.3.4.2.3"),
	PKCS_1("1.2.840.113549.1.1"),
	RSAEncryption(PKCS_1.getOid() + ".1"),
	sha224WithRSAEncryption(PKCS_1.getOid() + ".14"),
	sha256WithRSAEncryption(PKCS_1.getOid() + ".11"),
	sha384WithRSAEncryption(PKCS_1.getOid() + ".12"),
	sha512WithRSAEncryption(PKCS_1.getOid() + ".13");
	
	

	private String oid;
	
	private ASN1SignatureAlgorithmsIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}
}
