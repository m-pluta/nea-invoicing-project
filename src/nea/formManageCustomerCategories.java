/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Michal
 */
public class formManageCustomerCategories extends javax.swing.JFrame {

    /**
     * Creates new form formManageCustomerCategories
     */
    formMainMenu previousForm = null;                               // Stores the previous Form object
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model = null;                                 // The table model
    public static String sp = "";                                   // SearchParameter, this stores whatever is currently in the Search box

    public formManageCustomerCategories() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        model = (DefaultTableModel) jTable_CustomerCategories.getModel(); // Fetches the table model of the table

        JTableHeader header = jTable_CustomerCategories.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {    // Document Listener for when the user wants to search for something new
            @Override
            public void insertUpdate(DocumentEvent e) {             // When an insert occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadCategories();                                   // Refreshes the category table as the search term has changed
            }

            @Override
            public void removeUpdate(DocumentEvent e) {             // When a remove occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadCategories();                                   // Refreshes the category table as the search term has changed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
    }

    public formManageCustomerCategories getFrame() {
        return this;
    }

    public void loadCategories() {
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        model.setRowCount(0);                                       // Empties the table
        String query = "SELECT customer_category_id, category_name, date_created FROM tblCustomerCategories";

        if (!sp.equals("")) {                                       // When searchParameter is something
            query += " WHERE";
            query += " customer_category_id LIKE '%" + sp + "%'";   // \
            query += " OR category_name LIKE '%" + sp + "%'";       //  |-- Check whether a column value contains the searchParameter
            query += " OR date_created LIKE '%" + sp + "%'";        // /
        }

        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            int categoryCounter = 0;                                // variable for counting how many category are being shown in the table
            while (rs.next()) {
                System.out.println("-------------------------------");
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));                // For debugging, shows each customer category that is in the table
                System.out.println(rs.getString(3));

                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});  // Adds the category data as a new row in the table
                categoryCounter++;                                  // Increments category counter as a new category was added to the table
            }
            lblCategoryCount.setText("Number of categories: " + String.valueOf(categoryCounter)); // Updates category counter label
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to DB
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        lblManageCustomerCategories = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_CustomerCategories = new javax.swing.JTable();
        btnAddNew = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        lblCategoryCount = new javax.swing.JLabel();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Customer Category Management");

        lblManageCustomerCategories.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblManageCustomerCategories.setText("Manage Customer Categories");

        jTable_CustomerCategories.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable_CustomerCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category ID", "Category name", "Date created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable_CustomerCategories);

        btnAddNew.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnRemove.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblSearch.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSearch.setText("Search");

        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setName(""); // NOI18N

        lblCategoryCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCategoryCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCategoryCount.setText("Number of categories:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblSearch)
                            .addComponent(btnBack))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addComponent(lblManageCustomerCategories)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                                .addComponent(lblCategoryCount, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblManageCustomerCategories)
                    .addComponent(btnBack))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCategoryCount))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category"); // Asks user for the name of the customer category
        if (inputCategory != null) {                                // If the dialog input was valid    
            conn = sqlManager.openConnection();                     // Opens connection to the DB

            inputCategory = inputCategory.trim();                   // Removes all leading and trailing whitespace characters           

            if (sqlManager.RecordExists(conn, "tblCustomerCategories", "category_name", inputCategory)) { // Checks if category already exists in DB
                System.out.println("-------------------------------");
                System.out.println("Category under this name already exists");
            } else {                                                // If it is a unique category
                String query = "INSERT INTO tblCustomerCategories (customer_category_id, category_name, date_created) VALUES (?,?,?)";
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    int newID = sqlManager.getNextPKValue(conn, "tblCustomerCategories", "customer_category_id");   // Gets the next available value of the primary key
                    pstmt.setInt(1, newID);
                    pstmt.setString(2, inputCategory);
                    pstmt.setString(3, Utility.getCurrentDate());

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("-------------------------------");
                    System.out.println(rowsAffected + " row inserted.");
                    loadCategories();                               // Refreshes Table

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            sqlManager.closeConnection(conn);                       // Closes connection to DB
        }
    }//GEN-LAST:event_btnAddNewActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int row = jTable_CustomerCategories.getSelectedRow();       // Gets the currently selected row in the table

        if (row == -1) {                                            // If no row is selected
            System.out.println("-------------------------------");
            System.out.println("No row selected");
        } else {
            String string_id = model.getValueAt(row, 0).toString(); // Gets the values from the selected row in the table as strings
            String category = model.getValueAt(row, 1).toString();

            int id = Utility.StringToInt(string_id);                // Converts the id in string type to integer type

            if (id == 1) {                                          // Checks if the user is trying to remove the first row - this is the default row and cannot be removed
                System.out.println("-------------------------------");
                System.out.println("This is the default row and cannot be removed");
            } else {                                                // If it is any other row other than row 1
                conn = sqlManager.openConnection();                 // Opens connection to DB
                int usersWithCategory = sqlManager.countRecordsWithCategory(conn, "tblCustomers", "type_id", id);
                if (usersWithCategory == -1) {
                    System.out.println("Error fetching customers with this category");
                } else if (usersWithCategory > 0) {
                    System.out.println("Cannot remove category since " + usersWithCategory + " customer(s) are under this category");
                } else {

                    // Asks user whether they really want to remove the category
                    int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the category - '" + category + "'?", "Remove Category", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                    if (YesNo == 0) {                               // If response is yes
                        System.out.println("-------------------------------");
                        System.out.println("Removing category " + string_id + " - " + category + ".");  // For debugging

                        sqlManager.removeRecord(conn, "tblCustomerCategories", "customer_category_id", id); // Removes the selected category
                        loadCategories();                           //Refreshes table since a record was removed
                    }
                }
                sqlManager.closeConnection(conn);                   // Closes connection to DB
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = jTable_CustomerCategories.getSelectedRow();       // Gets the currently selected row in the table

        if (row == -1) {                                            // If no row is selected
            System.out.println("-------------------------------");
            System.out.println("No row selected");
        } else {                                                    // If a row was selected
            String string_id = model.getValueAt(row, 0).toString(); // Gets the values from the selected row in the table as strings
            String category = model.getValueAt(row, 1).toString();

            int id = Utility.StringToInt(string_id);                // Converts the id in string type to integer type

            if (id == 1) {                                          // Checks if the user is trying to edit the first row - this is the default row and therefore cannot be edited
                System.out.println("-------------------------------");
                System.out.println("This is the default row and cannot be edited");
            } else {
                // Asks user what the new name of the category should be
                String inputCategory = Utility.StringInputDialog("Current name:  '" + category + "'", "Edit category name");

                if (inputCategory != null) {                        // If the dialog window was closed    
                    inputCategory = inputCategory.trim();           // Removes all leading and trailing whitespace characters

                    conn = sqlManager.openConnection();             // Opens connection to the DB
                    if (sqlManager.RecordExists(conn, "tblCustomerCategories", "category_name", inputCategory)) { // Checks if category already exists in DB

                        System.out.println("-------------------------------");
                        System.out.println("Category under this name already exists");
                        // # TODO reopen dialog
                        // # TODO Allow the user to merge the two categories together under the wanted name

                    } else {

                        // Update category name in the DB
                        String query = "UPDATE tblCustomerCategories SET category_name = ? WHERE customer_category_id = ?";
                        PreparedStatement pstmt = null;
                        try {
                            pstmt = conn.prepareStatement(query);
                            pstmt.setString(1, inputCategory);
                            pstmt.setInt(2, id);

                            int rowsAffected = pstmt.executeUpdate();
                            System.out.println(rowsAffected + " row updated.");
                            loadCategories();                       //Refreshes table since a record was updated
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    sqlManager.closeConnection(conn);               // Closes connection to the DB
                }
            }
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        previousForm.setVisible(true);                              // Makes main previous form visible
        this.dispose();                                             // Closes the customer category management form (current form)

    }//GEN-LAST:event_btnBackActionPerformed

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
            java.util.logging.Logger.getLogger(formManageCustomerCategories.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formManageCustomerCategories.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formManageCustomerCategories.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formManageCustomerCategories.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formManageCustomerCategories().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_CustomerCategories;
    private javax.swing.JLabel lblCategoryCount;
    private javax.swing.JLabel lblManageCustomerCategories;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
