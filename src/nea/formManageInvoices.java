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
public class formManageInvoices extends javax.swing.JFrame {

    /**
     * Creates new form formManageInvoices
     */
    int EmployeeID = 1;
    formMainMenu previousForm = null;                               // Stores the previously open form
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model;                                        // The table model
    formOneInvoice Invoice_in_view = null;                          // could be null or could store whichever invoice the user is currently viewing
    public static String sp = "";                                   // SearchParameter, this stores whatever is currently in the Search box

    public formManageInvoices() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        model = (DefaultTableModel) jTable_Invoices.getModel();    // Fetches the table model of the table
        jTable_Invoices.setDefaultEditor(Object.class, null);      // Makes it so the user cannot edit the table

        JTableHeader header = jTable_Invoices.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        jTable_Invoices.addMouseListener(new MouseListener() {     // Mouse listener for when the user clicks on a row in the invoice table
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
                int selectedID = getSelectedInvoice();              // Gets the id of the invoice which is currently selected in the table
                if (selectedID != -1) {                             // id of the invoice is not '-1', this is the default return value from getSelectedInvoice()
                    if (Invoice_in_view != null) {
                        Invoice_in_view.dispose();
                    }
                    formOneInvoice form = new formOneInvoice().getFrame();    // Opens a new instance of the formOneInvoice() form
                    form.setLocation(1630, 422);                    // Sets the location of the invoice view to the right of the current invoice management form
                    form.setVisible(true);                          // Makes the new invoice view visible
                    form.InvoiceID = selectedID;                   // Tells the invoice view form which invoice to load
                    form.previousForm = formManageInvoices.this;   // Informs the invoice view what the previous form is 
                    form.loadInvoice();                            // Runs the loadInvoice() method which will load all of the specified invoice's details
                    Invoice_in_view = form;                        // Sets the invoice in view to this
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {    // Document Listener for when the user wants to search for something new
            @Override
            public void insertUpdate(DocumentEvent e) {             // When an insert occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadInvoices();                                     // Refreshes the invoice table as the search term has changed
            }

            @Override
            public void removeUpdate(DocumentEvent e) {             // When a remove occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadInvoices();                                     // Refreshes the invoice table as the search term has changed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });

        jTable_Invoices = Utility.setColumnWidths(jTable_Invoices, new int[]{40, 120, 120, 120, 80});
    }

    public formManageInvoices getFrame() {
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

        lblManageInvoices = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        lblInvoiceCount = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Invoices = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Invoice Management");

        lblManageInvoices.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblManageInvoices.setText("Manage Invoices");

        lblSearch.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSearch.setText("Search");

        lblInvoiceCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInvoiceCount.setText("Number of invoices:");

        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setName(""); // NOI18N

        btnAddNew.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        jTable_Invoices.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable_Invoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Customer", "Employee", "Date created", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_Invoices);

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
                                .addComponent(lblManageInvoices)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblInvoiceCount, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10))))
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
                    .addComponent(lblManageInvoices))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInvoiceCount))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void loadInvoices() {    // Raw SQL query: https://pastebin.com/xQ9xpUYT
        model.setRowCount(0);                                       // Empties the table
        conn = sqlManager.openConnection();

        String queryInvoiceTotals = "SELECT i.invoice_id, "
                + " COALESCE(SUM(iD.quantity * iD.unit_price), 0) AS invoiceTotal"
                + " FROM tblInvoices AS i"
                + " INNER JOIN tblInvoiceDetails AS iD ON i.invoice_id = iD.invoice_id"
                + " GROUP BY i.invoice_id";

        String mainQuery = "SELECT i.invoice_id AS id,"
                + " CONCAT(c.forename, ' ', c.surname) AS customerFullName,"
                + " CONCAT(e.forename, ' ', e.surname) AS employeeFullName,"
                + " i.date_created,"
                + " iT.invoiceTotal"
                + " FROM tblInvoices AS i"
                + " INNER JOIN tblCustomers AS c ON i.customer_id = c.customer_id"
                + " INNER JOIN tblEmployees AS e ON i.employee_id = e.employee_id"
                + " LEFT JOIN (" + queryInvoiceTotals + ") AS iT ON i.invoice_id = iT.invoice_id";

        if (!sp.equals("")) {                                       // When searchParameter is something
            mainQuery += " WHERE";
            mainQuery += " i.invoice_id LIKE '%" + sp + "%'";                           // \
            mainQuery += " OR CONCAT(c.forename, ' ', c.surname) LIKE '%" + sp + "%'";  //  |
            mainQuery += " OR CONCAT(e.forename, ' ', e.surname) LIKE '%" + sp + "%'";  //  |-- Check whether a column value contains the searchParameter
            mainQuery += " OR i.date_created LIKE '%" + sp + "%'";                      //  |
            mainQuery += " OR iT.invoiceTotal LIKE '%" + sp + "%'";                     // /
        }

        mainQuery += " GROUP BY i.invoice_id"
                + " ORDER BY i.invoice_id";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(mainQuery);
            
            int invoiceCounter = 0;                                 // variable for counting how many invoices are being shown in the table
            while (rs.next()) {                                     // If there is another result from the DBMS
                String[] invoiceData = new String[5];
                invoiceData[0] = String.valueOf(rs.getInt(1));              // Invoice ID
                invoiceData[1] = rs.getString(2);                           // Customer name
                invoiceData[2] = rs.getString(3);                           // Employee name
                invoiceData[3] = rs.getString(4);                           // Creation date
                invoiceData[4] = Utility.formatCurrency(rs.getDouble(5));   // Invoice total
                
                model.addRow(new Object[]{invoiceData[0], invoiceData[1], invoiceData[2], invoiceData[3], invoiceData[4]}); // Adds the invoice to the table
                
                invoiceCounter++;                                   // Increments invoice counter as a new invoice was added to the table
            }
            lblInvoiceCount.setText("Number of invoices: " + String.valueOf(invoiceCounter)); // Updates invoice counter label
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);
    }

    // Returns the invoice_id of the selected invoice in the invoice table
    public int getSelectedInvoice() {
        int selectedRow = jTable_Invoices.getSelectedRow();         // Gets the selected row in the table
        
        if (selectedRow == -1) {                                    // If no row is selected in the table
            System.out.println("No row selected");
        } else {
            String string_id = model.getValueAt(selectedRow, 0).toString(); // Gets the id of the selected in string form
            int id = Utility.StringToInt(string_id);                // Converts the id from string type to integer type
            return id;
        }
        return -1;                                                  //  Returns -1 if there were to be an error somewhere
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        if (Invoice_in_view != null) {                             // Checks whether there is another form opened showing the selected invoice
            Invoice_in_view.dispose();                             // If there is another form then it gets rid of it
        }
        previousForm.setVisible(true);                              // Makes main previous form visible
        this.dispose();                                             // Closes the document management form (current form)
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
        formNewInvoice form = new formNewInvoice().getFrame();
        form.previousForm2 = this;                                  // Makes this form the previousForm so the back buttons work
        form.EmployeeID = EmployeeID;
        this.setVisible(false);                                     // Makes main menu invisible
        form.setVisible(true);                                      // makes the next form visible

    }//GEN-LAST:event_btnAddNewActionPerformed

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
            java.util.logging.Logger.getLogger(formManageInvoices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formManageInvoices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formManageInvoices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formManageInvoices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formManageInvoices().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnBack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Invoices;
    private javax.swing.JLabel lblInvoiceCount;
    private javax.swing.JLabel lblManageInvoices;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
