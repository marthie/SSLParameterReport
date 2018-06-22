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
import CertificateExtensionDetail from './CertificateExtensionDetails'

import * as uuid from 'uuid/v4';

export default class Certificate extends React.Component {

    constructor(props) {
        super(props);
    }

    getDetails() {
        const {certificate} = this.props;

        let displayDetailsInOrder = [];
        let element = null;

        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Certificate Version:" value={`${certificate.version}`} />));
        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Certificate Serial Number:" value={`${certificate.serialNumber}`} />));
        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Subject" value={certificate.subjectName} />));

        element = this.renderArray("Subject Alternative Names:", certificate.subjectAlternativeNames);
        if(element != null) { displayDetailsInOrder.push(element); }

        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Issuer:" value={certificate.issuerName} />));

        element = this.renderArray("Issuer Alternative Names:", certificate.issuerAlternativeNames);
        if(element != null) { displayDetailsInOrder.push(element); }

        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Not Before:" value={certificate.notBefore}/>));
        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Not After:" value={certificate.notAfter}/>));
        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Public Key Algorithm:" value={certificate.pubKeyName}/>));

        element = this.renderValue("Public Key Size:", certificate.pubKeySize);
        if(element != null) { displayDetailsInOrder.push(element); }

        element = this.renderArray("Key Usage:", certificate.keyUsageList);
        if(element != null) { displayDetailsInOrder.push(element); }

        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Signature Algorithm:" value={certificate.signatureAlgorithm}/>));
        displayDetailsInOrder.push((<CertificateDetail key={uuid.default()} label="Fingerprint:" value={certificate.fingerprint}/>));

        element = this.renderArray("CRL Distribution Points:", certificate.crlDistributionPoints);
        if(element != null) { displayDetailsInOrder.push(element); }

        displayDetailsInOrder.push((<CertificateExtensionDetail label="Included Certificate Extensions:" extensionList={certificate.extensionInfoList} />));

        const details = (<React.Fragment>{displayDetailsInOrder}</React.Fragment>);

        return details;
    }

    renderArray(label, values) {
        if(values && values.length && values.length > 0) {
            let stringValue = values.reduce((acc, cv) => acc + ", " + cv);
            return (<CertificateDetail key={uuid.default()} label={label} value={stringValue} />);
        } else {
            return null;
        }
    }

    renderValue(label, value) {
        if(value) {
            return (<CertificateDetail key={uuid.default()} label={label} value={value} />);
        } else {
            return null;
        }
    }

    render() {
        const {version, certificate} = this.props;

        const certificateDetails = this.getDetails();
        var count = React.Children.count(certificateDetails.props.children);

        console.log("children count: " + count);

        const row = (<React.Fragment>
            <tr>
                <th scope="row" rowSpan={count + 1}>{version}</th>
                <th scope="row">Send in order:</th>
                <td>{certificate.order}</td>
            </tr>
            {certificateDetails}
        </React.Fragment>);

        return row;
    }
}

Certificate.propTypes = {
    version: PropTypes.string.isRequired,
    certificate: PropTypes.object.isRequired
};