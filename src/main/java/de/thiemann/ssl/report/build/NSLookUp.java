package de.thiemann.ssl.report.build;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Singleton;

@Singleton
public class NSLookUp {

	
	public NSLookUp() {
	}
	
	public InetAddress[] getAllByName(String host) {
		try {
			return nslookupByNativJava(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	private InetAddress[] nslookupByDNSJava(String host) throws UnknownHostException {
		return Address.getAllByName(host);
	}
	*/

	private InetAddress[] nslookupByNativJava(String host) throws UnknownHostException {
		return InetAddress.getAllByName(host);
	}
}
