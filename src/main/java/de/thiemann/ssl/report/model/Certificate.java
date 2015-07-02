package de.thiemann.ssl.report.model;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public abstract class Certificate implements Comparable<Certificate> {
	
	public Integer order;
	
	// for layz processing
	public boolean isProcessed;

	public abstract String certificateReport();
	
	public abstract Certificate processCertificateBytes();
	
	@Override
	public int compareTo(Certificate c) {
		return order.compareTo(c.order);
	}
}
