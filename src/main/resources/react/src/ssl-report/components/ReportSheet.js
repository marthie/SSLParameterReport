import React from 'react';
import PropTypes from 'prop-types';

import ReportTable from './ReportTable';

export default class ReportSheet extends React.Component {

    constructor(props) {
        super(props);
    }

    getBack(event) {
        console.log("pushed back to form...");
        this.props.newReport();
    }

    reportObjectToArray({sslReports}) {
        var arraySSLReports;

        if(!Array.isArray(sslReports)) {
            console.log("Transfer single report to array...");
            arraySSLReports = new Array( sslReports );
        } else {
            arraySSLReports = sslReports;
        }

        return arraySSLReports;
    }

    render() {
        const sslReports = this.reportObjectToArray(this.props);

        console.log(`SSL\TLS report count: ${sslReports.length}`);

        const backButton = (<div className="row">
            <div className="col-xs-12">
                <button className="btn btn-default"
                        onClick={(e)=>this.getBack()}>
                    <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                    Back
                </button>
            </div>
        </div>);

        if (sslReports.length > 0) {
            console.log("Start display reports...");

            return (<div>
                {backButton}
                <div>{sslReports.map((report) => <ReportTable key={report.reportId} report={report}/>)}</div>
            </div>);
        }

        return backButton;
    }
}

ReportSheet.propTypes = {
    sslReports: PropTypes.array.isRequired,
    newReport: PropTypes.func.isRequired
};