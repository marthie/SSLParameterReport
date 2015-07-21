package de.thiemann.ssl.report.build;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.xbill.DNS.Address;

import com.google.inject.Singleton;

@Singleton
public class XBillLookup implements Lookup {

	@Override
	public InetAddress[] getAllByName(String host) {	
		try {
			return Address.getAllByName(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
