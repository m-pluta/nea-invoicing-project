/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

    public static String[] JOptionPaneMultiInput(String[] Fieldnames) {
        int NoInputs = Fieldnames.length;                           // Number of inputs the user must enter
        JTextField[] inputBoxes = new JTextField[NoInputs];         // Array of JTextFields. One Field for each input
        JPanel myPanel = new JPanel();                              // JPanel to hold all of the TextFields and Labels
        
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS)); // Box Layout in order to vertically align all the components
        
        for (int i = 0; i < NoInputs; i++) {                        // Goes through each input
            inputBoxes[i] = new JTextField(30);                     // Creates new TextField and adds it to the array holding all the TextFields
            myPanel.add(new JLabel(Fieldnames[i] + ": "));          // Label which tells the user what piece of information to enter
            myPanel.add(inputBoxes[i]);                             // Adds the text box
            myPanel.add(Box.createHorizontalGlue());               // <--- ?????? Confused about this
        }
        
        // Creates a new instance of the Dialog
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter these details to change your login details", JOptionPane.OK_CANCEL_OPTION);
        if (result == 0) {                                          // If the user selected OK
            String[] output = new String[NoInputs];                 // Array holds all of the user's inputs
            for (int i = 0; i < NoInputs; i++) {                    //
                output[i] = inputBoxes[i].getText();                // Grabs whatever the user put in the TextField and adds it to the array

            }
            return output;                                          // Returns the array of all the inputs
        }
        return null;
    }
}
