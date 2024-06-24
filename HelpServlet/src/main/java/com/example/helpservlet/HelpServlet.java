package com.example.helpservlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "HelpServlet", value = "/help-servlet")
public class HelpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String helpContent;

    public void init() {
        // Initialize help content (you can load it from a file or database)
        helpContent = "<h1>Help Page</h1>"
                + "<p>Welcome to the Help section of our application.</p>"
                + "<p>This page provides assistance on how to use our application effectively.</p>"
                + "<p>Feel free to contact support if you need further assistance.</p>";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><title>Help Servlet</title></head>");
            out.println("<body>");
            out.println(helpContent);
            out.println("</body>");
            out.println("</html>");
        }
    }

    public void destroy() {
        // Cleanup resources, if any
    }
}
