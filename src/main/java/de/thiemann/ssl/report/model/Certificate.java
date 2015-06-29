package de.thiemann.ssl.report.model;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public abstract class Certificate implements Comparable<Certificate> {
	
	public Integer order;

	public abstract String certificateReport();
	
	@Override
	public int compareTo(Certificate c) {
		return order.compareTo(c.order);
	}
}
