package org.oddjob.web;

import org.junit.Ignore;
import org.junit.Test;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ClientServerTest {

    @Ignore
    @Test
    public void testInvoke() throws FailedToStopException, ArooaConversionException {

        Oddjob server = new Oddjob();
        server.setConfiguration(new XMLConfiguration(
                "org/oddjob/web/WebServerExample.xml",
                getClass().getClassLoader()));

        server.run();

        int port = new OddjobLookup(server).lookup("server.port", int.class);

        WebClientJob clientJob = new WebClientJob();
        clientJob.setArooaSession(new StandardArooaSession());
        clientJob.setHost("localhost");
        clientJob.setPort(port);

        clientJob.run();

        Runnable runnable = new OddjobLookup(clientJob).lookup("echo", Runnable.class);

        runnable.run();

        clientJob.stop();

        server.stop();

    }

}