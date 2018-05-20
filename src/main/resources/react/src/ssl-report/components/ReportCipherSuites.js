import React from 'react';
import PropTypes from 'prop-types';

export default function ReportCipherSuitesTable({reportCipherSuites}) {
    const cipherSuitesRows = [];

    reportCipherSuites.map((cipherSuiteObject) => {
        console.log("rendering cipher suites for version " + cipherSuiteObject.version + " with "
            + cipherSuiteObject.cipherSuiteStrings.length + " items...");

        cipherSuitesRows.push(<CipherSuitesRow key={cipherSuiteObject.key}
                                               version={cipherSuiteObject.version}
                                               cipherSuiteStrings={cipherSuiteObject.cipherSuiteStrings}/>);
    });


    return (<table className="table table-bordered">
        <tbody>{cipherSuitesRows}</tbody>
    </table>);
}

ReportCipherSuitesTable.propTypes = {
    reportCipherSuites: PropTypes.array.isRequired
}

function CipherSuitesRow({version, cipherSuiteStrings}) {
    const row = (<tr>
        <th scope="row">{version}</th>
        <td>
            {cipherSuiteStrings.map((cipherSuiteString) => <p key={cipherSuiteString.key}>{cipherSuiteString.name}</p>)}
        </td>
    </tr>);

    return row;
}

CipherSuitesRow.propTypes = {
    version: PropTypes.string.isRequired,
    cipherSuiteStrings: PropTypes.array.isRequired
}