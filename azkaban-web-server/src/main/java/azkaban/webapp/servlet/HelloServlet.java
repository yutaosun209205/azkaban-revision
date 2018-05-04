package azkaban.webapp.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends AbstractAzkabanServlet {

    public HelloServlet(){

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Page page = newPage(req, resp, "azkaban\\webapp\\servlet\\velocity\\login.vm");
        page.add("passwordPlaceholder", this.passwordPlaceholder);
        /*if (errorMsg != null) {
            page.add("errorMsg", "ERROR");
        }*/

        page.render();
    }
}
