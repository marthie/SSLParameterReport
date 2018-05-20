import React from 'react';

import ReportTable from './ReportTable';

export default class ReportPanel extends React.Component {

    constructor(props) {
        super(props);
    }

    reportPanel() {
        const {report} = this.props;

        const panelTitle = `${report.ipAddress}:${report.port}`;

        const panel = (<div className="row">
            <div className="col-xs-12">
            <div className="panel panel-default" style={{marginTop: 10 + 'px'}}>
                <div className="panel-heading">
                    <h3 className="panel-title">{panelTitle}</h3>
                </div>
                <div className="panel-body">
                    <ReportTable {...this.props} />
                </div>
            </div>
            </div>
        </div>);

        return panel;
    }

    render() {
        return this.reportPanel();
    }
}