package org.oddjob.jetty;

import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Something that can provide client SSL Configuration.
 */
public interface ClientSslProvider {

    SslContextFactory provideClientSsl();
}
