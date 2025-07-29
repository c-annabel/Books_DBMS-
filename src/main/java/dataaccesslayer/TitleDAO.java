package dataaccesslayer;

import transferobjects.TitleDTO;
import transferobjects.AuthorDTO; // Added for author-title relationship
import java.util.List;

/*******************************************************************************
 * TitleDAO (Data Access Object) Interface
 * Defines the contract for CRUD operations on Title data, including AuthorISBN relationship.
 * 
 * @see TitleDAO
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: Defines the contract for CRUD operations on Title data
 ******************************************************************************/

public interface TitleDAO {
    /**
     * Retrieves all titles from the database.
     * @return A list of TitleDTO objects, or an empty list if no titles are found.
     */
    List<TitleDTO> getAllTitles();

    /**
     * Retrieves a title by its unique ISBN.
     * @param isbn The ISBN of the title to retrieve.
     * @return The TitleDTO object if found, null otherwise.
     */
    TitleDTO getTitleByISBN(String isbn);

    /**
     * Adds a new title to the database and associates it with authors.
     * @param title The TitleDTO object containing the new title's data.
     * @param authorIds A list of Author IDs to associate with this title.
     * @return true if the title and its author associations were added successfully, false otherwise.
     */
    boolean addTitle(TitleDTO title, List<Integer> authorIds);

    /**
     * Updates an existing title in the database and its author associations.
     * @param title The TitleDTO object containing the updated title's data (ISBN must exist).
     * @param newAuthorIds A list of new Author IDs to associate with this title. Existing associations will be replaced.
     * @return true if the title and its author associations were updated successfully, false otherwise.
     */
    boolean updateTitle(TitleDTO title, List<Integer> newAuthorIds);

    /**
     * Deletes a title from the database by its unique ISBN.
     * This will also delete associated entries in AuthorISBN due to CASCADE.
     * @param isbn The ISBN of the title to delete.
     * @return true if the title was deleted successfully, false otherwise.
     */
    boolean deleteTitle(String isbn);

    /**
     * Retrieves authors associated with a specific title.
     * @param isbn The ISBN of the title.
     * @return A list of AuthorDTO objects associated with the title.
     */
    List<AuthorDTO> getAuthorsForTitle(String isbn);
}
