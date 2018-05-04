package azkaban.webapp;

import azakban.utils.Props;
import azkaban.AzkabanCommonModule;
import azkaban.ServiceProvider;
import azkaban.server.AzkabanServer;
import azkaban.utils.StdOutErrRedirect;
import azkaban.webapp.servlet.IndexRedirectServlet;
import azkaban.webapp.servlet.ProjectServlet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

@Singleton
public class AzkabanWebServer extends AzkabanServer {
    private static final Logger logger = Logger.getLogger(AzkabanWebServer.class);
    private static final int MAX_FORM_CONTENT_SIZE = 10 * 1024 * 1024;
    private static final String DEFAULT_STATIC_DIR = "";

    private final Props props;
    private final Server server;
    private final VelocityEngine velocityEngine;

    @Inject
    public AzkabanWebServer(final Props props, final Server server, final VelocityEngine velocityEngine) {
        this.props = props;
        this.server = server;
        this.velocityEngine = requireNonNull(velocityEngine, "velocityEngine is null.");
    }

    public static void main(String[] args) throws Exception{
        StdOutErrRedirect.redirectOutAndErrToLog();
        logger.info("Starting Jetty Azkaban Web Server...");
        final Props props = AzkabanServer.loadProps(new String[]{"-conf", "local\\conf"});
        if(props == null){
            logger.error("Azkaban Properties not loaded. Exiting..");
            System.exit(1);
        }

        Injector injector = Guice.createInjector(
                new AzkabanCommonModule(props),
                new AzkabanWebServerModule()
                );
        ServiceProvider.SERVICE_PROVIDER.setInjector(injector);
        lanch(injector.getInstance(AzkabanWebServer.class));
    }

    public static void lanch(final AzkabanWebServer azkabanWebServer) throws Exception{

        azkabanWebServer.prepareAndStartServer();

    }

    private void prepareAndStartServer() throws Exception{
        addThreadPoolGauges(this.server.getThreadPool());
        configureRoutes();

        try{
            this.server.start();
        }catch (final Exception e){
            logger.error(e);
        }
    }

    private void configureRoutes(){
        final String staticDir =
                this.props.getString("web.resource.dir", DEFAULT_STATIC_DIR);
        logger.info("Setting up web resource dir " + staticDir);

        //One ServletContext
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        this.server.setHandler(contextHandler);

        //Set the maximum size of a form post, to protect against DOS attacks from large forms
        contextHandler.setMaxFormContentSize(MAX_FORM_CONTENT_SIZE);
        final String defaultServletPath =
                this.props.getString("azkaban.default.servlet.path", "/index");

        //equals to 'contextHandler.setBaseResource(staticDir);'
        contextHandler.setResourceBase(staticDir);

        contextHandler.addServlet(new ServletHolder(new ProjectServlet()),"/index");

        contextHandler.addServlet(new ServletHolder(new IndexRedirectServlet(defaultServletPath)),"/");
        final ServletHolder staticServlet = new ServletHolder(new DefaultServlet());
        contextHandler.addServlet(staticServlet, "/css/*");
        contextHandler.addServlet(staticServlet, "/js/*");
        contextHandler.addServlet(staticServlet, "/images/*");
        contextHandler.addServlet(staticServlet, "/fonts/*");
        contextHandler.addServlet(staticServlet, "/favicon.ico");




        //contextHandler.addServlet(new ServletHolder(new HelloServlet()), "/");






    }

    private void addThreadPoolGauges(final ThreadPool threadPool) {
        //TODO
        QueuedThreadPool queuedThreadPool = (QueuedThreadPool) threadPool;

    }




    @Override
    public Props getServerProps() {
        return this.props;
    }

    @Override
    public VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }
}
