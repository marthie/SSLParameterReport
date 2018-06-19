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

import ReportPanel from './__ReportSheet__/ReportPanel';
import ReportTable from './__ReportSheet__/ReportTable';
import ReportJump from './__ReportSheet__/ReportJump';

class ReportSheet extends React.Component {

    constructor(props) {
        super(props);
    }

    getBack(event) {
        console.log("pushed back to form...");

        const {newReport} = this.props;
        newReport();
    }

    reportObjectToArray({sslReports}) {
        var arraySSLReports;

        if (!Array.isArray(sslReports)) {
            console.log("Transfer single report to array...");
            arraySSLReports = new Array(sslReports);
        } else {
            arraySSLReports = sslReports;
        }

        return arraySSLReports;
    }

    backButton() {
        const backButton = (<React.Fragment>
            <ReportJump/>
            <div className="row">
                <div className="col-xs-12">
                    <button className="btn btn-default"
                            onClick={(e) => this.getBack()}>
                        <span className="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                        Back
                    </button>
                </div>
            </div>
        </React.Fragment>);

        return backButton;
    }

    render() {
        const {reportState} = this.props;

        const sslReports = this.reportObjectToArray(reportState);

        console.log(`SSL\TLS report count: ${sslReports.length}`);

        if (sslReports.length > 0) {
            console.log("Start display reports...");

            return (<React.Fragment>
                {this.backButton()}
                <React.Fragment>
                    {sslReports.map((report) => {
                            const title = `${report.ipAddress}:${report.port}`;
                            console.log("rendering report for" + title);

                            return (<ReportPanel key={'panel-' + report.uiKey} panelTitle={title}>
                                <ReportTable key={'table-' + report.uiKey} report={report}/>
                            </ReportPanel>);
                        }
                    )}
                </React.Fragment>
            </React.Fragment>);
        }

        return this.backButton();
    }
}

import {connect} from 'react-redux';
import {newReport} from '../actions/reportActions'

function mapStateToProps(state) {
    return {
        reportState: state
    };
}

function mapDispatchToProps(dispatch) {
    return {
        newReport: () => dispatch(newReport())
    };
}

export default connect(mapStateToProps, mapDispatchToProps) (ReportSheet);