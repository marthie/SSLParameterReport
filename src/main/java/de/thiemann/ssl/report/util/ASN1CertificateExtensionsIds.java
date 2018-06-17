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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public enum ASN1CertificateExtensionsIds {
	
	
	
	//From RFC 5280
	
	CRLDistributionPoints("2.5.29.31"),
	AuthorityKeyIdentifier("2.5.29.35"),
	SubjectKeyIdentifier("2.5.29.14"),
	KeyUsage("2.5.29.15"),
	CertificatePolicies("2.5.29.15"),
	PolicyMappings("2.5.29.33"),
	SubjectAlternativeName("2.5.29.17"),
	IssuerAlternativeName("2.5.29.18"),
	SubjectDirectoryAttributes("2.5.29.9"),
	BasicConstraints("2.5.29.19"),
	NameConstraints("2.5.29.30"),
	PolicyConstraints("2.5.29.36"),
	ExtendedKeyUsage("2.5.29.37"),
	InhibitAnyPolicy("2.5.29.54"),
	FreshestCRL("2.5.29.46");

	private String oid;
	
	private ASN1CertificateExtensionsIds(String oid) {
		this.oid = oid;
	}

	public String getOid() {
		return oid;
	}

	public ASN1ObjectIdentifier getASN1ObjectIdentifier() { return new ASN1ObjectIdentifier(this.oid); }

	public String toString() {
		return this.name() + "[" + this.oid + "]";
	}
}
