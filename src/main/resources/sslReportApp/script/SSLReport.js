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
			"dataType" : "json"
		}).done(function(data) {
			sslRportInstance.showReport(data);
		});
	};

	this.showReport = function(data) {
		this.showReportData(data);
		this._setViewStateReport();
	};

	this.showInputPanel = function() {
		this.clearOutput();
		this._setViewStateInput();
	};

	this.initViewState = function() {
		this.reportOutputArea = $("#reportOutputArea");
		this.reportBtnGrp = $("#reportButtonGroup");
		this.inputPanel = $("#inputPanel");

		this.reportOutputArea.hide();
		this.reportBtnGrp.hide();
	};

	this._setViewStateInput = function() {
		this.inputPanel.show(1500);
		this.reportBtnGrp.hide(100);
		this.reportOutputArea.hide(1500);
	};

	this._setViewStateReport = function() {
		this.inputPanel.hide(1500);
		this.reportBtnGrp.show(100);
		this.reportOutputArea.show(1500);
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
		var cipherSuites = reportOutput.find("#cipherSuites");

		for ( var sslVersion in report.cipherSuites) {
			if (report.cipherSuites.hasOwnProperty(sslVersion)) {
				var cipherList = report.cipherSuites[sslVersion];

				var cipherSuiteTemplate = $("#cipherSuiteTemplate").html();
				cipherSuites.after(cipherSuiteTemplate);

				var cipherSuite = $("#cipherSuite").first();
				cipherSuite.find("#cipherSuiteHeader").text(sslVersion);
				var cipherSuiteList = cipherSuite.find(".list-group");

				for (var i = 0; i < cipherList.length; i++) {
					cipherSuiteList.append("<li class=\"list-group-item\">"
							+ cipherList[i] + "</li>");
				}
			}
		}

		// certificates
		var certifiactes = reportOutput.find("#certificates");

		for ( var sslVersion in report.certificates) {
			if (report.certificates.hasOwnProperty(sslVersion)) {
				var certificateList = report.certificates[sslVersion];

				var certificateTemplate = $("#certificateTemplate").html();
				certifiactes.after(certificateTemplate);

				var certificate = $("#certificate").first();
				certificate.find("#certificateHeader").text(sslVersion);

				for (var i = 0; i < certificateList.length; i++) {
					var certificateData = certificateList[i];
					var certificateFormTemplate = $("#certificateFormTemplate")
							.html();
					certificate.append(certificateFormTemplate);

					var panelHeader = certificate.find(".panel-heading").last()
							.text("#" + certificateData.order);
					var certificateForm = certificate.find("form").last();

					certificateForm.find("#out_order").text(
							certificateData.order);
					certificateForm.find("#out_version").text(
							certificateData.version);
					certificateForm.find("#out_subjectName").text(
							certificateData.subjectName);
					certificateForm.find("#out_alternativeNames").text(
							certificateData.alternativeNames);
					certificateForm.find("#out_notBefore").text(
							certificateData.notBefore);
					certificateForm.find("#out_notAfter").text(
							certificateData.notAfter);

					var key = certificateData.pubKeyName;
					if (certificateData.pubKeySize !== null) {
						key += " (" + certificateData.pubKeySize + " bit)"
					}

					certificateForm.find("#out_key").text(key);
					certificateForm.find("#out_issuer").text(
							certificateData.issuerName);
					certificateForm.find("#out_sigAlgo").text(
							certificateData.signatureAlgorithm);
					certificateForm.find("#out_fingerprint").text(
							certificateData.fingerprint);
					certificateForm.find("#out_crl").text(
							certificateData.crlDistributionPoints);

				}
			}
		}
	};

	this.clearOutput = function() {
		$("div").filter("#reportOutput").each(function(index) {
			$(this).remove();
		});
	};
}