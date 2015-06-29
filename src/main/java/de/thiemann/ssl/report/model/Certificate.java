package de.thiemann.ssl.report.model;

public abstract class Certificate implements Comparable<Certificate> {
	
	public Integer order;

	public abstract String certificateReport();
	
	@Override
	public int compareTo(Certificate c) {
		return order.compareTo(c.order);
	}
}
