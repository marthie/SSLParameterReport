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
import PropTypes from "prop-types";

import CertificateDetail  from './CertificateDetail';

export default class Certificate extends React.Component {

    constructor(props) {
        super(props);
    }

    getDetail() {
        const {certificate} = this.props;

        const detailRow = (<React.Fragment>
            <CertificateDetail label="Certificate Version:" value={`${certificate.version}`} />
            <CertificateDetail label="Subject" value={certificate.subjectName} />
            <CertificateDetail label="Issuer:" value={certificate.issuerName} />
            <CertificateDetail label="Alternative Names:" value={certificate.alternativeNames} />
            <CertificateDetail label="Not Before:" value={certificate.notBefore}/>
            <CertificateDetail label="Not After:" value={certificate.notAfter}/>
            <CertificateDetail label="Public Key Algorithm:" value={certificate.pubKeyName}/>
            <CertificateDetail label="Public Key Size:" value={certificate.pubKeySize}/>
            <CertificateDetail label="Signature Algorithm:" value={certificate.signatureAlgorithm}/>
            <CertificateDetail label="Fingerprint:" value={certificate.fingerprint}/>
            <CertificateDetail label="CRL Distribution Points:" value={certificate.crlDistributionPoints}/>
        </React.Fragment>);

        console.log(React.Children.count(detailRow.props.children));

        return detailRow;
    }

    render() {
        const {version, certificate} = this.props;

        const certificateDetails = this.getDetail();
        var count = React.Children.count(certificateDetails.props.children);

        console.log("children count: " + count);

        const row = (<React.Fragment>
            <tr>
                <th scope="row" rowSpan={count + 1}>{version}</th>
                <th scope="row">Send in order:</th>
                <td>{certificate.order}</td>
            </tr>
            {this.getDetail()}
        </React.Fragment>);

        return row;
    }
}

Certificate.propTypes = {
    version: PropTypes.string.isRequired,
    certificate: PropTypes.object.isRequired
};