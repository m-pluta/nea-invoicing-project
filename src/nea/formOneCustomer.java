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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Michal
 */
public class formOneCustomer extends javax.swing.JFrame {

    /**
     * Creates new form formOneCustomer
     */
    int EmployeeID = 1;
    int CustomerID = 0;                                             // customer_id of currently loaded customer
    Connection conn = null;                                         // Stores the connection object
    formManageCustomers previousForm = null;                        // Stores the previous Form object

    public formOneCustomer() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadCustomerCategoriesIntoCB();                             // Loads all the possible customer categories into combo box

        btnConfirmEdit.setVisible(false);                           // Makes the Confirm Changes button invisible
        JTextField[] fields = {txtCustomerID, txtForename, txtSurname, txtAddress1, txtAddress2, txtAddress3, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};
        setEditable(fields, false);                                         // Makes all the fields uneditable

        cbCategory.addActionListener(new ActionListener() {         // When an action happens within the combo box - e.g. the selectedIndex changed
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAddNewCategorySelected()) {   // If the user selected the last item ('Add a new category...')
                    addNewCategory();                               // Prompts the user to add the new category
                }
            }
        });

    }

    // Returns true if the 'Add new category' option in the combo box is selected
    private boolean isAddNewCategorySelected() {
        return cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1;
    }

    // Allows the user to add a new customer category - This is almost entirely the same code as in fromManageCustomerCategories with minor changes
    public void addNewCategory() {
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
                    loadCustomerCategoriesIntoCB();                 // Refreshes Combo box so the new category is visible
                    cbCategory.setSelectedIndex(newID - 1);         // Set the selected index to whatever category the user just added
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            sqlManager.closeConnection(conn);                       // Closes connection to DB
        }
    }

    public formOneCustomer getFrame() {
        return this;
    }

    public void loadCustomerCategoriesIntoCB() {
        cbCategory.removeAllItems();
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT category_name FROM tblCustomerCategories";
        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("-------------------------------");
            while (rs.next()) {
                System.out.println(rs.getString(1));                // For debugging
                cbCategory.addItem(rs.getString(1));                // Ads the category to the combo box
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
        cbCategory.addItem("Add a new category...");                // Set one of the option to a custom category
    }

    // Sets these components to either visible or invisible depending on the boolean state
    public void setEditable(JTextField[] fields, boolean state) {
        for (JTextField field : fields) {
            field.setEditable(state);
        }
    }

    // Loads the customer data into the form
    public void loadCustomer() {
        txtCustomerID.setText(String.valueOf(CustomerID));

        conn = sqlManager.openConnection();

        String query = "SELECT CONCAT(forename,' ', surname), forename, surname, address1, address2, address3, county, postcode, phone_number, email_address, cc.category_name FROM tblCustomers as c"
                + " INNER JOIN tblcustomercategories as cc"
                + " ON c.type_id = cc.customer_category_id"
                + " WHERE customer_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, CustomerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("-------------------------------");
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));    // For debugging, shows customer data
                System.out.println(rs.getString(3));
                System.out.println(rs.getString(4));
                System.out.println(rs.getString(5));
                System.out.println(rs.getString(6));
                System.out.println(rs.getString(7));
                System.out.println(rs.getString(8));
                System.out.println(rs.getString(9));
                System.out.println(rs.getString(10));
                System.out.println(rs.getString(11));

                lblFullName.setText(rs.getString(1));
                txtForename.setText(rs.getString(2));
                txtSurname.setText(rs.getString(3));
                txtAddress1.setText(rs.getString(4));
                txtAddress2.setText(rs.getString(5));
                txtAddress3.setText(rs.getString(6));
                txtCounty.setText(rs.getString(7));
                txtPostcode.setText(rs.getString(8));
                txtPhoneNumber.setText(rs.getString(9));
                txtEmailAddress.setText(rs.getString(10));
                cbCategory.setSelectedItem(rs.getString(11));
            } else {
                System.out.println("-------------------------------");
                System.out.println("Error occurred fetching customer data");
            }
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
                .addContainerGap(9, Short.MAX_VALUE)
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
                        .addComponent(btnConfirmEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
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
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        JTextField[] fields = {txtForename, txtSurname, txtAddress1, txtAddress2, txtAddress3, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};
        setEditable(fields, true);                                  // Makes all the fields editable
        btnConfirmEdit.setVisible(true);                            // Makes the confirm button visible
        txtForename.requestFocus();

    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        conn = sqlManager.openConnection();                         // Opens the connection to the DB
        int NoInvoices = sqlManager.countRecords(conn, "tblInvoices", "customer_id", CustomerID);        // Counts how many invoices the customer has
        int NoQuotations = sqlManager.countRecords(conn, "tblQuotations", "customer_id", CustomerID);    // Counts how many quotations the customer has    
        System.out.println("-------------------------------");
        System.out.println("Customer ID: " + CustomerID);
        System.out.println("No. of invoices: " + NoInvoices);
        System.out.println("No. of quotations: " + NoQuotations);
        if (NoInvoices == 0 && NoQuotations == 0) {                 // If the customer has no invoices or quotations stored under their name
            // Asks user whether they really want to remove this customer
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this customer?", "Remove Customer", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {                                       // If response is yes
                sqlManager.removeRecord(conn, "tblCustomers", "customer_id", CustomerID);   // Removes the customer
                this.dispose();                                     // Closes this form since the customer no longer exists
                previousForm.loadCustomers();                       // Refreshes the customer table in the previous form since a customer was removed

            }
        } else {                                                    // If the customer had any invoices or quotations associated with them then the user is informed
            JOptionPane.showMessageDialog(null, "This customer has " + NoInvoices + " invoices and " + NoQuotations + " quotations associated with them and therefore cannot be removed.", "Not possible to remove customer", JOptionPane.WARNING_MESSAGE);
        }
        sqlManager.closeConnection(conn);                           // Closes the connection to the DB
    }//GEN-LAST:event_btnRemoveActionPerformed

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


    private void btnConfirmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmEditActionPerformed
        JTextField[] inputFields = {txtForename, txtSurname, txtAddress1, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};
        // Checks if any of the input fields are empty
        if (countEmptyFields(inputFields) != 0 || isAddNewCategorySelected()) {                   // If any one of the fields is empty
            System.out.println("-------------------------------");
            System.out.println("One of the required input fields is empty");
        } else {                                                    // If none of the fields are empty
            // Asks user whether they really want to edit this customer's details
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to update this customer's details?", "Update customer details", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {                                       // If response is yes
                conn = sqlManager.openConnection();                 // Opens connection to the DB
                String query = "UPDATE tblCustomers SET forename = ?, surname = ?, address1 = ?, address2 = ?, address3 = ?, county = ?, postcode = ?, phone_number = ?, email_address = ?, type_id = ? WHERE customer_id = ?";
                PreparedStatement pstmt = null;
                try {
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, txtForename.getText());
                    pstmt.setString(2, txtSurname.getText());
                    pstmt.setString(3, txtAddress1.getText());
                    pstmt.setString(4, (txtAddress2.getText().equals("") ? null : txtAddress2.getText()));  // If the address2 or address3 is empty then it is replaced by null instead of ""
                    pstmt.setString(5, (txtAddress3.getText().equals("") ? null : txtAddress3.getText()));
                    pstmt.setString(6, txtCounty.getText());
                    pstmt.setString(7, txtPostcode.getText());
                    pstmt.setString(8, txtPhoneNumber.getText());
                    pstmt.setString(9, txtEmailAddress.getText());
                    pstmt.setInt(10, cbCategory.getSelectedIndex() + 1);  // Gets the index of the selected customer category
                    pstmt.setInt(11, CustomerID);

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println(rowsAffected + " row updated.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sqlManager.closeConnection(conn);                   // Closes connection to the DB
                setEditable(inputFields, false);                    // Makes all the fields no longer editable
                txtAddress2.setEditable(false);                     // Makes txtAddress2 non editable as the previous line doesnt take care of that
                txtAddress3.setEditable(false);                     // Makes txtAddress3 non editable as the previous line doesnt take care of that
                btnConfirmEdit.setVisible(false);                   // Hides the Confirm details button
                previousForm.loadCustomers();                       // Refreshes the customer table in the previous form since a customer details were changed
            }
        }
        txtCustomerID.requestFocus();
    }//GEN-LAST:event_btnConfirmEditActionPerformed

    private void btnSetInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetInvoiceActionPerformed
        formNewInvoice form = new formNewInvoice().getFrame();
        form.previousForm3 = this;                                  // Makes this form the previousForm so the back buttons work
        form.EmployeeID = EmployeeID;
        form.selectCustomer(CustomerID);
        this.setVisible(false);                                     // Makes main menu invisible
        form.setVisible(true);                                      // makes the next form visible

        previousForm.setVisible(false);
    }//GEN-LAST:event_btnSetInvoiceActionPerformed

    private void btnSetQuotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetQuotationActionPerformed
        formNewQuotation form = new formNewQuotation().getFrame();
        form.previousForm3 = this;                                  // Makes this form the previousForm so the back buttons work
        form.EmployeeID = EmployeeID;
        form.selectCustomer(CustomerID);
        this.setVisible(false);                                     // Makes main menu invisible
        form.setVisible(true);                                      // makes the next form visible

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
