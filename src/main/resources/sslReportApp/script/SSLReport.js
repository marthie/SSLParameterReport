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

function SSLReport() {

	this.slidePanel = new SlidePanel();

	// functions

	this.getReportData = function() {
		var sslRportInstance = this;
		var requestData = {};

		requestData.host = $("#host").val();
		requestData.port = $("#port").val();

		$.ajax({
			"url" : "/service/sslReport",
			"type" : "POST",
			"data" : requestData,
			"dataType" : "json",
			"beforeSend" : function() {
				sslRportInstance._loadingAlert(sslRportInstance.alertOutput);
				sslRportInstance._setViewState2();
			}
		}).done(function(data) {
			sslRportInstance.showReport(data);
		});
	};

	this.showReport = function(data) {
		this.showReportData(data);
		this._setViewState3();
	};

	this.showInputPanel = function() {
		this.clearOutput();
		this._setViewState1();
	};
	
	// one page application - states init
	this.initViewState = function() {
		this.reportOutputArea = $("#reportOutputArea");
		this.reportBtnGrp = $("#reportButtonGroup");
		this.inputPanel = $("#inputPanel");
		this.alertOutput = $("#alert");

		this.reportOutputArea.hide();
		this.reportBtnGrp.hide();
		this.alertOutput.hide();
	};

	// view state #1 - the input of host and port
	this._setViewState1 = function() {
		this.reportBtnGrp.hide(100);
		this.reportOutputArea.hide(100);
		
		this.inputPanel.show(1500);
	};
	
	// view state #2 - loading report
	this._setViewState2 = function() {
		this.inputPanel.hide(100);
		
		this.alertOutput.show(1500);
	};

	// view state #3 - show report
	this._setViewState3 = function() {
		this.alertOutput.hide(100);
		
		this.reportBtnGrp.show(100);
		this.reportOutputArea.show(1500);
	};
	
	this._loadingAlert = function(alertOutput) {
		this._clearAlertOutput(alertOutput);
		
		alertOutput.attr("class", "alert alert-info");
		alertOutput.append("<img src=\"/pics/ajax-loader.gif\"></img>");
		alertOutput.append("<strong> Loading...</strong>");
	};

	this.showReportData = function(data) {
		if ($.isArray(data) && data.length > 0) {
			for (var i = 0; i < data.length; i++) {
				this._addDataToDOM(data[i]);
			}
		} else
			this._addDataToDOM(data);

	};

	this._addDataToDOM = function(report) {
		// create Template
		var template = $("#reportTemplate").html();

		this.reportOutputArea.append(template);
		var reportOutput = this.reportOutputArea.find("#reportOutput").last();

		// set css to reportOutput
		reportOutput.css("margin-top", "10px");

		// set title
		reportOutput.find(".panel-title").text(report.ipAddress);

		// make panel slidable
		this.slidePanel.makeSlidable(reportOutput);

		// common informations
		var ciPart = reportOutput.find("#commonInformation");
		ciPart.find("#out_from").text(report.createdOn);
		ciPart.find("#out_host").text(report.host);
		ciPart.find("#out_ip").text(report.ipAddress);
		ciPart.find("#out_port").text(report.port);

		// protocol
		var protPart = reportOutput.find("#protocolInformation");
		protPart.find("#out_supportedVersions").text(
				report.supportedSSLVersions.join());
		protPart.find("#out_compress").text(report.compress ? "Yes" : "No");

		// cipher suites
		var cipherSuitesHeader = reportOutput.find("#cipherSuites");
		this._addCipherSuitesToDOM(cipherSuitesHeader, report.cipherSuites);

		// certificates
		var certifiactesHeader = reportOutput.find("#certificates");
		this._addCertificatesToDOM(certifiactesHeader, report.certificates)
	};

	this._addCertificatesToDOM = function(header, certificates) {
		var certificateTemplate = $("#certificateTemplate2").html();
		header.after(certificateTemplate);
		
		var certificateDiv = header.next();
		var tbody = certificateDiv.find("tbody");
		
		for (var sslVersion in certificates) {
			if (certificates.hasOwnProperty(sslVersion)) {			
				var versionCertificates = certificates[sslVersion];
		
				if ($.isArray(versionCertificates)
						&& versionCertificates.length > 0) {
					for(var i = 0; i < versionCertificates.length; i++) {
						var certificateData = versionCertificates[i];
						tbody.append("<tr><th scope=\"row\" rowspan=\"11\">" + sslVersion + "</th><th scope=\"row\">Send in order:</th><td>" + certificateData.order + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Certificate Version:</th><td>" + certificateData.version + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Subject:</th><td>" + certificateData.subjectName + "</td></tr>");
						
						var alternativeNames = new String();
						if(certificateData.alternativeNames) {
							for(var j = 0; j < certificateData.alternativeNames.length; j++) {
								alternativeNames += "<p>" + certificateData.alternativeNames[j] + "</p>";
							}
						}
						
						tbody.append("<tr><th scope=\"row\">Alternative Names:</th><td>" + alternativeNames + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Not Before:</th><td>" + certificateData.notBefore + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Not After:</th><td>" + certificateData.notAfter + "</td></tr>");
						
						var key = certificateData.pubKeyName;
						if (certificateData.pubKeySize !== null) {
							key += " (" + certificateData.pubKeySize + " bit)"
						}
						
						tbody.append("<tr><th scope=\"row\">Key:</th><td>" + key + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Issuer:</th><td>" + certificateData.issuerName + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Signature Algorithm:</th><td>" + certificateData.signatureAlgorithm + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">Fingerprint:</th><td>" + certificateData.fingerprint + "</td></tr>");
						tbody.append("<tr><th scope=\"row\">CRL Distribution Points:</th><td>" + certificateData.crlDistributionPoints + "</td></tr>");
					}
				}
			}
		}
	};

	this._addCipherSuitesToDOM = function(header, cipherSuites) {

		var cipherSuiteTemplate = $("#cipherSuiteTemplate").html();
		header.after(cipherSuiteTemplate);

		var cipherSuiteDiv = header.next();
		var tbody = cipherSuiteDiv.find("tbody");

		for (var sslVersion in cipherSuites) {
			if (cipherSuites.hasOwnProperty(sslVersion)) {
				tbody.append("<tr><th scope=\"row\">" + sslVersion
						+ "</th><td></td></tr>");
				var tr = tbody.children("tr").last();
				var td = tr.children().last();

				var versionCipherSuites = cipherSuites[sslVersion];
				if ($.isArray(versionCipherSuites)
						&& versionCipherSuites.length > 0) {
					var htmlText = new String();

					for (var i = 0; i < versionCipherSuites.length; i++) {
						htmlText += "<p>" + versionCipherSuites[i] + "</p>";
					}

					td.append(htmlText);
				}
			}
		}
	};

	this.clearOutput = function() {
		$("div").filter("#reportOutput").each(function(index) {
			$(this).remove();
		});
	};
	
	this._clearAlertOutput = function(alertOutput) {
		alertOutput.removeAttr("class");
		alertOutput.children("*").each(function(index) {
			$(this).remove();
		});
	};
}