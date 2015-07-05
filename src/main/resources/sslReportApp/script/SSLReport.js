/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

function SSLReport() {

	this.getReportData = function(webName, port) {
		var data = {};

		if (webName != null)
			data.webName = webName;

		if (port != null)
			data.port = port;

		$.ajax({
			"url" : "/service/sslReport",
			"type" : "POST",
			"data" : data,
			"dataType" : "json",
		}).done(this.fillForms);
	};

	this.fillForms = function(data) {

		// common informations
		$("#out_from").text(data.createdOn);
		$("#out_webName").text(data.webName);
		$("#out_ip").text(data.ipAddress);
		$("#out_port").text(data.port);

		// protocol
		$("#out_supportedVersions").text(data.supportedSSLVersions.join());
		$("#out_compress").text(data.compress ? "Yes" : "No");

		// cipher suites
		var cipherSuites = $("#cipherSuites");

		for ( var sslVersion in data.cipherSuites) {
			if (data.cipherSuites.hasOwnProperty(sslVersion)) {
				var cipherList = data.cipherSuites[sslVersion];

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

		for ( var sslVersion in data.certificates) {
			if (data.certificates.hasOwnProperty(sslVersion)) {
				var certificateList = data.certificates[sslVersion];

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

		// remove hidden
		$("#reportOutput").removeAttr("class");
	};

	this.clearOutput = function() {
		$("#reportOutput").attr("class", "hidden");

		$("div").filter("#certificate, #cipherSuite").each(function(index) {
			$(this).remove();
		});
	};
}