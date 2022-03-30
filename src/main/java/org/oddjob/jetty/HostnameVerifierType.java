package org.oddjob.jetty;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @oddjob.description Verify a host against a given name or regular expression.
 *
 */
public class HostnameVerifierType implements ValueFactory<HostnameVerifier> {

    private static final Logger logger = LoggerFactory.getLogger(HostnameVerifierType.class);

    /**
     * @oddjob.property
     * @oddjob.description The hostname the server has to match.
     * @oddjob.required Yes.
     */
    private String hostname;

    /**
     * @oddjob.property
     * @oddjob.description Is the hostname a regular expression.
     * @oddjob.required No, defaults to false.
     */
    private boolean regex;

    @Override
    public HostnameVerifier toValue() throws ArooaConversionException {

        return (hostname, sslSession) -> {

            String expected = Objects.requireNonNull(
                    HostnameVerifierType.this.hostname, "Expect Hostname required.");

            boolean match;
            if (regex) {
                Pattern pattern = Pattern.compile(expected, Pattern.CASE_INSENSITIVE);
                match =  pattern.matcher(hostname).matches();
            }
            else {
                match = expected.equalsIgnoreCase(hostname);
            }

            if (match) {
                logger.info("Host Name Verification of {} passed against expected host {}",
                        hostname, expected);
            }
            else {
                logger.info("Host Name Verification of {} failed against expected host {}",
                        hostname, expected);
            }
            return match;
        };
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "HostnameVerifierType{" +
                "hostname='" + hostname + '\'' +
                '}';
    }
}
