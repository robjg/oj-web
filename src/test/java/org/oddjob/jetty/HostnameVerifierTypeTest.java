package org.oddjob.jetty;

import org.junit.Test;
import org.oddjob.arooa.convert.ArooaConversionException;

import javax.net.ssl.SSLSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class HostnameVerifierTypeTest {

    @Test
    public void testVariousHostnames() throws ArooaConversionException {

        HostnameVerifierType test = new HostnameVerifierType();

        SSLSession sslSession = mock(SSLSession.class);

        test.setHostname("localhost");

        assertThat(test.toValue().verify("localhost", sslSession), is(true));
        assertThat(test.toValue().verify("LOCALHOST", sslSession), is(true));

        assertThat(test.toValue().verify("foo", sslSession), is(false));
    }

    @Test
    public void testVariousRegex() throws ArooaConversionException {

        HostnameVerifierType test = new HostnameVerifierType();

        SSLSession sslSession = mock(SSLSession.class);

        test.setHostname("localhost");
        test.setRegex(true);

        assertThat(test.toValue().verify("localhost", sslSession), is(true));
        assertThat(test.toValue().verify("LOCALHOST", sslSession), is(true));

        assertThat(test.toValue().verify("foo", sslSession), is(false));

        test.setHostname(".*\\.mycomp\\.com");

        assertThat(test.toValue().verify("myserver.mycomp.com", sslSession), is(true));
        assertThat(test.toValue().verify("other.elsewhere.com", sslSession), is(false));
    }
}