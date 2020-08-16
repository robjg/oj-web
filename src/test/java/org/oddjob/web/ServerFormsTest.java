package org.oddjob.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.FileUtils;
import org.oddjob.http.InvokeRequest;
import org.oddjob.http.InvokeResponse;
import org.oddjob.jetty.JettyHttpClient;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.jmx.client.ComponentTransportable;
import org.oddjob.jmx.server.ServerSide;
import org.oddjob.jmx.server.ServerSideBuilder;
import org.oddjob.remote.OperationType;
import org.oddjob.web.gson.GsonUtil;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import java.io.File;

public class ServerFormsTest {

    @Test
    public void testFormsConfiguration() throws Exception {

        Oddjob serverJobs = new Oddjob();
        serverJobs.setFile(new File(getClass().getResource("serverJobs.xml")
                .getFile()));
        serverJobs.run();

        ArooaSession session = new StandardArooaSession();
        session.getBeanRegistry().register("serverJobs", serverJobs);

        MBeanServer mbs = MBeanServerFactory.createMBeanServer();

        ServerSide jmxServer = ServerSideBuilder
                .withSession(session)
                .buildWith(mbs, "OurServer", serverJobs);

        WebServerHandlerJmx jmxHandler = new WebServerHandlerJmx();
        jmxHandler.setJmxServer(jmxServer);
        jmxHandler.setArooaSession(session);

        JettyHttpServer server = new JettyHttpServer();
        server.setHandlers(0, jmxHandler.toValue());

        server.start();

        InvokeRequest request = InvokeRequest.forRemoteId(1L)
                .withOperation(OperationType.ofName("formFor")
                        .withSignature(Object.class)
                        .returning(String.class))
                .andArgs(new ComponentTransportable(2L));

        Gson gson = GsonUtil.createGson(getClass().getClassLoader());

        String jsonRequest = gson.toJson(request);

        JettyHttpClient client = new JettyHttpClient();
        client.setUrl("http://localhost:" + server.getPort() + "/invoke");
        client.setMethod(JettyHttpClient.RequestMethod.POST);
        client.setContent(jsonRequest);

        client.call();

        InvokeResponse<String> response =
                gson.fromJson(client.getContent(),
                        new TypeToken<InvokeResponse<String>>() {}.getType() );

        String expected = FileUtils.readToString(
                getClass().getResource("ServerFormsExpected.json"));

        JSONAssert.assertEquals(
                response.getValue(),
                expected,
                JSONCompareMode.LENIENT);

        System.out.println(response.getValue());

        server.stop();
    }
}
