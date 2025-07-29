package transferobjects;

import java.io.Serializable;

/*******************************************************************************
 * AuthorDTO (Data Transfer Object)
 * Represents the data structure for an Author.
 * This class is used to transfer author data between layers.
 *
 * @see AuthorDTO
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: Represents the data structure for an Author.
 * 
 ******************************************************************************/

public class AuthorDTO implements Serializable {
    
    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The unique ID of the author. */
    private int id;

    /** The first name of the author. */
    private String firstName;

    /** The last name of the author. */
    private String lastName;

    /**
     * Default no-argument constructor.
     * Initializes an empty AuthorDTO object.
     */
    public AuthorDTO() {
    }

    /**
     * Full constructor that initializes all fields.
     *
     * @param id        The unique ID of the author.
     * @param firstName The first name of the author.
     * @param lastName  The last name of the author.
     */
    public AuthorDTO(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor without ID, typically used for creating new authors
     * before they are inserted into the database.
     *
     * @param firstName The first name of the author.
     * @param lastName  The last name of the author.
     */
    public AuthorDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the author's ID.
     *
     * @return The unique ID of the author.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the author's ID.
     *
     * @param id The unique ID to set for the author.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the author's first name.
     *
     * @return The first name of the author.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the author's first name.
     *
     * @param firstName The first name to set for the author.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the author's last name.
     *
     * @return The last name of the author.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the author's last name.
     *
     * @param lastName The last name to set for the author.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns a string representation of the AuthorDTO object.
     *
     * @return A string containing the author's ID, first name, and last name.
     */
    @Override
    public String toString() {
        return "AuthorDTO{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               '}';
    }
}