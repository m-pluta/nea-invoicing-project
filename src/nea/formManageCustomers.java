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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Michal
 */
public class formManageCustomers extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(formManageCustomers.class.getName());
    formMainMenu previousForm = null;

    // Init
    DefaultTableModel model = null;
    private String sp = "";

    // Whether the user is currently viewing a customer in another form or adding a new customer 
    formOneCustomer Customer_in_view = null;
    public boolean CurrentlyAddingCustomer = false;

    public formManageCustomers() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Fetches Table model and makes table non-editable
        model = (DefaultTableModel) jTable_Customers.getModel();
        jTable_Customers.setDefaultEditor(Object.class, null);

        // Sets up the table header to be a bit larger
        JTableHeader header = jTable_Customers.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));

        // When the user clicks on a row in the table
        jTable_Customers.addMouseListener(new MouseListener() {
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
                // Gets the id of the customer which is currently selected in the table
                int selectedID = getSelectedCustomer();
                if (selectedID != -1) {
                    if (Customer_in_view != null) {
                        // If the user is viewing another customer then that form is closed
                        Customer_in_view.dispose();
                    }

                    formOneCustomer form = new formOneCustomer().getFrame();
                    form.setVisible(true);

                    // Loads customer into the other form and sets up previousForm variable
                    form.CustomerID = selectedID;
                    form.previousForm = formManageCustomers.this;
                    form.loadCustomer();

                    //Sets customer in view variable to the customer which is being viewed
                    Customer_in_view = form;
                }
            }
        });

        // When the user changes their search in the search box
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Updates the search parameter and refreshes the table
                sp = txtSearch.getText();
                loadCustomers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Updates the search parameter and refreshes the table
                sp = txtSearch.getText();
                loadCustomers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
        
        //Loads the initial data
        loadCustomers();

        // Adjusting the header widths
        jTable_Customers = Utility.setColumnWidths(jTable_Customers, new int[]{40, 120, 100, 100, 175});
    }

    // Used when the form is opened from within another form
    public formManageCustomers getFrame() {
        return this;
    }

    // Loads all the customers into the table, the results are filtered using the searchParameter (sp)
    public void loadCustomers() {
        // Empties the table
        model.setRowCount(0);

        String query = "SELECT customer_id, CONCAT(forename,' ', surname) AS FullName, postcode, phone_number, email_address FROM tblCustomer";
        // If the user entered a search into the search box, the WHERE clause is adjusted
        if (!sp.isEmpty()) {
            query += " WHERE";
            query += " customer_id LIKE '%" + sp + "%'";
            query += " OR CONCAT(forename,' ', surname) LIKE '%" + sp + "%'";
            query += " OR postcode LIKE '%" + sp + "%'";
            query += " OR phone_number LIKE '%" + sp + "%'";
            query += " OR email_address LIKE '%" + sp + "%'";
        }
        query += " ORDER BY customer_id";

        try (Connection conn = sqlManager.openConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Counts the amount of customers which are being shown
            int customerCounter = 0;
            while (rs.next()) {

                // Adds the customer from the DB to the table
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
                customerCounter++;
            }

            // Updates customer counter label
            lblCustomerCount.setText("Number of customers: " + String.valueOf(customerCounter));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblManageCustomers = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        lblCustomerCount = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Customers = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Customer Management");

        lblManageCustomers.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblManageCustomers.setText("Manage Customers");

        lblSearch.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSearch.setText("Search");

        lblCustomerCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomerCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCustomerCount.setText("Number of customers:");

        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setName(""); // NOI18N

        btnAddNew.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        jTable_Customers.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable_Customers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Full Name", "Postcode", "Phone Number", "Email Address"
            }
        ));
        jScrollPane1.setViewportView(jTable_Customers);

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
                                .addGap(179, 179, 179)
                                .addComponent(lblManageCustomers)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblCustomerCount, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(lblManageCustomers))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerCount))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Goes back to the previous form
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // Checks if the user is viewing a customer in another form
        if (Customer_in_view != null) {
            Customer_in_view.dispose();
        }

        // Makes previous form visible and closes current form
        previousForm.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
        // Checks if the user is adding a customer in another form
        if (!CurrentlyAddingCustomer) {
            formAddCustomer form = new formAddCustomer().getFrame();
            form.setVisible(true);
            form.previousForm1 = this;
            CurrentlyAddingCustomer = true;
        }
    }//GEN-LAST:event_btnAddNewActionPerformed

    // Returns the customer_id of the selected customer in the table
    private int getSelectedCustomer() {
        int selectedRow = jTable_Customers.getSelectedRow();

        if (selectedRow == -1) {
            // If no row is selected in the table
            ErrorMsg.throwError(ErrorMsg.NOTHING_SELECTED_ERROR);

        } else {
            // Returns id of selected customer
            String string_id = model.getValueAt(selectedRow, 0).toString();
            int id = Utility.StringToInt(string_id);
            return id;
        }

        // Returns -1 if there were to be an error somewhere
        return -1;
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
            java.util.logging.Logger.getLogger(formManageCustomers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formManageCustomers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formManageCustomers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formManageCustomers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formManageCustomers().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnBack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Customers;
    private javax.swing.JLabel lblCustomerCount;
    private javax.swing.JLabel lblManageCustomers;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
