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

    static String DEFAULT_url = "jdbc:mysql://localhost:3306/dbNEA?serverTimezone=GMT";
    static String DEFAULT_username = "root";
    static String DEFAULT_password = "root";

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

    public static Connection openConnection(String username, String password) {
        return openConnection(DEFAULT_url, username, password);
    }

    public static Connection openConnection() {
        return openConnection(DEFAULT_url, DEFAULT_username, DEFAULT_password);
    }

    public static boolean closeConnection(Connection conn) {
        try {
            if (!conn.isClosed()) {
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Could not close! " + e.getMessage());
        }
        return false;
    }

    public static int getNextPKValue(Connection conn, String tableName, String PK_name) {
        String stringID = "";
        try {
            String strSQL = "SELECT max(" + PK_name + ") as nextID from " + tableName + ""; // Fetches the highest value of the PK from the table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(strSQL);
            if (rs.next()) {
                stringID += (rs.getInt("nextID") + 1); // Increments the current max PK value to get the new max value
            } else {
                stringID = "1"; // Sets the default id to 1
            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.toString());
            e.printStackTrace();
        }

        int integerID = 1;
        try {
            integerID = Integer.parseInt(stringID); // Converts the PK value from string to int
        } catch (NumberFormatException e) {
            System.out.println("-------------------------------");
            System.out.println("NumberFormatException: " + e);
        }

        return integerID;
    }

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

    public static boolean RecordExists(Connection conn, String tableName, String key, String key_value) { // Checks if a category under a given name already exists
        String query = "SELECT 1 FROM " + tableName + " WHERE " + key + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, key_value);

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return false; // If it doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static String getCategory(Connection conn, String tableName, String key, int catID) {
        String query = "SELECT category_name FROM " + tableName + " WHERE " + key + " = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, catID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
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

    public static int countInvoices(Connection conn, int id) {
        try {
            String query = "SELECT COUNT(customer_id) FROM tblInvoices WHERE customer_id = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.toString());
            e.printStackTrace();
        }
        return -1;
    }

    public static int countQuotations(Connection conn, int id) {
        try {
            String query = "SELECT COUNT(customer_id) FROM tblQuotations WHERE customer_id = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.toString());
            e.printStackTrace();
        }
        return -1;
    }

}
