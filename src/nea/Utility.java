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

    // Returns the current system date as a string in the format yyyy-MM-dd HH:mm:ss
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String date_string = formatter.format(date);
        return date_string;
    }

    // Opens a default Input Dialog that has one jTextField component in it 
    public static String StringInputDialog(String message, String title) {
        String input = null;
        input = JOptionPane.showInputDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        if (input == null) {                                        // If the dialog window was closed    
            System.out.println("-------------------------------");
            System.out.println("Input window closed.");
        } else {                                                    // If the input dialog wasn't closed
            if (input.replaceAll(" ", "").equals("")) {             // Removes all whitespace characters and checks if the string is left as ""
                System.out.println("-------------------------------");
                System.out.println("No input was registered.");
            } else {                                                // If the user input is valid then the input string is returned
                return input;
            }
        }
        return null;
    }

    // Converts a String to an integer
    // This was implemented as the try catch statement block was repeated many times throughout the program.
    public static int StringToInt(String input) {
        int output = 0;
        try {
            output = Integer.parseInt(input);                       // String is converted to an integer
        } catch (NumberFormatException e) {                         // If there was an exception
            System.out.println("-------------------------------");
            System.out.println("NumberFormatException: " + e);
        }
        return output;                                              // Return the converted integer
    }
}
