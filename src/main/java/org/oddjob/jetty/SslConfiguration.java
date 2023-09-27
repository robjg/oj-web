package org.oddjob.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * @oddjob.description Provides TLS Configuration for Client and Servers. Some properties apply only to servers,
 * some only to clients. All passwords be obfuscated, see the Jetty documentation on how to do this.
 *
 * <h3>Debugging</h3>
 * <pre>-Djavax.net.debug=ssl</pre>
 * <pre>-Djavax.net.debug=all</pre>
 * <a href="https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-31B7E142-B874-46E9-8DD0-4E18EC0EB2CF">This Link</a> will
 * explain what's going on.
 *
 * <h3>Common Errors</h3>
 * <dl>
 *     <dt>PKIX path building failed: unable to find valid certification path to requested target</dt>
 *     <dd>There no certificate in the Trust Store matching the server.</dd>
 *
 *     <dt>org.eclipse.jetty.http.HttpParser$IllegalCharacterException: 400: Illegal character CNTL=0x15</dt>
 *     <dd>One side is using TLS, the other isn't.</dd>
 *
 *     <dt>javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No name matching <i>some-domain</i> found</dt>
 *     <dd>The certificate CN does not match the host name.</dd>
 *
 *     <dt>java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty</dt>
 *     <dd></dd>
 *
 *     <dt>java.security.UnrecoverableKeyException: Get Key failed: null</dt>
 *     <dd>I got this when the Key Store password was missing.</dd>
 *
 *     <dt>java.io.IOException: keystore password was incorrect</dt>
 *     <dd>This one is obvious.</dd>
 * </dl>
 *
 * @oddjob.example Client Accepts any certificate.
 * <p>
 * {@oddjob.xml.resource examples/ssl/TrustAllExample.xml}
 *
 * @oddjob.example Host Name Verification. The client accepts the host even if it doesn't match the certificate.
 * <p>
 * {@oddjob.xml.resource examples/ssl/TrustAnyExample.xml}
 *
 * @oddjob.example One Way Trust. The client verifies who the server is but the server doesn't care who the client is.
 * <p>
 * {@oddjob.xml.resource examples/ssl/OneWayTrustExample.xml}
 *
 * @oddjob.example Two Way Trust. The client verifies who the server is and the server verifies who the client is.
 * <p>
 * {@oddjob.xml.resource examples/ssl/MutualTrustExample.xml}
 */
public class SslConfiguration implements JettyServerModifier, ClientSslProvider {

    private static final Logger logger = LoggerFactory.getLogger(SslConfiguration.class);

    /**
     * @oddjob.property
     * @oddjob.description The path of the store that contains the private key and signed cert.
     * @oddjob.required Yes for Server, No for client unless doing Client Auth.
     */
    private volatile Path keyStorePath;

    /**
     * @oddjob.property
     * @oddjob.description The key store password.
     * @oddjob.required Yes.
     */
    private volatile String keyStorePassword;

    /**
     * @oddjob.property
     * @oddjob.description The key store type. Either JKS or PKCS12.
     * @oddjob.required No, defaults depending on JDK version.
     */
    private volatile String keyStoreType;

    /**
     * @oddjob.property
     * @oddjob.description The key password. Only applicable to JKS stores.
     * @oddjob.required No.
     */
    private volatile String keyManagerPassword;

    /**
     * @oddjob.property
     * @oddjob.description The path of the store that contains trusted public certs.
     * @oddjob.required No, unless you wish to verify your peer.
     */
    private volatile Path trustStorePath;

    /**
     * @oddjob.property
     * @oddjob.description The trust store password.
     * @oddjob.required Yes, if you have a trust store.
     */
    private volatile String trustStorePassword;

    /**
     * @oddjob.property
     * @oddjob.description The trust store type. Either JKS or PKCS12.
     * @oddjob.required No.
     */
    private volatile String trustStoreType;


    // Server Only

    /**
     * @oddjob.property
     * @oddjob.description Should the server perform client authentication. NONE/WANT/NEED.
     * @oddjob.required No, defaults to NONE, and only applicable to a server.
     */
    private volatile ClientAuth clientAuth;

    /**
     * @oddjob.property
     * @oddjob.description The port of the server ssl connector.
     * @oddjob.required No, can be set from the Server configuration.
     */
    private int port;

    // Client only

