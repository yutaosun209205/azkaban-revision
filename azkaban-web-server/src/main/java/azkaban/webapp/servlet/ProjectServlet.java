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

import azkaban.webapp.AzkabanWebServer;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * The main page
 */
public class ProjectServlet extends LoginAbstractAzkabanServlet {

  private static final Logger logger = Logger.getLogger(ProjectServlet.class
      .getName());
  private static final String LOCKDOWN_CREATE_PROJECTS_KEY =
      "lockdown.create.projects";
  private static final long serialVersionUID = -1;


  private boolean lockdownCreateProjects = false;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    final AzkabanWebServer server = (AzkabanWebServer) getApplication();

    this.lockdownCreateProjects =
        server.getServerProps().getBoolean(LOCKDOWN_CREATE_PROJECTS_KEY, false);
    if (this.lockdownCreateProjects) {
      logger.info("Creation of projects is locked down");
    }
  }

  @Override
  protected void handleGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {


      handlePageRender(req, resp);
  }


  /**
   * Renders the user homepage that users see when they log in
   */
  private void handlePageRender(final HttpServletRequest req,
                                final HttpServletResponse resp) {
    final Page page =
        newPage(req, resp, "azkaban/webapp/servlet/velocity/index.vm");


    page.add("hideCreateProject", true);



    page.add("viewProjects", "all");


    page.render();
  }

  @Override
  protected void handlePost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    // TODO Auto-generated method stub
  }

}
