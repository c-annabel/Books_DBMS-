package viewlayer;

import businesslayer.AuthorService;
import businesslayer.TitleService;
import transferobjects.AuthorDTO;
import transferobjects.TitleDTO;

// Changed from javax.servlet to jakarta.servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*******************************************************************************
 * FrontController (View Layer - Front Controller Pattern)
 * handles all incoming requests after authentication, processes 
 * user actions (such as retrieving, adding, updating, and deleting authors and titles), 
 * and generates HTML responses dynamically. It uses the AuthorService and TitleService 
 * classes from the business layer and DTOs for data transfer.
 * 
 * This servlet acts as the central point for all requests after login.
 * It dispatches requests to appropriate business logic methods and
 * formats the output as HTML tables.
 *
 * IMPORTANT: This version is updated to match the books-MySQL.sql schema
 * which uses ISBN for Titles and a join table AuthorISBN for many-to-many.
 * 
 * @see FrontController.java
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: 
 *   This Java servlet implements the Front Controller Pattern for a web-based
 *   database management system that interacts with Authors and Titles.
 *****************************************************************************/

/**
 * Servlet that serves as the Front Controller in the MVC pattern.
 * It handles all incoming requests after login and delegates actions
 * to the appropriate business services for processing.
 */
public class FrontController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
/** Service class that manages business logic for Author operations. */
    private AuthorService authorService;
    
