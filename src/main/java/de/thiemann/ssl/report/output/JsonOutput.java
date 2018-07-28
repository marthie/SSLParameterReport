package de.thiemann.ssl.report.output;

/*

The MIT License (MIT)

Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thiemann.ssl.report.exceptions.ProcessCertificateException;
import de.thiemann.ssl.report.exceptions.ReportOutputException;
import de.thiemann.ssl.report.model.*;
import de.thiemann.ssl.report.util.CertificateUtil;
import de.thiemann.ssl.report.util.CipherSuiteUtil;
import de.thiemann.ssl.report.util.SSLVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class JsonOutput extends AbstractOutput {

    private Logger log = LoggerFactory.getLogger(JsonOutput.class);

    @Override
    public String outputReports(Collection<Report> reportCollection) throws ReportOutputException {
        try {
            List<Map<String, Object>> jsonReportList = new ArrayList<Map<String, Object>>();

            for (Report report : reportCollection) {
                Map<String, Object> jsonObjectMap = transferToJSONObject(report);
                jsonReportList.add(jsonObjectMap);
            }

            String jsonString = transferToString(jsonReportList);

            return jsonString;
        } catch (ProcessCertificateException e) {
            log.error("error: {}", e.getMessage());

            throw new ReportOutputException(e);
        }
    }

    private String transferToString(Object jsonObject) throws ReportOutputException {
        if (jsonObject != null) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonString = null;
            try {
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            } catch (JsonProcessingException e) {
                log.error("Exception while transferring object to JSON.", e);
                throw new ReportOutputException(e);
            }
            return jsonString;
        }

        return "{}";
    }

    private Map<String, Object> transferToJSONObject(Report report) throws ProcessCertificateException {
        if (report.getSupportedSSLVersions().size() == 0) {
            return null;
        }

        Map<String, Object> jsonReport = map(keyEntry());

        // output common values
        jsonReport.put("createdOn",
                String.format("%1$tF %1$tT", System.currentTimeMillis()));
        jsonReport.put("host", report.getHost());
        jsonReport.put("ipAddress", report.getIp().toString());
        jsonReport.put("port", Integer.toString(report.getPort()));

        // protocol
        List<Object> supportedVersions = new ArrayList<Object>();
        for (int version : report.getSupportedSSLVersions()) {
            Map<String, Object> versionEntry = map(keyEntry(), entry("version", CertificateUtil.versionString(version)));
            supportedVersions.add(versionEntry);
        }
        jsonReport.put("supportedSSLVersions", supportedVersions);
        jsonReport.put("compress", Boolean.toString(report.isCompress()));

        // cipher suites
        List<Object> cipherSuites = new ArrayList<Object>();
        for (int version : report.getSupportedSSLVersions()) {

            Map<String, Object> cipherSuitesByVersion = map(keyEntry());
            cipherSuitesByVersion.put("version", CertificateUtil.versionString(version));

            List<Object> listCipherSuiteStrings = new ArrayList<Object>();
            cipherSuitesByVersion.put("cipherSuiteStrings", listCipherSuiteStrings);

            if (version == SSLVersions.SSLv2.getIntVersion()) {
                for (int cipherSuiteId : report.getSupportedCipherSuite().get(version)) {
                    Map<String, Object> cipherSuiteEntry = map(keyEntry(),
                            entry("name", CipherSuiteUtil.cipherSuiteStringV2(cipherSuiteId)));
                    listCipherSuiteStrings.add(cipherSuiteEntry);
                }
            } else {
                for (int cipherSuiteId : report.getSupportedCipherSuite().get(version)) {
                    Map<String, Object> cipherSuiteEntry = map(keyEntry(),
                            entry("name", CipherSuiteUtil.cipherSuiteString(cipherSuiteId)));
                    listCipherSuiteStrings.add(cipherSuiteEntry);
                }
            }

            cipherSuites.add(cipherSuitesByVersion);
        }
        jsonReport.put("cipherSuites", cipherSuites);

        // certificates
        List<Object> certificateList = new ArrayList<Object>();

        if (report.getServerCert() != null && report.getServerCert().size() != 0) {
            for (Integer version : report.getServerCert().keySet()) {
                Set<Certificate> versionCertificates = report.getServerCert()
                        .get(version);

                List<Object> transferedCertificates = new ArrayList<Object>();
                for (Certificate certificate : versionCertificates) {
                    transferedCertificates
                            .add(transferToJSONObject(certificate));
                }

                Map<String, Object> certificatesByVersion = map(keyEntry());
                certificatesByVersion.put("version", CertificateUtil.versionString(version));
                certificatesByVersion.put("certificatesChain", transferedCertificates);

                certificateList.add(certificatesByVersion);
            }
        }

        jsonReport.put("certificates", certificateList);

        return jsonReport;
    }

    private Map<String, Object> transferToJSONObject(Certificate cert) throws ProcessCertificateException {
        if (!cert.isProcessed()) {
            cert.processCertificateBytes();
        }

        Map<String, Object> jsonCert = map(keyEntry());
        jsonCert.put("order", cert.getOrder());

        if (cert instanceof SSLv2Certificate) {
            SSLv2Certificate sslv2Cert = (SSLv2Certificate) cert;

            jsonCert.put("fingerprint", sslv2Cert.getHash());
            jsonCert.put("certificate-order", sslv2Cert.getOrder());
            jsonCert.put("subject", sslv2Cert.getName());


            return jsonCert;
        } else if (cert instanceof CertificateV3) {
            CertificateV3 v3Cert = (CertificateV3) cert;

            jsonCert.put("version", v3Cert.getCertificateVersion());
            jsonCert.put("serialNumber", String.format("%1$d", v3Cert.getCertificateSerialNumber()));
            jsonCert.put("subjectName", v3Cert.getSubjectName());
            jsonCert.put("subjectAlternativeNames", processAlternativeNames(v3Cert.getSubjectAlternativeNames()));
            jsonCert.put("notBefore",
                    String.format("%1$tF %1$tT", v3Cert.getNotBefore()));
            jsonCert.put("notAfter",
                    String.format("%1$tF %1$tT", v3Cert.getNotAfter()));
            jsonCert.put("pubKeyName", v3Cert.getPubKeyInfo().getPubKeyAlgorithm());
            jsonCert.put("pubKeySize", v3Cert.getPubKeyInfo().getPubKeySize());
            jsonCert.put("issuerName", v3Cert.getIssuerName());
            jsonCert.put("issuerAlternativeNames", processAlternativeNames(v3Cert.getIssuerAlternativeNames()));
            jsonCert.put("signatureAlgorithm", v3Cert.getSignatureAlgorithm());
            jsonCert.put("fingerprint", v3Cert.getFingerprint());
            jsonCert.put("crlDistributionPoints", v3Cert.getCrlDistributionPoints());
            jsonCert.put("keyUsageList", v3Cert.getKeyUsageList());

            List<Map<String, Object>> extensionInfoList = processExtensionInfo(v3Cert.getExtensionInfoList());
            jsonCert.put("extensionInfoList", extensionInfoList);

            return jsonCert;
        }

        return null;
    }

    private List<Map<String, Object>> processExtensionInfo(List<ExtensionInfo> extensionInfoList) {
        return extensionInfoList.stream().map(extensionInfo -> {
            return map(keyEntry(),
                    entry("oid", extensionInfo.getOid()),
                    entry("description", extensionInfo.getDescription()),
                    entry("isCritical", extensionInfo.isCritical()));
        }).collect(Collectors.toList());
    }

    private List<String> processAlternativeNames(List<String> alternativeNames) {
        if (alternativeNames == null) {
            return new ArrayList<>();
        }

        List<String> lines = new ArrayList<String>();

        StringBuffer lineBuffer = new StringBuffer();
        Iterator<String> iterator = alternativeNames.iterator();
        while (iterator.hasNext()) {
            String alternativeName = iterator.next();

            lineBuffer.append(alternativeName);

            if (iterator.hasNext()) {
                lineBuffer.append(", ");
            }

            if (lineBuffer.length() > 80) {
                lines.add(lineBuffer.toString());
                lineBuffer.setLength(0);
            }
        }
        return lines;
    }
}
