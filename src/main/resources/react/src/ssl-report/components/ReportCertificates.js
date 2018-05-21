import React from 'react';
import PropTypes from 'prop-types';

export default function ReportCertifictaes({certificates}) {
    const certificateSections = [];

    certificates.map((certificateObject)=> {
        console.log("rendeering " + certificateObject.certificatesChain +
            " certificates for version " + certificateObject.version);


    });

    return (<table className="table table-bordered">
        <tbody>
        {certificateSections}
        </tbody>
    </table>);
}

ReportCertifictaes.propTypes = {
    certificates: PropTypes.array.isRequired
};

function CertificateRow({version, certificateChain}) {
    const certificateRows = [];

    certificateChain.map((certificate)=>{

    });

    return null;
}

CertificateRow.propTypes = {
    version: PropTypes.string.isRequired,
    certificateChain: PropTypes.array.isRequired
};

function CertificateDetails({version, certificate}) {
    const row = (<tr>
        <td></td>
    </tr>);
}

