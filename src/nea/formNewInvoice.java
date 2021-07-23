/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Michal
 */
public class formNewInvoice extends javax.swing.JFrame {

    /**
     * Creates new form formNewInvoice
     */
    int EmployeeID = 1;
    int InvoiceID = 1;
    formMainMenu previousForm1 = null;                              // Stores the previously open form
    formManageInvoices previousForm2 = null;                        // Stores the previously open form
    formOneCustomer previousForm3 = null;                           // Stores the previously open form
    DefaultTableModel model;                                        // The table model
    Connection conn = null;                                         // Stores the connection object
    boolean CurrentlyAddingCustomer = false;
    int selectedItem = 0;

    private void goBack() {
        if (previousForm1 != null) {
            previousForm1.setVisible(true);
            this.dispose();
        }
        if (previousForm2 != null) {
            previousForm2.setVisible(true);
            this.dispose();
        }
        if (previousForm3 != null) {
            previousForm3.setVisible(true);
            previousForm3.previousForm.setVisible(true);
            this.dispose();
        }
    }
    
    public void selectCustomer(int customerID) {
        conn = sqlManager.openConnection();
        String customer = sqlManager.getCustomerFullName(conn, customerID);
        sqlManager.closeConnection(conn);

        int NoCustomers = cbCustomers.getItemCount() - 1;
        for (int i = 0; i < NoCustomers; i++) {
            if (cbCustomers.getItemAt(i).equals(customer)) {
                cbCustomers.setSelectedIndex(i);
            }
        }
    }

    public formNewInvoice() {
        initComponents();
        this.setLocationRelativeTo(null);

        model = (DefaultTableModel) jTable_InvoiceDetails.getModel();    // Fetches the table model of the table
        jTable_InvoiceDetails.setDefaultEditor(Object.class, null);      // Makes it so the user cannot edit the table

        JTableHeader header = jTable_InvoiceDetails.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        JTextField[] fields = {txtInvoiceID, txtSubtotal, txtTotal, txtItemTotal};  // Makes some of the fields which are automatically filled uneditable
        setEditable(fields, false);
        InvoiceID = sqlManager.getNextPKValue(sqlManager.openConnection(), "tblInvoices", "invoice_id"); // Gets the next available invoice id
        txtInvoiceID.setText(String.valueOf(InvoiceID));
        loadCustomersIntoCB();
        loadItemCategoriesIntoCB();

        // Mouse Listener for when someone clicks on a row in the table
        jTable_InvoiceDetails.addMouseListener(new MouseListener() {     // Mouse listener for when the user clicks on a row in the invoice table
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!CurrentlyEditing) {
                    int selectedRow = jTable_InvoiceDetails.getSelectedRow();   // Gets the id of the invoice which is currently selected in the table
                    if (selectedRow != -1) {                            // -1 = no row selected
                        // Loads the row into the side view
                        selectedItem = selectedRow;
                        txtItem.setText(model.getValueAt(selectedRow, 0).toString());
                        Connection conn = sqlManager.openConnection();
                        int category_id = sqlManager.getIDofCategory(conn, model.getValueAt(selectedRow, 1).toString());
                        sqlManager.closeConnection(conn);
                        cbItemCategories.setSelectedIndex(category_id - 1);
                        txtQuantity.setText(model.getValueAt(selectedRow, 2).toString());
                        txtUnitPrice.setText(model.getValueAt(selectedRow, 3).toString());
                        txtItemTotal.setText(model.getValueAt(selectedRow, 4).toString());

                        // Makes some of the fields uneditable since a row was loaded into side view
                        txtItem.setEditable(false);
                        JTextField[] fields = {txtQuantity, txtUnitPrice, txtItemTotal};
                        setEditable(fields, false);

                        // Makes all the row management rows available
                        btnRemoveItem.setEnabled(true);
                        btnEditItem.setEnabled(true);
                        btnAddItem.setEnabled(true);

                    } else {
                        System.out.println("No row is selected");
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        // Add new customer row
        cbCustomers.addActionListener(new ActionListener() {        // When an action happens within the combo box - e.g. the selectedIndex changed
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbCustomers.getSelectedIndex() == cbCustomers.getItemCount() - 1) {   // If the user selected the last item ('Add a new customer...')
                    if (!CurrentlyAddingCustomer) {
                        formAddCustomer form = new formAddCustomer().getFrame();    // Opens a new instance of the formAddCustomer() form
                        form.setLocationRelativeTo(null);
                        form.setVisible(true);                      // Makes the new customer view visible
                        form.previousForm2 = formNewInvoice.this;
                        CurrentlyAddingCustomer = true;
                    }
                }
            }
        });
        // Add new item category row
        cbItemCategories.addActionListener(new ActionListener() {   // When an action happens within the combo box - e.g. the selectedIndex changed
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbItemCategories.getSelectedIndex() == cbItemCategories.getItemCount() - 1) {   // If the user selected the last item ('Add a new customer...')
                    addNewItemCategory();                           // Method for adding a new item category
                }
            }
        });

