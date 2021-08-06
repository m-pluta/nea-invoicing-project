/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import javax.swing.JOptionPane;

/**
 *
 * @author Michal
 */
public class ErrorMsg {

    static int DEFAULT_ERROR = -1;
    static int EMPTY_INPUT_FIELD_ERROR = 0;
    static int INVALID_CATEGORY_SELECTED_ERROR = 1;
    static int ALREADY_EXISTS_ERROR = 2;
    static int INPUT_LENGTH_ERROR = 3;
    static int INPUT_DETAILS_MISMATCH_ERROR = 4;
    static int INVALID_LOGIN_DETAILS_ERROR = 5;
    /**
     * This must have an accompanying message
     */
    static int NOTHING_SELECTED_ERROR = 6;
    static int CANNOT_REMOVE_ERROR = 7;
    static int CANNOT_EDIT_ERROR = 8;
    /**
     * This must have an accompanying message
     */
    static int INVALID_INPUT_ERROR = 9;

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
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 1:
                msg = customMessage.isEmpty() ? "The 'Add category' option is not a valid category" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Category Selected Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 2:
                msg = customMessage.isEmpty() ? "Category name under this name already exists" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Already Exists Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 3:
                msg = "The entered " + customMessage + " is too long";

                JOptionPane.showMessageDialog(null, msg, "Input Length Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 4:
                msg = customMessage.isEmpty() ? "Login details do not match, check if you have entered them correctly" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Input Details Mismatch Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 5:
                msg = customMessage.isEmpty() ? "Incorrect login details, check you have entered them correctly" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Login Details Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 6:
                msg = customMessage.isEmpty() ? "No row selected" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Nothing Selected Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 7:
                msg = customMessage.isEmpty() ? "This is the default row and cannot be removed" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Cannot Remove Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 8:
                msg = customMessage.isEmpty() ? "This is the default row and cannot be edited" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Cannot Edit Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
            case 9:
                msg = "You must enter something for the " + customMessage;

                JOptionPane.showMessageDialog(null, msg, "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
            default:
                msg = customMessage.isEmpty() ? "An error has occurred" : customMessage;

                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(msg);
                break;
        }

    }

}
