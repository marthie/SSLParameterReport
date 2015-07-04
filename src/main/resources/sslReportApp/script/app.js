/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */
$(document).ready(function() {
	
	$("form").submit(function(e) {
		e.preventDefault();
	});
	
	$("#getReport").on("click", function() {
		var webName = $("#webName").val();
		var port = $("#port").val();
		
		var data = {};
		
		if(webName != null)
			data.webName = webName;
		
		if(port != null)
			data.port = port;
		
		$.ajax({
			"url" : "/service/sslReport",
			"type" : "POST",
			"data" : data,
			"dataType" : "json",		
		}).done(function(data, textStatus) {
			
			$("#out_from").text(data.createdOn);
			$("#out_webName").text(data.webName);
			$("#out_ip").text(data.ipAddress);
			$("#out_port").text(data.port);
			
			$("#out_supportedVersions").text(data.supportedSSLVersions.join());
			$("#out_compress").text(data.compress ? "Yes" : "No");
			
			var cipherSuites = $("#cipherSuites");
			
			for (var sslVersion in data.cipherSuites) {
			   if (data.cipherSuites.hasOwnProperty(sslVersion)) {
			       var cipherList = data.cipherSuites[sslVersion];
			       
			       var cipherSuiteTemplate = $("#cipherSuiteTemplate").html();
			       cipherSuites.after(cipherSuiteTemplate);
			       
			       var cipherSuiteVersion = $("#cipherSuite").first();
			       cipherSuiteVersion.find("#cipherSuiteHeader").text(sslVersion);
			       var cipherSuiteList = cipherSuiteVersion.find(".list-group");
			       
			       for(var i = 0; i < cipherList.length; i++) {
			    	   cipherSuiteList.append("<li class=\"list-group-item\">" + cipherList[i] + "</li>");
			       }
			    }
			}
			
			$("#reportOutput").removeAttr("class");
		});
	});
});