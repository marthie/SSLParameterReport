package de.thiemann.ssl.report.build;

import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
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
