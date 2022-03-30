package org.oddjob.jetty;

import org.eclipse.jetty.server.Server;

/**
 * Something that can modify a Jetty Server before it is started.
 */
public interface JettyServerModifier {

    void modify(Server server);

}
