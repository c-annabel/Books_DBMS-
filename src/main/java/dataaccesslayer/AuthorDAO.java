package dataaccesslayer;

import transferobjects.AuthorDTO;
import transferobjects.TitleDTO; // Added for author-title relationship
import java.util.List;

/*******************************************************************************
 * AuthorDAO (Data Access Object) Interface
 * Defines the contract for CRUD operations on Author data.
 *
 * @see AuthorDAO
 * @since Java 21.0.7
 * @version 1.0
 * @author Annabel Cheng (041146557)
 * Course: CST8288 Section 013
 * Description: Defines the contract for CRUD operations on Author data.
 ******************************************************************************/

public interface AuthorDAO {
    /**
     * Retrieves all authors from the database.
     * @return A list of AuthorDTO objects, or an empty list if no authors are found.
     */
    List<AuthorDTO> getAllAuthors();

    /**
     * Retrieves an author by their unique ID.
     * @param id The ID of the author to retrieve.
     * @return The AuthorDTO object if found, null otherwise.
     */
    AuthorDTO getAuthorById(int id);

    /**
     * Adds a new author to the database.
     * @param author The AuthorDTO object containing the new author's data.
     * @return true if the author was added successfully, false otherwise.
     */
    boolean addAuthor(AuthorDTO author);

    /**
     * Updates an existing author in the database.
     * @param author The AuthorDTO object containing the updated author's data (ID must exist).
     * @return true if the author was updated successfully, false otherwise.
     */
    boolean updateAuthor(AuthorDTO author);

    /**
     * Deletes an author from the database by their unique ID.
     * @param id The ID of the author to delete.
     * @return true if the author was deleted successfully, false otherwise.
     */
    boolean deleteAuthor(int id);

    /**
     * Retrieves titles written by a specific author.
     * @param authorId The ID of the author.
     * @return A list of TitleDTO objects associated with the author.
     */
    List<TitleDTO> getTitlesByAuthor(int authorId);
}
