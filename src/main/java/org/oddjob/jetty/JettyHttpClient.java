package org.oddjob.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.FormContentProvider;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.Fields;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.utils.IoUtils;
import org.oddjob.util.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rob
 * @oddjob.description Execute an HTTP client request.
 * <p>
 * This is a very simple wrapper around Jetty's
 * <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/client/HttpClient.html">HTTPClient</a>.
 * It was developed to support testing of Oddjob's web service and as such it is quite
 * limited. It only supports PUT and GET requests and has no support for authentication.
 *
 * @oddjob.example Get the content of a URL using a parameter.
 * <p>
 * {@oddjob.xml.resource org/oddjob/jetty/ClientGetExample.xml}
 * @oddjob.example Basic Authentication.
 * <p>
 * {@oddjob.xml.resource org/oddjob/jetty/BasicAuthClient.xml}
 *
 * @oddjob.example Download to a file.
 *
 * {@oddjob.xml.resource org/oddjob/jetty/DownloadExample.xml}
 *
 */
public class JettyHttpClient implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(JettyHttpClient.class);

    public static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * @oddjob.property
     * @oddjob.description The name of the job. Can be any text.
     * @oddjob.required No.
     */
    private volatile String name;


    /**
     * @oddjob.property
     * @oddjob.description The URL to connect to. Must be a full URL, e.g. http://www.google.com
     * @oddjob.required Yes.
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    private volatile String url;

    /**
     * @oddjob.property
     * @oddjob.description The request method. GET/POST. PUT and DELETE are not supported yet.
     * @oddjob.required No defaults to GET.
     */
    private volatile RequestMethod method;

    /**
     * @oddjob.property
     * @oddjob.description The return status.
     * @oddjob.required Read Only.
     */
    private volatile int status;

    /**
     * @oddjob.property
     * @oddjob.description The content to send in a POST Request.
     * @oddjob.required No.
     */
    @ArooaText
    private volatile String requestBody;

    /**
     * @oddjob.property
     * @oddjob.description The content received if an output is not provided.
     * @oddjob.required Read only.
     */
    private volatile String responseBody;


    /**
     * @oddjob.property
     * @oddjob.description The output (such as a file) to download to.
     * @oddjob.required No.
     */
    private volatile OutputStream output;

    /**
     * @oddjob.property
     * @oddjob.description Parameters.
     * @oddjob.required No.
     */
    private volatile Map<String, String> parameters;

    /**
     * @oddjob.property
     * @oddjob.description The content-type of a POST request. Useful for sending forms.
     * @oddjob.required No.
     */
    private volatile String contentType;

    /**
     * @oddjob.property
     * @oddjob.description Provide Username/Password for Basic Authentication.
     * @oddjob.required No.
     */
    private volatile UsernamePassword basicAuthentication;

    /**
     * @oddjob.property
     * @oddjob.description Provide SSL Configuration.
     * @oddjob.required No.
     */
    private volatile ClientSslProvider ssl;

    /**
     * @oddjob.property
     * @oddjob.description Timeout of requests in seconds.
     * @oddjob.required No.
     */
    private volatile long timeout = DEFAULT_TIMEOUT_SECONDS;

    /**
     * @oddjob.property
     * @oddjob.description Progress of a download in a human-readable format. Only set
     * for a stream download.
     * @oddjob.required Read only.
     */
    private volatile Progress progress;


    /**
     * @oddjob.property
     * @oddjob.description Content length of a response.
     * @oddjob.required Read only.
     */
    private volatile long contentLength;

    /**
     * @oddjob.property
     * @oddjob.description The bytes downloaded so far. Only set for a stream download.
     * @oddjob.required Read only.
     */
    private volatile long downloadCount;

    private interface ResponseStrategy extends Closeable {

        Response doSend(Request request) throws ExecutionException, InterruptedException, TimeoutException, IOException;
    }

    class ContentResponseStrategy implements ResponseStrategy {

        @Override
        public Response doSend(Request request) throws ExecutionException, InterruptedException, TimeoutException {

            if (timeout > 0) {
                request.timeout(timeout, TimeUnit.SECONDS);
            }
            ContentResponse response = request.send();
            status = response.getStatus();
            responseBody = response.getContentAsString();
            contentLength = responseBody.length();
            return response;
        }

        @Override
        public void close() throws IOException {
            // Nothing to do.
        }
    }

    class StreamResponseStrategy implements ResponseStrategy {

        private final OutputStream outputStream;

        StreamResponseStrategy(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public Response doSend(Request request) throws ExecutionException, InterruptedException, TimeoutException, IOException {
            InputStreamResponseListener listener = new InputStreamResponseListener();

            progress = null;
            request.send(listener);

            Response response = listener.get(timeout, TimeUnit.SECONDS);

            status = response.getStatus();

            if (status == HttpStatus.OK_200) {
                HttpFields fields = response.getHeaders();
                contentLength = fields.getLongField("Content-Length");

                progress = new Progress(contentLength);

                try (InputStream inputStream = listener.getInputStream()) {
                    IoUtils.copy(inputStream, outputStream,
                            count -> {
                                progress.accept(count);
                                downloadCount = count;
                            });
                }
            }

            return response;
        }

        @Override
        public void close() throws IOException {
            outputStream.close();
        }
    }

    private interface RequestStrategy {

        Response doRequest(HttpClient httpClient, RequestConfiguration self)
                throws ExecutionException, InterruptedException, TimeoutException, IOException;
    }

    /**
     * Collect all request parameters together.
     */
    private class RequestConfiguration implements Closeable {

        private final String url;

        private final Map<String, String> parameters;

        private final String content;

        private final String contentType;

        private final URI uri;

        private final ResponseStrategy responseStrategy;

        public RequestConfiguration() throws URISyntaxException {

            url = JettyHttpClient.this.url;

            if (url == null) {
                throw new IllegalArgumentException("No URL");
            }

            if (JettyHttpClient.this.parameters == null) {
                parameters = null;
            } else {
                parameters = new LinkedHashMap<>(
                        JettyHttpClient.this.parameters);
            }

            content = JettyHttpClient.this.requestBody;
            contentType = JettyHttpClient.this.contentType;

            this.uri = new URI(url);

            if (output == null) {
                responseStrategy = new ContentResponseStrategy();
            } else {
                responseStrategy = new StreamResponseStrategy(output);
            }
        }

        @Override
        public void close() throws IOException {
            responseStrategy.close();
        }
    }

    public enum RequestMethod implements RequestStrategy {

        GET {
            @Override
            public Response doRequest(HttpClient httpClient, RequestConfiguration config)
                    throws ExecutionException, InterruptedException, TimeoutException, IOException {

                Request request = httpClient.newRequest(config.url);

                if (config.parameters != null) {
                    for (Map.Entry<String, String> entry : config.parameters.entrySet()) {
                        request.param(entry.getKey(), entry.getValue());
                    }
                }

                return config.responseStrategy.doSend(request);
            }
        },

        POST {
            @Override
            public Response doRequest(HttpClient httpClient, RequestConfiguration config)
                    throws InterruptedException, TimeoutException, ExecutionException, IOException {

                if (config.parameters == null) {

                    Request request = httpClient.POST(config.url);
                    request.content(new StringContentProvider(config.content),
                            config.contentType);

                    return config.responseStrategy.doSend(request);
                } else {

                    Fields fields = new Fields();

                    for (Map.Entry<String, String> entry : config.parameters.entrySet()) {
                        fields.put(entry.getKey(), entry.getValue());
                    }

                    return config.responseStrategy.doSend(
                            httpClient.POST(config.url).content(new FormContentProvider(fields)));
                }
            }
        },
    }

    @Override
    public Integer call() throws Exception {

        progress = null;
        responseBody = null;

        HttpClient httpClient = Optional.ofNullable(this.ssl)
                .map(ssl -> new HttpClient(ssl.provideClientSsl()))
                .orElseGet(HttpClient::new);
        try (RequestConfiguration config = new RequestConfiguration()) {

            httpClient.start();

            Optional.ofNullable(this.basicAuthentication)
                    .ifPresent(up ->
                            httpClient.getAuthenticationStore().addAuthentication(
                                    new BasicAuthentication(
                                            config.uri,
                                            Authentication.ANY_REALM,
                                            up.username,
                                            up.password)));

            if (method == null) {
                method = RequestMethod.GET;
            }

            logger.info("Making " + method + " request to " + config.url);

            Response response = method.doRequest(httpClient, config);

            if (HttpStatus.OK_200 == status) {
                logger.info("Response OK, response content length " + contentLength);
                return 0;
            } else {
                logger.info("Response " + HttpStatus.getCode(status) +
                        ", reason" + response.getReason());
                return 1;
            }
        } finally {
            httpClient.stop();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }

    /**
     * @oddjob.property content
     * @oddjob.description The request body to send or the response body received. This maps to
     * <cdoe>requestBody</cdoe> and <code>responseBody</code> as a convenience but is confusing
     * so should probably be deprecated.
     * @oddjob.required No.
     */
    public String getContent() {
        return responseBody;
    }

    public void setContent(String content) {
        this.requestBody = content;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> properties) {
        this.parameters = properties;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public UsernamePassword getBasicAuthentication() {
        return basicAuthentication;
    }

    public void setBasicAuthentication(UsernamePassword basicAuthentication) {
        this.basicAuthentication = basicAuthentication;
    }

    public ClientSslProvider getSsl() {
        return ssl;
    }

    public void setSsl(ClientSslProvider ssl) {
        this.ssl = ssl;
    }

    public Progress getProgress() {
        return progress;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        if (name == null) {
            return getClass().getSimpleName();
        } else {
            return name;
        }
    }

    public static class UsernamePassword {

        private volatile String username;

        private volatile String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
