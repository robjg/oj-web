package org.oddjob.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SslConfigurationTest {

    private static final Logger logger = LoggerFactory.getLogger(SslConfigurationTest.class);

    @Test
    public void testTrustAllClient() throws Exception {

        Path keystorePath = new File(Objects.requireNonNull(
                getClass().getResource("/ssl/keystore.jks")).getFile()).toPath();

        SslConfiguration serverSsl = new SslConfiguration();
        serverSsl.setKeyStorePath(keystorePath);
        serverSsl.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        serverSsl.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        serverSsl.setKeyStoreType("jks");

        Server server = new Server();

        server.setHandler(new HelloHandler());
        serverSsl.modify(server);

        ((ServerConnector) server.getConnectors()[0]).setPort(13013);

        logger.info("Starting Server");

        server.start();

        int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();

        SslConfiguration clientSsl = new SslConfiguration();
        clientSsl.setTrustAll(true);

        SslContextFactory.Client sslContextFactory = clientSsl.provideClientSsl();

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(sslContextFactory);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));

        logger.info("Starting Client");

        httpClient.start();

        Request request = httpClient.newRequest("https://localhost:" + port);

        ContentResponse response = request.send();

        assertThat(response.getStatus(), is(200));
        String responseBody = response.getContentAsString();

        assertThat(responseBody.trim(), is("<h1>Hello World</h1>"));

        logger.info("Stopping Client");

        httpClient.stop();

        logger.info("Stopping Server");

        server.stop();
    }

    @Test
    public void testClientAuthWithSameKeysAsServer() throws Exception {

        Path keystorePath = new File(Objects.requireNonNull(
                getClass().getResource("/ssl/keystore.jks")).getFile()).toPath();

        Path truststorePath = new File(Objects.requireNonNull(
                getClass().getResource("/ssl/truststore.p12")).getFile()).toPath();

        SslConfiguration serverSsl = new SslConfiguration();
        serverSsl.setKeyStorePath(keystorePath);
        serverSsl.setKeyStorePassword("storepwd");
        serverSsl.setKeyManagerPassword("keypwd");
        serverSsl.setKeyStoreType("jks");
        serverSsl.setTrustStorePath(truststorePath);
        serverSsl.setTrustStorePassword("trustpwd");
        serverSsl.setTrustStoreType("PKCS12");
        serverSsl.setClientAuth(SslConfiguration.ClientAuth.NEED);

        Server server = new Server();
        server.setHandler(new HelloHandler());
        serverSsl.modify(server);

        ((ServerConnector) server.getConnectors()[0]).setPort(13013);

        logger.info("Starting Server");

        server.start();

        int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();

        SslConfiguration clientSsl = new SslConfiguration();
        clientSsl.setKeyStorePath(keystorePath);
        clientSsl.setKeyStorePassword("storepwd");
        clientSsl.setKeyManagerPassword("keypwd");
        clientSsl.setKeyStoreType("jks");
        clientSsl.setTrustStorePath(truststorePath);
        clientSsl.setTrustStorePassword("trustpwd");
        clientSsl.setTrustStoreType("PKCS12");

        SslContextFactory.Client sslContextFactory = clientSsl.provideClientSsl();

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(sslContextFactory);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));

        logger.info("Starting Client");

        httpClient.start();

        Request request = httpClient.newRequest("https://localhost:" + port);

        ContentResponse response = request.send();

        assertThat(response.getStatus(), is(200));
        String responseBody = response.getContentAsString();

        assertThat(responseBody.trim(), is("<h1>Hello World</h1>"));

        logger.info("Stopping Client");

        httpClient.stop();

        logger.info("Stopping Server");

        server.stop();
    }
}