package de.thiemann.ssltest;

public enum ASN1CertificateExtensionsIds {
	
	//From RFC 5280
	
	CE("2.5.29"),
	CRLDistributionPoints(CE.getOid() + ".31");
	
	private String oid;
	
	private ASN1CertificateExtensionsIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}
}
