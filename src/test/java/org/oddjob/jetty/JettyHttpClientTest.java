package org.oddjob.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.state.ParentState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JettyHttpClientTest {

	@Test
	public void testExample() throws Exception {
		
		ResourceHandlerType resourceHandler = new ResourceHandlerType();
		resourceHandler.setBase("org/oddjob/jetty");
		resourceHandler.setResourceType(ResourceHandlerType.ResourceType.CLASSPATH);
		
		JettyHttpServer server = new JettyHttpServer();
		server.setHandler(resourceHandler.toValue());
		
		server.start();
		
		File file = new File(Objects.requireNonNull(
				getClass().getResource("ClientGetExample.xml")).getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		Properties properties = new Properties();
		properties.setProperty("some.url", "http://localhost:" + server.getPort() + 
				"/TestFile.txt");

		oddjob.setProperties(properties);
		

		oddjob.run();
		
		assertThat("Is complete.",
				oddjob.lastStateEvent().getState().isComplete(), is(true));
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		String result = lookup.lookup("request.content", String.class);
		
		assertThat(result.trim(), is("Test"));
		
		oddjob.destroy();
		
		server.stop();
	}

	@Test
	public void tesDownloadExample() throws Exception {

		Path workPath = OurDirs.workPathDir(SslExamplesTest.class);
		Path downloadFile = workPath.resolve("download.txt");

		BigContent bigContent = new BigContent();
		bigContent.setLines(1000);

		JettyHttpServer server = new JettyHttpServer();
		server.setHandler(bigContent);

		server.start();

		File file = new File(Objects.requireNonNull(
				getClass().getResource("DownloadExample.xml")).getFile());

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);

		Properties properties = new Properties();
		properties.setProperty("some.url", "http://localhost:" + server.getPort());
		properties.setProperty("some.file", downloadFile.toString());
		oddjob.setProperties(properties);


		oddjob.run();

		assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

		try (Stream<String> lines = Files.lines(downloadFile)) {
			long lineCount = lines.count();

			assertThat(lineCount, is(1000L));
		}

		OddjobLookup lookup = new OddjobLookup(oddjob);
		String progress = lookup.lookup("request.progress", String.class);

		assertThat(progress, Matchers.containsString("100%"));

		oddjob.destroy();

		server.stop();
	}

	static final String MESSAGE = "I LOVE ODDJOB\n";

	public static class BigContent extends AbstractHandler {

		private  long lines;

		private long delay;

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

			response.setHeader("content-length", Long.toString(MESSAGE.length() * lines));

			try (OutputStream outputStream = response.getOutputStream()) {

				for (int i = 0; i < lines; i++) {
					if (delay > 0) {
						try {
							//noinspection BusyWait
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
					outputStream.write(MESSAGE.getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		public long getLines() {
			return lines;
		}

		public void setLines(long lines) {
			this.lines = lines;
		}

		public long getDelay() {
			return delay;
		}

		public void setDelay(long delay) {
			this.delay = delay;
		}
	}

}


