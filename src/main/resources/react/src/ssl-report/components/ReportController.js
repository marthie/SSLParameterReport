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
import ReportForm from './ReportForm';
import ReportSheet from './ReportSheet';
import ReportLoader from './ReportLoader';
import {fetchSSLReport} from '../backend/RESTClient';

export const STATES = {
    form: 1,
    loader: 2,
    sheet: 3
};


export default class ReportController extends React.Component {

    constructor() {
        super();

        this.state = {
            activeState: STATES.form,
            sslReports: []
        };

        this.fetchReport = this.fetchReport.bind(this);
        this.submitData = this.submitData.bind(this);
        this.newReport = this.newReport.bind(this);
    }

    submitData(host, port) {
        console.log(`submitData(${host}, ${port})`);

        const requestData = {
            host: host,
            port: port
        };

        this.setState({
            ...this.state,
            activeState: STATES.loader,
            requestData: requestData
        });
    }

    fetchReport() {
        console.log(`fetchReport()`);

        const {requestData} = this.state;

        fetchSSLReport(requestData).done((data)=> {
            this.setState({
                activeState: STATES.sheet,
                sslReports: data
            });
        });
    }

    newReport() {
        console.log("new report...");

        this.setState({
            activeState: STATES.form
        });
    }

    formView() {
        const reportForm = <ReportForm submitData={this.submitData} />;

        return reportForm;
    }

    loaderView() {
        const  reportLoader = <ReportLoader fetchReport={this.fetchReport}/>;

        return reportLoader;
    }


    sheetView() {
        const reportSheet = <ReportSheet sslReports={this.state.sslReports} newReport={this.newReport} />;

        return reportSheet;
    }

    render() {
        var currentView = null;

        switch(this.state.activeState) {
            case STATES.form:
                currentView = this.formView();
                break;
            case STATES.loader:
                currentView = this.loaderView();
                break;
            case STATES.sheet:
                currentView = this.sheetView();
                break;
            default: {
                currentView = (<p className="text-danger">`Error: ${this.state.activeState} unknown...`</p>);
            }
        }

        return currentView;
    }
};