package org.oddjob.web;

import com.google.gson.Gson;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.jetty.JettyHttpClient;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.jmx.server.ServerSide;
import org.oddjob.jmx.server.ServerSideBuilder;
import org.oddjob.rest.model.NodeInfo;
import org.oddjob.rest.model.NodeInfos;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ServerRestTest {

    @Test
    public void testSomeRestCalls() throws Exception {

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
        server.setHandler(jmxHandler.toValue());

        server.start();

        Gson gson = new Gson();

        JettyHttpClient client = new JettyHttpClient();
        client.setUrl("http://localhost:" + server.getPort() + "/api/nodeInfo");
        client.setMethod(JettyHttpClient.RequestMethod.GET);

        Map<String, String> params = new HashMap<>();
        params.put("nodeIds", "1");
        params.put("eventSeq", "-1");
        client.setParameters(params);

        client.call();

        assertThat(client.getStatus(), is(200));

        NodeInfos nodeInfos = gson.fromJson(client.getResponseBody(),
                NodeInfos.class);

        NodeInfo nodeInfo = nodeInfos.getNodeInfo()[0];

        assertThat(nodeInfo.getName(), is("Oddjob serverJobs.xml"));

        server.stop();
    }
}
