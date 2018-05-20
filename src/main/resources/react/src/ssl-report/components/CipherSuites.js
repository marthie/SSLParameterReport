import React from 'react';

export default function CipherSuites({cipherSuites}) {
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