/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */
$(document).ready(function() {

	var sslReport = new SSLReport();
	sslReport.initViewState();
	

	$("form").submit(function(e) {
		e.preventDefault();
	});

	$("#getReport").on("click", function() {
		sslReport.getReportData();
	});
	
	$("#newReport").on("click", function() {
		sslReport.showInputPanel();
	});
});