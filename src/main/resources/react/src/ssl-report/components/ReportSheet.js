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

import ReportPanel from './ReportPanel';

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
                    <span className="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                    Back
                </button>
            </div>
        </div>);

        if (sslReports.length > 0) {
            console.log("Start display reports...");

            return (<React.Fragment>
                {backButton}
                <React.Fragment>{sslReports.map((report) => <ReportPanel key={report.key} report={report}/>)}</React.Fragment>
            </React.Fragment>);
        }

        return backButton;
    }
}

ReportSheet.propTypes = {
    sslReports: PropTypes.array.isRequired,
    newReport: PropTypes.func.isRequired
};