        // Updates the totals for the item currently being added if the value in txtQuantity and txtUnitPrice is changed
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateItemTotals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateItemTotals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        txtQuantity.getDocument().addDocumentListener(listener);
        txtUnitPrice.getDocument().addDocumentListener(listener);

        // If the payments value changes then the overall total is recalculated
        txtPayments.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTableTotals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTableTotals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        updateTableTotals();                                        // Calculates the initial totals and puts them in the text fields - these should just be £0
        resetSideView();                                            // Resets the side view

        dcDateCreated.setDate(new Date());                          // Puts the current date as the date created value in case the user forgets to specify it himself
    
        jTable_InvoiceDetails = Utility.setColumnWidths(jTable_InvoiceDetails, new int[]{300, 100, 60, 90, 90});
    }

    // Checks if the quantity and unit price are valid values
    // Updates the value for the Item Total
    public void updateItemTotals() {
        String sQuantity = txtQuantity.getText();
        String sUnitPrice = txtUnitPrice.getText().replace("£", "").replace(",", ""); // Gets rid of the £ sign and any commas
        if (sQuantity.equals("") || sUnitPrice.equals("")) {
            txtItemTotal.setText("");                               // If one of the fields is empty
        } else {
            if (Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sUnitPrice) && Pattern.matches("^[0-9]+$", txtQuantity.getText())) {   // If the quantity is a valid int and unit price is valid double

                int quantity = Utility.StringToInt(sQuantity);
                double unit_price = Double.valueOf(sUnitPrice);

                double item_subtotal = quantity * unit_price;       // The total value of the item

                txtItemTotal.setText(Utility.formatCurrency(item_subtotal));    // Updates the Item Total text field
            }
        }
    }

    // Calculates the Subtotal and Total for the entire invoice and updates the JTextFields
    public void updateTableTotals() {
        double subtotal = calculateSubtotal();
        txtSubtotal.setText(Utility.formatCurrency(subtotal));

        String sPayments = txtPayments.getText().replace("£", "").replace(",", "");
        double payments = 0;
        if (Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sPayments)) {    // If the payments value is a valid double
            payments = Double.valueOf(sPayments);
        }

        double total = subtotal - payments;
        txtTotal.setText(Utility.formatCurrency(total));                //Updates the total field
    }

    // Function for calculating the subtotal of the invoice by summing all the values in the table
    public double calculateSubtotal() {
        double subTotal = 0.0;                                      // Init
        int NoRows = model.getRowCount();                           // Gets the number of rows in the table
        for (int i = 0; i < NoRows; i++) {
            String value = model.getValueAt(i, 4).toString().replace("£", "");  // Gets the value of the item(s) as a string
            subTotal += Double.valueOf(value);                      // Converts the value in string type into double type and add it to the running subtotal
        }

        return subTotal;
    }

    // Method for allowing the user to add a new Item Category into the system - This was pretty much copied from one of the other forms
    public void addNewItemCategory() {
        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category"); // Asks user for the name of the new category
        if (inputCategory != null) {                                // If the dialog input was valid 
            conn = sqlManager.openConnection();                     // Opens connection to DB

            inputCategory = inputCategory.trim();                   // Removes all leading and trailing whitespace characters

            if (sqlManager.RecordExists(conn, "tblItemCategories", "category_name", inputCategory)) { // Checks if category already exists in DB
                System.out.println("-------------------------------");
                System.out.println("Category under this name already exists");
            } else {                                                // If it is a unique category
                String query = "INSERT INTO tblItemCategories (item_category_id, category_name, date_created) VALUES (?,?,?)";
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    int newID = sqlManager.getNextPKValue(conn, "tblItemCategories", "item_category_id");   // Gets the next available value of the primary key
                    pstmt.setInt(1, newID);
                    pstmt.setString(2, inputCategory);
                    pstmt.setString(3, Utility.getCurrentDate());

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("-------------------------------");
                    System.out.println(rowsAffected + " row inserted.");
                    loadItemCategoriesIntoCB();                     // Refreshes combo box

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            sqlManager.closeConnection(conn);                       // Closes connection to DB
        }

    }

    public formNewInvoice getFrame() {
        return this;
    }

    // Method for loading all the customers currently in the system into the combo box
    public void loadCustomersIntoCB() {
        cbCustomers.removeAllItems();
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT CONCAT(forename,' ', surname) FROM tblCustomers";
        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("-------------------------------");
            while (rs.next()) {
                System.out.println(rs.getString(1));                // For debugging
                cbCustomers.addItem(rs.getString(1));               // Adds the customer to the combo box
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
        cbCustomers.addItem("Add a new customer...");               // Adds an option for adding a new customer
    }

    // Method for loading all the item categories currently in the system into the combo box
    public void loadItemCategoriesIntoCB() {
        cbItemCategories.removeAllItems();
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT category_name FROM tblItemCategories";
        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("-------------------------------");
            while (rs.next()) {
                System.out.println(rs.getString(1));                // For debugging
                System.out.println(rs.getString(1));
                cbItemCategories.addItem(rs.getString(1));          // Adds the category to the combo box
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
        cbItemCategories.addItem("Add a new category...");          // Adds an option for adding a new category
    }

    // Sets these components to either visible or invisible depending on the boolean state
    public void setEditable(JTextField[] fields, boolean state) {
        for (JTextField field : fields) {
            field.setEditable(state);
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

        btnBack = new javax.swing.JButton();
        lblInvoiceID = new javax.swing.JLabel();
        lblCustomer = new javax.swing.JLabel();
        lblDateCreated = new javax.swing.JLabel();
        txtInvoiceID = new javax.swing.JTextField();
        cbCustomers = new javax.swing.JComboBox<>();
        dcDateCreated = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_InvoiceDetails = new javax.swing.JTable();
        lblSubtotal = new javax.swing.JLabel();
        lblPayments = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        txtPayments = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        lblSideView = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtItem = new javax.swing.JTextArea();
        lblQuantity = new javax.swing.JLabel();
        lblUnitPrice = new javax.swing.JLabel();
        lblItemTotal = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        txtUnitPrice = new javax.swing.JTextField();
        txtItemTotal = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        lblCategory = new javax.swing.JLabel();
        cbItemCategories = new javax.swing.JComboBox<>();
        btnRemoveItem = new javax.swing.JButton();
        btnEditItem = new javax.swing.JButton();
        btnAddItem = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        btnFinish = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("New Invoice");

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblInvoiceID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceID.setText("Invoice ID:");

        lblCustomer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomer.setText("Customer:");

        lblDateCreated.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateCreated.setText("Date Created:");

        cbCustomers.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        dcDateCreated.setDateFormatString("yyyy-MM-dd");

        jTable_InvoiceDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Category", "Quantity", "Unit Price", "Item Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_InvoiceDetails);

        lblSubtotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSubtotal.setText("Subtotal:");

        lblPayments.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblPayments.setText("Payments:");

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotal.setText("Total:");

        lblSideView.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblSideView.setText("Side View");

        txtItem.setColumns(20);
        txtItem.setRows(5);
        jScrollPane2.setViewportView(txtItem);

        lblQuantity.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblQuantity.setText("Quantity:");
        lblQuantity.setToolTipText("");

        lblUnitPrice.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblUnitPrice.setText("Unit Price:");

        lblItemTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblItemTotal.setText("Item Total:");

        lblCategory.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCategory.setText("Category:");

        btnRemoveItem.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnRemoveItem.setText("Remove Item");
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });

        btnEditItem.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnEditItem.setText("Edit Item");
        btnEditItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditItemActionPerformed(evt);
            }
        });

        btnAddItem.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnAddItem.setText("Add Item");
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        btnFinish.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnFinish.setText("Finish Invoice");
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblSubtotal)
                                    .addComponent(lblPayments)
                                    .addComponent(lblTotal))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .addComponent(txtPayments, javax.swing.GroupLayout.Alignment.TRAILING))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblInvoiceID)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtInvoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(lblDateCreated)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(dcDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblCustomer)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnBack)
                        .addGap(823, 823, 823)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblSideView)
                                    .addGap(361, 361, 361)
                                    .addComponent(btnClear))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblQuantity)
                                            .addGap(18, 18, 18)
                                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblUnitPrice))
                                        .addComponent(lblItemTotal))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtItemTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblCategory)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbItemCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jSeparator2)))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(btnEditItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblInvoiceID)
                            .addComponent(txtInvoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCustomer)
                            .addComponent(cbCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSubtotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPayments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPayments))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE))
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSideView)
                            .addComponent(btnClear, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQuantity)
                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUnitPrice)
                            .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblItemTotal)
                            .addComponent(txtItemTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCategory)
                            .addComponent(cbItemCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAddItem)
                                .addComponent(btnEditItem)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Add the item in the side view into the table if it is valid
    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        int checks = 0;                                             // Counter for all validity checks
        if (!txtItem.getText().equals("")) {                        // If the description of the item is not ""
            checks++;
        }
        if (Pattern.matches("^[0-9]+$", txtQuantity.getText())) {   // If the quantity entered is a valid integer
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtUnitPrice.getText())) { // If the Unit price entered is a valid double
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtItemTotal.getText())) { // If the calculated Item total is valid double
            checks++;                                                               // ... might be unnecessary but whatever
        }
        if (checks == 4) {                                          // If the item data passed all the checks
            // Adds the item to the table
            model.addRow(new Object[]{txtItem.getText(), cbItemCategories.getSelectedItem().toString(), txtQuantity.getText(), "£" + txtUnitPrice.getText().replace("£", ""), "£" + txtItemTotal.getText().replace("£", "")});
            resetSideView();                                        // Resets the side view
            updateTableTotals();                                    // Recalculates the totals for the entire invoice
        } else {
            System.out.println("Didn't pass checks - " + checks + "/4 checks passed");
        }
    }//GEN-LAST:event_btnAddItemActionPerformed

    // Back button for going back to the previous form
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        if (model.getRowCount() != 0) {
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back? All entered data will be lost", "Confirm going back", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {
                goBack();
            }
        } else {
            goBack();
        }
    }//GEN-LAST:event_btnBackActionPerformed

    // This button sets the invoice given all the inputs are valid and insert a row into the DB
    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        int checks = 0;                                             // Counter for all validity checks
        if (cbCustomers.getSelectedIndex() != cbCustomers.getItemCount() - 1) { // Makes sure the 'Add new customer' option isn't selected
            checks++;
        }
        if (dcDateCreated.getDate() != null) {                      // Makes sure the selected date is valid
            checks++;
        }
        if (model.getRowCount() != 0) {                             // Makes sure there items in the table - cannot be a blank invoice
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtSubtotal.getText())) {      // If the calculated subtotal is valid double
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$|^$", txtPayments.getText())) {   // If the payments value is a valid double
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtTotal.getText())) {         // If the calculated total is valid double
            checks++;
        }
        if (checks == 6) {                                          // If the input data passed all the validity checks
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDateCreated = dateFormat.format(dcDateCreated.getDate()); // Turns the Date Object in dcDateCreated into a string

            conn = sqlManager.openConnection();
            String query = "INSERT INTO tblInvoices (invoice_id,customer_id,date_created,payments,employee_id) VALUES (?,?,?,?,?)";
            try {
                // Makes a new record in tblInvoices with the invoice metadata
                PreparedStatement pstmt = conn.prepareStatement(query);
                int new_invoiceID = sqlManager.getNextPKValue(conn, "tblInvoices", "invoice_id");   // Gets the next available value of the primary key
                pstmt.setInt(1, new_invoiceID);
                pstmt.setInt(2, cbCustomers.getSelectedIndex() + 1);
                pstmt.setString(3, strDateCreated);
                pstmt.setDouble(4, txtPayments.getText().equals("") ? 0.0 : Double.valueOf(txtPayments.getText().replace("£", "")));
                pstmt.setInt(5, EmployeeID);

                System.out.println(pstmt);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("-------------------------------");
                System.out.println(rowsAffected + " row inserted.");
                if (rowsAffected > 0) {                             // If the invoice was successfully added to the DBMS
                    uploadInvoiceDetails(new_invoiceID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sqlManager.closeConnection(conn);
            goBack();
        } else {
            System.out.println("Didn't pass checks - " + checks + "/6 checks passed");
        }
    }//GEN-LAST:event_btnFinishActionPerformed

    // Uploads each individual row of the table to tblInvoiceDetails
    public void uploadInvoiceDetails(int invoiceID) {
        int NoRows = model.getRowCount();

        conn = sqlManager.openConnection();
        for (int i = 0; i < NoRows; i++) {
            String Item = model.getValueAt(i, 0).toString();
            int quantity = Utility.StringToInt(model.getValueAt(i, 2).toString());
            double unit_price = Double.valueOf(model.getValueAt(i, 3).toString().replace("£", ""));
            int category = sqlManager.getIDofCategory(conn, model.getValueAt(i, 1).toString());

            String query = "INSERT INTO tblInvoiceDetails (row_id,invoice_id,description,quantity,unit_price,item_category_id) VALUES (?,?,?,?,?,?)";
            try {
                // Inserts data about each row into tblInvoiceDetails
                PreparedStatement pstmt = conn.prepareStatement(query);
                int new_rowID = sqlManager.getNextPKValue(conn, "tblInvoiceDetails", "row_id");   // Gets the next available value of the primary key
                pstmt.setInt(1, new_rowID);
                pstmt.setInt(2, invoiceID);
                pstmt.setString(3, Item);
                pstmt.setInt(4, quantity);
                pstmt.setDouble(5, unit_price);
                pstmt.setInt(6, category);

                System.out.println(pstmt);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("-------------------------------");
                System.out.println(rowsAffected + " row inserted.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Remove button to remove the selected item in the table from the table
    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        int selectedRow = jTable_InvoiceDetails.getSelectedRow();   // Gets the index of the selected row
        if (selectedRow != -1) {                                    // -1 = no row selected
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this item?", "Remove invoice item", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {
                model.removeRow(selectedRow);                       // Removes the row
                updateTableTotals();                                // Recalculates the invoice totals
                resetSideView();                                    // Resets the side view
                selectedItem = 0;                                   // No item in the table is now 'selected'
            }
        }
    }//GEN-LAST:event_btnRemoveItemActionPerformed

    boolean CurrentlyEditing = false;
    private void btnEditItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditItemActionPerformed
        if (!CurrentlyEditing) {                                    // If the user is not yet editing
            CurrentlyEditing = true;                                // Flips the boolean
            txtItem.setEditable(true);
            txtQuantity.setEditable(true);                          // Makes the fields editable
            txtUnitPrice.setEditable(true);
            txtItemTotal.setEditable(true);
            btnEditItem.setText("Confirm Edit");                    // Changes the button text
            btnRemoveItem.setEnabled(false);
            btnAddItem.setEnabled(false);                           // Disables the other buttons
        } else {
            int checks = 0;                                         // Counter for all validity checks
            if (!txtItem.getText().equals("")) {                    // If the description of the item is not ""
                checks++;
            }
            if (Pattern.matches("^[0-9]+$", txtQuantity.getText())) {   // If the quantity entered is a valid integer
                checks++;
            }
            if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtUnitPrice.getText())) { // If the Unit price entered is a valid double
                checks++;
            }
            if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtItemTotal.getText())) { // If the calculated Item total is valid double
                checks++;                                                               // ... might be unnecessary but whatever
            }
            if (checks == 4) {                                      // If the edit passed al validity checks
                model.setValueAt(txtItem.getText(), selectedItem, 0);
                model.setValueAt(cbItemCategories.getSelectedItem(), selectedItem, 1);
                model.setValueAt(txtQuantity.getText(), selectedItem, 2);               // Changes the value of the 'selected' row
                model.setValueAt(txtUnitPrice.getText(), selectedItem, 3);
                model.setValueAt(txtItemTotal.getText(), selectedItem, 4);

                updateTableTotals();                                // Recalculates the invoice totals

                CurrentlyEditing = false;                           // Flips the boolean
                txtItem.setEditable(false);
                txtQuantity.setEditable(false);                     // Makes the fields uneditable
                txtUnitPrice.setEditable(false);
                txtItemTotal.setEditable(false);
                btnEditItem.setText("Edit Item");                   // Changes the button text
                btnRemoveItem.setEnabled(true);
                btnAddItem.setEnabled(true);                        // Enables the other buttons
            } else {
                System.out.println("Didn't pass checks - " + checks + "/4 checks passed");
            }
        }

    }//GEN-LAST:event_btnEditItemActionPerformed

    // Clear button for clearing all the fields in the side view
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the side view?", "Clear side view", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
        if (YesNo == 0) {
            resetSideView();
        }
    }//GEN-LAST:event_btnClearActionPerformed

    // Method for clearing/resetting all the fields and buttons in the side view to their original state
    public void resetSideView() {
        txtItem.setText("");
        txtItem.setEditable(true);

        txtQuantity.setText("");
        txtQuantity.setEditable(true);

        txtUnitPrice.setText("");
        txtUnitPrice.setEditable(true);

        txtItemTotal.setText("");
        txtItemTotal.setEditable(true);

        cbItemCategories.setSelectedIndex(0);

        btnRemoveItem.setEnabled(false);
        btnEditItem.setEnabled(false);
        btnAddItem.setEnabled(true);
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
            java.util.logging.Logger.getLogger(formNewInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formNewInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formNewInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formNewInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formNewInvoice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEditItem;
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JComboBox<String> cbCustomers;
    private javax.swing.JComboBox<String> cbItemCategories;
    private com.toedter.calendar.JDateChooser dcDateCreated;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable jTable_InvoiceDetails;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblItemTotal;
    private javax.swing.JLabel lblPayments;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblSideView;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUnitPrice;
    private javax.swing.JTextField txtInvoiceID;
    private javax.swing.JTextArea txtItem;
    private javax.swing.JTextField txtItemTotal;
    private javax.swing.JTextField txtPayments;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
