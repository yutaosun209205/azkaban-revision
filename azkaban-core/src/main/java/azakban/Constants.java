package azakban;

/**
 * Constants used in configuration files or shared among classes.
 *
 * <p>Conventions:
 *
 * <p>Internal constants to be put in the {@link Constants} class
 *
 * <p>Configuration keys to be put in the {@link ConfigurationKeys} class
 *
 * <p>Flow level properties keys to be put in the {@link FlowProperties} class
 *
 * <p>Job level Properties keys to be put in the {@link JobProperties} class
 */
public class Constants {

    // Max number of memory check retry
    public static final int MEMORY_CHECK_RETRY_LIMIT = 720;
    public static final int DEFAULT_PORT_NUMBER = 8081;
    public static final int DEFAULT_SSL_PORT_NUMBER = 8443;
    public static final int DEFAULT_JETTY_MAX_THREAD_COUNT = 20;

    // Names and paths of various file names to configure Azkaban
    public static final String AZKABAN_PROPERTIES_FILE = "azkaban.properties";
    public static final String AZKABAN_PRIVATE_PROPERTIES_FILE = "azkaban.private.properties";
    public static final String DEFAULT_CONF_PATH = "conf";
    public static final String AZKABAN_EXECUTOR_PORT_FILENAME = "executor.port";
    public static final String AZKABAN_EXECUTOR_PORT_FILE = "executor.portfile";

    public static final String AZKABAN_SERVLET_CONTEXT_KEY = "azkaban_app";

}
