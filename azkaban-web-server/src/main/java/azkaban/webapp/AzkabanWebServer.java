package azkaban.webapp;

import azakban.utils.Props;
import azkaban.server.AzkabanServer;
import azkaban.utils.StdOutErrRedirect;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AzkabanWebServer extends AzkabanServer {
    private static final Logger logger = Logger.getLogger(AzkabanWebServer.class);

    private final Props props;
    private final Server server;

    @Inject
    public AzkabanWebServer(final Props props, final Server server) {
        this.props = props;
        this.server = server;
    }

    public static void main(String[] args) {
        StdOutErrRedirect.redirectOutAndErrToLog();
        logger.info("Starting Jetty Azkaban Web Server...");
        final Props props = AzkabanServer.loadProps(new String[]{"-conf", "conf/azkaban.properties"});
    }
}
