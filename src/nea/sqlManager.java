/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal
 */
public class sqlManager {

    private static final Logger logger = Logger.getLogger(sqlManager.class.getName());

    // The default connection details for the database
    static String DEFAULT_url = "jdbc:mysql://localhost:3306/dbNEA?serverTimezone=GMT";
    static String DEFAULT_username = "root";
    static String DEFAULT_password = "root";

    // Opens a connection to the database
    public static Connection openConnection(String url, String username, String password) {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "ClassNotFoundException");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return conn;
    }

    // Opens connection to db
    // No specific url, username or password passed as a parameter so a default connection is opened
    public static Connection openConnection() {
        return openConnection(DEFAULT_url, DEFAULT_username, DEFAULT_password);
    }

    // Closes the connection to the database
    // true returned if connection was closed, false otherwise
    public static boolean closeConnection(Connection conn) {
        try {
            // Checks if the connection is already closed to prevent an exception
            if (!conn.isClosed()) {
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }
        return false;
    }

    // Fetches the next available primary key value in a DB table
    public static int getNextPKValue(Connection conn, String tableName, String key) {
        // Default value if the queried table is empty
        int id = 1;

        // Query Setup & Execution
        String query = String.format("SELECT MAX(%s) FROM %s", key, tableName);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Increments the current max PK value to get a new maximum
                id = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return id;
    }

    // Removes a record from a given table with a specfic primary key value
    public static void removeRecord(Connection conn, String tableName, String key, int PK_value) {

        // Query Setup & Execution
        String query = String.format("DELETE FROM %s WHERE %s = ?", tableName, key);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, PK_value);

            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, rowsAffected + " row(s) removed.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }
    }

    // Check the presence of a specific record in a DB table
    public static boolean RecordExists(Connection conn, String tableName, String key, String key_value) {

        // Query Setup & Execution
        String query = String.format("SELECT 1 FROM %s WHERE %s = ?", tableName, key);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, key_value);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                // If no results were fetched then the record doesn't exist
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        // In the event of an error, it returns that the row DOES exist
        return true;
    }

    // Gets the category name of any category given its category_id
    public static String getCategory(Connection conn, String tableName, String key, int catID) {

        // Query Setup & Execution
        String query = String.format("SELECT category_name FROM %s WHERE %s = ?", tableName, key);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, catID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a category with the provided PK value was found
                return rs.getString(1);
            } else {
                logger.log(Level.WARNING, "Error fetching category");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return null;
    }

    // Returns the amount of records that are in a given table
    public static int countRecords(Connection conn, String tableName, String key, int id) {

        // Query Setup & Execution
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?", key, tableName, key);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Returns the last time a given employee_id logged into the system
    public static String getLastLogin(Connection conn, int employee_id) {

        // Query Setup & Execution
        String query = "SELECT date_last_logged_in FROM tblEmployee WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                logger.log(Level.WARNING, "Error fetching last login date");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return null;
    }

    // Returns true/false whether an employee has admin permissions
    public static boolean isAdmin(Connection conn, int employee_id) {

        // Query Setup & Execution
        String query = "SELECT admin FROM tblEmployee WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);

            } else {
                logger.log(Level.WARNING, "Error fetching admin status");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        // Default is no admin permissions
        return false;
    }

    // Returns the full name of the employee with the given employee_id
    public static String getEmployeeFullName(Connection conn, int employee_id) {

        // Query Setup & Execution
        String query = "SELECT CONCAT(forename,' ', surname) FROM tblEmployee WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                logger.log(Level.WARNING, "Error fetching employee name with id: " + employee_id);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return null;
    }

    // Returns the full name of the customer with the given customer_id
    public static String getCustomerFullName(Connection conn, int customer_id) {

        // Query Setup & Execution
        String query = "SELECT CONCAT(forename,' ', surname) FROM tblCustomer WHERE customer_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customer_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                logger.log(Level.WARNING, "Error fetching customer name with id: " + customer_id);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return null;
    }

    // Returns the total value of all the items in a given receipt.
    public static double getReceiptTotal(Connection conn, String tableName, String key, int document_id) {

        // Query Setup & Execution
        String query = String.format("SELECT COALESCE(SUM(unit_price * quantity), 0) as total FROM %s WHERE %s = ?", tableName, key);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, document_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a receipt with the given id was found
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Returns the id of a category given its name
    public static int getIDofCategory(Connection conn, String tableName, String category) {

        // Query Setup & Execution
        String query = String.format("SELECT category_id FROM %s WHERE category_name = ?", tableName);
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a category with that id was found
                return rs.getInt(1);
            } else {
                logger.log(Level.WARNING, "Error fetching category id of category with name: " + category);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Returns the id of a customer given their name
    public static int getIDofCustomer(Connection conn, String name) {

        // Query Setup & Execution
        String query = "SELECT customer_id FROM tblCustomer WHERE CONCAT(forename,' ',surname) = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a customer with that id was found
                return rs.getInt(1);
            } else {
                logger.log(Level.WARNING, "Error fetching category id of customer with name: " + name);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Updates the date when the user last logged in given the employee_id
    public static void updateLastLogin(Connection conn, int employee_id) {

        // Query Setup & Execution
        String query = "UPDATE tblEmployee SET date_last_logged_in = ? WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, Utility.getCurrentDate());
            pstmt.setInt(2, employee_id);

            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, rowsAffected + " row(s) updated.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }
    }

    // Returns the date_created of the first invoice or quotation ever created
    public static LocalDateTime getEarliestDateTime(Connection conn, String tableName, String key) {

        // Query Setup & Execution
        String query = String.format("SELECT %s FROM %s ORDER BY %s LIMIT 1", key, tableName, key);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Returns the date and sets the time to 00:00:00
                return rs.getDate(1).toLocalDate().atTime(0, 0, 0);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        // If an error occurs or no result is fetched then the date is set to unix epoch
        return LocalDate.of(1970, 1, 1).atTime(0, 0, 0);
    }

    // Returns the receipt number of a receipt in the current financial year, given a date
    public static int getReceiptNoThisFinancialYear(Connection conn, String tableName, String key, LocalDateTime datetime) {

        // Gets the start date of the current financial year
        LocalDate financialyear = Utility.getFinancialYear(datetime);

        // Gets the amount of receipt between the financial year start date and the date of the invoice
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE date_created BETWEEN ? AND ?", key, tableName);
        try {
            // Query Setup & Execution
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setObject(1, financialyear.atTime(0, 0, 0));
            pstmt.setObject(2, datetime);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a result is found
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Gets the max length of data which can be stored in a certain column in a DB table
    public static int getMaxColumnLength(Connection conn, String tableName, String column) {

        // Query Setup & Execution
        String query = "SELECT character_maximum_length FROM information_schema.columns"
                + " WHERE table_name = ? AND column_name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tableName);
            pstmt.setString(2, column);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return -1;
    }

    // Allows the user to add a new customer category
    public static String addNewCustomerCategory(Connection conn) {
        return addNewCategory(conn, "tblCustomerCategory");
    }

    // Allows the user to add a new item category
    public static String addNewItemCategory(Connection conn) {
        return addNewCategory(conn, "tblItemCategory");
    }

    // Allows the user to add a new category
    public static String addNewCategory(Connection conn, String tableName) {

        // Prompts user for the name of the new category
        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category");

        if (inputCategory != null) {

            // Removes all leading and trailing whitespace characters 
            inputCategory = inputCategory.trim();

            if (inputCategory.length() > sqlManager.getMaxColumnLength(conn, tableName, "category_name")) {
                ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "category name");

            } else if (sqlManager.RecordExists(conn, tableName, "category_name", inputCategory)) {
                ErrorMsg.throwError(ErrorMsg.ALREADY_EXISTS_ERROR, "Category");

            } else {
                // Checks if category name is unique i.e. not already in the DB

                // Gets the next available category_id for the new category
                int newID = sqlManager.getNextPKValue(conn, tableName, "category_id");

                String query = String.format("INSERT INTO %s (category_id, category_name, date_created) VALUES (?,?,?)", tableName);
                try {
                    // Query Setup & Execution
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, newID);
                    pstmt.setString(2, inputCategory);
                    pstmt.setString(3, Utility.getCurrentDate());

                    int rowsAffected = pstmt.executeUpdate();
                    logger.log(Level.INFO, rowsAffected + " row(s) inserted.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQLException");
                }
                return inputCategory;
            }
        }

        return null;
    }
}
