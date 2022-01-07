/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Michal
 */
public class formOneCustomer extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(formOneCustomer.class.getName());
    formManageCustomers previousForm = null;
    Connection conn = null;

    // customer_id of the customer which is currently loaded into the form
    int CustomerID = 0;

    // Stores all the editable/uneditable JTextFields in the form so it's easy to access them throughout the code
    JTextField[] fields;

    public formOneCustomer() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Loads all the possible customer categories into ComboBox
        loadCustomerCategoriesIntoCB();

        // Confirm Edit button is made invisible since it only appears after editing has begun
        btnConfirmEdit.setVisible(false);

        // Initialises global array which stores all the textfields in the form
        fields = new JTextField[]{txtForename, txtSurname, txtAddress1, txtAddress2,
            txtAddress3, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};

        // Makes all the fields uneditable
        Utility.setEditable(fields, false);

        // When the user changes the selectedIndex in the ComboBox
        cbCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAddNewCategorySelected()) {
                    //Prompts the user to add a new category
                    conn = sqlManager.openConnection();
                    String addedCategory = sqlManager.addNewCustomerCategory(conn);
                    sqlManager.closeConnection(conn);

                    if (addedCategory != null) {
                        // If the user entered a name for the new category
                        // Refreshes ComboBox and selects the newly added category
                        loadCustomerCategoriesIntoCB();
                        cbCategory.setSelectedItem(addedCategory);
                    } else {
                        // If the user exited out of the new category dialog
                        // Selects the default category
                        cbCategory.setSelectedIndex(0);
                    }
                }
            }
        });
    }

    // Returns true if the 'Add new category' option in the ComboBox is selected
    private boolean isAddNewCategorySelected() {
        return cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1;
    }

    // Used when the form is opened from within another form
    public formOneCustomer getFrame() {
        return this;
    }

    // Loads all the customer categories from the DB into the ComboBox
    public void loadCustomerCategoriesIntoCB() {
        // Clears ComboBox
        cbCategory.removeAllItems();

        conn = sqlManager.openConnection();
        try {
            // Query Setup & Execution
            String query = "SELECT category_name FROM tblCustomerCategory";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Adds category to the ComboBox
                cbCategory.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        sqlManager.closeConnection(conn);
        cbCategory.addItem("Add a new category...");
    }

    // Loads the customer details into the form
    public void loadCustomer() {
        conn = sqlManager.openConnection();

        txtCustomerID.setText(String.valueOf(CustomerID));

        String query = "SELECT CONCAT(forename,' ', surname), forename, surname,"
                + " address1, address2, address3, county, postcode, phone_number,"
                + " email_address, cc.category_name"
                + " FROM tblCustomer as c"
                + " INNER JOIN tblCustomerCategory as cc"
                + " ON c.category_id = cc.category_id"
                + " WHERE customer_id = ?";

        try {
            // Query Setup & Execution
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, CustomerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                // Loads all the data from the DB into the JTextFields
                lblFullName.setText(rs.getString(1));
                for (int i = 0; i < 9; i++) {
                    fields[i].setText(rs.getString(i + 2));
                }
                cbCategory.setSelectedItem(rs.getString(11));

            } else {
                logger.log(Level.WARNING, "Error occured fetching customer data");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        sqlManager.closeConnection(conn);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFullName = new javax.swing.JLabel();
        lblCustomerID = new javax.swing.JLabel();
        lblForename = new javax.swing.JLabel();
        lblSurname = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        lblCounty = new javax.swing.JLabel();
        lblPostcode = new javax.swing.JLabel();
        lblPhoneNumber = new javax.swing.JLabel();
        lblEmailAddress = new javax.swing.JLabel();
        lblCustomerCategory = new javax.swing.JLabel();
        txtCustomerID = new javax.swing.JTextField();
        txtForename = new javax.swing.JTextField();
        txtSurname = new javax.swing.JTextField();
        txtAddress1 = new javax.swing.JTextField();
        txtAddress2 = new javax.swing.JTextField();
        txtAddress3 = new javax.swing.JTextField();
        txtCounty = new javax.swing.JTextField();
        txtPostcode = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        txtEmailAddress = new javax.swing.JTextField();
        cbCategory = new javax.swing.JComboBox<>();
        btnRemove = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnConfirmEdit = new javax.swing.JButton();
        btnSetQuotation = new javax.swing.JButton();
        btnSetInvoice = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("View Customer");

        lblFullName.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblFullName.setText("Full name here");

        lblCustomerID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomerID.setText("Customer ID:");

        lblForename.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblForename.setText("Forename:");

        lblSurname.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSurname.setText("Surname:");

        lblAddress.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblAddress.setText("Address:");

        lblCounty.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCounty.setText("County:");

        lblPostcode.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblPostcode.setText("Postcode:");
        lblPostcode.setToolTipText("");

        lblPhoneNumber.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblPhoneNumber.setText("Phone number:");

        lblEmailAddress.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmailAddress.setText("Email address:");

        lblCustomerCategory.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomerCategory.setText("Category:");

        txtCustomerID.setEditable(false);

        txtAddress1.setToolTipText("");

        btnRemove.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnEdit.setText("Edit details");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnConfirmEdit.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnConfirmEdit.setText("Confirm Edit");
        btnConfirmEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmEditActionPerformed(evt);
            }
        });

        btnSetQuotation.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnSetQuotation.setText("Set Quotation");
        btnSetQuotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetQuotationActionPerformed(evt);
            }
        });

        btnSetInvoice.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnSetInvoice.setText("Set Invoice");
        btnSetInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetInvoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(lblAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtAddress2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblPhoneNumber)
                            .addComponent(lblEmailAddress)
                            .addComponent(lblPostcode)
                            .addComponent(lblCounty)
                            .addComponent(lblCustomerCategory))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtPostcode, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtCounty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAddress3)))
                    .addComponent(lblFullName)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblCustomerID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCustomerID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblForename)
                            .addComponent(lblSurname))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtForename, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(txtSurname))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(63, 63, 63)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                                .addComponent(btnConfirmEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSetQuotation, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSetInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFullName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCustomerID)
                            .addComponent(txtCustomerID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblForename)
                            .addComponent(txtForename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSurname)
                            .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblAddress)
                            .addComponent(txtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAddress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtAddress3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCounty))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPostcode)
                            .addComponent(txtPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPhoneNumber)
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEmailAddress)
                            .addComponent(txtEmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCustomerCategory)
                            .addComponent(cbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSetInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSetQuotation, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // Makes all the fields editable, disables the edit button and makes the confirm button visible
        Utility.setEditable(fields, true);
        btnEdit.setEnabled(false);
        btnConfirmEdit.setVisible(true);

        // Moves the insertion pointer to the forename field for convenience
        txtForename.requestFocus();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        conn = sqlManager.openConnection();

        // Counts how many invoices and quotations the customer has
        int NoInvoices = sqlManager.countRecords(conn, "tblInvoice", "customer_id", CustomerID);
        int NoQuotations = sqlManager.countRecords(conn, "tblQuotation", "customer_id", CustomerID);

        if (NoInvoices > 0 || NoQuotations > 0) {
            // If the customer has any invoices or quotations associated with them then the user is informed
            String sInvoice = (NoInvoices > 0 ? NoInvoices + " invoice" : "") + (NoInvoices > 1 ? "s" : "");
            String conjunction = (NoInvoices > 0 && NoQuotations > 0 ? " and " : "");
            String sQuotation = (NoQuotations > 0 ? NoQuotations + " quotation" : "") + (NoQuotations > 1 ? "s" : "");

            String msg = "This customer has " + sInvoice + conjunction + sQuotation + " associated with them and therefore cannot be removed.";
            ErrorMsg.throwError(ErrorMsg.CANNOT_REMOVE_ERROR, msg);
        } else {
            // Asks user whether they really want to remove this customer
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this customer?",
                    "Remove Customer", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

            // If response is yes
            if (YesNo == 0) {
                // Removes customer and closes the form since the customer no longer exists
                sqlManager.removeRecord(conn, "tblCustomer", "customer_id", CustomerID);
                this.dispose();

                // Refreshes the customer table in the previous form since a customer was removed
                previousForm.loadCustomers();
            }
        }

        sqlManager.closeConnection(conn);
    }//GEN-LAST:event_btnRemoveActionPerformed

    // When the user has finished making edits to the customer's details
    private void btnConfirmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmEditActionPerformed
        JTextField[] requiredFields = {txtForename, txtSurname, txtAddress1, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};

        if (Utility.countEmptyFields(requiredFields) != 0) {
            // Checks if any of the required input fields are empty
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR);

        } else if (validInputs()) {

            // Asks user whether they really want to edit this customer's details
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to update this customer's details?",
                    "Update customer details", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

            // If response is yes
            if (YesNo == 0) {
                conn = sqlManager.openConnection();

                String query = "UPDATE tblCustomer SET forename = ?, surname = ?, address1 = ?,"
                        + " address2 = ?, address3 = ?, county = ?, postcode = ?,"
                        + " phone_number = ?, email_address = ?, category_id = ?"
                        + " WHERE customer_id = ?";

                try {
                    // Query Setup & Execution
                    PreparedStatement pstmt = conn.prepareStatement(query);

                    pstmt.setString(1, txtForename.getText());
                    pstmt.setString(2, txtSurname.getText());
                    pstmt.setString(3, txtAddress1.getText());
                    // If the address2 or address3 is empty then it is replaced by null instead of ""
                    pstmt.setString(4, (txtAddress2.getText().isEmpty() ? null : txtAddress2.getText()));
                    pstmt.setString(5, (txtAddress3.getText().isEmpty() ? null : txtAddress3.getText()));
                    pstmt.setString(6, txtCounty.getText());
                    pstmt.setString(7, txtPostcode.getText());
                    pstmt.setString(8, txtPhoneNumber.getText());
                    pstmt.setString(9, txtEmailAddress.getText());
                    pstmt.setInt(10, sqlManager.getIDofCategory(conn, "tblCustomerCategory", cbCategory.getSelectedItem().toString()));
                    pstmt.setInt(11, CustomerID);

                    int rowsAffected = pstmt.executeUpdate();
                    logger.log(Level.INFO, rowsAffected + " row(s) updated.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQLException");
                }
                sqlManager.closeConnection(conn);

                // Resets JTextFields and buttons to their default (non-editing) state
                Utility.setEditable(fields, false);
                btnConfirmEdit.setVisible(false);
                btnEdit.setEnabled(true);

                // Refreshes the customer table in the previous form since a customer's details were changed
                previousForm.loadCustomers();
            }
        }
        txtCustomerID.requestFocus();
    }//GEN-LAST:event_btnConfirmEditActionPerformed

    // Validates input lengths against the max lengths allowed in the DBMS
    private boolean validInputs() {
        conn = sqlManager.openConnection();
        boolean output = false;

        if (txtForename.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "forename")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "forename");

        } else if (txtSurname.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "surname")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "surname");

        } else if (txtAddress1.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "address1")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "address line 1");

        } else if (txtAddress2.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "address2")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "address line 2");

        } else if (txtAddress3.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "address3")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "address line 3");

        } else if (txtCounty.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "county")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "county");

        } else if (txtPostcode.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "postcode")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "postcode");

        } else if (txtPhoneNumber.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "phone_number")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "phone number");

        } else if (txtEmailAddress.getText().length() > sqlManager.getMaxColumnLength(conn, "tblCustomer", "email_address")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "email address");

        } else {
            // If all inputs passed the validity checks then boolean set to true
            output = true;
        }

        sqlManager.closeConnection(conn);
        return output;
    }

    private void btnSetInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetInvoiceActionPerformed
        formNewInvoice form = new formNewInvoice().getFrame();
        // Makes this form the previousForm so the back buttons work
        form.previousForm3 = this;
        this.setVisible(false);
        form.setVisible(true);

        // For convenience, the customer the user was looking at is automatically selected
        form.selectCustomer(CustomerID);

        previousForm.setVisible(false);
    }//GEN-LAST:event_btnSetInvoiceActionPerformed

    private void btnSetQuotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetQuotationActionPerformed
        formNewQuotation form = new formNewQuotation().getFrame();
        // Makes this form the previousForm so the back buttons work
        form.previousForm3 = this;
        this.setVisible(false);
        form.setVisible(true);

        // For convenience, the customer the user was looking at is automatically selected
        form.selectCustomer(CustomerID);

        previousForm.setVisible(false);
    }//GEN-LAST:event_btnSetQuotationActionPerformed

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
            java.util.logging.Logger.getLogger(formOneCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formOneCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formOneCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formOneCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formOneCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmEdit;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSetInvoice;
    private javax.swing.JButton btnSetQuotation;
    private javax.swing.JComboBox<String> cbCategory;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblCounty;
    private javax.swing.JLabel lblCustomerCategory;
    private javax.swing.JLabel lblCustomerID;
    private javax.swing.JLabel lblEmailAddress;
    private javax.swing.JLabel lblForename;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblPhoneNumber;
    private javax.swing.JLabel lblPostcode;
    private javax.swing.JLabel lblSurname;
    private javax.swing.JTextField txtAddress1;
    private javax.swing.JTextField txtAddress2;
    private javax.swing.JTextField txtAddress3;
    private javax.swing.JTextField txtCounty;
    private javax.swing.JTextField txtCustomerID;
    private javax.swing.JTextField txtEmailAddress;
    private javax.swing.JTextField txtForename;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtPostcode;
    private javax.swing.JTextField txtSurname;
    // End of variables declaration//GEN-END:variables
}
