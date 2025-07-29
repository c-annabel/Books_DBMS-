package dataaccesslayer;

import transferobjects.TitleDTO;
import transferobjects.AuthorDTO; // Added for author-title relationship
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * TitleDAOImpl (Data Access Object Implementation)
 * Implements the TitleDAO interface for MySQL database operations,
 * specifically handling the `Titles` and `AuthorISBN` tables.
 * Uses the DataSource singleton to get database connections.
 * 
 * @see TitleDAOImpl
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: Implements the TitleDAO interface for MySQL database operations.
 ******************************************************************************/

public class TitleDAOImpl implements TitleDAO {

    private DataSource dataSource;

    /**
     * Constructor.
     * Initializes the DataSource instance for database connections.
     */
    public TitleDAOImpl() {
        dataSource = DataSource.getInstance();
    }

    /**
     * Retrieves all titles from the database.
     *
     * @return A list of TitleDTO objects representing all titles.
     *         Returns an empty list if no titles are found.
     */
    @Override
    public List<TitleDTO> getAllTitles() {
        List<TitleDTO> titles = new ArrayList<>();
        String sql = "SELECT ISBN, Title, EditionNumber, Copyright FROM Titles";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TitleDTO title = new TitleDTO();
                title.setIsbn(resultSet.getString("ISBN"));
                title.setTitle(resultSet.getString("Title"));
                title.setEditionNumber(resultSet.getInt("EditionNumber"));
                title.setCopyright(resultSet.getString("Copyright"));
                titles.add(title);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all titles: " + e.getMessage());
        }
        return titles;
    }

    /**
     * Retrieves a title by its ISBN.
     *
     * @param isbn The ISBN of the title to retrieve.
     * @return The TitleDTO object if found, otherwise null.
     */
    @Override
    public TitleDTO getTitleByISBN(String isbn) {
        TitleDTO title = null;
        String sql = "SELECT ISBN, Title, EditionNumber, Copyright FROM Titles WHERE ISBN = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    title = new TitleDTO();
                    title.setIsbn(resultSet.getString("ISBN"));
                    title.setTitle(resultSet.getString("Title"));
                    title.setEditionNumber(resultSet.getInt("EditionNumber"));
                    title.setCopyright(resultSet.getString("Copyright"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving title by ISBN " + isbn + ": " + e.getMessage());
        }
        return title;
    }

    /**
     * Adds a new title and its associations with authors.
     * <p>
     * Uses a transaction to ensure both the title and its author relationships are inserted together.
     * </p>
     *
     * @param title     The TitleDTO object representing the title to add.
     * @param authorIds A list of author IDs to associate with the title.
     * @return true if the title and associations were added successfully; false otherwise.
     */
    @Override
    public boolean addTitle(TitleDTO title, List<Integer> authorIds) {
        String sqlInsertTitle = "INSERT INTO Titles (ISBN, Title, EditionNumber, Copyright) VALUES (?, ?, ?, ?)";
        String sqlInsertAuthorISBN = "INSERT INTO AuthorISBN (AuthorID, ISBN) VALUES (?, ?)";
        boolean success = false;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Insert into Titles table
            try (PreparedStatement statement = connection.prepareStatement(sqlInsertTitle)) {
                statement.setString(1, title.getIsbn());
                statement.setString(2, title.getTitle());
                statement.setInt(3, title.getEditionNumber());
                statement.setString(4, title.getCopyright());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return false; // Title not added
                }
            }

            // 2. Insert into AuthorISBN table for each author
            try (PreparedStatement statement = connection.prepareStatement(sqlInsertAuthorISBN)) {
                for (Integer authorId : authorIds) {
                    statement.setInt(1, authorId);
                    statement.setString(2, title.getIsbn());
                    statement.addBatch(); // Add to batch for efficiency
                }
                int[] batchResults = statement.executeBatch(); // Execute all batch statements
                // Check if all batch operations were successful (optional, but good for robust error checking)
                for (int result : batchResults) {
                    if (result == Statement.EXECUTE_FAILED) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            connection.commit(); // Commit transaction
            success = true;

        } catch (SQLException e) {
            System.err.println("Error adding title and author associations: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * Updates an existing title and its associated authors.
     * <p>
     * First updates the title details, then replaces all author associations for the title.
     * </p>
     *
     * @param title        The updated TitleDTO object.
     * @param newAuthorIds A list of new author IDs to associate with the title.
     * @return true if the update was successful; false otherwise.
     */
    @Override
    public boolean updateTitle(TitleDTO title, List<Integer> newAuthorIds) {
        String sqlUpdateTitle = "UPDATE Titles SET Title = ?, EditionNumber = ?, Copyright = ? WHERE ISBN = ?";
        String sqlDeleteAuthorISBN = "DELETE FROM AuthorISBN WHERE ISBN = ?";
        String sqlInsertAuthorISBN = "INSERT INTO AuthorISBN (AuthorID, ISBN) VALUES (?, ?)";
        boolean success = false;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Update Titles table
            try (PreparedStatement statement = connection.prepareStatement(sqlUpdateTitle)) {
                statement.setString(1, title.getTitle());
                statement.setInt(2, title.getEditionNumber());
                statement.setString(3, title.getCopyright());
                statement.setString(4, title.getIsbn());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return false; // Title not found or not updated
                }
            }

            // 2. Delete existing AuthorISBN associations for this title
            try (PreparedStatement statement = connection.prepareStatement(sqlDeleteAuthorISBN)) {
                statement.setString(1, title.getIsbn());
                statement.executeUpdate();
            }

            // 3. Insert new AuthorISBN associations
            try (PreparedStatement statement = connection.prepareStatement(sqlInsertAuthorISBN)) {
                for (Integer authorId : newAuthorIds) {
                    statement.setInt(1, authorId);
                    statement.setString(2, title.getIsbn());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            connection.commit(); // Commit transaction
            success = true;

        } catch (SQLException e) {
            System.err.println("Error updating title and author associations: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * Deletes a title and its associated records in AuthorISBN.
     * <p>
     * This operation is done in a transaction: it deletes all related AuthorISBN entries first,
     * then deletes the title record.
     * </p>
     *
     * @param isbn The ISBN of the title to delete.
     * @return true if the title was deleted successfully; false otherwise.
     */
    @Override
    public boolean deleteTitle(String isbn) {
        // Due to FOREIGN KEY (ISBN) References Titles(ISBN) with ON DELETE CASCADE in books-MySQL.sql,
        // deleting from Titles will not automatically delete from AuthorISBN
        // thus deleteing data from AuthorISBN first, then from Titles. 
        String deleteAuthorISBN = "DELETE FROM AuthorISBN WHERE ISBN = ?";
        String deleteTitle = "DELETE FROM Titles WHERE ISBN = ?";
        boolean success = false;
        try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt1 = connection.prepareStatement(deleteAuthorISBN);
                 PreparedStatement stmt2 = connection.prepareStatement(deleteTitle)) {

                stmt1.setString(1, isbn);
                stmt1.executeUpdate();

                stmt2.setString(1, isbn);
                int rowsAffected = stmt2.executeUpdate();

                if (rowsAffected > 0) {
                    success = true;
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error deleting title with ISBN " + isbn + ": " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.err.println("Error handling delete transaction: " + e.getMessage());
    }
    return success;
}
    
    /**
     * Retrieves all authors associated with a given title.
     *
     * @param isbn The ISBN of the title.
     * @return A list of AuthorDTO objects representing authors linked to the title.
     */
    @Override
    public List<AuthorDTO> getAuthorsForTitle(String isbn) {
        List<AuthorDTO> authors = new ArrayList<>();
        String sql = "SELECT A.AuthorID, A.FirstName, A.LastName " +
                     "FROM Authors A JOIN AuthorISBN AI ON A.AuthorID = AI.AuthorID " +
                     "WHERE AI.ISBN = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AuthorDTO author = new AuthorDTO();
                    author.setId(resultSet.getInt("AuthorID"));
                    author.setFirstName(resultSet.getString("FirstName"));
                    author.setLastName(resultSet.getString("LastName"));
                    authors.add(author);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving authors for title ISBN " + isbn + ": " + e.getMessage());
        }
        return authors;
    }
}
