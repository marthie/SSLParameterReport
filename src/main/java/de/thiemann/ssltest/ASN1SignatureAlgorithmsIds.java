package de.thiemann.ssltest;

public enum ASN1SignatureAlgorithmsIds {
	
	// From RFC3279
	
	MD2("1.2.840.113549.2.2"),
	MD5("1.2.840.113549.2.5"),
	SHA1("1.3.14.3.2.26"),
	MD2WithRSAEncryption("1.2.840.113549.1.1.2"),
	MD5WithRSAEncryption("1.2.840.113549.1.1.4"),
	SHA1WithRSAEncryption("1.2.840.113549.1.1.5"),
	DSAWithSHA1("1.2.840.10040.4.3"),
	ECDSAWithSHA1("1.2.840.10045.4.1");
	

	private String oid;
	
	private ASN1SignatureAlgorithmsIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}
}
