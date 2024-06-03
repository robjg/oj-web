package org.oddjob.http;

import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.RemoteException;
import org.oddjob.web.JsonRemoteInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Servlet to handle invoke requests.
 */
@WebServlet(name = "InvokableServlet", urlPatterns = "/invoke")
public class InvokerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(InvokerServlet.class);

    public static final String REMOTE_INVOKER = "remote-invoker";

    private JsonRemoteInvoker remoteInvoker;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        remoteInvoker = (JsonRemoteInvoker) config.getServletContext()
                .getAttribute(REMOTE_INVOKER);

        if (remoteInvoker == null) {
            throw new ServletException("No " + REMOTE_INVOKER + " in Servlet Context.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String json = request.getReader().lines().collect(Collectors.joining());

        String reply;
        try {
            reply = remoteInvoker.invoke(json);
        } catch (RemoteException e) {
            throw new ServletException("Failed Invoking request: " + json, e);
        }

        response.getWriter().print(reply);
    }

    <T> InvokeResponse<T> inferResponseType(Class<T> type, Object value) {
        return InvokeResponse.from(type, ClassUtils.cast(type, value));
    }
}
