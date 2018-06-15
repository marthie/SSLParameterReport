/*

 The MIT License (MIT)

 Copyright (c) 2018 Marius Thiemann <marius dot thiemann at ploin dot de>

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

import React from 'react';
import PropTypes from 'prop-types';

import ReportCipherSuites from './ReportCipherSuites';
import ReportCertificates from './ReportCertificates';

export default class ReportTable extends React.Component {

    constructor(props) {
        super(props);
    }

    reportTable() {
        const {report} = this.props;

        console.log(`Start render report: ${report.key}`);

        const reportTable = (<React.Fragment>
            <div className="page-header">
                <h2>Common Information</h2>
            </div>

            <form className="form-horizontal">
                <div className="form-group">
                    <label htmlFor="report_created"
                           className="col-sm-4 control-label">Report created on:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_created">{report.createdOn}</p>
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="report_host"
                           className="col-sm-4 control-label">Host:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_host">{report.host}</p>
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="report_ip"
                           className="col-sm-4 control-label">IP-Address:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_ip">{report.ipAddress}</p>
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="report_port"
                        className="col-sm-4 control-label">Port:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_port">{report.port}</p>
                    </div>
                </div>
            </form>

            <div className="page-header">
                <h2>Protocol Information</h2>
            </div>

            <form className="form-horizontal">
                <div className="form-group">
                    <label htmlFor="report_supportedVersions"
                           className="col-sm-4 control-label">Supported protocol versions:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_supportedVersions">{this.buildVersionString()}</p>
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="report_deflate"
                           className="col-sm-4 control-label">Deflate compression support:</label>
                    <div className="col-sm-8">
                        <p className="form-control-static" id="report_deflate">{report.compress ? 'Yes' : 'No'}</p>
                    </div>
                </div>
            </form>

            <div className="page-header">
                <h2>Cipher Suites <small>order is not relevant</small></h2>
            </div>
            <ReportCipherSuites reportCipherSuites={report.cipherSuites}/>

            <div className="page-header">
                <h2>Certificates</h2>
            </div>
            <ReportCertificates certificates={report.certificates} />
        </React.Fragment>);

        return reportTable;
    }

    buildVersionString() {
        const {report} = this.props;

        return report.supportedSSLVersions.map((versionObject) => versionObject.version)
            .reduce((acc, value) => acc + ', ' + value);
    }

    render() {
        return this.reportTable();
    }
}

ReportTable.propTypes = {
    report: PropTypes.object.isRequired
};