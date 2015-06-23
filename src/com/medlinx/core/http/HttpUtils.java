package com.medlinx.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import com.medlinx.core.constant.HttpConstants;

public class HttpUtils {

    private static final String TRUST_STORE_RESOURCE_PATH = "tls/mlnx.truststore";

    public static String getHttpUrl(String hostname, int httpPort) {

        return String.format("http://%s:%d", hostname, httpPort);
    }

    public static String getHttpUrl(String hostname) {

        return getHttpUrl(hostname, HttpConstants.DEFAULT_HTTP_PORT);
    }

    public static String getHttpsUrl(String hostname, int httpsPort) {

        return String.format("https://%s:%d", hostname, httpsPort);
    }

    public static String getHttpsUrl(String hostname) {

        return getHttpUrl(hostname);
    }

    public static HttpClient getHttpsClient(int httpsPort)
            throws GeneralSecurityException, IOException {

        HttpClient httpsClient = new DefaultHttpClient();

        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream trustStoreIn = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(TRUST_STORE_RESOURCE_PATH);
        try {
            trustStore.load(trustStoreIn, null);
        } finally {
            trustStoreIn.close();
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Scheme httpsScheme = new Scheme("https", httpsPort, sslSocketFactory);
        httpsClient.getConnectionManager().getSchemeRegistry()
                .register(httpsScheme);
        return httpsClient;
    }

    public static HttpClient getHttpsClient() throws GeneralSecurityException,
            IOException {

        return getHttpsClient(HttpConstants.DEFAULT_HTTPS_PORT);
    }
}
