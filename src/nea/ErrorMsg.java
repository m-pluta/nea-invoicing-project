/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Michal
 */
public class ErrorMsg {

    private static final Logger logger = Logger.getLogger(ErrorMsg.class.getName());

    /**
     * Default Message: An error has occurred
     */
    static final int DEFAULT_ERROR = -1;

    /**
     * Default Message: One or more of the input fields is empty
     */
    static final int EMPTY_INPUT_FIELD_ERROR = 0;

    /**
     * Default Message: The 'Add category' option is not a valid category
     */
    static final int INVALID_CATEGORY_SELECTED_ERROR = 1;

    /**
     * This must have an accompanying message
     * <p>
     * Default Message: <code>customMessage</code> already exists
     */
    static final int ALREADY_EXISTS_ERROR = 2;

    /**
     * This must have an accompanying message
     * <p>
     * Default Message: The entered <code>customMessage</code> is too short
     */
    static final int INPUT_LENGTH_ERROR_SHORT = 3;

    /**
     * This must have an accompanying message
     * <p>
     * Default Message: The entered <code>customMessage</code> is too long
     */
    static final int INPUT_LENGTH_ERROR_LONG = 4;

    /**
     * Default Message: Login details do not match, check if you have entered
     * them correctly
     */
    static final int INPUT_DETAILS_MISMATCH_ERROR = 5;

    /**
     * Default Message: Incorrect username or password
     */
    static final int INVALID_LOGIN_DETAILS_ERROR = 6;

    /**
     * Default Message: No row selected
     */
    static final int NOTHING_SELECTED_ERROR = 7;

    /**
     * Default Message: This is the default row and cannot be removed
     */
    static final int CANNOT_REMOVE_ERROR = 8;

    /**
     * Default Message: This is the default row and cannot be edited
     */
    static final int CANNOT_EDIT_ERROR = 9;

    /**
     * This must have an accompanying message
     * <p>
     * Default Message: You must enter something for the
     * <code>customMessage</code>
     */
    static final int INVALID_INPUT_ERROR = 10;

    /**
     * Default Message: File does not exist
     */
    static final int DOES_NOT_EXIST_ERROR = 11;

    /**
     * This must have an accompanying message
     * <p>
     * Default Message: None
     */
    static final int NUMBER_FORMAT_ERROR = 12;

    public static void throwCustomError(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
        logger.log(Level.INFO, msg);
    }

    public static void throwError() {
        throwError(DEFAULT_ERROR, "");
    }

    public static void throwError(int error_id) {
        throwError(error_id, "");
    }

    public static void throwError(String customMessage) {
        throwError(DEFAULT_ERROR, customMessage);
    }

    public static void throwError(int error_id, String customMessage) {
        String msg = "";
        switch (error_id) {
            case 0:
                msg = customMessage.isEmpty() ? "One or more of the input fields is empty" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Empty Input Field Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 1:
                msg = customMessage.isEmpty() ? "The 'Add category' option is not a valid category" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Category Selected Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 2:
                msg = customMessage + " already exists";

                JOptionPane.showMessageDialog(null, msg, "Already Exists Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 3:
                msg = "The entered " + customMessage + " is too short";

                JOptionPane.showMessageDialog(null, msg, "Input Length Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 4:
                msg = "The entered " + customMessage + " is too long";

                JOptionPane.showMessageDialog(null, msg, "Input Length Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 5:
                msg = customMessage.isEmpty() ? "Login details do not match, check if you have entered them correctly" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Input Details Mismatch Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 6:
                msg = customMessage.isEmpty() ? "Incorrect username or password" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Login Details Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 7:
                msg = customMessage.isEmpty() ? "No row selected" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Nothing Selected Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 8:
                msg = customMessage.isEmpty() ? "This is the default row and cannot be removed" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Cannot Remove Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 9:
                msg = customMessage.isEmpty() ? "This is the default row and cannot be edited" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Cannot Edit Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 10:
                msg = "You must enter something for the " + customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 11:
                msg = customMessage.isEmpty() ? "File does not exist" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "File Does Not Exist Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
            case 12:
                JOptionPane.showMessageDialog(null, customMessage, "Number Format Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;

            default:
                msg = customMessage.isEmpty() ? "An error has occurred" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.INFO, msg);
                break;
        }
    }
}
