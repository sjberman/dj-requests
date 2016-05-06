
package edu.colostate.cs464.dj.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ResourceHandler resources = new ResourceHandler();
		resources.setResourceBase("./public");
		resources.setWelcomeFiles(new String[] { "request.html", "index.html" });
		resources.setDirectoriesListed(true);

		ContextHandler resourcesContext = new ContextHandler("/");
		resourcesContext.setHandler(resources);

		ResourceConfig config = new ResourceConfig();
		config.register(JacksonFeature.class);
		config.packages("edu.colostate.cs464.dj.web");

		ServletHolder apiHolder = new ServletHolder(new ServletContainer(config));
		ServletHolder twilioHolder = new ServletHolder(TwilioServlet.class);

		ServletContextHandler apiContext = new ServletContextHandler(server, "/");
		apiContext.addServlet(apiHolder, "/api/*");
		apiContext.addServlet(twilioHolder, "/sms");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
			resourcesContext,
			apiContext
		});
		server.setHandler(handlers);

		server.start();
		server.dumpStdErr();
		server.join();
	}

}
