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

        return (<div>{reportForm}</div>);
    }

    loaderView() {
        const  reportLoader = <ReportLoader fetchReport={this.fetchReport}/>;

        return (<div>{reportLoader}</div>);
    }


    sheetView() {
        const reportSheet = <ReportSheet sslReports={this.state.sslReports} newReport={this.newReport} />;

        return (<div>{reportSheet}</div>);
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
            default:
                currentView = (<div>Error!</div>);
        }

        return currentView;
    }
};