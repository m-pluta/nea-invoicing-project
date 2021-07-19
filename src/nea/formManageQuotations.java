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
public class formManageQuotations extends javax.swing.JFrame {

    /**
     * Creates new form formManageQuotations
     */
    formMainMenu previousForm = null;                               // Stores the previously open form
    Connection conn = null;                                         // Stores the connection object
    DefaultTableModel model;                                        // The table model
    formOneQuotation Quotation_in_view = null;                      // could be null or could store whichever quotation the user is currently viewing
    public static String sp = "";                                   // SearchParameter, this stores whatever is currently in the Search box

    public formManageQuotations() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        model = (DefaultTableModel) jTable_Quotations.getModel();   // Fetches the table model of the table
        jTable_Quotations.setDefaultEditor(Object.class, null);     // Makes it so the user cannot edit the table

        JTableHeader header = jTable_Quotations.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        jTable_Quotations.addMouseListener(new MouseListener() {    // Mouse listener for when the user clicks on a row in the quotation table
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
                int selectedID = getSelectedQuotation();            // Gets the id of the quotation which is currently selected in the table
                if (selectedID != -1) {                             // id of the quotation is not '-1', this is the default return value from getSelectedQuotation()
                    if (Quotation_in_view != null) {
                        Quotation_in_view.dispose();
                    }
                    formOneQuotation form = new formOneQuotation().getFrame();    // Opens a new instance of the formOneQuotation() form
                    form.setLocation(1630, 422);                    // Sets the location of the quotation view to the right of the current quotation management form
                    form.setVisible(true);                          // Makes the new quotation view visible
                    form.QuotationID = selectedID;                  // Tells the quotation view form which quotation to load
                    form.previousForm = formManageQuotations.this;  // Informs the quotation view what the previous form is 
                    form.loadQuotation();                           // Runs the loadQuotation() method which will load all of the specified quotation's details
                    Quotation_in_view = form;                       // Sets the quotation in view to this

                } else {
                    System.out.println("Something is truly wrong"); // Not sure how you would reach this point
                }

            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {    // Document Listener for when the user wants to search for something new
            @Override
            public void insertUpdate(DocumentEvent e) {             // When an insert occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadQuotations();                                   // Refreshes the quotation table as the search term has changed
            }

            @Override
            public void removeUpdate(DocumentEvent e) {             // When a remove occured in the search bar
                sp = txtSearch.getText();                           // sets the sp (searchParameter) to whatever value the text field holds
                loadQuotations();                                   // Refreshes the quotation table as the search term has changed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });

    }

    public formManageQuotations getFrame() {
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

        lblManageQuotations = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        lblQuotationCount = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Quotations = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quotation Management");

        lblManageQuotations.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblManageQuotations.setText("Manage Quotations");
        lblManageQuotations.setToolTipText("");

        lblSearch.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSearch.setText("Search");

        lblQuotationCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblQuotationCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblQuotationCount.setText("Number of quotations:");

        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setName(""); // NOI18N

        btnAddNew.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        jTable_Quotations.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable_Quotations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Customer", "Employee", "Date created", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_Quotations);

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
                                .addComponent(lblManageQuotations)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblQuotationCount, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(lblManageQuotations))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuotationCount))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(btnAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public boolean doesQuotationContainSearch(String[] data, String sp) {
        if (sp.equals("")) {
            return true;
        } else {
            int matchCounter = 0;
            for (String singleton : data) {
                if (singleton.toLowerCase().contains(sp.toLowerCase())) {
                    matchCounter++;
                }
            }
            if (matchCounter > 0) {
                return true;
            }
        }
        return false;
    }

    public void loadQuotations() {
        model.setRowCount(0);                                       // Empties the table
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT quotation_id, customer_id, employee_id, date_created FROM tblQuotations";

        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            int quotationCounter = 0;                               // variable for counting how many quotations are being shown in the table
            while (rs.next()) {                                     // If there is another result from the DBMS
                String[] quotationData = new String[5];
                quotationData[0] = String.valueOf(rs.getInt(1));    // Quotation ID
                quotationData[1] = sqlManager.getCustomerFullName(conn, rs.getInt(2)); // Customer name
                quotationData[2] = sqlManager.getEmployeeFullName(conn, rs.getInt(3)); // Employee name
                quotationData[3] = String.valueOf(rs.getDate(4)).replace("-", " - ");                     // Creation date
                quotationData[4] = String.valueOf(sqlManager.totalDocument(conn, "tblQuotationDetails", "quotation_id", rs.getInt(1))); // quotation total
                if (doesQuotationContainSearch(quotationData, sp)) {

                    model.addRow(new Object[]{quotationData[0], quotationData[1], quotationData[2], quotationData[3], quotationData[4]}); // Adds the quotation to the table
                    quotationCounter++;                             // Increments quotation counter as a new quotation was added to the table
                }
            }
            lblQuotationCount.setText("Number of quotations: " + String.valueOf(quotationCounter)); // Updates quotation counter label
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
    }

    // Returns the quotation_id of the selected quotation in the quotation table
    public int getSelectedQuotation() {
        int selectedRow = jTable_Quotations.getSelectedRow();       // Gets the selected row in the table
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

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        if (Quotation_in_view != null) {                            // Checks whether there is another form opened showing the selected quotation
            Quotation_in_view.dispose();                            // If there is another form then it gets rid of it
        }
        previousForm.setVisible(true);                              // Makes main previous form visible
        this.dispose();                                             // Closes the document management form (current form)
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
//        if (!CurrentlyAddingCustomer) {
//            formAddCustomer form = new formAddCustomer().getFrame();    // Opens a new instance of the formAddCustomer() form
//            form.setLocationRelativeTo(null);               // Sets the location of the customer view to the right of the current customer management form
//            form.setVisible(true);                          // Makes the new customer view visible
//            form.previousForm = this;
//            CurrentlyAddingCustomer = true;
//        }
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
            java.util.logging.Logger.getLogger(formManageQuotations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formManageQuotations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formManageQuotations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formManageQuotations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formManageQuotations().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnBack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Quotations;
    private javax.swing.JLabel lblManageQuotations;
    private javax.swing.JLabel lblQuotationCount;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
