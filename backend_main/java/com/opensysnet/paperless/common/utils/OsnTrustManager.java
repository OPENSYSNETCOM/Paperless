package com.opensysnet.paperless.common.utils;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class OsnTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted( java.security.cert.X509Certificate[] x509Certificates, String s ) throws CertificateException {

    }

    @Override
    public void checkServerTrusted( java.security.cert.X509Certificate[] x509Certificates, String s ) throws CertificateException {

    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers( ) {
        return null;
    }

    public void checkClientTrusted( X509Certificate[] certs, String authType ) {
    }

    public void checkServerTrusted( X509Certificate[] certs, String authType ) {
    }
}