/** Service class that manages business logic for Title operations. */
    private TitleService titleService;

    /**
     * Initializes the servlet and sets up required service instances.
     *
     * @throws ServletException if initialization fails
     */ 
    @Override
    public void init() throws ServletException {
        super.init();
        authorService = new AuthorService();
        titleService = new TitleService();
    }

    /**
     * Handles HTTP GET requests by delegating to processRequest().
     *
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles HTTP POST requests by delegating to processRequest().
     *
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * Processes incoming requests and routes actions to appropriate methods.
     * Generates HTML response including forms, tables, and status messages.
     *
     * @param request  HttpServletRequest containing request data and parameters
     * @param response HttpServletResponse for sending the response to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !(Boolean)session.getAttribute("authenticated")) {
            response.sendRedirect("login");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>DBMS Operations</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 20px; color: #333; }");
        out.println("        .container { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); max-width: 1000px; margin: 20px auto; }");
        out.println("        h1, h2 { color: #007bff; text-align: center; margin-bottom: 20px; }");
        out.println("        .button-group { text-align: left; margin-bottom: 20px; display: flex; flex-wrap: wrap; justify-content: left; gap: 10px; }");
        out.println("        .button-group button, .button-group input[type=\"submit\"] { background-color: #28a745; color: white; padding: 10px 15px; border: none; border-radius: 5px; cursor: pointer; font-size: 14px; transition: background-color 0.3s ease; white-space: nowrap; }");
        out.println("        .button-group button:hover, .button-group input[type=\"submit\"]:hover { background-color: #218838; }");
        out.println("        .button-group input[type=\"text\"], .button-group input[type=\"number\"] { padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 120px; }");
        out.println("        .form-row { display: flex; flex-wrap: wrap; justify-content: left; align-items: left; gap: 10px; margin-bottom: 10px; }");
        out.println("        .form-row label { margin-right: 5px; }");
        out.println("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        out.println("        th { background-color: #007bff; color: white; }");
        out.println("        tr:nth-child(even) { background-color: #f2f2f2; }");
        out.println("        .message { padding: 10px; margin-top: 20px; border-radius: 5px; }");
        out.println("        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }");
        out.println("        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }");
        out.println("        .info { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }");
        out.println("        .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; text-align: center; font-size: 0.9em; color: #777; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class=\"container\">");
        out.println("        <h1>DBMS Operations</h1>");

        String message = (String) request.getAttribute("message");
        String messageType = (String) request.getAttribute("messageType");
        if (message != null && messageType != null) {
            out.println("<div class=\"message " + messageType + "\">" + message + "</div>");
        }
        
        out.println("    <div style='text-align:right; margin-bottom:20px;'>");
        out.println("       <form action='frontController' method='post' style='display:inline;'>");
        out.println("           <input type='hidden' name='action' value='logout'>");
        out.println("           <button type='submit' style='background-color:red; color:white; padding:8px 16px; border:none; border-radius:5px; cursor:pointer;'>Logout</button>");
        out.println("       </form>");
        out.println("    </div>");

        // --- Author Operations ---
        out.println("        <h2>Author Operations</h2>");
        out.println("        <div class=\"button-group\">");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\" style=\"margin-right: 17px;\">");
        out.println("                <input type=\"hidden\" name=\"action\" value=\"getAllAuthors\">");
        out.println("                <button type=\"submit\" style=\"background-color: orange;\">Get All Authors</button>");
        out.println("            </form>");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\">");
        out.println("                <input type=\"number\" name=\"authorId\" placeholder=\"Author ID\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"getAuthorById\"       value=\"Get Author By ID\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"getTitlesByAuthorId\" value=\"Get Titles By Author\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"deleteAuthorById\"    value=\"Delete Author\">");
        out.println("            </form>");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\" style=\"flex-basis:100%;\">");
        out.println("                <input type=\"text\" name=\"firstName\" placeholder=\"First Name\">");
        out.println("                <input type=\"text\" name=\"lastName\" placeholder=\"Last Name\">");
        out.println("                <input type=\"hidden\" name=\"action\" value=\"addAuthor\">");
        out.println("                <input type=\"submit\" value=\"Add Author\">");
        out.println("            </form>");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\" style=\"flex-basis:100%;\">");
        out.println("                <input type=\"number\" name=\"authorIdUpdate\"  placeholder=\"Author ID\">");
        out.println("                <input type=\"text\"   name=\"firstNameUpdate\" placeholder=\"New First Name\">");
        out.println("                <input type=\"text\"   name=\"lastNameUpdate\"  placeholder=\"New Last Name\">");
        out.println("                <input type=\"hidden\" name=\"action\" value=\"updateAuthor\">");
        out.println("                <input type=\"submit\" value=\"Update Author\">");
        out.println("            </form>");
        out.println("        </div>");

        // --- Title Operations ---
        out.println("        <h2>Title Operations (ISBN based)</h2>");
        out.println("        <div class=\"button-group\">");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\" style=\"margin-right: 32px;\">");
        out.println("                <input type=\"hidden\" name=\"action\" value=\"getAllTitles\">");
        out.println("                <button type=\"submit\" style=\"background-color: orange;\">Get All Titles  </button>");
        out.println("            </form>");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\">");
        out.println("                <input type=\"text\" name=\"titleISBN\" placeholder=\"Title ISBN\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"getTitleByISBN\"     value=\"Get Title By ISBN\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"getAuthorsForTitle\" value=\"Get Authors For Title\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"deleteTitle\"        value=\"Delete Title\">");
        out.println("            </form><br>");
        out.println("            <form action=\"frontController\" method=\"post\" class=\"form-row\">");
        out.println("                <input type=\"text\" name=\"newTitleISBN\" placeholder=\"ISBN (e.g., 013...) \">");
        out.println("                <input type=\"text\" name=\"newTitleName\" placeholder=\"Title Name\" style=\"width:150px;\">");
        out.println("                <input type=\"number\" name=\"newEditionNumber\" placeholder=\"Edition #\" style=\"width:50px;\">");
        out.println("                <input type=\"text\" name=\"newCopyright\" placeholder=\"Copyright (YYYY)\" style=\"width:80px;\">");
        out.println("                <input type=\"text\" name=\"newAuthorIds\" placeholder=\"Author IDs (comma-separated)\" style=\"width:180px;\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"addTitle\"    value=\"Add Title\">");
        out.println("                <input type=\"submit\" name=\"action\" value=\"updateTitle\" value=\"Update Title\">");
        out.println("            </form>");
        out.println("        </div>");


        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                // --- Author Actions ---
                case "getAllAuthors":
                    displayAllAuthors(out);
                    break;
                case "getAuthorById":
                    getAuthorById(request, out);
                    break;
                case "addAuthor":
                    addAuthor(request, out);
                    break;
                case "updateAuthor":
                    updateAuthor(request, out);
                    break;
                case "deleteAuthor":
                    deleteAuthor(request, out);
                    break;
                case "getTitlesByAuthor":
                    getTitlesByAuthor(request, out);
                    break;

                // --- Title Actions ---
                case "getAllTitles":
                    displayAllTitles(out);
                    break;
                case "getTitleByISBN":
                    getTitleByISBN(request, out);
                    break;
                case "addTitle":
                    addTitle(request, out);
                    break;
                case "updateTitle":
                    updateTitle(request, out);
                    break;
                case "deleteTitle":
                    deleteTitle(request, out);
                    break;
                case "getAuthorsForTitle":
                    getAuthorsForTitle(request, out);
                    break;
                case "logout":
                    logout(request, response);
                    break;
                default:
                    out.println("<p class=\"error\">Unknown action: " + action + "</p>");
                    break;
            }
        } else {
            out.println("<p class=\"info\">Select an operation above.</p>");
            displayAllAuthors(out);
            displayAllTitles(out);
        }

        out.println("        <div class=\"footer\">");
        out.println("            <p>Program by: Annabel Cheng (041146557)</p>"); // Updated with your name
        out.println("            <p>For: 25S CST8288 Section 013 Assignment 2</p>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }

    // --- Helper methods for Author Operations ---
    /**
     * Displays all authors in a tabular format.
     *
     * @param out PrintWriter for writing HTML output
     */

    private void displayAllAuthors(PrintWriter out) {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        out.println("<h3>All Authors:</h3>");
        if (authors.isEmpty()) {
            out.println("<p class=\"info\">No authors found.</p>");
        } else {
            out.println("<table>");
            out.println("    <thead>");
            out.println("        <tr><th>Author ID</th><th>First Name</th><th>Last Name</th></tr>");
            out.println("    </thead>");
            out.println("    <tbody>");
            for (AuthorDTO author : authors) {
                out.println("        <tr>");
                out.println("            <td>" + author.getId() + "</td>");
                out.println("            <td>" + escapeHtml(author.getFirstName()) + "</td>");
                out.println("            <td>" + escapeHtml(author.getLastName()) + "</td>");
                out.println("        </tr>");
            }
            out.println("    </tbody>");
            out.println("</table>");
        }
    }
    
    /**
     * Retrieves and displays an author by ID.
     *
     * @param request HttpServletRequest containing authorId parameter
     * @param out     PrintWriter for writing HTML output
     */
    private void getAuthorById(HttpServletRequest request, PrintWriter out) {
        try {
            int id = Integer.parseInt(request.getParameter("authorId"));
            AuthorDTO author = authorService.getAuthorById(id);
            out.println("<h3>Author with ID " + id + ":</h3>");
            if (author != null) {
                out.println("<table>");
                out.println("    <thead>");
                out.println("        <tr><th>Author ID</th><th>First Name</th><th>Last Name</th></tr>");
                out.println("    </thead>");
                out.println("    <tbody>");
                out.println("        <tr>");
                out.println("            <td>" + author.getId() + "</td>");
                out.println("            <td>" + escapeHtml(author.getFirstName()) + "</td>");
                out.println("            <td>" + escapeHtml(author.getLastName()) + "</td>");
                out.println("        </tr>");
                out.println("    </tbody>");
                out.println("</table>");
            } else {
                out.println("<p class=\"error\">Error: Author with ID " + id + " not found.</p>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Author ID format. Please enter a number.</p>");
        }
    }
    
    /**
     * Adds a new author based on user input.
     *
     * @param request HttpServletRequest containing author details
     * @param out     PrintWriter for writing HTML output
     */
    private void addAuthor(HttpServletRequest request, PrintWriter out) {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
            boolean success = authorService.addAuthor(firstName, lastName);
            if (success) {
                out.println("<p class=\"success\">Author added successfully!</p>");
                displayAllAuthors(out);
            } else {
                out.println("<p class=\"error\">Error: Failed to add author.</p>");
            }
        } else {
            out.println("<p class=\"error\">Error: First Name and Last Name cannot be empty for adding an author.</p>");
        }
    }
    
    /**
     * Updates an existing author's details.
     *
     * @param request HttpServletRequest containing updated author details
     * @param out     PrintWriter for writing HTML output
     */
    private void updateAuthor(HttpServletRequest request, PrintWriter out) {
        try {
            int id = Integer.parseInt(request.getParameter("authorIdUpdate"));
            String firstName = request.getParameter("firstNameUpdate");
            String lastName = request.getParameter("lastNameUpdate");

            if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
                boolean success = authorService.updateAuthor(id, firstName, lastName);
                if (success) {
                    out.println("<p class=\"success\">Author with ID " + id + " updated successfully!</p>");
                    displayAllAuthors(out);
                } else {
                    out.println("<p class=\"error\">Error: Author with ID " + id + " not found or failed to update.</p>");
                }
            } else {
                out.println("<p class=\"error\">Error: New First Name and Last Name cannot be empty for updating an author.</p>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Author ID format for update. Please enter a number.</p>");
        }
    }
    
    /**
     * Deletes an author by ID.
     *
     * @param request HttpServletRequest containing authorId parameter
     * @param out     PrintWriter for writing HTML output
     */
    private void deleteAuthor(HttpServletRequest request, PrintWriter out) {
        try {
            int id = Integer.parseInt(request.getParameter("authorId"));
            boolean success = authorService.deleteAuthor(id);
            if (success) {
                out.println("<p class=\"success\">Author with ID " + id + " deleted successfully!</p>");
                displayAllAuthors(out);
            } else {
                out.println("<p class=\"error\">Error: Author with ID " + id + " not found or failed to delete.</p>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Author ID format for delete. Please enter a number.</p>");
        }
    }
    
    /**
     * Retrieves and displays titles associated with a specific author.
     *
     * @param request HttpServletRequest containing authorId parameter
     * @param out     PrintWriter for writing HTML output
     */
    private void getTitlesByAuthor(HttpServletRequest request, PrintWriter out) {
        try {
            int id = Integer.parseInt(request.getParameter("authorId"));
            AuthorDTO author = authorService.getAuthorById(id);
            if (author == null) {
                out.println("<p class=\"error\">Error: Author with ID " + id + " not found.</p>");
                return;
            }
            List<TitleDTO> titles = authorService.getTitlesByAuthor(id);
            out.println("<h3>Titles by Author: " + escapeHtml(author.getFirstName()) + " " + escapeHtml(author.getLastName()) + " (ID: " + id + ")</h3>");
            if (titles.isEmpty()) {
                out.println("<p class=\"info\">No titles found for this author.</p>");
            } else {
                out.println("<table>");
                out.println("    <thead>");
                out.println("        <tr><th>ISBN</th><th>Title</th><th>Edition #</th><th>Copyright</th></tr>");
                out.println("    </thead>");
                out.println("    <tbody>");
                for (TitleDTO title : titles) {
                    out.println("        <tr>");
                    out.println("            <td>" + escapeHtml(title.getIsbn()) + "</td>");
                    out.println("            <td>" + escapeHtml(title.getTitle()) + "</td>");
                    out.println("            <td>" + title.getEditionNumber() + "</td>");
                    out.println("            <td>" + escapeHtml(title.getCopyright()) + "</td>");
                    out.println("        </tr>");
                }
                out.println("    </tbody>");
                out.println("</table>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Author ID format for getting titles by author. Please enter a number.</p>");
        }
    }

    // --- Helper methods for Title Operations ---
    /**
     * Displays all titles along with their associated authors.
     *
     * @param out PrintWriter for writing HTML output
     */
    private void displayAllTitles(PrintWriter out) {
        List<TitleDTO> titles = titleService.getAllTitles();
        out.println("<h3>All Titles:</h3>");
        if (titles.isEmpty()) {
            out.println("<p class=\"info\">No titles found.</p>");
        } else {
            out.println("<table>");
            out.println("    <thead>");
            out.println("        <tr><th>ISBN</th><th>Title</th><th>Edition #</th><th>Copyright</th><th>Authors</th></tr>");
            out.println("    </thead>");
            out.println("    <tbody>");
            for (TitleDTO title : titles) {
                List<AuthorDTO> authorsForTitle = titleService.getAuthorsForTitle(title.getIsbn());
                String authorNames = authorsForTitle.stream()
                                                    .map(a -> escapeHtml(a.getFirstName()) + " " + escapeHtml(a.getLastName()) + " (ID: " + a.getId() + ")")
                                                    .collect(Collectors.joining("<br>"));
                if (authorNames.isEmpty()) {
                    authorNames = "N/A";
                }

                out.println("        <tr>");
                out.println("            <td>" + escapeHtml(title.getIsbn()) + "</td>");
                out.println("            <td>" + escapeHtml(title.getTitle()) + "</td>");
                out.println("            <td>" + title.getEditionNumber() + "</td>");
                out.println("            <td>" + escapeHtml(title.getCopyright()) + "</td>");
                out.println("            <td>" + authorNames + "</td>");
                out.println("        </tr>");
            }
            out.println("    </tbody>");
            out.println("</table>");
        }
    }
    
    /**
     * Retrieves and displays a title by its ISBN along with associcated authors.
     * 
     * @param request HttpServletRequest containing the parameter "titleISBN"
     * @param out PrintWriter used to generate HTML response
     */ 
    private void getTitleByISBN(HttpServletRequest request, PrintWriter out) {
        String isbn = request.getParameter("titleISBN");
        if (isbn == null || isbn.trim().isEmpty()) {
            out.println("<p class=\"error\">Error: Title ISBN cannot be empty.</p>");
            return;
        }

        TitleDTO title = titleService.getTitleByISBN(isbn);
        out.println("<h3>Title with ISBN " + escapeHtml(isbn) + ":</h3>");
        if (title != null) {
            List<AuthorDTO> authorsForTitle = titleService.getAuthorsForTitle(title.getIsbn());
            String authorNames = authorsForTitle.stream()
                                                .map(a -> escapeHtml(a.getFirstName()) + " " + escapeHtml(a.getLastName()) + " (ID: " + a.getId() + ")")
                                                .collect(Collectors.joining("<br>"));
            if (authorNames.isEmpty()) {
                authorNames = "N/A";
            }

            out.println("<table>");
            out.println("    <thead>");
            out.println("        <tr><th>ISBN</th><th>Title</th><th>Edition #</th><th>Copyright</th><th>Authors</th></tr>");
            out.println("    </thead>");
            out.println("    <tbody>");
            out.println("        <tr>");
            out.println("            <td>" + escapeHtml(title.getIsbn()) + "</td>");
            out.println("            <td>" + escapeHtml(title.getTitle()) + "</td>");
            out.println("            <td>" + title.getEditionNumber() + "</td>");
            out.println("            <td>" + escapeHtml(title.getCopyright()) + "</td>");
            out.println("            <td>" + authorNames + "</td>");
            out.println("        </tr>");
            out.println("    </tbody>");
            out.println("</table>");
        } else {
            out.println("<p class=\"error\">Error: Title with ISBN " + escapeHtml(isbn) + " not found.</p>");
        }
    }
    
    /**
     * Adds a new title with its details (ISBN, name, edition, copyright)
     * and associates it with provided author IDs.
     *
     * @param request HttpServletRequest containing title details and author IDs
     * @param out     PrintWriter used to generate HTML response
     */
    private void addTitle(HttpServletRequest request, PrintWriter out) {
        String isbn = request.getParameter("newTitleISBN");
        String titleName = request.getParameter("newTitleName");
        String editionNumberStr = request.getParameter("newEditionNumber");
        String copyright = request.getParameter("newCopyright");
        String authorIdsStr = request.getParameter("newAuthorIds");

        if (isbn == null || isbn.trim().isEmpty() || titleName == null || titleName.trim().isEmpty() ||
            editionNumberStr == null || editionNumberStr.trim().isEmpty() || copyright == null || copyright.trim().isEmpty() ||
            authorIdsStr == null || authorIdsStr.trim().isEmpty()) {
            out.println("<p class=\"error\">Error: All fields (ISBN, Title, Edition #, Copyright, Author IDs) are required for adding a title.</p>");
            return;
        }

        try {
            int editionNumber = Integer.parseInt(editionNumberStr);
            List<Integer> authorIds = Arrays.stream(authorIdsStr.split(","))
                                            .map(String::trim)
                                            .filter(s -> !s.isEmpty())
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());

            if (authorIds.isEmpty()) {
                out.println("<p class=\"error\">Error: At least one Author ID is required for adding a title.</p>");
                return;
            }

            boolean success = titleService.addTitle(isbn, titleName, editionNumber, copyright, authorIds);
            if (success) {
                out.println("<p class=\"success\">Title '" + escapeHtml(titleName) + "' (ISBN: " + escapeHtml(isbn) + ") added successfully!</p>");
                displayAllTitles(out);
            } else {
                out.println("<p class=\"error\">Error: Failed to add title. Check if ISBN already exists or if Author IDs are valid.</p>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Edition Number or Author ID(s) format. Please enter numbers.</p>");
        }
    }
    
    /**
     * Updates an existing title's details (name, edition, copyright)
     * and its associated authors.
     *
     * @param request HttpServletRequest containing updated title details
     * @param out     PrintWriter used to generate HTML response
     */
    private void updateTitle(HttpServletRequest request, PrintWriter out) {
        String isbn = request.getParameter("newTitleISBN");
        String newTitleName = request.getParameter("newTitleName");
        String newEditionNumberStr = request.getParameter("newEditionNumber");
        String newCopyright = request.getParameter("newCopyright");
        String newAuthorIdsStr = request.getParameter("newAuthorIds");

        if (isbn == null || isbn.trim().isEmpty() || newTitleName == null || newTitleName.trim().isEmpty() ||
            newEditionNumberStr == null || newEditionNumberStr.trim().isEmpty() || newCopyright == null || newCopyright.trim().isEmpty() ||
            newAuthorIdsStr == null || newAuthorIdsStr.trim().isEmpty()) {
            out.println("<p class=\"error\">Error: All fields (ISBN, Title, Edition #, Copyright, Author IDs) are required for updating a title.</p>");
            return;
        }

        try {
            int newEditionNumber = Integer.parseInt(newEditionNumberStr);
            List<Integer> newAuthorIds = Arrays.stream(newAuthorIdsStr.split(","))
                                               .map(String::trim)
                                               .filter(s -> !s.isEmpty())
                                               .map(Integer::parseInt)
                                               .collect(Collectors.toList());

            if (newAuthorIds.isEmpty()) {
                out.println("<p class=\"error\">Error: At least one Author ID is required for updating a title.</p>");
                return;
            }

            boolean success = titleService.updateTitle(isbn, newTitleName, newEditionNumber, newCopyright, newAuthorIds);
            if (success) {
                out.println("<p class=\"success\">Title with ISBN " + escapeHtml(isbn) + " updated successfully!</p>");
                displayAllTitles(out);
            } else {
                out.println("<p class=\"error\">Error: Title with ISBN " + escapeHtml(isbn) + " not found or failed to update. Check if new Author IDs are valid.</p>");
            }
        } catch (NumberFormatException e) {
            out.println("<p class=\"error\">Error: Invalid Edition Number or Author ID(s) format for update. Please enter numbers.</p>");
        }
    }
    
    /**
     * Deletes a title by its ISBN.
     *
     * @param request HttpServletRequest containing the parameter "deleteTitleISBN"
     * @param out     PrintWriter used to generate HTML response
     */
    private void deleteTitle(HttpServletRequest request, PrintWriter out) {
        String isbn = request.getParameter("titleISBN");
        if (isbn == null || isbn.trim().isEmpty()) {
            out.println("<p class=\"error\">Error: Title ISBN cannot be empty for deletion.</p>");
            return;
        }
        boolean success = titleService.deleteTitle(isbn);
        if (success) {
            out.println("<p class=\"success\">Title with ISBN " + escapeHtml(isbn) + " deleted successfully!</p>");
            displayAllTitles(out);
        } else {
            out.println("<p class=\"error\">Error: Title with ISBN " + escapeHtml(isbn) + " not found or failed to delete.</p>");
        }
    }

    /**
     * Retrieves and displays all authors associated with a given title.
     *
     * @param request HttpServletRequest containing the parameter "titleISBNForAuthors"
     * @param out     PrintWriter used to generate HTML response
     */
    private void getAuthorsForTitle(HttpServletRequest request, PrintWriter out) {
        String isbn = request.getParameter("titleISBN");
        if (isbn == null || isbn.trim().isEmpty()) {
            out.println("<p class=\"error\">Error: Title ISBN cannot be empty for getting authors.</p>");
            return;
        }

        TitleDTO title = titleService.getTitleByISBN(isbn);
        if (title == null) {
            out.println("<p class=\"error\">Error: Title with ISBN " + escapeHtml(isbn) + " not found.</p>");
            return;
        }

        List<AuthorDTO> authors = titleService.getAuthorsForTitle(isbn);
        out.println("<h3>Authors for Title: '" + escapeHtml(title.getTitle()) + "' (ISBN: " + escapeHtml(isbn) + ")</h3>");
        if (authors.isEmpty()) {
            out.println("<p class=\"info\">No authors found for this title.</p>");
        } else {
            out.println("<table>");
            out.println("    <thead>");
            out.println("        <tr><th>Author ID</th><th>First Name</th><th>Last Name</th></tr>");
            out.println("    </thead>");
            out.println("    <tbody>");
            for (AuthorDTO author : authors) {
                out.println("        <tr>");
                out.println("            <td>" + author.getId() + "</td>");
                out.println("            <td>" + escapeHtml(author.getFirstName()) + "</td>");
                out.println("            <td>" + escapeHtml(author.getLastName()) + "</td>");
                out.println("        </tr>");
            }
            out.println("    </tbody>");
            out.println("</table>");
        }
    }

    /**
     * Escapes HTML special characters to prevent XSS attacks.
     *
     * @param text The text to escape
     * @return Escaped text safe for HTML rendering
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break; // Apostrophe
                case '/': sb.append("&#x2F;"); break; // Solidus
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * Logs out the current user by invalidating the session and redirecting to the login page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an input or output error occurs during the redirect.
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false); // Get session without creating a new one
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        response.sendRedirect("login"); // Redirect to LoginServlet
    }
}
