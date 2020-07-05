package org.oddjob.http;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.remote.RemoteInvoker;

/**
 * Jetty Handler for handling invoke requests via an {@link InvokerServlet}.
 */
public class JettyInvokerHandler implements ValueFactory<Handler> {

    private RemoteInvoker remoteInvoker;

    @Override
    public Handler toValue() {

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath( "/" );

        context.setAttribute(InvokerServlet.REMOTE_INVOKER,
                remoteInvoker);

        ServletHolder servletHolder = new ServletHolder(
                new InvokerServlet());

        context.addServlet(servletHolder, "/invoke");

        return context;
    }

    public RemoteInvoker getRemoteInvoker() {
        return remoteInvoker;
    }

    public void setRemoteInvoker(RemoteInvoker remoteInvoker) {
        this.remoteInvoker = remoteInvoker;
    }
}
