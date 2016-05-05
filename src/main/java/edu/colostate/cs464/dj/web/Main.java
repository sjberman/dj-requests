
package edu.colostate.cs464.dj.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ResourceHandler resources = new ResourceHandler();
		resources.setResourceBase("./public");
		resources.setWelcomeFiles(new String[] { "index.html" });
		resources.setDirectoriesListed(true);

		ContextHandler resourcesContext = new ContextHandler("/");
		resourcesContext.setHandler(resources);

		ServletContextHandler apiContext = new ServletContextHandler(server, "/");
		ServletHolder apiHolder = apiContext.addServlet(ServletContainer.class, "/api/*");
		apiHolder.setInitParameter(
				"jersey.config.server.provider.packages",
				"edu.colostate.cs464.dj.web");

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { resourcesContext, apiContext });
		//server.setHandler(contexts);
		//server.setHandler(apiContext);

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
