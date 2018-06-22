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

export default function CertificateExtensionDetail({label, extensionList}) {

    const extensions =
        extensionList.map(
            (e) => <Extension key={e.uiKey} oid={e.oid} description={e.description} isCritical={e.isCritical} />
        );

    const detail = (<tr>
        <th scope="row">{label}</th>
        <td><ul className="list-group">
            {extensions}
        </ul></td>
    </tr>);

    return detail;
}

CertificateExtensionDetail.propTypes = {
    label: PropTypes.string.isRequired,
    extensionList: PropTypes.array.isRequired
}

function Extension({oid, description, isCritical}) {

    let elementClassNames = ["list-group-item"];
    if (isCritical) {
        elementClassNames.push("active");
    }

    const extension = (
        <li className={elementClassNames.reduce((acc, cv)=> acc + " " + cv)}>
            {isCritical ? (<span className="badge">critical</span>) : null}
            {`${description} (${oid})`}
        </li>
    );

    return extension;
}

Extension.propTypes = {
    oid: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    isCritical: PropTypes.bool.isRequired
}