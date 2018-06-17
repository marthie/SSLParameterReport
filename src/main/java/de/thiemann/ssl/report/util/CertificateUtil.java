package de.thiemann.ssl.report.util;

/*
 * ----------------------------------------------------------------------
 * Copyright (c) 2012  Thomas Pornin <pornin@bolet.org>
 * Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ----------------------------------------------------------------------
 */

import de.thiemann.ssl.report.model.PubKeyInfo;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class CertificateUtil {

    private static Logger log = LoggerFactory.getLogger(CertificateUtil.class);
    /*
     * Compute the SHA-1 hash of some bytes, returning the hash value in
     * hexadecimal.
     */
    public static String computeFingerprint(byte[] buf) {
        return doSHA1(buf, 0, buf.length);
    }

    public static String computeFingerprint(byte[] buf, int off, int len) {
        return doSHA1(buf, off, len);
    }

    private static String doSHA1(byte[] buf, int off, int len) {
        Formatter f = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(buf, off, len);
            byte[] fingerprintVector = md.digest();

            f = new Formatter();
            for (int i = 0; i < fingerprintVector.length; i++) {
                if (i == fingerprintVector.length - 1)
                    f.format("%02x", fingerprintVector[i] & 0xFF);
                else
                    f.format("%02x:", fingerprintVector[i] & 0xFF);
            }
            return f.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        } finally {
            if (f != null)
                f.close();
        }
    }

    public static String transferSignatureAlgorithm(String oid) {
        StringBuffer sb = new StringBuffer();

        boolean foundAlgorithm = false;
        for (ASN1SignatureAlgorithmsIds sa : ASN1SignatureAlgorithmsIds
                .values()) {
            if (sa.getOid().equals(oid)) {
                sb.append(sa.name()).append(" (").append(oid).append(')');
                foundAlgorithm = true;
                break;
            }
        }

        if (!foundAlgorithm)
            sb.append("Unknown signature algorithm! OID: ").append(oid);

        return sb.toString();
    }

    public static PubKeyInfo transferPublicKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        PubKeyInfo info = new PubKeyInfo();

        AlgorithmIdentifier algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
        ASN1ObjectIdentifier asn1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        String oid = asn1ObjectIdentifier.getId();

        if (oid.equals(ASN1PublicKeyIds.RSA.getOid())) {
            info.setPubKeyAlgorithm(ASN1PublicKeyIds.RSA.name());

            try {
                ASN1Sequence rsaPublicKeySequence = (ASN1Sequence) subjectPublicKeyInfo.parsePublicKey();
                ASN1Integer rsaModulus = (ASN1Integer) rsaPublicKeySequence.getObjectAt(0);
                BigInteger modulus = rsaModulus.getPositiveValue();

                info.setPubKeySize(modulus.bitLength());
            } catch (IOException e) {
                log.error("{}", e.getMessage());
                log.error("{}", e);
            }
        } else if (oid.equals(ASN1PublicKeyIds.DSA.getOid())) {
            info.setPubKeyAlgorithm(ASN1PublicKeyIds.DSA.name());
        } else if (oid.equals(ASN1PublicKeyIds.Diffie_Hellman
                .getOid())) {
            info.setPubKeyAlgorithm(ASN1PublicKeyIds.Diffie_Hellman.name());
        } else if (oid.equals(ASN1PublicKeyIds.KEA.getOid())) {
            info.setPubKeyAlgorithm(ASN1PublicKeyIds.KEA.name());
        } else if (oid.equals(ASN1PublicKeyIds.ECDH.getOid())) {
            info.setPubKeyAlgorithm(ASN1PublicKeyIds.ECDH.name());
        } else {
            info.setPubKeyAlgorithm("Unknown public key! OID: " + oid);
        }

        return info;
    }

    public static List<String> transferGeneralNames(GeneralNames generalNames) {
        if (generalNames == null)
            return null;

        List<String> l = Arrays.stream(generalNames.getNames())
				.filter(gn-> {
			int tag = gn.getTagNo();

			/*
				RFC 5280 -> 4.2.1.6.  Subject Alternative Name
				GeneralName ::= CHOICE {
        			otherName                       [0]     OtherName,
        			rfc822Name                      [1]     IA5String,
        			dNSName                         [2]     IA5String,
        			x400Address                     [3]     ORAddress,
        			directoryName                   [4]     Name,
        			ediPartyName                    [5]     EDIPartyName,
        			uniformResourceIdentifier       [6]     IA5String,
        			iPAddress                       [7]     OCTET STRING,
        			registeredID                    [8]     OBJECT IDENTIFIER }
			 */
			if(tag == 1 || tag == 2 || tag == 6 || tag == 7) {
				return true;
			}

			return false;
		}).map(gn -> {
			int tag = gn.getTagNo();

			if(tag == 1 || tag == 2 || tag == 6) {
				return ((DERIA5String)gn.getName()).getString();
			}

			if(tag == 7) {
				return ((ASN1OctetString)gn.getName()).toString();
			}

			return "[Wrong tag value!]";
		}).collect(Collectors.toList());

        return l;
    }

    public static List<String> transferDistributionPoints(byte[] extension) {
        if (extension == null)
            return null;

        ASN1Sequence crlDistributionPoints = null;

        try {
            ASN1Object o = null;

            o = DEROctetString.fromByteArray(extension);
            if (o instanceof DEROctetString) {
                DEROctetString octStr = (DEROctetString) o;

                o = ASN1Sequence.fromByteArray(octStr.getOctets());
                if (o instanceof ASN1Sequence) {
                    crlDistributionPoints = (ASN1Sequence) o;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (crlDistributionPoints == null)
            return null;

        List<String> l = new ArrayList<String>();
        Enumeration<?> e = crlDistributionPoints.getObjects();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();

            if (o instanceof ASN1Sequence) {
                ASN1Sequence seqDP = (ASN1Sequence) o;
                DistributionPoint dp = new DistributionPoint(seqDP);

                DistributionPointName dpn = dp.getDistributionPoint();
                ASN1Encodable enc = dpn.getName();

                if (enc instanceof GeneralNames) {
                    GeneralNames gns = (GeneralNames) enc;

                    for (GeneralName gn : gns.getNames()) {
                        l.add(gn.toString());
                    }
                }
            }
        }

        if (!l.isEmpty())
            return l;
        else
            return null;
    }
}
