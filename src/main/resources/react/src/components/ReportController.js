import React from 'react';
import ReportForm from './ReportForm';

export default class ReportController extends React.Component {

    constructor() {
        super();
        this.state = {
            isActiveForm : true
        };

        this.fetchReport = this.fetchReport.bind(this);
    }

    fetchReport(host, port) {
        console.log(`${host}:${port}`);

        //this.setState({ isActiveForm : false });
    }

    render() {
        const reportForm = <ReportForm
            isActiveForm={this.state.isActiveForm}
            fetchReport={this.fetchReport} />;

        return (<div>{reportForm}</div>);
    }
};