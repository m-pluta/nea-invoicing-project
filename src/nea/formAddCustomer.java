/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
public class formAddCustomer extends javax.swing.JFrame {

    /**
     * Creates new form formAddCustomer
     */
    int CustomerID = 0;                                             // customer_id of new customer being added
    Connection conn = null;                                         // Stores the connection object
    formManageCustomers previousForm = null;                        // Stores the previous Form object

    public formAddCustomer() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadCustomerCategoriesIntoCB();                             // Loads all the possible customer categories into combo box

        conn = sqlManager.openConnection();
        CustomerID = sqlManager.getNextPKValue(conn, "tblCustomers", "customer_id");   // Tells the customer view form which customer to load
        sqlManager.closeConnection(conn);
        txtCustomerID.setText(String.valueOf(CustomerID));
        txtCustomerID.setEditable(false);

        cbCategory.addActionListener(new ActionListener() {         // When an action happens within the combo box - e.g. the selectedIndex changed
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1) {   // If the user selected the last item ('Add a new category...')
                    addNewCategory();                               // Prompts the user to add the new category
                }
            }
        });

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            
            @Override
            public void windowClosing(WindowEvent e) {
                previousForm.CurrentlyAddingCustomer = false;
            }
            
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
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

    public formAddCustomer getFrame() {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        txtCounty = new javax.swing.JTextField();
        txtPostcode = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        txtEmailAddress = new javax.swing.JTextField();
        lblFullName = new javax.swing.JLabel();
        cbCategory = new javax.swing.JComboBox<>();
        txtAddress1 = new javax.swing.JTextField();
        txtAddress2 = new javax.swing.JTextField();
        txtAddress3 = new javax.swing.JTextField();
        btnAddCustomer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Add Customer");

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

        lblFullName.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblFullName.setText(" ");
        lblFullName.setToolTipText("");

        txtAddress1.setToolTipText("");

        btnAddCustomer.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        btnAddCustomer.setText("Add customer");
        btnAddCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(lblAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtAddress2)
                            .addComponent(txtAddress3)))
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
                            .addComponent(cbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGap(63, 63, 63)
                .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtAddress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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


    // Returns true if the 'Add new category' option in the combo box is selected
    private boolean isAddNewCategorySelected() {
        return cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1;
    }
    
    private void btnAddCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomerActionPerformed
        JTextField[] inputFields = {txtForename, txtSurname, txtAddress1, txtCounty, txtPostcode, txtPhoneNumber, txtEmailAddress};
        // Checks if any of the input fields are empty
        if (countEmptyFields(inputFields) != 0 || isAddNewCategorySelected()) {    // If any one of the fields is empty and if the selected customer category is valid
            System.out.println("-------------------------------");
            System.out.println("One of the required input fields is empty");
        } else {                                                    // If none of the fields are empty
            // Asks user whether they really want to add this customer
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to add this customer?", "Add new customer", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {                                       // If response is yes
                conn = sqlManager.openConnection();                 // Opens connection to the DB
                String query = "INSERT into tblCustomers (customer_id, forename, surname, address1, address2, address3, county, postcode, phone_number, email_address, type_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = null;
                try {
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, CustomerID);
                    pstmt.setString(2, txtForename.getText());
                    pstmt.setString(3, txtSurname.getText());
                    pstmt.setString(4, txtAddress1.getText());
                    pstmt.setString(5, (txtAddress2.getText().equals("") ? null : txtAddress2.getText()));  // If the address2 or address3 is empty then it is replaced by null instead of ""
                    pstmt.setString(6, (txtAddress3.getText().equals("") ? null : txtAddress3.getText()));
                    pstmt.setString(7, txtCounty.getText());
                    pstmt.setString(8, txtPostcode.getText());
                    pstmt.setString(9, txtPhoneNumber.getText());
                    pstmt.setString(10, txtEmailAddress.getText());
                    pstmt.setInt(11, cbCategory.getSelectedIndex() + 1);  // Gets the index of the selected customer category

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println(rowsAffected + " row updated.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sqlManager.closeConnection(conn);                   // Closes connection to the DB
                previousForm.loadCustomers();                       // Refreshes the customer table in the previous form since a customer details were changed
                previousForm.CurrentlyAddingCustomer = false;
                this.dispose();                                             // Closes the add new customer form (current form)
            }
        }
    }//GEN-LAST:event_btnAddCustomerActionPerformed

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
            java.util.logging.Logger.getLogger(formAddCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formAddCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formAddCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formAddCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formAddCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCustomer;
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
