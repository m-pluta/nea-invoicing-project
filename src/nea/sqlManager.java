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

/**
 *
 * @author Michal
 */
public class sqlManager {

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
        } catch (ClassNotFoundException cE) {
            cE.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // Opens connection to db, no specific url given so the default url is used.
    public static Connection openConnection(String username, String password) {
        return openConnection(DEFAULT_url, username, password);
    }

    // Opens connection to db, no specific url, username or password given so a default connection is opened
    public static Connection openConnection() {
        return openConnection(DEFAULT_url, DEFAULT_username, DEFAULT_password);
    }

    // Closes the connection to the database
    public static boolean closeConnection(Connection conn) {
        try {
            if (!conn.isClosed()) {                                 // Checks if the connection is already closed to prevent an exception from happening
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Could not close!");
            e.printStackTrace();
        }
        return false;
    }

    // Fetches the next available value of the primary key in the DB 
    public static int getNextPKValue(Connection conn, String tableName, String PK_name) {
        int id = 0;
        try {
            String strSQL = "SELECT max(" + PK_name + ") as nextID from " + tableName + ""; // Fetches the current max value
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(strSQL);
            if (rs.next()) {
                id = rs.getInt("nextID") + 1;              // Increments the current max PK value to get the new max value
            } else {
                id = 1;                                     // If there are not records in the table then the default value is 1
            }

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }

        return id;
    }

    // Removes a record from a given table with a specfic primary key value
    public static void removeRecord(Connection conn, String tableName, String PK_name, int PK_value) { // Removes a record from the DB in a given table with a certain attribute value
        String query = "DELETE FROM " + tableName + " WHERE " + PK_name + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, PK_value);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("-------------------------------");
            System.out.println(rowsAffected + " row affected.");

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }

    }

    // Check if a specific record with a specific primary key value exists in a table returning a boolean of the result
    public static boolean RecordExists(Connection conn, String tableName, String key, String key_value) {
        String query = "SELECT 1 FROM " + tableName + " WHERE " + key + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, key_value);

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {                                       // If no results were fetched then the record doesn't exist
                return false;
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }

        return true;                                                // In the event of an error, it returns that the row DOES exist
    }

    // Gets the category name of any category given its' category_id
    public static String getCategory(Connection conn, String tableName, String key, int catID) {
        String query = "SELECT category_name FROM " + tableName + " WHERE " + key + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, catID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a category with the provided PK value was found
                return rs.getString(1);
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching category");
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }

        return null;
    }

    // Returns the amount of records are in a given table with a certain key value
    public static int countRecords(Connection conn, String tableName, String key, int id) {
        try {
            String query = "SELECT COUNT(" + key + ") FROM " + tableName + " WHERE " + key + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    public static int countRecordsWithCategory(Connection conn, String tableName, String key, int catID) {
        try {
            String query = "SELECT COUNT(" + key + ") FROM " + tableName + " WHERE " + key + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, catID);
            ResultSet rs = pstmt.executeQuery();
            rs.next();                                              // Gets the next result from query
            return rs.getInt(1);                                    // Returns the number of invoices

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    // Counts how many invoices a given customer_id or employee_id has
    public static int countInvoices(Connection conn, String user_key, int id) {
        try {
            String query = "SELECT COUNT(" + user_key + ") FROM tblInvoices WHERE " + user_key + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();                                              // Gets the next result from query
            return rs.getInt(1);                                    // Returns the number of invoices

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    // Counts how many quotations a given customer_id or employee_id has
    public static int countQuotations(Connection conn, String user_key, int id) {
        try {
            String query = "SELECT COUNT(" + user_key + ") FROM tblQuotations WHERE " + user_key + " = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();                                              // Gets the next result from query
            return rs.getInt(1);                                    // Returns the number of quotations

        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    // Returns the last time a given employee (id) was logged in
    public static String getLastLogin(Connection conn, int employee_id) {
        String query = "SELECT date_last_logged_in FROM tblLogins WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a employee with the given id was found
                return rs.getString(1);
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching last login date");
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return null;
    }

    // Returns true/false whether an employee has admin permissions
    public static boolean isAdmin(Connection conn, int employee_id) {
        String query = "SELECT admin FROM tblLogins WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a employee with the given id was found
                if (rs.getString(1).equals("Y")) {
                    return true;
                }
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching admin status");
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return false;
    }

    // Returns the full name of the employee with the given employee_id
    public static String getEmployeeFullName(Connection conn, int employee_id) {
        String query = "SELECT CONCAT(forename,' ', surname) FROM tblEmployees WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employee_id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a employee with the given id was found
                return rs.getString(1);                             // Returns the full name
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching employee name with id: " + employee_id);
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return null;
    }

    // Returns the full name of the customer with the given customer_id
    public static String getCustomerFullName(Connection conn, int customer_id) {
        String query = "SELECT CONCAT(forename,' ', surname) FROM tblCustomers WHERE customer_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customer_id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a customer with the given id was found
                return rs.getString(1);                             // Returns the full name
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching customer name with id: " + customer_id);
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return null;
    }

    // Returns the total value of all the items in a given document (invoice/quotation). This is the sum of all the quantity * unit_price
    public static double totalDocument(Connection conn, String tableName, String PK_name, int document_id) {
        String query = "SELECT quantity, unit_price FROM " + tableName + " WHERE " + PK_name + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, document_id);

            ResultSet rs = pstmt.executeQuery();
            double total = 0.00;
            while (rs.next()) {                                     // If an document with the given id was found
                total += rs.getInt(1) * rs.getDouble(2);            // Adds the quantity * unit_price to the total
            }
            return total;                                           // Returns the total value of the document
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    // Returns the id of the item category given its name
    public static int getIDofCategory(Connection conn, String category) {
        String query = "SELECT item_category_id FROM tblItemCategories WHERE category_name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, category);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If an id is found
                return rs.getInt(1);
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching category id of category with name: " + category);
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        return -1;
    }

    // Updates the date when the user last logged in given the employee_id
    public static void updateLastLogin(Connection conn, int employee_id) {
        String query = "UPDATE tblLogins SET date_last_logged_in = ? WHERE employee_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, Utility.getCurrentDate());
            pstmt.setInt(2, employee_id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("-------------------------------");
            System.out.println(rowsAffected + " row updated.");
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
    }

    // Returns the date_created of the invoice or quotation with the earliest date in the table
    public static LocalDateTime getEarliestDateTime(Connection conn, String tableName, String key) {
        String query = "SELECT " + key + " FROM " + tableName + " ORDER BY " + key + " LIMIT 1";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {                                        // If a result is found
                return rs.getDate(1).toLocalDate().atTime(0, 0, 0); // Gets the date and sets the time to 00:00:00
            }
        } catch (SQLException e) {
            System.out.println("-------------------------------");
            System.out.println("SQL Exception: " + e);
        }

        System.out.println("No match found or error occured");
        return LocalDate.of(1970, 1, 1).atTime(0, 0, 0);            // If an error occurs or no result is found then the date is set to the unix base date
    }
}
