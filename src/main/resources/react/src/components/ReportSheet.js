import React from 'react';
import PropTypes from 'prop-types';

export default function ReportSheet(props) {
    const {sslReports} = props;

    if(sslReports.length > 0) {
        return (<div>
            {sslReports.forEach((report) => console.log(report.ipAddress))}
        </div>);
    }

    return null;
}

ReportSheet.propTypes = {
    sslReports: PropTypes.array.isRequired
};