package azkaban.webapp;

import azakban.utils.Props;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;
import javax.inject.Provider;

public class WebServerProvider implements Provider<Server> {
    private static final Logger logger = Logger.getLogger(WebServerProvider.class);
    private static final int MAX_HEADER_BUFFER_SIZE = 10 * 1024 * 1024;

    @Inject
    private Props props;

    @Override
    public Server get() {
        return null;
    }
}
