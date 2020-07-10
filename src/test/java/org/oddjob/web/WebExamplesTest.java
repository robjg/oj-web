package org.oddjob.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.state.ServiceState;
import org.oddjob.tools.StateSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class WebExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger(WebExamplesTest.class);

    Oddjob serverOddjob;
    Oddjob clientOddjob;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        logger.debug("================= " + name.getMethodName() + "==================");
    }

    @After
    public void tearDown() {


        if (clientOddjob != null) {
            clientOddjob.destroy();
        }
        if (serverOddjob != null) {
            serverOddjob.destroy();
        }
    }

    @Test
    public void testSimpleClientServerExample() throws FailedToStopException, ArooaConversionException {

        File serverConfig = new File(getClass().getResource("ServerExample.xml").getFile());

        serverOddjob = new Oddjob();
        serverOddjob.setFile(serverConfig);

        serverOddjob.run();

        assertEquals(ParentState.STARTED,
                serverOddjob.lastStateEvent().getState());

        int port = new OddjobLookup(serverOddjob).lookup("webServer.port", int.class);

        Properties props = new Properties();
        props.setProperty("hosts.freds.pc", "localhost");
        props.setProperty("server.port", "" + port);


        Oddjob clientOddjob = new Oddjob();
        clientOddjob.setFile(new File(
                getClass().getResource("ClientExample.xml").getFile()));
        clientOddjob.setProperties(props);
        clientOddjob.load();

        WebClientJob client = new OddjobLookup(clientOddjob)
				.lookup("freds-pc", WebClientJob.class);

        StateSteps clientSteps = new StateSteps(client);
        clientSteps.startCheck(ServiceState.STARTABLE, ServiceState.STARTING,
                ServiceState.STARTED);

        client.run();

        clientSteps.checkNow();

        clientSteps.startCheck(ServiceState.STARTED, ServiceState.STOPPED);

        client.stop();

        clientSteps.checkNow();
    }

    @Test
    public void testClientRunsServerJobExample() throws InterruptedException, ArooaPropertyException, ArooaConversionException, FailedToStopException {

        File serverConfig = new File(getClass().getResource("ServerExample.xml").getFile());

        serverOddjob = new Oddjob();
        serverOddjob.setFile(serverConfig);

        serverOddjob.run();

        assertEquals(ParentState.STARTED,
                serverOddjob.lastStateEvent().getState());

        OddjobLookup serverLookup = new OddjobLookup(serverOddjob);

        Stateful serverJob = serverLookup.lookup("server-jobs/greeting",
                Stateful.class);

        int port = serverLookup.lookup("webServer.port", int.class);

        File clientConfig = new File(getClass().getResource("ClientRunsServerJob.xml").getFile());

        Properties props = new Properties();
        props.setProperty("hosts.freds.pc", "localhost");
        props.setProperty("server.port", "" + port);

        Oddjob clientOddjob = new Oddjob();
        clientOddjob.setProperties(props);
        clientOddjob.setFile(clientConfig);

        StateSteps serverJobStates = new StateSteps(serverJob);
        serverJobStates.startCheck(JobState.READY, JobState.EXECUTING,
                JobState.COMPLETE);

        StateSteps clientOddjobStates = new StateSteps(clientOddjob);
        clientOddjobStates.startCheck(ParentState.READY, ParentState.EXECUTING,
                ParentState.COMPLETE);

        logger.info("** running Oddjob Client **");

        clientOddjob.run();

        logger.info("** Client complete **");

        clientOddjobStates.checkWait();

        serverJobStates.checkWait();
    }

    @Test
    public void testClientTriggersOnServerJobExample() throws InterruptedException, ArooaPropertyException, ArooaConversionException {

        File serverConfig = new File(getClass().getResource("ServerExample.xml").getFile());

        serverOddjob = new Oddjob();
        serverOddjob.setFile(serverConfig);

        serverOddjob.run();

        assertEquals(ParentState.STARTED,
                serverOddjob.lastStateEvent().getState());

        OddjobLookup serverLookup = new OddjobLookup(serverOddjob);

        Runnable serverJob = serverLookup.lookup("server-jobs/greeting",
                Runnable.class);

        int port = serverLookup.lookup("webServer.port", int.class);

        Properties props = new Properties();
        props.setProperty("hosts.freds.pc", "localhost");
        props.setProperty("server.port", "" + port);

        File clientConfig = new File(getClass().getResource("ClientTrigger.xml").getFile());

        Oddjob clientOddjob = new Oddjob();
        clientOddjob.setProperties(props);
        clientOddjob.setFile(clientConfig);

        logger.info("** Running Oddjob Client **");

        clientOddjob.run();

        logger.info("** Oddjob Client Started **");

        OddjobLookup clientLookup = new OddjobLookup(clientOddjob);

        Stateful localJob = clientLookup.lookup("local-job",
                Stateful.class);

        assertEquals(ParentState.STARTED,
                clientOddjob.lastStateEvent().getState());

        assertEquals(JobState.READY,
                localJob.lastStateEvent().getState());

        StateSteps clientState = new StateSteps(clientOddjob);

        clientState.startCheck(ParentState.STARTED, ParentState.ACTIVE,
                ParentState.COMPLETE);

        logger.info("** Running Server Job **");

        serverJob.run();

        logger.info("** Server Job Complete **");

        clientState.checkWait();

        clientOddjob.destroy();
    }

}
