import React from 'react';
import ReportForm from './ReportForm';
import ReportSheet from './ReportSheet';
import {fetchSSLReport} from '../backend/RESTClient';

export default class ReportController extends React.Component {

    constructor() {
        super();

        this.state = {
            isActiveForm : true,
            sslReports: []
        };

        this.fetchReport = this.fetchReport.bind(this);
    }

    fetchReport(host, port) {
        console.log(`fetchReport(${host}, ${port})`);

        fetchSSLReport(host, port).done((data)=> {
            this.setState({
                isActiveForm : false,
                sslReports: data
            });
        });
    }

    render() {
        const reportForm = <ReportForm
            isActiveForm={this.state.isActiveForm}
            fetchReport={this.fetchReport} />;

        const reportSheet = <ReportSheet
            sslReports={this.state.sslReports} />;

        return (<div>
            <div>{reportForm}</div>
            <div>{reportSheet}</div>
        </div>);
    }
};