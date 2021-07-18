/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
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

    public static String[] JOptionPaneMultiInput(String windowTitle, String[] Fieldnames) {
        int NoInputs = Fieldnames.length;                           // Number of inputs the user must enter
        
        JPanel myPanel = new JPanel();                              // JPanel to hold all of the TextFields and Labels
        
        JTextField[] inputBoxes = new JTextField[NoInputs];         // Array of JTextFields. One Field for each input
        JLabel[] labels = new JLabel[NoInputs];                     // Array of JLabels. Each describes what the corresponding textbox wants from the user

        for (int i = 0; i < NoInputs; i++) {                        // Goes through each input
            inputBoxes[i] = new JTextField(20);                     // Creates new TextField
            labels[i] = new JLabel(Fieldnames[i] + ": ");           // Creates new JLabel
        }

        GroupLayout layout = new GroupLayout(myPanel);              // New instance of the GridLayout layout manager 
        myPanel.setLayout(layout);                                  // sets it as the layout of the JPanel

        layout.setAutoCreateGaps(true);                             // Gaps between components
        layout.setAutoCreateContainerGaps(true);                    //

        // Explanation of code https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
        
        // Both the horizontal and vertical layout need to be specified otherwise there is an exception
        
        //Horizontal Layout     
        SequentialGroup H_sg = layout.createSequentialGroup();                              // Group which goes LEFT -> RIGHT
        
        ParallelGroup H_pg1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);    // 
        ParallelGroup H_pg2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);    // These groups are 'parallel' so they go TOP -> BOTTOM
        
        for (int i = 0; i < NoInputs; i++) {
            H_pg1.addComponent(labels[i]);                          // Adds each JLabel to the group going TOP -> BOTTOM
            H_pg2.addComponent(inputBoxes[i]);                      // Adds each JTextField
        }
        H_sg.addGroup(H_pg1);                                       //
        H_sg.addGroup(H_pg2);                                       // Adds each group that goes TOP -> BOTTOM to the group that goes LEFT -> RIGHT
        
        layout.setHorizontalGroup(H_sg);                            // Adds the horizontal layout to the overall layout
        
        // Vertical Layout
        
        SequentialGroup V_sg = layout.createSequentialGroup();      // Group which goes TOP -> BOTTOM
        
        ParallelGroup temp = null;                                  // There will be NoInputs amount of rows in the dialog box so it is easier to use a temporary Parallel Group that goes LEFT -> RIGHT
        for (int i = 0; i < NoInputs; i++) {
            temp = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);  // Creates a new parallel group that goes LEFT -> RIGHT
            temp.addComponent(labels[i]);                           // Adds the JLabel
            temp.addComponent(inputBoxes[i]);                       // Adds the JTextField
            V_sg.addGroup(temp);                                    // Adds the parallel group (the row) to the group that goes TOP -> BOTTOM
        }
        
        layout.setVerticalGroup(V_sg);                              // Adds the vertical layout to the overall layout
        
        

        // Creates a new instance of the Dialog
        int result = JOptionPane.showConfirmDialog(null, myPanel, windowTitle, JOptionPane.OK_CANCEL_OPTION);
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
