package org.oddjob.jetty;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class BasicAuthenticationTest {

    private static final Logger logger =
            LoggerFactory.getLogger(BasicAuthenticationTest.class);

    @Test
    public void testBasicAuthenticationClientServerInOddjob() throws Exception {

        Oddjob serverOddjob = new Oddjob();
        serverOddjob.setFile(new File(
                getClass().getResource("BasicAuthServer.xml").getFile()));

        serverOddjob.run();

        assertThat(serverOddjob.lastStateEvent().getState(), is(ParentState.STARTED));

        logger.info("** Server Started.");

        OddjobLookup serverLookup = new OddjobLookup(serverOddjob);

        int port = serverLookup.lookup("server.port", int.class);

        Properties properties = new Properties();
        properties.setProperty("server.port", Integer.toString(port));

        Oddjob clientOddjob = new Oddjob();
        clientOddjob.setFile(new File(
                getClass().getResource("BasicAuthClient.xml").getFile()));
        clientOddjob.setProperties(properties);
        clientOddjob.run();

        assertThat(clientOddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup clientLookup = new OddjobLookup(clientOddjob);

        String content = clientLookup.lookup("client.content", String.class);

        logger.info(content);

        int status = clientLookup.lookup("client.status", int.class);

        Assert.assertEquals(200, status);

        serverOddjob.stop();
    }

}