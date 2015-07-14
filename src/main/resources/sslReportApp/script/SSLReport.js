/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

function SSLReport() {
	
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
		this.fillForms(data);
		this._setViewStateReport();
	};
	
	this.showInputPanel = function() {
		this.clearOutput();
		this._setViewStateInput();
	};
	
	this.initViewState = function() {
		this.reportOutput = $("#reportOutput");
		this.reportBtnGrp = $("#reportButtonGroup");
		this.inputPanel = $("#inputPanel");
		
		this.reportOutput.hide();
		this.reportBtnGrp.hide();
	};
	
	this._setViewStateInput = function() {
		this.inputPanel.show(1500);
		this.reportBtnGrp.hide(100);
		this.reportOutput.hide(1500);
	};
	
	this._setViewStateReport = function() {
		this.inputPanel.hide(1500);
		this.reportBtnGrp.show(100);
		this.reportOutput.show(1500);
	};

	this.fillForms = function(data) {
		var report = null;
		
		if($.isArray(data) && data.length > 0)
			report = data[0];
		else
			report = data;

		// common informations
		$("#out_from").text(report.createdOn);
		$("#out_host").text(report.host);
		$("#out_ip").text(report.ipAddress);
		$("#out_port").text(report.port);

		// protocol
		$("#out_supportedVersions").text(report.supportedSSLVersions.join());
		$("#out_compress").text(report.compress ? "Yes" : "No");

		// cipher suites
		var cipherSuites = $("#cipherSuites");

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
		var certifiactes = $("#certificates");

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

					var panelHeader = certificate.find(".panel-heading").last().text("#" + certificateData.order);
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
		$("div").filter("#certificate, #cipherSuite").each(function(index) {
			$(this).remove();
		});
	};
}