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
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Michal
 */
public class formOneInvoice extends javax.swing.JFrame {

    /**
     * Creates new form formOneinvoice
     */
    int InvoiceID = 0;                                              // invoice_id of currently loaded invoice
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model;                                        // The table model
    formManageInvoices previousForm = null;                         // Stores the previous Form object

    public formOneInvoice() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = (DefaultTableModel) jTable_InvoiceDetails.getModel();           // Fetches the table model of the table
        jTable_InvoiceDetails.setDefaultEditor(Object.class, null);             // Makes it so the user cannot edit the table

        JTableHeader header = jTable_InvoiceDetails.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        JTextField[] fields = {txtInvoiceID, txtCustomer, txtEmployee, txtDateCreated, txtSubtotal, txtPayments, txtTotal};
        setEditable(fields, false);

        jTable_InvoiceDetails = Utility.setColumnWidths(jTable_InvoiceDetails, new int[]{300, 100, 60, 90, 90});

    }

    // Sets these components to either visible or invisible depending on the boolean state
    public void setEditable(JTextField[] fields, boolean state) {
        for (JTextField field : fields) {
            field.setEditable(state);
        }
    }

    public void loadInvoice() {
        conn = sqlManager.openConnection();
        String query = "SELECT CONCAT(tblCustomers.forename,' ',tblCustomers.surname) AS customerFullName,"
                + " CONCAT(tblEmployees.forename,' ',tblEmployees.surname) AS employeeFullName, date_created, payments FROM tblInvoices"
                + " INNER JOIN tblCustomers ON tblInvoices.customer_id=tblCustomers.customer_id"
                + " INNER JOIN tblEmployees ON tblInvoices.employee_id=tblEmployees.employee_id"
                + " WHERE invoice_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, InvoiceID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtInvoiceID.setText(String.valueOf(InvoiceID));
                txtCustomer.setText(rs.getString(1));
                txtEmployee.setText(rs.getString(2));
                txtDateCreated.setText(rs.getString(3));
                txtPayments.setText(Utility.formatCurrency(rs.getDouble(4)));

                double subTotal = loadInvoiceDetails(InvoiceID);

                txtSubtotal.setText(Utility.formatCurrency(subTotal));
                txtTotal.setText(Utility.formatCurrency(subTotal - rs.getDouble(4)));

            } else {
                System.out.println("-------------------------------");
                System.out.println("No invoice with this invoice_id was found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);
    }

    public double loadInvoiceDetails(int invoiceID) {
        double InvoiceTotal = 0;
        conn = sqlManager.openConnection();
        String query = "SELECT description, category_id, quantity, unit_price FROM tblInvoiceDetails WHERE invoice_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, InvoiceID);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double itemTotal = rs.getInt(3) * rs.getDouble(4);
                InvoiceTotal += itemTotal;

                String itemCategory = sqlManager.getCategory(conn, "tblItemCategories", "category_id", rs.getInt(2));
                String sItemTotal = Utility.formatCurrency(itemTotal);
                String sUnitPrice = Utility.formatCurrency(rs.getDouble(4));
                model.addRow(new Object[]{rs.getString(1), itemCategory, rs.getInt(3), sUnitPrice, sItemTotal}); // Adds the invoice to the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);
        return InvoiceTotal;
    }

    public formOneInvoice getFrame() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInvoiceID = new javax.swing.JLabel();
        lblCustomer = new javax.swing.JLabel();
        lblEmployee = new javax.swing.JLabel();
        lblDateCreated = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblPayments = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        txtInvoiceID = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        txtEmployee = new javax.swing.JTextField();
        txtDateCreated = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_InvoiceDetails = new javax.swing.JTable();
        txtSubtotal = new javax.swing.JTextField();
        txtPayments = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        btnFormat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("View Invoice");

        lblInvoiceID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceID.setText("Invoice ID:");

        lblCustomer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomer.setText("Customer:");

        lblEmployee.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmployee.setText("Employee:");

        lblDateCreated.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateCreated.setText("Date created:");

        lblSubtotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSubtotal.setText("Sub total:");

        lblPayments.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblPayments.setText("Payments:");

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotal.setText("Total:");

        jTable_InvoiceDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Category", "Quantity", "Unit Price", "Item Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_InvoiceDetails);

        btnFormat.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnFormat.setText("Format into Word Document");
        btnFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFormatActionPerformed(evt);
            }
        });

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
                        .addComponent(btnFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblSubtotal)
                            .addComponent(lblPayments)
                            .addComponent(lblTotal))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                        .addComponent(lblEmployee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(txtDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
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
                        .addContainerGap(12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // This button is for converting the invoice into a word document given a template
    private void btnFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFormatActionPerformed
        formFormatIntoWord form = new formFormatIntoWord().getFrame();          // Opens the Word Document generation form
        form.documentID = InvoiceID;                                            // Tells the invoice formatting form the invoiceID in question
        form.DOCUMENT_TYPE = form.INVOICE;
        form.setVisible(true);                                                  
    }//GEN-LAST:event_btnFormatActionPerformed

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
    private javax.swing.JButton btnFormat;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_InvoiceDetails;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblPayments;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDateCreated;
    private javax.swing.JTextField txtEmployee;
    private javax.swing.JTextField txtInvoiceID;
    private javax.swing.JTextField txtPayments;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
