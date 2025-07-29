package transferobjects;

import java.io.Serializable;

/*******************************************************************************
 * Data Transfer Object (DTO) representing a Title entity.

 * This class is used to transfer book title data between different layers
 * of the application (DAO, Service, Controller). It implements {@link Serializable}
 * to allow safe serialization when required (e.g., session storage or distributed systems).
 *
 * This class provides getters to access the field values and a
 * {@code toString()} method for formatted display.
 * 
 * @see TitleDTO
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: Represents the data structure for a Title, 
 *              matching the books-MySQL.sql schema.
 ******************************************************************************/

public class TitleDTO implements Serializable {
    
    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The unique ISBN identifier for the title (Primary Key). */
    private String isbn;

    /** The name of the title. */
    private String title;

    /** The edition number of the title. */
    private int editionNumber;

    /** The copyright year of the title. */
    private String copyright;

    /**
     * Default constructor.
     * Creates an empty TitleDTO object.
     */
    public TitleDTO() {
    }

    /**
     * Full constructor that initializes all fields.
     *
     * @param isbn          The ISBN of the title.
     * @param title         The title name.
     * @param editionNumber The edition number of the title.
     * @param copyright     The copyright year.
     */
    public TitleDTO(String isbn, String title, int editionNumber, String copyright) {
        this.isbn = isbn;
        this.title = title;
        this.editionNumber = editionNumber;
        this.copyright = copyright;
    }

    /**
     * Gets the ISBN of the title.
     *
     * @return The ISBN of the title.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the title.
     *
     * @param isbn The ISBN to set.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the name of the title.
     *
     * @return The title name.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the name of the title.
     *
     * @param title The title name to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the edition number of the title.
     *
     * @return The edition number.
     */
    public int getEditionNumber() {
        return editionNumber;
    }

    /**
     * Sets the edition number of the title.
     *
     * @param editionNumber The edition number to set.
     */
    public void setEditionNumber(int editionNumber) {
        this.editionNumber = editionNumber;
    }

    /**
     * Gets the copyright year of the title.
     *
     * @return The copyright year.
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Sets the copyright year of the title.
     *
     * @param copyright The copyright year to set.
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Returns a string representation of the TitleDTO object.
     *
     * @return A string containing the ISBN, title name, edition number, and copyright year.
     */
    @Override
    public String toString() {
        return "TitleDTO{" +
               "isbn='" + isbn + '\'' +
               ", title='" + title + '\'' +
               ", editionNumber=" + editionNumber +
               ", copyright='" + copyright + '\'' +
               '}';
    }
}