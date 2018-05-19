import React from 'react';
import PropTypes from 'prop-types';

export default class ReportTable extends React.Component {

    constructor(props) {
        super(props);
    }

    reportTable() {
        const {report} = this.props;

        console.log(`Start render report: ${report.reportId}`);

        const reportTable = (<div>
            <div>
                <div>
                    <div>Report created on</div>
                    <div>{report.createdOn}</div>
                </div>
                <div>
                    <div>Host</div>
                    <div>{report.host}</div>
                </div>
                <div>
                    <div>IP Address</div>
                    <div>{report.ipAddress}</div>
                </div>
                <div>
                    <div>Port</div>
                    <div>{report.port}</div>
                </div>
                <div>
                    <div>Compressed</div>
                    <div>{report.compress ? 'Yes' : 'No'}</div>
                </div>
                <div>
                    <div>Supported SSL/TLS Versions</div>
                    <div>{this.buildVersionString()}</div>
                </div>
            </div>
            <CipherSuites cipherSuites={report.cipherSuites} />
        </div>);

        return reportTable;
    }

    buildVersionString() {
        const {report} = this.props;

        return report.supportedSSLVersions.reduce((acc, value) => acc + ', ' + value);
    }

    render() {
        return this.reportTable();
    }
}

function CipherSuites({cipherSuites}) {
    const allCipherSuites = [];

    for(var sslVersion in cipherSuites) {
        if(cipherSuites.hasOwnProperty(sslVersion)) {
            console.log(sslVersion + " " + cipherSuites[sslVersion]);

            var cipherSuitesBySSLVersion = cipherSuites[sslVersion];

            var cipherSuiteComponents = [];

            cipherSuitesBySSLVersion.forEach((element)=>cipherSuiteComponents.push((<p>{element}</p>)));

            var bySSLVersionComponent = (<div>
                <div>{sslVersion}</div>
                <div>{cipherSuiteComponents}</div>
            </div>);

            allCipherSuites.push(bySSLVersionComponent);
        }
    }

    return (<div>{allCipherSuites}</div>);
}

ReportTable.propTypes = {
    report: PropTypes.object.isRequired
};