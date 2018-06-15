package de.thiemann.ssl.report.util;

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

public enum ASN1CertificateExtensionsIds {
	
	//From RFC 5280
	
	CE("2.5.29"),
	CRLDistributionPoints(CE.getOid() + ".31"),
	AuthorityKeyIdentifier(CE.getOid() + ".35"),
	SubjectKeyIdentifier(CE.getOid() + ".14"),
	KeyUsage(CE.getOid() + ".15"),
	CertificatePolicies(CE.getOid() + ".15"),
	PolicyMappings(CE.getOid() + ".33"),
	SubjectAlternativeName(CE.getOid() + ".17"),
	IssuerAlternativeName(CE.getOid() + ".18"),
	SubjectDirectoryAttributes(CE.getOid() + ".9"),
	BasicConstraints(CE.getOid() + ".19"),
	NameConstraints(CE.getOid() + ".30"),
	PolicyConstraints(CE.getOid() + ".36"),
	ExtendedKeyUsage(CE.getOid() + ".37"),
	InhibitAnyPolicy(CE.getOid() + ".54"),
	FreshestCRL(CE.getOid() + ".46");

	private String oid;
	
	private ASN1CertificateExtensionsIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}

	public String toString() {
		return this.name() + "[" + this.oid + "]";
	}
}
