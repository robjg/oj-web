package org.oddjob.jetty;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.state.StateConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SslExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger(SslExamplesTest.class);

    private Path workPath;

    @Rule
    public TestName name = new TestName();

    public String getName() {
        return name.getMethodName();
    }

    @Before
    public void setUp() throws IOException {

        logger.info("--------------------------------  " + getName() +
                "  -----------------------------");

        workPath = OurDirs.workPathDir(SslExamplesTest.class);
    }

    @Test
    public void testTrustAllExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/TrustAllExample.xml")).getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", workPath.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.setProperties(properties);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()),
                is(true));

        oddjob.destroy();
    }

    @Test
    public void testHostnameVerifierExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/TrustAnyExample.xml")).getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", workPath.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.setProperties(properties);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()),
                is(true));

        oddjob.destroy();
    }

    @Test
    public void testOneWayTrustExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/OneWayTrustExample.xml")).getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", workPath.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.setProperties(properties);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()),
                is(true));

        oddjob.destroy();
    }

    @Test
    public void testMutualTrustExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/MutualTrustExample.xml")).getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", workPath.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.setProperties(properties);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()),
                is(true));

        oddjob.destroy();
    }
}
