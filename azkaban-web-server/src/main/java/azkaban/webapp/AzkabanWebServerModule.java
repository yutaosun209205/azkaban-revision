package azkaban.webapp;

import com.google.inject.AbstractModule;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

public class AzkabanWebServerModule extends AbstractModule {
    private static final Logger log = Logger.getLogger(AzkabanWebServerModule.class);

    @Override
    protected void configure() {
        bind(Server.class).toProvider(WebServerProvider.class);
    }
}
