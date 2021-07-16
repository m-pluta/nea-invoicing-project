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
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            System.out.println("Could not close! " + e.getMessage());
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
            System.out.println("SQLException: " + e.toString());
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
            e.printStackTrace();
        }

        return null;
    }

    // Counts how many invoices a given customer_id or employee_id has
    public static int countInvoices(Connection conn, String user_key, int id) {
        try {
            String query = "SELECT COUNT(" + user_key + ") FROM tblInvoices WHERE " + user_key + " = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();                                              // Gets the next result from query
            return rs.getInt(1);                                    // Returns the number of invoices

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.toString());
            e.printStackTrace();
        }
        return -1;
    }

    // Counts how many quotations a given customer_id or employee_id has
    public static int countQuotations(Connection conn, String user_key, int id) {
        try {
            String query = "SELECT COUNT(" + user_key + ") FROM tblQuotations WHERE " + user_key + " = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();                                              // Gets the next result from query
            return rs.getInt(1);                                    // Returns the number of quotations

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.toString());
            e.printStackTrace();
        }
        return -1;
    }
    
    
    // This method might be implemented later on since im unsure if a full removal should even be allowed
    public static void removeCustomer(Connection conn, boolean fullRemoval, int customerID) {
        removeRecord(conn, "tblCustomers", "customer_id", customerID);
           
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
            e.printStackTrace();
        }
        return false;
    }
}
