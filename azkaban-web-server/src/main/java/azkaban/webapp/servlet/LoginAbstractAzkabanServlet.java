/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.webapp.servlet;

import azkaban.utils.WebUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Servlet that handles auto login when the session hasn't been verified.
 */
public abstract class LoginAbstractAzkabanServlet extends
        AbstractAzkabanServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger
      .getLogger(LoginAbstractAzkabanServlet.class.getName());
  private static final String SESSION_ID_NAME = "azkaban.browser.session.id";
  private static final int DEFAULT_UPLOAD_DISK_SPOOL_SIZE = 20 * 1024 * 1024;

  private static final HashMap<String, String> contextType =
      new HashMap<>();

  static {
    contextType.put(".js", "application/javascript");
    contextType.put(".css", "text/css");
    contextType.put(".png", "image/png");
    contextType.put(".jpeg", "image/jpeg");
    contextType.put(".gif", "image/gif");
    contextType.put(".jpg", "image/jpeg");
    contextType.put(".eot", "application/vnd.ms-fontobject");
    contextType.put(".svg", "image/svg+xml");
    contextType.put(".ttf", "application/octet-stream");
    contextType.put(".woff", "application/x-font-woff");
  }

  //private final WebMetrics webMetrics = SERVICE_PROVIDER.getInstance(WebMetrics.class);
  private File webResourceDirectory = null;
  //private MultipartParser multipartParser;
  private boolean shouldLogRawUserAgent = false;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

    //this.multipartParser = new MultipartParser(DEFAULT_UPLOAD_DISK_SPOOL_SIZE);

    this.shouldLogRawUserAgent =
        getApplication().getServerProps().getBoolean("accesslog.raw.useragent",
            false);
  }

  public void setResourceDirectory(final File file) {
    this.webResourceDirectory = file;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

    handleGet(req,resp);
  }



  private boolean handleFileGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    if (this.webResourceDirectory == null) {
      return false;
    }

    // Check if it's a resource
    final String prefix = req.getContextPath() + req.getServletPath();
    final String path = req.getRequestURI().substring(prefix.length());
    final int index = path.lastIndexOf('.');
    if (index == -1) {
      return false;
    }

    final String extension = path.substring(index);
    if (contextType.containsKey(extension)) {
      final File file = new File(this.webResourceDirectory, path);
      if (!file.exists() || !file.isFile()) {
        return false;
      }

      resp.setContentType(contextType.get(extension));

      final OutputStream output = resp.getOutputStream();
      BufferedInputStream input = null;
      try {
        input = new BufferedInputStream(new FileInputStream(file));
        IOUtils.copy(input, output);
      } finally {
        if (input != null) {
          input.close();
        }
      }
      output.flush();
      return true;
    }

    return false;
  }

  private String getRealClientIpAddr(final HttpServletRequest req) {

    // If some upstream device added an X-Forwarded-For header
    // use it for the client ip
    // This will support scenarios where load balancers or gateways
    // front the Azkaban web server and a changing Ip address invalidates
    // the session
    final HashMap<String, String> headers = new HashMap<>();
    headers.put(WebUtils.X_FORWARDED_FOR_HEADER,
        req.getHeader(WebUtils.X_FORWARDED_FOR_HEADER.toLowerCase()));

    final WebUtils utils = new WebUtils();

    return utils.getRealClientIpAddr(headers, req.getRemoteAddr());
  }



  private void handleLogin(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    handleLogin(req, resp, null);
  }

  private void handleLogin(final HttpServletRequest req, final HttpServletResponse resp,
                           final String errorMsg) throws ServletException, IOException {
    final Page page = newPage(req, resp, "azkaban/webapp/servlet/velocity/login.vm");
    page.add("passwordPlaceholder", this.passwordPlaceholder);
    if (errorMsg != null) {
      page.add("errorMsg", errorMsg);
    }

    page.render();
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

      handlePost(req, resp);
  }

  /**
   * Disallows users from logging in by passing their username and password via the request header
   * where it'd be logged.
   *
   * Example of illegal post request: curl -X POST http://localhost:8081/?action=login\&username=azkaban\&password=azkaban
   *
   * req.getParameterMap() or req.getParameterNames() cannot be used because they draw no
   * distinction between the illegal request above and the following valid request: curl -X POST -d
   * "action=login&username=azkaban&password=azkaban" http://localhost:8081/
   *
   * "password=" is searched for because it leverages the query syntax to determine that the user is
   * passing the password as a parameter name. There is no other ajax call that has a parameter that
   * includes the string "password" at the end which could throw false positives.
   */
  private boolean isIllegalPostRequest(final HttpServletRequest req) {
    return (req.getQueryString() != null && req.getQueryString().contains("password="));
  }



  protected void writeResponse(final HttpServletResponse resp, final String response)
      throws IOException {
    final Writer writer = resp.getWriter();
    writer.append(response);
    writer.flush();
  }

  protected boolean isAjaxCall(final HttpServletRequest req) throws ServletException {
    final String value = req.getHeader("X-Requested-With");
    if (value != null) {
      logger.info("has X-Requested-With " + value);
      return value.equals("XMLHttpRequest");
    }

    return false;
  }

  /**
   * The get request is handed off to the implementor after the user is logged in.
   */
  protected abstract void handleGet(HttpServletRequest req,
                                    HttpServletResponse resp) throws ServletException,
      IOException;

  /**
   * The post request is handed off to the implementor after the user is logged in.
   */
  protected abstract void handlePost(HttpServletRequest req,
                                     HttpServletResponse resp) throws ServletException,
      IOException;

  /**
   * The post request is handed off to the implementor after the user is logged in.
   */
  protected void handleMultiformPost(final HttpServletRequest req,
                                     final HttpServletResponse resp, final Map<String, Object> multipart)
      throws ServletException, IOException {
  }
}
