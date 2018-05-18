import $ from 'jquery';

export function fetchSSLReport(requestData) {
    console.log("entry fetchSSLReport()...");

    const settings = {
        method: 'POST',
        data: JSON.stringify(requestData),
        contentType: 'application/json'
    };

    const url = '/service/sslReport';

    return $.ajax(url, settings);
}