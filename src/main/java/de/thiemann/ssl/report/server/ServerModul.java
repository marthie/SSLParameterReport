package de.thiemann.ssl.report.server;

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

import com.google.inject.AbstractModule;

import de.thiemann.ssl.report.build.Lookup;
import de.thiemann.ssl.report.build.XBillLookup;
import de.thiemann.ssl.report.output.JsonOutput;
import de.thiemann.ssl.report.output.Output;
import de.thiemann.ssl.report.server.cache.ReportCache;
import de.thiemann.ssl.report.server.cache.SimpleReportCache;
import de.thiemann.ssl.report.server.service.SSLReportService;

public class ServerModul extends AbstractModule {

	@Override
	protected void configure() {
		bind(SSLReportService.class);
		bind(Output.class).to(JsonOutput.class);
		bind(Lookup.class).to(XBillLookup.class);
		bind(ReportCache.class).to(SimpleReportCache.class);
	}

}
