package de.thiemann.ssl.report.output;


/*
 * ----------------------------------------------------------------------
 * Copyright (c) 2012  Thomas Pornin <pornin@bolet.org>
 * Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ----------------------------------------------------------------------
 */

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractOutput implements Output {

	public String versionString(int version) {
		if (version == 0x0200) {
			return "SSLv2";
		} else if (version == 0x0300) {
			return "SSLv3";
		} else if ((version >>> 8) == 0x03) {
			return "TLSv1." + ((version & 0xFF) - 1);
		} else {
			return String.format("UNKNOWN_VERSION:0x%04X", version);
		}
	}

	public static Map.Entry<String, Object> entry(String key, Object value) {
		return new AbstractMap.SimpleEntry(key, value);
	}

	public static Map.Entry<String, Object> keyEntry() {
		return entry("key", UUID.randomUUID().toString());
	}

	public static Map<String, Object> map(Map.Entry<String, Object>...entries) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (Map.Entry<String, Object> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}

		return map;
	}
}
