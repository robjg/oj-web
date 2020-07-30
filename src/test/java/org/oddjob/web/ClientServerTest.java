package org.oddjob.web;

import org.junit.Test;
import org.oddjob.Iconic;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.parsing.ConfigurationOwner;
import org.oddjob.arooa.parsing.ConfigurationSession;
import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.jetty.JettyHttpServer;
import org.oddjob.jmx.RemoteDirectory;
import org.oddjob.jmx.RemoteDirectoryOwner;
import org.oddjob.jmx.server.ServerSide;
import org.oddjob.jmx.server.ServerSideBuilder;
import org.oddjob.state.StateConditions;
import org.oddjob.tools.StateSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ClientServerTest {

    private static final Logger logger = LoggerFactory.getLogger(ClientServerTest.class);

    @Test
    public void testRunnableAndStateful() throws Exception {

        Oddjob serverJobs = new Oddjob();
        serverJobs.setFile(new File(getClass().getResource("serverJobs.xml")
                .getFile()));

        ArooaSession session = new StandardArooaSession();
        session.getBeanRegistry().register("serverJobs", serverJobs);

        MBeanServer mbs = MBeanServerFactory.createMBeanServer();

        try (ServerSide jmxServer = ServerSideBuilder
                .withSession(session)
                .buildWith(mbs, "OurServer", serverJobs)) {

            WebServerHandlerJmx jmxHandler = new WebServerHandlerJmx();
            jmxHandler.setJmxServer(jmxServer);
            jmxHandler.setArooaSession(session);

            JettyHttpServer server = new JettyHttpServer();
            server.setHandlers(0, jmxHandler.toValue());

            server.start();

            WebClientJob clientJob = new WebClientJob();
            clientJob.setArooaSession(new StandardArooaSession());
            clientJob.setHost("localhost");
            clientJob.setPort(server.getPort());

            clientJob.run();

            Runnable runnable = new OddjobLookup(clientJob).lookup("serverJobs", Runnable.class);

            StateSteps serverJobsState = new StateSteps((Stateful) runnable);
            serverJobsState.startCheck(
                    StateConditions.READY, StateConditions.EXECUTING, StateConditions.COMPLETE);

            runnable.run();

            serverJobsState.checkWait();

            clientJob.stop();

            server.stop();
        }
    }

    @Test
    public void testRegistryOwner() throws Exception {

        Oddjob serverJobs = new Oddjob();
        serverJobs.setFile(new File(getClass().getResource("serverJobs.xml")
                .getFile()));
        serverJobs.run();

        ArooaSession session = new StandardArooaSession();
        session.getBeanRegistry().register("serverJobs", serverJobs);

        MBeanServer mbs = MBeanServerFactory.createMBeanServer();

        try (ServerSide jmxServer = ServerSideBuilder
                .withSession(session)
                .buildWith(mbs, "OurServer", serverJobs)) {

            WebServerHandlerJmx jmxHandler = new WebServerHandlerJmx();
            jmxHandler.setJmxServer(jmxServer);
            jmxHandler.setArooaSession(session);

            JettyHttpServer server = new JettyHttpServer();
            server.setHandlers(0, jmxHandler.toValue());

            server.start();

            WebClientJob clientJob = new WebClientJob();
            clientJob.setArooaSession(new StandardArooaSession());
            clientJob.setHost("localhost");
            clientJob.setPort(server.getPort());

            clientJob.run();

            BeanDirectoryOwner remoteOj = new OddjobLookup(clientJob).lookup("serverJobs",
                    BeanDirectoryOwner.class);

            assertThat(remoteOj instanceof RemoteDirectoryOwner, is(true));

            RemoteDirectoryOwner owner = (RemoteDirectoryOwner) remoteOj;

            RemoteDirectory directory = owner.provideBeanDirectory();

            assertThat(directory.getServerId().toString(), is("OurServer"));

            Runnable greeting = directory.lookup("greeting", Runnable.class);

            assertThat(greeting, notNullValue());

            logger.info("** Listing Beans **");

            Iterable<Object> it = directory.getAllByType(Object.class);

            List<Object> all = StreamSupport.stream(it.spliterator(), false)
                    .collect(Collectors.toList());

            assertThat(all.size(), is(1));

            assertThat(all.get(0), is(greeting));

            logger.info("** Get id for **");

            String id = directory.getIdFor(greeting);

            assertThat(id, is("greeting"));

            clientJob.stop();
            server.stop();
        }

    }

    @Test
    public void testConfigurationOwner() throws Exception {

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

        WebClientJob clientJob = new WebClientJob();
        clientJob.setArooaSession(new StandardArooaSession());
        clientJob.setHost("localhost");
        clientJob.setPort(server.getPort());

        clientJob.run();

        ConfigurationOwner remoteOj = new OddjobLookup(clientJob).lookup("serverJobs",
                ConfigurationOwner.class);

        logger.info("** Getting Config Session **");

        ConfigurationSession configSession = remoteOj.provideConfigurationSession();

        assertThat(configSession, notNullValue());

        Object greeting = new OddjobLookup(clientJob).lookup("serverJobs/greeting");

        logger.info("** Getting Drag Point **");

        DragPoint dragPoint = configSession.dragPointFor(greeting);

        assertThat(dragPoint, notNullValue());

        clientJob.stop();
        server.stop();
        jmxServer.close();
    }

    @Test
    public void testIconic() throws Exception {

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

        WebClientJob clientJob = new WebClientJob();
        clientJob.setArooaSession(new StandardArooaSession());
        clientJob.setHost("localhost");
        clientJob.setPort(server.getPort());

        clientJob.run();

        Iconic remoteOj = new OddjobLookup(clientJob).lookup("serverJobs",
                Iconic.class);

        logger.info("** Getting Icon **");

        ImageIcon icon = remoteOj.iconForId("complete");

        assertThat(icon, notNullValue());

        clientJob.stop();
        server.stop();
        jmxServer.close();
    }
}