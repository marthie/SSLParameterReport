import React from 'react';
import ReportController from './ReportController';

export default function Layout() {

    const layout = (
        <div className="container">
            <div className="row">
                <div className="page-header">
                    <h1>SSL Report</h1>
                </div>
            </div>
            <ReportController />
        </div>
    );

    return layout;
}