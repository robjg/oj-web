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
import java.util.Objects;

public class ServerFormsTest {

    // Todo: This is really fragile and fails every time a new value is added!
    @Test
    public void testFormsConfiguration() throws Exception {

        Oddjob serverJobs = new Oddjob();
        serverJobs.setFile(new File(Objects.requireNonNull(getClass().getResource("serverJobs.xml"))
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
        server.setHandler(jmxHandler.toValue());

        server.start();

        InvokeRequest request = InvokeRequest.forRemoteId(1L)
                .withOperation(OperationType.ofName("formFor")
                        .withSignature(Object.class)
                        .returning(String.class))
                .andArgs(new ComponentTransportable(2L));

        Gson gson = GsonUtil.createGson(session);

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
                Objects.requireNonNull(getClass().getResource("ServerFormsExpected.json")));

        System.out.println(response.getValue());

        String formDef = response.getValue().replaceAll("\"options\":\"[a-z,-:]*\"",
                "\"options\":\"SOME_OPTIONS_THAT_CHANGE_TOO_MUCH\"");

        JSONAssert.assertEquals(
                formDef,
                expected,
                JSONCompareMode.LENIENT);

        server.stop();
    }
}
