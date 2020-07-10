package org.oddjob.http;

import com.google.gson.Gson;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteInvoker;
import org.oddjob.web.gson.GsonUtil;
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

    private final Gson gson;

    private RemoteInvoker remoteInvoker;

    public InvokerServlet() {
        this.gson = GsonUtil.createGson(getClass().getClassLoader());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        remoteInvoker = (RemoteInvoker) config.getServletContext().getAttribute(REMOTE_INVOKER);

        if (remoteInvoker == null) {
            throw new ServletException("No " + REMOTE_INVOKER + " in Servlet Context.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String json = request.getReader().lines().collect(Collectors.joining());

        InvokeRequest invokeRequest;
        try {
            invokeRequest = gson.fromJson(json, InvokeRequest.class);
        }
        catch (RuntimeException e) {
            throw new ServletException("Failed parsing: " + json, e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request:" + invokeRequest);
        }

        OperationType<?> operationType = invokeRequest.getOperationType();

        Object result;
        try {
            result = this.remoteInvoker.invoke(invokeRequest.getRemoteId(),
                    operationType,
                    invokeRequest.getArgs());
        } catch (RemoteException e) {
            throw new ServletException(e);
        }

        Class<?> returnType = operationType.getReturnType();
        if (result != null && result.getClass() != returnType) {
            returnType = result.getClass();
            if (logger.isDebugEnabled()) {
                logger.debug("Actual return type is " + returnType.getName());
            }
        }

        InvokeResponse<?> invokeResponse = inferResponseType(returnType, result);

        gson.toJson(invokeResponse, response.getWriter());
    }

    <T> InvokeResponse<T> inferResponseType(Class<T> type, Object value) {
        return InvokeResponse.from(type, ClassUtils.cast(type, value));
    }
}
