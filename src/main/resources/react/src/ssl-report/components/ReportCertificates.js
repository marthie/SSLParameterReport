import React from 'react';
import PropTypes from 'prop-types';

export default function ReportCertifictaes({certificates}) {
    const certificateSections = [];

    certificates.map((certificateObject)=> {
        console.log("rendeering " + certificateObject.certificatesChain.length +
            " certificates for version " + certificateObject.version);

        certificateSections.push(<CertificateRows version={certificateObject.version}
                                                  certificateChain={certificateObject.certificatesChain} />);
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

function CertificateRows({version, certificateChain}) {
    const certificateDetailRows = [];

    certificateChain.map((certificate)=>{
        certificateDetailRows.push(<CertificateDetailRow version={version} certificate={certificate} />);
    });

    return (<React.Fragment>{certificateDetailRows}</React.Fragment>);
}

CertificateRows.propTypes = {
    version: PropTypes.string.isRequired,
    certificateChain: PropTypes.array.isRequired
};

function CertificateDetailRow({version, certificate}) {
    const row = (<React.Fragment>
        <tr>
            <th scope="row" rowSpan="12">{version}</th>
            <th scope="row">Send in order:</th>
            <td>{certificate.order}</td>
        </tr>
        <tr>
            <th scope="row">Certificate Version:</th>
            <td>{certificate.version}</td>
        </tr>
        <tr>
            <th scope="row">Subject:</th>
            <td>{certificate.subjectName}</td>
        </tr>
        <tr>
            <th scope="row">Issuer:</th>
            <td>{certificate.issuerName}</td>
        </tr>
        <tr>
            <th scope="row">Alternative Names:</th>
            <td>{certificate.alternativeNames}</td>
        </tr>
        <tr>
            <th scope="row">Not Before:</th>
            <td>{certificate.notBefore}</td>
        </tr>
        <tr>
            <th scope="row">Not After:</th>
            <td>{certificate.notAfter}</td>
        </tr>
        <tr>
            <th scope="row">Public Key Algorithm:</th>
            <td>{certificate.pubKeyName}</td>
        </tr>
        <tr>
            <th scope="row">Public Key Size:</th>
            <td>{certificate.pubKeySize}</td>
        </tr>
        <tr>
            <th scope="row">Signature Algorithm:</th>
            <td>{certificate.signatureAlgorithm}</td>
        </tr>
        <tr>
            <th scope="row">Fingerprint:</th>
            <td>{certificate.fingerprint}</td>
        </tr>
        <tr>
            <th scope="row">CRL Distribution Points:</th>
            <td>{certificate.crlDistributionPoints}</td>
        </tr>
    </React.Fragment>);

    return row;
}

CertificateDetailRow.propTypes = {
    version: PropTypes.string.isRequired,
    certificate: PropTypes.object.isRequired
};
