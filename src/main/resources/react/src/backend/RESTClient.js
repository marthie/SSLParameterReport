import $ from 'jquery';

export function fetchSSLReport(host, port) {
    console.log("entry fetchSSLReport...");

    const request = {
        host: host,
        port: port
    };

    const settings = {
        method: 'POST',
        data: JSON.stringify(request),
        contentType: 'application/json'
    };

    const url = '/service/sslReport';

    return $.ajax(url, settings);
}