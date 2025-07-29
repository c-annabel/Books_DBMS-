package viewlayer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * LoginServlet (View Layer)
 * Handles the initial login page and authentication.
 * If authentication is successful, redirects to the FrontController.
 *
 * Handles user login authentication.
 * This servlet verifies user credentials and creates an HTTP session
 * for authenticated users.
 */
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String VALID_USERNAME = "cst8288";
    private static final String VALID_PASSWORD = "cst8288";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Display the login form
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Enter DBMS Credentials</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
        out.println("        .login-container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center; width: 350px; }");
        out.println("        h2 { color: #333; margin-bottom: 20px; }");
        out.println("        .form-group { margin-bottom: 15px; text-align: left; }");
        out.println("        label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }");
        out.println("        input[type=\"text\"], input[type=\"password\"] { width: calc(100% - 22px); padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; }");
        out.println("        button { background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; transition: background-color 0.3s ease; width: 100%; }");
        out.println("        button:hover { background-color: #0056b3; }");
        out.println("        .error-message { color: red; margin-top: 10px; }");
        out.println("        .footer { margin-top: 30px; font-size: 0.9em; color: #777; line-height: 1.5; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class=\"login-container\">");
        out.println("        <h2>Enter DBMS Credentials</h2>");
        out.println("        <form action=\"login\" method=\"post\">");
        out.println("            <div class=\"form-group\">");
        out.println("                <label for=\"username\">Username:</label>");
        out.println("                <input type=\"text\" id=\"username\" name=\"username\" value=\"cst8288\" required>");
        out.println("            </div>");
        out.println("            <div class=\"form-group\">");
        out.println("                <label for=\"password\">Password:</label>");
        out.println("                <input type=\"password\" id=\"password\" name=\"password\" value=\"cst8288\" required>");
        out.println("            </div>");
        out.println("            <button type=\"submit\">Login</button>");
        // Display error message if present in request attributes
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
            out.println("<p class=\"error-message\">" + errorMessage + "</p>");
        }
        out.println("        </form>");
        out.println("        <div class=\"footer\">");
        out.println("            <p>Program by: Annabel Cheng (041146557)</p>");
        out.println("            <p>For: 25S CST8288 Section 013 Assignment 2</p>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            // Authentication successful
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true); // Mark session as authenticated
            response.sendRedirect("frontController"); // Redirect to FrontController
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid username or password. Please try again.");
            doGet(request, response); // Redisplay login page with error message
        }
    }
}
