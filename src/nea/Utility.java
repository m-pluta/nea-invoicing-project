/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = java.util.logging.Logger.getLogger(Utility.class.getName());

    // Hashes any string input using SHA256
    public static String hash(String input) {
        byte[] outputHash = null;

        try {
            // Initialises the MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hashes the input using SHA256
            outputHash = digest.digest(String.valueOf(input).getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, "No such algorithm exception: SHA256");
        }

        // Converts the hash byte array to a String
        StringBuilder hexString = new StringBuilder(2 * outputHash.length);
        for (int i = 0; i < outputHash.length; i++) {
            String hex = Integer.toHexString(0xff & outputHash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Returns the String version of the hash
        return hexString.toString();
    }

    // Sets the input JTextFields to either editable or uneditable
    public static void setEditable(JTextField[] fields, boolean state) {
        // Loops through all the JTextFields
        for (JTextField field : fields) {
            field.setEditable(state);
        }
    }

    // Returns number of input fields which are empty
    public static int countEmptyFields(JTextField[] fields) {
        int emptyFields = 0;
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                emptyFields++;
            }
        }
        return emptyFields;
    }

    // Returns the date of the financial year for a given date
    public static LocalDate getFinancialYear(LocalDate input) {
        if (input.isAfter(LocalDate.of(input.getYear(), Month.APRIL, 5))) {
            return LocalDate.of(input.getYear(), Month.APRIL, 6);
        } else {
            return LocalDate.of(input.getYear() - 1, Month.APRIL, 6);
        }
    }

    // Overloaded method for LocalDateTime param
    public static LocalDate getFinancialYear(LocalDateTime input) {
        return getFinancialYear(input.toLocalDate());
    }

    // Returns the quarter of the year a given date is in
    public static String getQuarter(LocalDate input) {
        // Gets the month as an int between 1-12
        int month = input.getMonthValue();

        if (month > 9) {
            return "Q4";
        } else if (month > 6) {
            return "Q3";
        } else if (month > 3) {
            return "Q2";
        } else {
            return "Q1";
        }
    }

    // Given a date, this method returns the start date date of the quarter the date is in
    public static LocalDate getQuarterStart(LocalDate input) {
        // Gets the month as an int between 1-12
        int month = input.getMonthValue();

        if (month > 9) {
            return LocalDate.now().withMonth(10).withDayOfMonth(1);
        } else if (month > 6) {
            return LocalDate.now().withMonth(7).withDayOfMonth(1);
        } else if (month > 3) {
            return LocalDate.now().withMonth(4).withDayOfMonth(1);
        } else {
            return LocalDate.now().withMonth(1).withDayOfMonth(1);
        }
    }

    // Returns the current system date as a string in the format yyyy-MM-dd HH:mm:ss
    public static String getCurrentDate() {
        // Initialises the Date Formatter
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        // Converts the Date into a String
        String date_string = formatter.format(date);
        return date_string;
    }

    // Opens an Input Dialog that has one JTextField component in it and returns the input
    public static String StringInputDialog(String message, String title) {
        // Init
        String input;
        input = JOptionPane.showInputDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);

        // If the window was not closed.
        if (input != null) {

            // If the string is not all whitespace or empty
            if (!input.chars().allMatch(Character::isWhitespace)) {
                return input;
            } else {
                ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "Category name cannot be empty");
                // Reopens the input dialog
                return Utility.StringInputDialog(message, title);
            }
        } else {
            logger.log(Level.INFO, "Input window closed.");
        }
        return null;
    }

    // Converts a String to an Integer
    public static int StringToInt(String input) {
        int output = 0;
        try {
            // String is converted to an integer
            output = Integer.parseInt(input);

        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "NumberFormatException");
        }
        return output;
    }

    // Takes the Title of the dialog box and all the field names as parameters
    // Creates a dialog box with all the field names as the label and a corresponding JTextField for each field
    // Returns the String[] of all the user inputs
    public static String[] JOptionPaneMultiInput(String windowTitle, String[] Fieldnames) {
        // Number of inputs the user must enter
        int NoInputs = Fieldnames.length;
        // JPanel to hold all of the TextFields and Labels
        JPanel myPanel = new JPanel();

        JTextField[] inputBoxes = new JTextField[NoInputs];
        JLabel[] labels = new JLabel[NoInputs];

        for (int i = 0; i < NoInputs; i++) {
            // New JTextField: One Field for each input
            inputBoxes[i] = new JTextField(20);
            // New JLabel: Each describes what the corresponding textbox wants from the user
            labels[i] = new JLabel(Fieldnames[i] + ": ");
        }

        // Sets the layout of the JPanel
        GroupLayout layout = new GroupLayout(myPanel);
        myPanel.setLayout(layout);

        // Gaps between components
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Full Explanation of code https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
        // Both the horizontal and vertical layout need to be specified
        // Horizontal Layout     
        SequentialGroup H_sg = layout.createSequentialGroup();

        ParallelGroup H_pg1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        ParallelGroup H_pg2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        for (int i = 0; i < NoInputs; i++) {
            // Adds each JLabel and JTextField to the group
            H_pg1.addComponent(labels[i]);
            H_pg2.addComponent(inputBoxes[i]);
        }
        // Adds the two parallel group to the main sequential group
        H_sg.addGroup(H_pg1);
        H_sg.addGroup(H_pg2);

        layout.setHorizontalGroup(H_sg);

        // Vertical Layout
        SequentialGroup V_sg = layout.createSequentialGroup();

        ParallelGroup temp;
        for (int i = 0; i < NoInputs; i++) {
            // Temporary parallel group
            temp = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            // Adds the JLabels and JTextFields to the temp parallel group
            temp.addComponent(labels[i]);
            temp.addComponent(inputBoxes[i]);
            // Adds the temporary group to the main sequential group
            V_sg.addGroup(temp);
        }

        layout.setVerticalGroup(V_sg);

        // Creates a new Dialog with the custom layout
        int result = JOptionPane.showConfirmDialog(null, myPanel, windowTitle, JOptionPane.OK_CANCEL_OPTION);
        // If the user selected OK
        if (result == 0) {
            String[] output = new String[NoInputs];
            for (int i = 0; i < NoInputs; i++) {
                // Puts whatever the user entered into the textbox, into the array
                output[i] = inputBoxes[i].getText();
            }
            // Returns the array of all the inputs
            return output;
        }
        return null;
    }

    // Formats a double to a string with the user's default currency (with rounding)
    public static String formatCurrency(double cost) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String costString = formatter.format(cost);
        return costString;
    }

    // This method adjusts the column widths of a given table based on an input integer array
    public static JTable setColumnWidths(JTable table, int[] widths) {
        int NoCols = table.getModel().getColumnCount();
        if (NoCols == 0 || widths == null) {
            return null;
        }

        // Current width of the table:
        int currentWidth = table.getWidth();

        int totalWidthRequested = 0;
        int nrRequestedWidths = widths.length;
        int defaultWidth = (int) Math.floor((double) currentWidth / (double) NoCols);

        for (int col = 0; col < NoCols; col++) {
            int width = 0;
            if (widths.length > col) {
                width = widths[col];
            }
            totalWidthRequested += width;
        }
        // defaultWidth used for columns with undefined new width
        if (nrRequestedWidths < NoCols) {
            totalWidthRequested += ((NoCols - nrRequestedWidths) * defaultWidth);
        }

        // Calculate the scale factor for the column width
        double factor = (double) currentWidth / (double) totalWidthRequested;

        for (int col = 0; col < NoCols; col++) {
            int width = defaultWidth;
            if (col < widths.length) {
                // Scale the requested width
                width = (int) Math.floor(factor * (double) widths[col]);
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(width);
            table.getColumnModel().getColumn(col).setWidth(width);
        }

        return table;
    }
}
