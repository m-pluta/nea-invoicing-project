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
    static int NOTHING_SELECTED_ERROR = 6;
    static int CANNOT_REMOVE_ERROR = 7;
    static int CANNOT_EDIT_ERROR = 8;
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
        switch (error_id) {
            case 0:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "One or more of the input fields is empty" : customMessage, "Empty Input Field Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("One or more of the input fields is empty");
                break;
            case 1:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "The 'Add category' option is not a valid category" : customMessage, "Invalid Category Selected Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("The 'Add category' option is not a valid category");
                break;
            case 2:
                JOptionPane.showMessageDialog(null, customMessage + " under this name already exists", "Already Exists Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println(customMessage + " under this name already exists");
                break;
            case 3:
                JOptionPane.showMessageDialog(null, "The entered " + customMessage + " is too long", "Input Length Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Category name is too long");
                break;
            case 4:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "Login details do not match, check if you have entered them correctly" : customMessage, "Input Details Mismatch Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Login details do not match, check you have entered them correctly");
                break;
            case 5:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "Incorrect login details, check you have entered them correctly" : customMessage, "Invalid Login Details Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Incorrect login details");
                break;
            case 6:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "No row selected" : customMessage, "Nothing Selected Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("No row selected");
                break;
            case 7:
                JOptionPane.showMessageDialog(null, "This is the default row and cannot be removed", "Cannot Remove Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Default row cannot be removed");
                break;
            case 8:
                JOptionPane.showMessageDialog(null, "This is the default row and cannot be edited", "Cannot Edit Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Default row cannot be edited");
                break;
            case 9:
                JOptionPane.showMessageDialog(null, "You must enter something for the " + customMessage, "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Empty " + customMessage);
            default:
                JOptionPane.showMessageDialog(null, customMessage.isEmpty() ? "An error has occurred" : customMessage, "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("An error has occurred");
                break;
        }

    }

}