    /**
     * @oddjob.property
     * @oddjob.description Something that can verify if a hostname is acceptable when the host doesn't match
     * the certificate CN. In Jetty, to get this work, Client Endpoint Identification Algorithm is set to null. This
     * generates this warning: <em>No Client EndPointIdentificationAlgorithm configured for Client</em>
     * @oddjob.required No, and only applicable to a client.
     */
    private volatile HostnameVerifier hostnameVerifier;

    /**
     * @oddjob.property
     * @oddjob.description Should the client trust all certificates.
     * @oddjob.required No, defaults to false, and only applicable to a client.
     */
    private volatile boolean trustAll;

    /**
     * @oddjob.property
     * @oddjob.description Ignore SNI checks on the server. This allows us to use localhost without getting
     * an {@code org.eclipse.jetty.http.BadMessageException: 400: Invalid SNI} which is happening since upgrading
     * Jetty version.
     * @oddjob.required No, defaults to false, and only applicable to the server.
     *
     * @since 1.7
     */
    private volatile boolean ignoreSni;


    public enum ClientAuth {
        NONE,
        WANT,
        NEED
    }

    @Override
    public void modify(Server server) {

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

        configureFactory(sslContextFactory);

        if (this.clientAuth == ClientAuth.WANT) {
            sslContextFactory.setWantClientAuth(true);
        }
        if (this.clientAuth == ClientAuth.NEED) {
            sslContextFactory.setNeedClientAuth(true);
        }

        ServerConnector connector;

        if (this.ignoreSni) {
            HttpConfiguration httpConfig = new HttpConfiguration();
            SecureRequestCustomizer customizer = new SecureRequestCustomizer();
            customizer.setSniHostCheck(false);
            customizer.setSniRequired(false);
            httpConfig.addCustomizer(customizer);
            HttpConnectionFactory http = new HttpConnectionFactory(httpConfig);
            SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, http.getProtocol());
            connector = new ServerConnector(server, ssl, http);
        }
        else {
            connector = new ServerConnector(server, sslContextFactory);
        }

        connector.setPort(port);

        logger.info("Adding SSl Connector.");
        server.addConnector(connector);
    }

    @Override
    public SslContextFactory.Client provideClientSsl() {

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client(this.trustAll);

        logger.info("Providing Client SSL.");

        configureFactory(sslContextFactory);

        return sslContextFactory;
    }

    protected void configureFactory(SslContextFactory sslContextFactory) {

        Optional.ofNullable(this.keyStorePath).map(Object::toString)
                .ifPresent(sslContextFactory::setKeyStorePath);
        Optional.ofNullable(this.keyStorePassword)
                .ifPresent(sslContextFactory::setKeyStorePassword);
        Optional.ofNullable(this.keyStoreType)
                .ifPresent(sslContextFactory::setKeyStoreType);
        Optional.ofNullable(this.keyManagerPassword)
                .ifPresent(sslContextFactory::setKeyManagerPassword);
        Optional.ofNullable(this.trustStorePath).map(Objects::toString)
                .ifPresent(sslContextFactory::setTrustStorePath);
        Optional.ofNullable(this.trustStorePassword)
                .ifPresent(sslContextFactory::setTrustStorePassword);
        Optional.ofNullable(this.trustStoreType)
                .ifPresent(sslContextFactory::setTrustStoreType);
        Optional.ofNullable(this.hostnameVerifier)
                .ifPresent(hostnameVerifier -> {
                    sslContextFactory.setHostnameVerifier(hostnameVerifier);
                    sslContextFactory.setEndpointIdentificationAlgorithm(null);
                });
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Path getKeyStorePath() {
        return keyStorePath;
    }

    @ArooaAttribute
    public void setKeyStorePath(Path keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

    public Path getTrustStorePath() {
        return trustStorePath;
    }

    @ArooaAttribute
    public void setTrustStorePath(Path trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    public boolean isTrustAll() {
        return trustAll;
    }

    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public boolean isIgnoreSni() {
        return ignoreSni;
    }

    public void setIgnoreSni(boolean ignoreSni) {
        this.ignoreSni = ignoreSni;
    }

    @Override
    public String toString() {
        return "SslConfiguration{" +
                "keyStorePath=" + keyStorePath +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", keyManagerPassword='" + keyManagerPassword + '\'' +
                ", trustStorePath=" + trustStorePath +
                ", trustStorePassword='" + trustStorePassword + '\'' +
                '}';
    }
}
