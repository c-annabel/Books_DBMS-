package dataaccesslayer;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*******************************************************************************
 * DataSource (Singleton Design Pattern):
 * It provides a single point of access for obtaining database connections.
 * Thread-safety is implemented using a double-checked locking mechanism.
 * The {@code DataSource} class encapsulates the database connection logic for the application.
 * It implements the Singleton Design Pattern to ensure only one instance of the DataSource is used,
 * providing a centralized and consistent way to manage database connections.
 *
 * This class loads database connection properties from an external configuration file
 * {@code database.properties}, registers the MySQL JDBC driver, and provides a method to obtain
 * a database connection.
 * 
 * @see DataSource
 * @since Java 21.0.7
 * @author Annabel Cheng
 * @version 1.0
 * Course: CST8288 Lab013 Assignment 2
 * Description: This class encapsulates the database connection logic.
 ******************************************************************************/

public class DataSource {

    /**
     * The single instance of the DataSource (Singleton).
     * Declared {@code volatile} to ensure proper visibility of the instance
     * variable across threads during initialization.
     */
    private static volatile DataSource instance;

    /** The database URL loaded from properties file. */
    private String url;

    /** The database username loaded from properties file. */
    private String username;

    /** The database password loaded from properties file. */
    private String password;

    /**
     * Private constructor to prevent external instantiation.
     * Loads the MySQL JDBC driver and reads database connection properties from the classpath.
     * @throws RuntimeException if the JDBC driver is not found or if an error
     * occurs when reading properties.
     */
    private DataSource() {
        // Load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Log the error and re-throw as a runtime exception as this is a critical setup failure
            System.err.println("Error: MySQL JDBC Driver not found. Please ensure the JDBC driver JAR is in your classpath.");
            throw new RuntimeException("Failed to load JDBC driver", e);
        }

        // Load database connection properties from database.properties
        // This path is relative to the classpath (e.g., inside WEB-INF/classes in a WAR)
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (in == null) {
                // This means database.properties was not found on the classpath
                System.err.println("Error: database.properties file not found on classpath. " +
                                   "Ensure it's in src/main/resources and packaged correctly.");
                throw new IOException("database.properties not found");
            }

            Properties props = new Properties();
            props.load(in); // Load properties from the InputStream

            // Retrieve properties
            url = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");

            // Basic validation for properties
            if (url == null || username == null || password == null) {
                System.err.println("Error: Missing one or more JDBC properties (jdbc.url, jdbc.username, jdbc.password) in database.properties.");
                throw new IOException("Incomplete JDBC properties in database.properties");
            }

        } catch (IOException e) {
            // Log the error and re-throw as a runtime exception
            System.err.println("Error reading database.properties: " + e.getMessage());
            throw new RuntimeException("Failed to load database connection properties", e);
        }
    }

    /**
     * Returns the single instance of the DataSource.
     * This method implements the Double-Checked Locking pattern for thread-safe lazy initialization.
     *
     * @return the singleton instance of the DataSource
     */
    public static DataSource getInstance() {
        // First check: no need to synchronize if instance already exists
        if (instance == null) {
            // Synchronize only if instance is null to reduce overhead
            synchronized (DataSource.class) {                     //threads-safe
                // Second check: after acquiring lock, check again to prevent redundant initialization
                if (instance == null) {
                    instance = new DataSource();
                }
            }
        }
        return instance;
    }

    /**
     * Provides a new {@link java.sql.Connection} to the database using the configured properties.
     *
     * @return a new database connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        // Use the loaded properties to establish a connection
        return DriverManager.getConnection(url, username, password);
    }
}
