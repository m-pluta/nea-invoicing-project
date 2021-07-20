/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Michal
 */
public class formManageEmployees extends javax.swing.JFrame {

    /**
     * Creates new form formAddNewEmployee
     */
    formMainMenu previousForm = null;                               // Stores the previously open form
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model;                                        // The table model
    formOneEmployee Employee_in_view = null;                        // could be null or could store whichever employee the user is currently viewing
    public static String sp = "";                                   // SearchParameter, this stores whatever is currently in the Search box
    public boolean CurrentlyAddingEmployee = false;
    
    public formManageEmployees() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        model = (DefaultTableModel) jTable_Employees.getModel();    // Fetches the table model of the table
        jTable_Employees.setDefaultEditor(Object.class, null);      // Makes it so the user cannot edit the table

        JTableHeader header = jTable_Employees.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        jTable_Employees.addMouseListener(new MouseListener() {     // Mouse listener for when the user clicks on a row in the employee table
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedID = getSelectedEmployee();             // Gets the id of the employee which is currently selected in the table
                if (selectedID != -1) {                             // id of the employee is not '-1', this is the default return value from getSelectedEmployee()
                    if (Employee_in_view != null) {                 // If there is another employee in view then it closes it
                        Employee_in_view.dispose();
                    }

                    formOneEmployee form = new formOneEmployee().getFrame();    // Opens a new instance of the formOneEmployee() form
                    form.setLocation(1630, 422);                    // Sets the location of the employee view to the right of the current employee management form
                    form.setVisible(true);                          // Makes the new employee view visible
                    form.EmployeeID = selectedID;                   // Tells the employee view form which employee to load
                    form.previousForm = formManageEmployees.this;   // Informs the employee view what the previous form is 
                    form.loadEmployee();                            // Runs the loadEmployee() method which will load all of the specified employee's details
                    Employee_in_view = form;                        // Sets the employee in view to this

                } else {
                    System.out.println("Something is truly wrong"); // Not sure how you would reach this point
                }

            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {    // Document Listener for when the user wants to search for something new
            @Override
            public void insertUpdate(DocumentEvent e) {             // When an insert occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadEmployees();                                    // Refreshes the employee table as the search term has changed
            }

            @Override
            public void removeUpdate(DocumentEvent e) {             // When a remove occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadEmployees();                                    // Refreshes the employee table as the search term has changed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
    }

    public formManageEmployees getFrame() {
        return this;
    }

    // Loads all the employees in the DB into the table, the results are limited by whatever the searchParameter is (the value in the search bar)
    public void loadEmployees() {
        model.setRowCount(0);                                       // Empties the table
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT employee_id, forename, surname, phone_number, email_address FROM tblEmployees";

        if (!sp.equals("")) {                                       // When searchParameter is something
            query += " WHERE";
            query += " employee_id LIKE '%" + sp + "%'";            // \
            query += " OR forename LIKE '%" + sp + "%'";            //  |
            query += " OR surname LIKE '%" + sp + "%'";             //  |-- Check whether a column value contains the searchParameter
            query += " OR phone_number LIKE '%" + sp + "%'";        //  |
            query += " OR email_address LIKE '%" + sp + "%'";       // /
        }

        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            int employeeCounter = 0;                                // variable for counting how many employees are being shown in the table
            while (rs.next()) {                                     // If there is another result from the DBMS
                System.out.println("-------------------------------");
                System.out.println(rs.getString(1));
                String FullName = rs.getString(2) + " " + rs.getString(3);
                System.out.println(FullName);                       // For debugging, shows each employee's data
                System.out.println(rs.getString(4));
                System.out.println(rs.getString(5));
                String last_login_date = sqlManager.getLastLogin(conn, Utility.StringToInt(rs.getString(1)));

                model.addRow(new Object[]{rs.getString(1), FullName, rs.getString(4), rs.getString(5), last_login_date}); // Adds the employee to the table
                employeeCounter++;                                  // Increments employee counter as a new employee was added to the table

            }
            lblEmployeeCount.setText("Number of employees: " + String.valueOf(employeeCounter)); // Updates employee counter label
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblManageEmployees = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        lblEmployeeCount = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Employees = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Management");

        lblManageEmployees.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblManageEmployees.setText("Manage Employees");

        lblSearch.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSearch.setText("Search");

        lblEmployeeCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmployeeCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblEmployeeCount.setText("Number of employees:");

        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setName(""); // NOI18N

        btnAddNew.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        jTable_Employees.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable_Employees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Full Name", "Phone Number", "Email Address", "Last Logged In"
            }
        ));
        jScrollPane1.setViewportView(jTable_Employees);

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblSearch)
                            .addComponent(btnBack))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblEmployeeCount, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(179, 179, 179)
                                .addComponent(lblManageEmployees)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBack)
                    .addComponent(lblManageEmployees))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmployeeCount))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        if (Employee_in_view != null) {                             // Checks whether there is another form opened showing the selected employee
            Employee_in_view.dispose();                             // If there is another form then it gets rid of it
        }
        previousForm.setVisible(true);                              // Makes main previous form visible
        this.dispose();                                             // Closes the employee management form (current form)
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
        if (!CurrentlyAddingEmployee) {
            formAddEmployee form = new formAddEmployee().getFrame();    // Opens a new instance of the formAddCustomer() form
            form.setLocationRelativeTo(null);               // Sets the location of the customer view to the right of the current customer management form
            form.setVisible(true);                          // Makes the new customer view visible
            form.previousForm = this;
            CurrentlyAddingEmployee = true;
        }
    }//GEN-LAST:event_btnAddNewActionPerformed

    // Returns the Employee_id of the selected employee in the employee table
    public int getSelectedEmployee() {
        int selectedRow = jTable_Employees.getSelectedRow();        // Gets the selected row in the table
        if (selectedRow == -1) {                                    // If no row is selected in the table
            System.out.println("-------------------------------");
            System.out.println("No row selected");
        } else {                                                    // If there is a row selected in the table
            String string_id = model.getValueAt(selectedRow, 0).toString(); // Gets the id of the selected in string form
            int id = Utility.StringToInt(string_id);                // Converts the id from string type to integer type
            return id;
        }
        return -1;                                                  //  Returns -1 if there were to be an error somewhere
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formManageEmployees.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formManageEmployees.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formManageEmployees.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formManageEmployees.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formManageEmployees().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnBack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Employees;
    private javax.swing.JLabel lblEmployeeCount;
    private javax.swing.JLabel lblManageEmployees;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
