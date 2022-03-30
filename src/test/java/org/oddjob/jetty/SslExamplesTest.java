package org.oddjob.jetty;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.state.StateConditions;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SslExamplesTest {

    @Test
    public void testTrustAllExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/TrustAllExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()), is(true));

        oddjob.destroy();
    }

    @Test
    public void testHostnameVerifierExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/TrustAnyExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()), is(true));

        oddjob.destroy();
    }

    @Test
    public void testOneWayTrustExample() {

        File file = new File(Objects.requireNonNull(
                getClass().getResource("/examples/ssl/OneWayTrustExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()), is(true));

        oddjob.destroy();
    }

    @Test
    public void testMutualTrustExample() {

        File file = new File(Objects.requireNonNull(getClass().getResource("/examples/ssl/MutualTrustExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.run();

        assertThat(StateConditions.COMPLETE.test(oddjob.lastStateEvent().getState()), is(true));

        oddjob.destroy();
    }

}
