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
public class formOneQuotation extends javax.swing.JFrame {

    /**
     * Creates new form formOneQuotation
     */
    int QuotationID = 0;                                            // quotation_id of currently loaded quotation
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model;                                        // The table model
    formManageQuotations previousForm = null;                       // Stores the previous Form object

    public formOneQuotation() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = (DefaultTableModel) jTable_QuotationDetails.getModel();    // Fetches the table model of the table
        jTable_QuotationDetails.setDefaultEditor(Object.class, null);      // Makes it so the user cannot edit the table

        JTableHeader header = jTable_QuotationDetails.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        JTextField[] fields = {txtQuotationID, txtCustomer, txtEmployee, txtDateCreated, txtTotal};
        setEditable(fields, false);

        jTable_QuotationDetails = Utility.setColumnWidths(jTable_QuotationDetails, new int[]{300, 100, 60, 90, 90});
    }

    // Sets these components to either visible or invisible depending on the boolean state
    public void setEditable(JTextField[] fields, boolean state) {
        for (JTextField field : fields) {
            field.setEditable(state);
        }
    }

    public void loadQuotation() {
        conn = sqlManager.openConnection();
        String query = "SELECT CONCAT(tblCustomers.forename,' ',tblCustomers.surname) AS customerFullName,"
                + " CONCAT(tblEmployees.forename,' ',tblEmployees.surname) AS employeeFullName, date_created FROM tblQuotations"
                + " INNER JOIN tblCustomers ON tblQuotations.customer_id=tblCustomers.customer_id"
                + " INNER JOIN tblEmployees ON tblQuotations.employee_id=tblEmployees.employee_id"
                + " WHERE quotation_id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, QuotationID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtQuotationID.setText(String.valueOf(QuotationID));
                txtCustomer.setText(rs.getString(1));
                txtEmployee.setText(rs.getString(2));
                txtDateCreated.setText(String.valueOf(rs.getDate(3)));

                double Total = loadQuotationDetails(QuotationID);

                txtTotal.setText(Utility.formatCurrency(Total));

            } else {
                System.out.println("-------------------------------");
                System.out.println("No quotation with this quotation_id was found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);

    }

    public double loadQuotationDetails(int quotationID) {
        double QuotationTotal = 0;
        conn = sqlManager.openConnection();
        String query = "SELECT description, item_category_id, quantity, unit_price FROM tblQuotationDetails WHERE quotation_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quotationID);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double itemTotal = rs.getInt(3) * rs.getDouble(4);
                QuotationTotal += itemTotal;

                String itemCategory = sqlManager.getCategory(conn, "tblItemCategories", "item_category_id", rs.getInt(2));
                String sItemTotal = Utility.formatCurrency(itemTotal);
                String sUnitPrice = Utility.formatCurrency(rs.getDouble(4));
                model.addRow(new Object[]{rs.getString(1), itemCategory, rs.getInt(3), sUnitPrice, sItemTotal}); // Adds the quotation to the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);
        return QuotationTotal;
    }

    public formOneQuotation getFrame() {
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
        lblTotal = new javax.swing.JLabel();
        txtQuotationID = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        txtEmployee = new javax.swing.JTextField();
        txtDateCreated = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_QuotationDetails = new javax.swing.JTable();
        txtTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("View Quotation");

        lblInvoiceID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceID.setText("Quotation ID:");

        lblCustomer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomer.setText("Customer:");

        lblEmployee.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmployee.setText("Employee:");

        lblDateCreated.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateCreated.setText("Date created:");

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotal.setText("Total:");

        jTable_QuotationDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Category", "Quantity", "Unit Price", "Item Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_QuotationDetails);

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
                        .addComponent(txtQuotationID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 691, Short.MAX_VALUE)
                        .addComponent(lblTotal)
                        .addGap(12, 12, 12)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtQuotationID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(18, 18, 18)
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
            java.util.logging.Logger.getLogger(formOneQuotation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formOneQuotation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formOneQuotation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formOneQuotation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formOneQuotation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_QuotationDetails;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDateCreated;
    private javax.swing.JTextField txtEmployee;
    private javax.swing.JTextField txtQuotationID;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
