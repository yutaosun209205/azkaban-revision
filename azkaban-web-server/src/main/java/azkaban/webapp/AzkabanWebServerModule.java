package azkaban.webapp;

import azakban.utils.Props;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.JarResourceLoader;
import org.eclipse.jetty.server.Server;

import javax.inject.Singleton;

public class AzkabanWebServerModule extends AbstractModule {
    private static final Logger log = Logger.getLogger(AzkabanWebServerModule.class);

    private static final String VELOCITY_DEV_MODE_PARAM = "velocity.dev.mode";

    @Override
    protected void configure() {
        bind(Server.class).toProvider(WebServerProvider.class);
    }


    @Singleton
    @Provides
    public VelocityEngine createVelocityEngine(final Props props) {
        final boolean devMode = props.getBoolean(VELOCITY_DEV_MODE_PARAM, false);

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "classpath, jar");
        engine.setProperty("classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        engine.setProperty("classpath.resource.loader.cache", !devMode);
        engine.setProperty("classpath.resource.loader.modificationCheckInterval",
                5L);
        engine.setProperty("jar.resource.loader.class",
                JarResourceLoader.class.getName());
        engine.setProperty("jar.resource.loader.cache", !devMode);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("input.encoding", "UTF-8");
        engine.setProperty("output.encoding", "UTF-8");
        engine.setProperty("directive.set.null.allowed", true);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("velocimacro.permissions.allow.inline", true);
        engine.setProperty("velocimacro.library.autoreload", devMode);
        //engine.setProperty("velocimacro.library", "/azkaban/webapp/servlet/velocity/macros.vm");
        engine.setProperty(
                "velocimacro.permissions.allow.inline.to.replace.global", true);
        engine.setProperty("velocimacro.arguments.strict", true);
        engine.setProperty("runtime.log.invalid.references", devMode);
        engine.setProperty("runtime.log.logsystem.class", Log4JLogChute.class);
        engine.setProperty("runtime.log.logsystem.log4j.logger",
                Logger.getLogger("org.apache.velocity.Logger"));
        engine.setProperty("parser.pool.size", 3);
        return engine;
    }
}
