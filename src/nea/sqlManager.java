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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
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
                id = rs.getInt("nextID") + 1;                       // Increments the current max PK value to get the new max value
            } else {
                id = 1;                                             // If there are not records in the table then the default value is 1
            }

        } catch (SQLException e) {
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
            System.out.println(rowsAffected + " row(s) removed.");

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
            e.printStackTrace();
        }
        return null;
    }

    // Returns the total value of all the items in a given document (invoice/quotation). This is the sum of all the quantity * unit_price
    public static double totalDocument(Connection conn, String tableName, String PK_name, int document_id) {
        String query = "SELECT COALESCE(SUM(unit_price * quantity), 0) as total FROM " + tableName + " WHERE " + PK_name + " = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, document_id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If an document with the given id was found
                return rs.getDouble(1);                             // Returns the total value of the document
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Returns the id of the item category given its name and table
    public static int getIDofCategory(Connection conn, String tableName, String category) {
        String query = "SELECT category_id FROM " + tableName + " WHERE category_name = ?";
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
            e.printStackTrace();
        }
        return -1;
    }

    // Returns the id of the item category given its name and table
    public static int getIDofCustomer(Connection conn, String name) {
        String query = "SELECT customer_id FROM tblCustomers WHERE CONCAT(forename,' ',surname) = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If an id is found
                return rs.getInt(1);
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error fetching category id of customer with name: " + name);
            }
        } catch (SQLException e) {
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
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
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
            e.printStackTrace();
        }
        return LocalDate.of(1970, 1, 1).atTime(0, 0, 0);            // If an error occurs or no result is found then the date is set to the unix base date
    }

    // Returns the invoice number in the current financial year given a date
    public static int getInvoiceNoThisFinancialYear(Connection conn, LocalDateTime datetime) {

        LocalDate financialyear = Utility.getFinancialYear(datetime);           // Gets the start date of the financial year

        String query = "SELECT COUNT(invoice_id) FROM tblInvoices WHERE date_created BETWEEN ? AND ?";  // Gets the amount of invoices between the financial 
        try {                                                                                           // year start date and the date of the invoice
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setObject(1, financialyear.atTime(0, 0, 0));
            pstmt.setObject(2, datetime.minusSeconds(1));           // Takes away a second from the enddate param to not include the invoice in question

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a result is found
                return rs.getInt(1) + 1;                            // Adds one to the fetched count to include the current invoice
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Returns the quotation number in the current financial year given a date
    public static int getQuotationNoThisFinancialYear(Connection conn, LocalDateTime datetime) {

        LocalDate financialyear = Utility.getFinancialYear(datetime);           // Gets the start date of the financial year

        String query = "SELECT COUNT(quotation_id) FROM tblQuotations WHERE date_created BETWEEN ? AND ?";  // Gets the amount of quotations between the financial 
        try {                                                                                               // year start date and the date of the quotation
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setObject(1, financialyear.atTime(0, 0, 0));
            pstmt.setObject(2, datetime.minusSeconds(1));           // Takes away a second from the enddate param to not include the invoice in question

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a result is found
                return rs.getInt(1) + 1;                            // Adds one to the fetched count to include the current invoice
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getMaxColumnLength(Connection conn, String tableName, String column) {
        String query = "SELECT character_maximum_length FROM information_schema.columns"
                + " WHERE table_name = ? AND column_name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tableName);
            pstmt.setString(2, column);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If a result is found
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Allows the user to add a new customer category
    public static String addNewCustomerCategory(Connection conn) {
        return addNewCategory(conn, "tblCustomerCategories");
    }

    // Allows the user to add a new item category
    public static String addNewItemCategory(Connection conn) {
        return addNewCategory(conn, "tblItemCategories");
    }

    // Allows the user to add a new category
    public static String addNewCategory(Connection conn, String tableName) {
        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category"); // Asks user for the name of the category

        if (inputCategory != null) {

            inputCategory = inputCategory.trim();                   // Removes all leading and trailing whitespace characters           

            if (inputCategory.length() > sqlManager.getMaxColumnLength(conn, tableName, "category_name")) {
                ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "category name");

            } else if (sqlManager.RecordExists(conn, tableName, "category_name", inputCategory)) { // Checks if category already exists in DB
                ErrorMsg.throwError(ErrorMsg.ALREADY_EXISTS_ERROR, "Category");

            } else {                                                // If it is a unique category
                String query = "INSERT INTO " + tableName + " (category_id, category_name, date_created) VALUES (?,?,?)";
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    int newID = sqlManager.getNextPKValue(conn, tableName, "category_id");   // Gets the next available value of the primary key
                    pstmt.setInt(1, newID);
                    pstmt.setString(2, inputCategory);
                    pstmt.setString(3, Utility.getCurrentDate());

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("-------------------------------");
                    System.out.println(rowsAffected + " row(s) inserted.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return inputCategory;
            }
        }
        return null;
    }
}
