package businesslayer;

import dataaccesslayer.AuthorDAO;
import dataaccesslayer.AuthorDAOImpl;
import java.util.ArrayList;
import transferobjects.AuthorDTO;
import java.util.List;
import transferobjects.TitleDTO;

/*******************************************************************************
 * AuthorService (Business/Domain Layer)
 * Handles business logic related to Authors.
 * Interacts with the data access layer (AuthorDAO).
 *
 * @see AuthorService
 * @since Java 21.0.7
 * @version 1.0
 * @author Annabel Cheng (041146557)
 * Course: CST8288 Section 013
 * Description: Handles business logic related to Authors.
 ******************************************************************************/

public class AuthorService {

    private final AuthorDAO authorDAO;

    /**
     * Constructor. Initializes the AuthorDAO.
     */
    public AuthorService() {
        // In a real application, consider dependency injection or a DAOFactory
        this.authorDAO = new AuthorDAOImpl();
    }

    /**
     * Retrieves all authors.
     * @return A list of AuthorDTO objects.
     */
    public List<AuthorDTO> getAllAuthors() {
        return authorDAO.getAllAuthors();
    }

    /**
     * Retrieves an author by ID.
     * @param id The ID of the author to retrieve.
     * @return The AuthorDTO object if found, null otherwise.
     */
    public AuthorDTO getAuthorById(int id) {
        return authorDAO.getAuthorById(id);
    }

    /**
     * Adds a new author.
     * @param firstName The first name of the author.
     * @param lastName The last name of the author.
     * @return true if the author was added successfully, false otherwise.
     */
    public boolean addAuthor(String firstName, String lastName) {
        // Basic validation example
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            System.err.println("Author first name and last name cannot be empty.");
            return false;
        }
        AuthorDTO newAuthor = new AuthorDTO(firstName, lastName);
        return authorDAO.addAuthor(newAuthor);
    }

    /**
     * Updates an existing author.
     * @param id The ID of the author to update.
     * @param firstName The new first name.
     * @param lastName The new last name.
     * @return true if the author was updated successfully, false otherwise.
     */
    public boolean updateAuthor(int id, String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            System.err.println("Author first name and last name cannot be empty for update.");
            return false;
        }
        AuthorDTO existingAuthor = authorDAO.getAuthorById(id);
        if (existingAuthor == null) {
            System.err.println("Author with ID " + id + " not found for update.");
            return false;
        }
        existingAuthor.setFirstName(firstName);
        existingAuthor.setLastName(lastName);
        return authorDAO.updateAuthor(existingAuthor);
    }

    /**
     * Deletes an author by ID.
     * @param id The ID of the author to delete.
     * @return true if the author was deleted successfully, false otherwise.
     */
    public boolean deleteAuthor(int id) {
        AuthorDTO existingAuthor = authorDAO.getAuthorById(id);
        if (existingAuthor == null) {
            System.err.println("Author with ID " + id + " not found for deletion.");
            return false;
        }
        return authorDAO.deleteAuthor(id);
    }

//    public List<TitleDTO> getTitlesByAuthor(int authorId) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    
    /**
     * Retrieves all titles associated with a specific author.
     *
     * @param authorId The ID of the author.
     * @return A list of TitleDTO objects representing the titles the author contributed to.
     */
    public List<TitleDTO> getTitlesByAuthor(int authorId) {
        // Validate ID before calling DAO
         if (authorId <= 0) {
           System.err.println("Invalid Author ID: " + authorId);
           return new ArrayList<>();
        }
        return authorDAO.getTitlesByAuthor(authorId);
   }
}
