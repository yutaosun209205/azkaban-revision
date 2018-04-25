package azkaban.webapp.servlet;

import azakban.utils.JSONUtils;
import azakban.utils.Props;
import azkaban.server.AzkabanServer;
import azkaban.utils.WebUtils;
import azkaban.webapp.AzkabanWebServer;
import org.joda.time.DateTime;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static azkaban.ServiceProvider.SERVICE_PROVIDER;

public class AbstractAzkabanServlet extends HttpServlet {
    private static final long serialVersionUID = -1;
    public static final String JSON_MIME_TYPE = "application/json";
    public static final String jarVersion = AbstractAzkabanServlet.class.getPackage().getImplementationVersion();
    protected static final WebUtils utils = new WebUtils();
    private static final String AZKABAN_SUCCESS_MESSAGE =
            "azkaban.success.message";
    private static final String AZKABAN_WARN_MESSAGE =
            "azkaban.warn.message";
    private static final String AZKABAN_FAILURE_MESSAGE =
            "azkaban.failure.message";

    protected String passwordPlaceholder;
    private AzkabanServer application;
    private String name;
    private String label;
    private String color;

    public static String createJsonResponse(final String status, final String message,
                                            final String action, final Map<String, Object> params) {
        final HashMap<String, Object> response = new HashMap<>();
        response.put("status", status);
        if (message != null) {
            response.put("message", message);
        }
        if (action != null) {
            response.put("action", action);
        }
        if (params != null) {
            response.putAll(params);
        }

        return JSONUtils.toJSON(response);
    }

    /**
     * To retrieve the application for the servlet
     */
    public AzkabanServer getApplication() {
        return this.application;
    }


    @Override
    public void init(final ServletConfig config) throws ServletException {
        this.application = SERVICE_PROVIDER.getInstance(AzkabanWebServer.class);

        if (this.application == null) {
            throw new IllegalStateException(
                    "No batch application is defined in the servlet context!");
        }

        final Props props = this.application.getServerProps();
        this.name = props.getString("azkaban.name", "");
        this.label = props.getString("azkaban.label", "");
        this.color = props.getString("azkaban.color", "#FF0000");
        this.passwordPlaceholder = props.getString("azkaban.password.placeholder", "Password");

        if (this.application instanceof AzkabanWebServer) {
            final AzkabanWebServer server = (AzkabanWebServer) this.application;
            //this.viewerPlugins = PluginRegistry.getRegistry().getViewerPlugins();
            //this.triggerPlugins =
                    //new ArrayList<>(server.getTriggerPlugins().values());
        }
    }

    /**
     * Creates a new velocity page to use.
     */
    protected Page newPage(final HttpServletRequest req, final HttpServletResponse resp,
                           final String template) {
        final Page page = new Page(req, resp, getApplication().getVelocityEngine(), template);
        page.add("version", jarVersion);
        page.add("azkaban_name", this.name);
        page.add("azkaban_label", this.label);
        page.add("azkaban_color", this.color);
        //page.add("note_type", NoteServlet.type);
        //page.add("note_message", NoteServlet.message);
        //page.add("note_url", NoteServlet.url);
        page.add("timezone", TimeZone.getDefault().getID());
        page.add("currentTime", (new DateTime()).getMillis());
        page.add("context", req.getContextPath());

        // @TODO, allow more than one type of viewer. For time sake, I only install
        // the first one
        /*if (this.viewerPlugins != null && !this.viewerPlugins.isEmpty()) {
            page.add("viewers", this.viewerPlugins);
            final ViewerPlugin plugin = this.viewerPlugins.get(0);
            page.add("viewerName", plugin.getPluginName());
            page.add("viewerPath", plugin.getPluginPath());
        }

        if (this.triggerPlugins != null && !this.triggerPlugins.isEmpty()) {
            page.add("triggers", this.triggerPlugins);
        }*/

        return page;
    }
}
