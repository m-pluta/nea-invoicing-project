/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author Michal
 */
public class formOneInvoice extends javax.swing.JFrame {

    /**
     * Creates new form formOneCustomer
     */
    int InvoiceID = 0;                                              // customer_id of currently loaded customer
    Connection conn = null;                                         // Stores the connection object
    formManageInvoices previousForm = null;                         // Stores the previous Form object

    public formOneInvoice() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        

    }

    public void loadInvoice() {
        conn = sqlManager.openConnection();
        String query = "SELECT customer_id, employee_id, date_created, date_deadline, payments FROM tblInvoices WHERE invoice_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, InvoiceID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {                                        // If an document with the given id was found
                txtInvoiceID.setText(String.valueOf(InvoiceID));
                txtCustomer.setText(sqlManager.getCustomerFullName(conn, rs.getInt(1)));
                txtEmployee.setText(sqlManager.getCustomerFullName(conn, rs.getInt(2)));
                txtDateCreated.setText(String.valueOf(rs.getDate(3)));
                txtDateDeadline.setText(String.valueOf(rs.getDate(4)));
                txtPayments.setText(String.valueOf(rs.getDouble(5)));  
            } else {
                System.out.println("-------------------------------");
                System.out.println("No invoice with this invoice_id was found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

//    // Returns true if the 'Add new category' option in the combo box is selected
//    private boolean isAddNewCategorySelected() {
//        return cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1;
//    }
//    // Allows the user to add a new customer category - This is almost entirely the same code as in fromManageCustomerCategories with minor changes
//    public void addNewCategory() {
//        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category"); // Asks user for the name of the customer category
//        if (inputCategory != null) {                                // If the dialog input was valid    
//            conn = sqlManager.openConnection();                     // Opens connection to the DB
//
//            inputCategory = inputCategory.trim();                   // Removes all leading and trailing whitespace characters           
//
//            if (sqlManager.RecordExists(conn, "tblCustomerCategories", "category_name", inputCategory)) { // Checks if category already exists in DB
//                System.out.println("-------------------------------");
//                System.out.println("Category under this name already exists");
//            } else {                                                // If it is a unique category
//                String query = "INSERT INTO tblCustomerCategories (customer_category_id, category_name, date_created) VALUES (?,?,?)";
//                try {
//                    PreparedStatement pstmt = conn.prepareStatement(query);
//                    int newID = sqlManager.getNextPKValue(conn, "tblCustomerCategories", "customer_category_id");   // Gets the next available value of the primary key
//                    pstmt.setInt(1, newID);
//                    pstmt.setString(2, inputCategory);
//                    pstmt.setString(3, Utility.getCurrentDate());
//
//                    int rowsAffected = pstmt.executeUpdate();
//                    System.out.println("-------------------------------");
//                    System.out.println(rowsAffected + " row inserted.");
//                    loadCustomerCategoriesIntoCB();                 // Refreshes Combo box so the new category is visible
//                    cbCategory.setSelectedIndex(newID - 1);         // Set the selected index to whatever category the user just added
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            sqlManager.closeConnection(conn);                       // Closes connection to DB
//        }
//    }
    public formOneInvoice getFrame() {
        return this;
    }

//    public void loadCustomerCategoriesIntoCB() {
//        cbCategory.removeAllItems();
//        conn = sqlManager.openConnection();                         // Opens connection to the DB
//        String query = "SELECT category_name FROM tblCustomerCategories";
//        try {
//            Statement stmt = conn.createStatement();
//
//            ResultSet rs = stmt.executeQuery(query);
//            System.out.println("-------------------------------");
//            while (rs.next()) {
//                System.out.println(rs.getString(1));                // For debugging
//                cbCategory.addItem(rs.getString(1));                // Ads the category to the combo box
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        sqlManager.closeConnection(conn);                           // Closes connection to the DB
//        cbCategory.addItem("Add a new category...");                // Set one of the option to a custom category
//    }
//    // Sets these components to either visible or invisible depending on the boolean state
//    public void setEditable(JTextField[] fields, boolean state) {
//        for (JTextField field : fields) {
//            field.setEditable(state);
//        }
//    }
//    // Loads the customer data into the form
//    public void loadCustomer() {
//        txtCustomerID.setText(String.valueOf(CustomerID));
//
//        conn = sqlManager.openConnection();
//
//        String query = "SELECT forename, surname, address1, address2, address3, county, postcode, phone_number, email_address, type_id FROM tblCustomers WHERE customer_id = ?";
//        try {
//            PreparedStatement pstmt = conn.prepareStatement(query);
//
//            pstmt.setInt(1, CustomerID);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                System.out.println("-------------------------------");
//                System.out.println(rs.getString(1));
//                System.out.println(rs.getString(2));    // For debugging, shows customer data
//                System.out.println(rs.getString(3));
//                System.out.println(rs.getString(4));
//                System.out.println(rs.getString(5));
//                System.out.println(rs.getString(6));
//                System.out.println(rs.getString(7));
//                System.out.println(rs.getString(8));
//                System.out.println(rs.getString(9));
//                System.out.println(rs.getString(10));
//
//                String FullName = rs.getString(1) + " " + rs.getString(2); // Formats the name components into one full name
//                lblFullName.setText(FullName);
//                txtForename.setText(rs.getString(1));
//                txtSurname.setText(rs.getString(2));
//                txtAddress1.setText(rs.getString(3));
//                txtAddress2.setText(rs.getString(4));
//                txtAddress3.setText(rs.getString(5));
//                txtCounty.setText(rs.getString(6));
//                txtPostcode.setText(rs.getString(7));
//                txtPhoneNumber.setText(rs.getString(8));
//                txtEmailAddress.setText(rs.getString(9));
//                cbCategory.setSelectedIndex(rs.getInt(10) - 1);
//            } else {
//                System.out.println("-------------------------------");
//                System.out.println("Error occurred fetching customer data");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        sqlManager.closeConnection(conn);                           // Closes connection to the DB
//
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtInvoiceID = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        txtEmployee = new javax.swing.JTextField();
        txtDateCreated = new javax.swing.JTextField();
        txtDateDeadline = new javax.swing.JTextField();
        txtSubtotal = new javax.swing.JTextField();
        txtPayments = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_InvoiceDetails = new javax.swing.JTable();
        lblInvoiceID = new javax.swing.JLabel();
        lblCustomer = new javax.swing.JLabel();
        lblEmployee = new javax.swing.JLabel();
        lblDateCreated = new javax.swing.JLabel();
        lblDateDeadline = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblPayments = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("View Customer");

        jTable_InvoiceDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Row", "Description", "Quantity", "Unit Price", "Item Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_InvoiceDetails);

        lblInvoiceID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceID.setText("Invoice ID:");

        lblCustomer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomer.setText("Customer:");

        lblEmployee.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmployee.setText("Employee:");

        lblDateCreated.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateCreated.setText("Date created:");

        lblDateDeadline.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateDeadline.setText("Deadline Date:");

        lblSubtotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSubtotal.setText("Sub total:");

        lblPayments.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblPayments.setText("Payments:");

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotal.setText("Total:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInvoiceID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtInvoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTotal)
                            .addComponent(lblPayments)
                            .addComponent(lblSubtotal))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addComponent(txtPayments)
                            .addComponent(txtSubtotal)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblDateCreated)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCustomer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblEmployee)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDateDeadline)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDateDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInvoiceID)
                    .addComponent(txtInvoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustomer)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmployee)
                    .addComponent(txtEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDateCreated)
                    .addComponent(lblDateDeadline)
                    .addComponent(txtDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDateDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubtotal)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPayments)
                    .addComponent(txtPayments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Counts how many of the input fields is empty and returns the integer value
    public int countEmptyFields(JTextField[] fields) {
        int emptyFields = 0;
        for (JTextField field : fields) {
            if (field.getText().equals("")) {
                emptyFields++;
            }
        }
        return emptyFields;
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
            java.util.logging.Logger.getLogger(formOneInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formOneInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formOneInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formOneInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formOneInvoice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_InvoiceDetails;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblDateDeadline;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblPayments;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDateCreated;
    private javax.swing.JTextField txtDateDeadline;
    private javax.swing.JTextField txtEmployee;
    private javax.swing.JTextField txtInvoiceID;
    private javax.swing.JTextField txtPayments;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
