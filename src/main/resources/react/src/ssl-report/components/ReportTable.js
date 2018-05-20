import React from 'react';
import PropTypes from 'prop-types';

import ReportCipherSuites from './ReportCipherSuites';

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