/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
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

    // Takes the Title of the dialog box and all the field names as parameters
    // Creates a dialog box with all the field names as the label and a corresponding JTextField for each field#
    // Returns the String[] of all the user inputs
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

    // Formats a double to a string with the user's default currency and does some rounding as well
    public static String formatCurrency(double cost) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String costString = formatter.format(cost);
        return costString;
    }

    public static JTable setColumnWidths(JTable table, int[] widths) {
        int NoCols = table.getModel().getColumnCount();
        if (NoCols == 0 || widths == null) {
            return null;
        }

        //current width of the table:
        int totalWidth = table.getWidth();
        System.out.println(totalWidth);

        int totalWidthRequested = 0;
        int nrRequestedWidths = widths.length;
        int defaultWidth = (int) Math.floor((double) totalWidth / (double) NoCols);

        for (int col = 0; col < NoCols; col++) {
            int width = 0;
            if (widths.length > col) {
                width = widths[col];
            }
            totalWidthRequested += width;
        }
        //Note: for the not defined columns: use the defaultWidth
        if (nrRequestedWidths < NoCols) {
            totalWidthRequested += ((NoCols - nrRequestedWidths) * defaultWidth);
        }
        //calculate the scale for the column width
        double factor = (double) totalWidth / (double) totalWidthRequested;

        for (int col = 0; col < NoCols; col++) {
            int width = defaultWidth;
            if (widths.length > col) {
                //scale the requested width to the current table width
                width = (int) Math.floor(factor * (double) widths[col]);
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(width);
            table.getColumnModel().getColumn(col).setWidth(width);
        }

        return table;
    }
}
