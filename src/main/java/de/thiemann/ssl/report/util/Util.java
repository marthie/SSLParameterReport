package de.thiemann.ssl.report.util;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

public class Util {

	public static List<String> asList(String... args) {
		if(args == null)
			return null;
		
		List<String> list = new ArrayList<String>();
		for (String arg : args) {
			list.add(arg);
		}
		
		return list;
	}
}
