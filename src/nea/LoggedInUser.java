/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

public class LoggedInUser {

    // employee_id of the employee who logged into the program
    private static int ID = -1;
    private static boolean admin = false;

    public static void setID(int ID) {
        LoggedInUser.ID = ID;
    }

    public static int getID() {
        return ID;
    }

    public static boolean isAdmin() {
        updateAdminStatus();
        return admin;
    }

    public static void updateAdminStatus() {
        LoggedInUser.admin = sqlManager.isAdmin(ID);
    }
}
