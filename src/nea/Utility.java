/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Michal
 */
public class Utility {

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String date_string = formatter.format(date); // Formats the current system date into 'yyyy-MM-dd HH:mm:ss'
        return date_string;
    }

    public static String StringInputDialog(String message, String title) {
        String input = null;
        input = JOptionPane.showInputDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        if (input == null) { // If the dialog window was closed    
            System.out.println("-------------------------------");
            System.out.println("Input window closed.");
        } else {
            if (input.replaceAll(" ", "").equals("")) { // Removes all whitespace characters and checks if the string is left as ""
                System.out.println("-------------------------------");
                System.out.println("No input was registered.");
            } else {
                return input;
            }
        }
        return null;
    }

    public static int StringToInt(String input) {
            int output = 0;
        try {
            output = Integer.parseInt(input); // id value from selected row is converted to int
        } catch (NumberFormatException e) {
            System.out.println("-------------------------------");
            System.out.println("NumberFormatException: " + e);
        }
        return output;
    }
}
