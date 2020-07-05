package org.oddjob.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.remote.OperationType;
import org.oddjob.remote.RemoteException;
import org.oddjob.remote.RemoteInvoker;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to handle invoke requests.
 */
@WebServlet(name = "InvokableServlet", urlPatterns = "/invoke")
public class InvokerServlet extends HttpServlet {

    public static final String REMOTE_INVOKER = "remote-invoker";

    private final Gson gson;

    private RemoteInvoker remoteInvoker;

    public InvokerServlet() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(OperationType.class,
                        new OperationTypeDeSer(getClass().getClassLoader()))
                .registerTypeAdapter(InvokeRequest.class,
                        new InvokeRequestDeserializer())
                .registerTypeAdapter(InvokeResponse.class,
                        new InvokeResponseDesSer(getClass().getClassLoader()))
                .create();
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

        InvokeRequest invokeRequest = gson.fromJson(request.getReader(), InvokeRequest.class);

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

        InvokeResponse<?> invokeResponse = inferResponseType(returnType, result);

        gson.toJson(invokeResponse, response.getWriter());
    }

    <T> InvokeResponse<T> inferResponseType(Class<T> type, Object value) {
        return InvokeResponse.from(type, ClassUtils.cast(type, value));
    }
}
