/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Michal
 */
public class sqlManager {
    
    public static Connection connectToDB(String url, String username, String password) {
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
    
    
}
