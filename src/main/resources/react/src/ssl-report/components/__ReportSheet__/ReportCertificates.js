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
import PropTypes from 'prop-types';

export default function ReportCertificates({certificates}) {
    const certificateSections = [];

    certificates.map((certificateObject)=> {
        console.log("rendeering " + certificateObject.certificatesChain.length +
            " certificates for version " + certificateObject.version);

        certificateSections.push(<CertificateRows version={certificateObject.version}
                                                  certificateChain={certificateObject.certificatesChain}
                                                  key={certificateObject.uiKey}/>);
    });

    return (<table className="table table-bordered">
        <tbody>
        {certificateSections}
        </tbody>
    </table>);
}

ReportCertificates.propTypes = {
    certificates: PropTypes.array.isRequired
};

function CertificateRows({version, certificateChain}) {
    const certificateDetailRows = [];

    certificateChain.map((certificate)=>{
        certificateDetailRows.push(<CertificateDetailRow key={certificate.uiKey} version={version} certificate={certificate} />);
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
