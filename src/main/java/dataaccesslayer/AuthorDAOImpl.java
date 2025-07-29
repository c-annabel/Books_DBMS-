package dataaccesslayer;

import transferobjects.AuthorDTO;
import transferobjects.TitleDTO; // Added for author-title relationship
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * Implementation of the AuthorDAO interface.
 *
 * This class provides CRUD operations for Authors and supports retrieving titles
 * associated with a given author. It uses JDBC for database interactions and
 * depends on a DataSource for connections.
 * 
 * @see AuthorDAOImpl
 * @since Java 21.0.7
 * @version 1.0
 * @author Annabel Cheng (041146557)
 * Course: CST8288 Section 013
 * Description: Implements the AuthorDAO interface for MySQL database operations.
 ******************************************************************************/

public class AuthorDAOImpl implements AuthorDAO {

    private final DataSource dataSource;

    /**
     * Constructor. Initializes the DataSource.
     */
    public AuthorDAOImpl() {
        dataSource = DataSource.getInstance();
    }

    /**
     * Retrieves all authors from the database.
     *
     * @return A list of AuthorDTO objects representing all authors. Returns an empty list if no authors are found.
     */
    @Override
    public List<AuthorDTO> getAllAuthors() {
        List<AuthorDTO> authors = new ArrayList<>();
        String sql = "SELECT AuthorID, FirstName, LastName FROM Authors";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                AuthorDTO author = new AuthorDTO();
                author.setId(resultSet.getInt("AuthorID"));
                author.setFirstName(resultSet.getString("FirstName"));
                author.setLastName(resultSet.getString("LastName"));
                authors.add(author);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all authors: " + e.getMessage());
        }
        return authors;
    }

    /**
     * Retrieves an author by their unique ID.
     *
     * @param id The ID of the author to retrieve.
     * @return The AuthorDTO object if found; otherwise, null.
     */
    @Override
    public AuthorDTO getAuthorById(int id) {
        AuthorDTO author = null;
        String sql = "SELECT AuthorID, FirstName, LastName FROM Authors WHERE AuthorID = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    author = new AuthorDTO();
                    author.setId(resultSet.getInt("AuthorID"));
                    author.setFirstName(resultSet.getString("FirstName"));
                    author.setLastName(resultSet.getString("LastName"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving author by ID " + id + ": " + e.getMessage());
        }
        return author;
    }

    /**
     * Adds a new author to the database.
     *
     * @param author The AuthorDTO object containing author details.
     * @return true if the author was added successfully; false otherwise.
     */
    @Override
    public boolean addAuthor(AuthorDTO author) {
        String sql = "INSERT INTO Authors (FirstName, LastName) VALUES (?, ?)";
        boolean success = false;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        author.setId(generatedKeys.getInt(1));
                    }
                }
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding author: " + e.getMessage());
        }
        return success;
    }

    /**
     * Updates an existing author's details in the database.
     *
     * @param author The AuthorDTO object containing updated author information.
     * @return true if the update was successful; false otherwise.
     */
    @Override
    public boolean updateAuthor(AuthorDTO author) {
        String sql = "UPDATE Authors SET FirstName = ?, LastName = ? WHERE AuthorID = ?";
        boolean success = false;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.setInt(3, author.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating author with ID " + author.getId() + ": " + e.getMessage());
        }
        return success;
    }
    
    /**
     * Deletes an author from the database by their ID.
     *
     * @param id The ID of the author to delete.
     * @return true if the author was deleted successfully; false otherwise.
     */
    @Override
    public boolean deleteAuthor(int id) {
        String sql = "DELETE FROM Authors WHERE AuthorID = ?";
        boolean success = false;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting author with ID " + id + ": " + e.getMessage());
        }
        return success;
    }

    /**
     * Retrieves all titles associated with a specific author.
     *
     * @param authorId The ID of the author.
     * @return A list of TitleDTO objects representing titles linked to the author.
     */
    @Override
    public List<TitleDTO> getTitlesByAuthor(int authorId) {
        List<TitleDTO> titles = new ArrayList<>();
        String sql = "SELECT T.ISBN, T.Title, T.EditionNumber, T.Copyright " +
                     "FROM Titles T JOIN AuthorISBN AI ON T.ISBN = AI.ISBN " +
                     "WHERE AI.AuthorID = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, authorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    TitleDTO title = new TitleDTO();
                    title.setIsbn(resultSet.getString("ISBN"));
                    title.setTitle(resultSet.getString("Title"));
                    title.setEditionNumber(resultSet.getInt("EditionNumber"));
                    title.setCopyright(resultSet.getString("Copyright"));
                    titles.add(title);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving titles for author ID " + authorId + ": " + e.getMessage());
        }
        return titles;
    }
}
