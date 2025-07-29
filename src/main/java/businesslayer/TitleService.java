package businesslayer;

import dataaccesslayer.TitleDAO;
import dataaccesslayer.TitleDAOImpl;
import transferobjects.TitleDTO;
import transferobjects.AuthorDTO; // Needed to display author names with titles
import java.util.List;

/*******************************************************************************
 * TitleService (Business/Domain Layer)
 * Handles business logic related to Titles.
 * Interacts with the data access layer (TitleDAO).
 *
 * @see TitleService
 * @since Java 21.0.7
 * @version 1.0
 * @author Annabel Cheng (041146557)
 * Course: CST8288 Section 013
 * Description: Handles business logic related to Titles.
 * 
 ******************************************************************************/

public class TitleService {

    private final TitleDAO titleDAO;

    /**
     * Constructor. Initializes the TitleDAO.
     */
    public TitleService() {
        this.titleDAO = new TitleDAOImpl();
    }

    /**
     * Retrieves all titles.
     * @return A list of TitleDTO objects.
     */
    public List<TitleDTO> getAllTitles() {
        return titleDAO.getAllTitles();
    }

    /**
     * Retrieves a title by its ISBN.
     * @param isbn The ISBN of the title to retrieve.
     * @return The TitleDTO object if found, null otherwise.
     */
    public TitleDTO getTitleByISBN(String isbn) {
        return titleDAO.getTitleByISBN(isbn);
    }

    /**
     * Adds a new title.
     * @param isbn The ISBN of the title.
     * @param titleName The title string.
     * @param editionNumber The edition number.
     * @param copyright The copyright year.
     * @param authorIds A list of author IDs associated with this title.
     * @return true if the title was added successfully, false otherwise.
     */
    public boolean addTitle(String isbn, String titleName, int editionNumber, String copyright, List<Integer> authorIds) {
        if (isbn == null || isbn.trim().isEmpty() || titleName == null || titleName.trim().isEmpty() || copyright == null || copyright.trim().isEmpty() || authorIds == null || authorIds.isEmpty()) {
            System.err.println("Title ISBN, name, copyright, and at least one author are required.");
            return false;
        }
        TitleDTO newTitle = new TitleDTO(isbn, titleName, editionNumber, copyright);
        return titleDAO.addTitle(newTitle, authorIds);
    }

    /**
     * Updates an existing title.
     * @param isbn The ISBN of the title to update.
     * @param newTitleName The new title string.
     * @param newEditionNumber The new edition number.
     * @param newCopyright The new copyright year.
     * @param newAuthorIds A list of new author IDs associated with this title.
     * @return true if the title was updated successfully, false otherwise.
     */
    public boolean updateTitle(String isbn, String newTitleName, int newEditionNumber, String newCopyright, List<Integer> newAuthorIds) {
        if (isbn == null || isbn.trim().isEmpty() || newTitleName == null || newTitleName.trim().isEmpty() || newCopyright == null || newCopyright.trim().isEmpty() || newAuthorIds == null || newAuthorIds.isEmpty()) {
            System.err.println("Title ISBN, name, copyright, and at least one author are required for update.");
            return false;
        }
        TitleDTO existingTitle = titleDAO.getTitleByISBN(isbn);
        if (existingTitle == null) {
            System.err.println("Title with ISBN " + isbn + " not found for update.");
            return false;
        }
        existingTitle.setTitle(newTitleName);
        existingTitle.setEditionNumber(newEditionNumber);
        existingTitle.setCopyright(newCopyright);
        return titleDAO.updateTitle(existingTitle, newAuthorIds);
    }

    /**
     * Deletes a title by ISBN.
     * @param isbn The ISBN of the title to delete.
     * @return true if the title was deleted successfully, false otherwise.
     */
    public boolean deleteTitle(String isbn) {
        TitleDTO existingTitle = titleDAO.getTitleByISBN(isbn);
        if (existingTitle == null) {
            System.err.println("Title with ISBN " + isbn + " not found for deletion.");
            return false;
        }
        return titleDAO.deleteTitle(isbn);
    }

    /**
     * Retrieves authors for a specific title.
     * @param isbn The ISBN of the title.
     * @return A list of AuthorDTO objects associated with the title.
     */
    public List<AuthorDTO> getAuthorsForTitle(String isbn) {
        return titleDAO.getAuthorsForTitle(isbn);
    }
}