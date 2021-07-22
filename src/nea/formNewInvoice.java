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
     * Creates new form formNewDocument
     */
    int EmployeeID = 1;
    int InvoiceID = 1;
    formMainMenu previousForm1 = null;                              // Stores the previously open form
    DefaultTableModel model;                                        // The table model
    Connection conn = null;                                         // Stores the connection object
    boolean CurrentlyAddingCustomer = false;

    public formNewInvoice() {
        initComponents();
        this.setLocationRelativeTo(null);

        model = (DefaultTableModel) jTable_InvoiceDetails.getModel();    // Fetches the table model of the table
        jTable_InvoiceDetails.setDefaultEditor(Object.class, null);      // Makes it so the user cannot edit the table

        JTableHeader header = jTable_InvoiceDetails.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));         // Makes the font of the of header in the table larger - this may just be a windows 1440p scaling issue on my end

        JTextField[] fields = {txtInvoiceID, txtSubtotal, txtTotal, txtItemTotal};
        setEditable(fields, false);
        InvoiceID = sqlManager.getNextPKValue(sqlManager.openConnection(), "tblInvoices", "invoice_id");
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
                int selectedRow = jTable_InvoiceDetails.getSelectedRow();   // Gets the id of the invoice which is currently selected in the table
                if (selectedRow != -1) {                            // -1 = no row selected
                    txtItem.setText(model.getValueAt(selectedRow, 0).toString());
                    Connection conn = sqlManager.openConnection();
                    int category_id = sqlManager.getIDofCategory(conn, model.getValueAt(selectedRow, 1).toString());
                    sqlManager.closeConnection(conn);
                    cbItemCategories.setSelectedIndex(category_id - 1);
                    txtQuantity.setText(model.getValueAt(selectedRow, 2).toString());
                    txtUnitPrice.setText(model.getValueAt(selectedRow, 3).toString());
                    txtItemTotal.setText(model.getValueAt(selectedRow, 4).toString());

                    txtItem.setEditable(false);
                    JTextField[] fields = {txtQuantity, txtUnitPrice, txtItemTotal};
                    setEditable(fields, false);
                    cbItemCategories.setEditable(false);

                    btnRemoveItem.setEnabled(true);
                    btnEditItem.setEnabled(true);
                    btnAddItem.setEnabled(true);

                } else {
                    System.out.println("No row is selected");
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
                        form.setLocationRelativeTo(null);           // Sets the location of the customer view to the right of the current customer management form
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
                    addNewItemCategory();
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

        updateTableTotals();
        btnRemoveItem.setEnabled(false);
        btnEditItem.setEnabled(false);
    }

    // Updates the values in txtItemTotal by first checking if the quantity and unit price are valid values
    public void updateItemTotals() {
        String sQuantity = txtQuantity.getText();
        String sUnitPrice = txtUnitPrice.getText().replace("£", "").replace(",", "");
        if (sQuantity.equals("") || sUnitPrice.equals("")) {
            txtItemTotal.setText("");
        } else {
            if (Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sUnitPrice)) {

                int quantity = Utility.StringToInt(sQuantity);
                double unit_price = Double.valueOf(sUnitPrice);

                double item_subtotal = quantity * unit_price;

                txtItemTotal.setText(Utility.formatCurrency(item_subtotal));
            }
        }
    }

    // Calculates the Subtotal and Total for the entire invoice by summing all the values in the table
    public void updateTableTotals() {
        double subtotal = calculateSubtotal();
        txtSubtotal.setText(Utility.formatCurrency(subtotal));

        String sPayments = txtPayments.getText().replace("£", "").replace(",", "");
        double payments = 0;
        if (Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sPayments)) {
            payments = Double.valueOf(sPayments);
        }

        double total = subtotal - payments;
        txtTotal.setText(Utility.formatCurrency(total));

    }

    // Functionn for calculating the subtotal of the invoice by summing all the values in the table
    public double calculateSubtotal() {
        double subTotal = 0.0;
        int NoRows = model.getRowCount();
        for (int i = 0; i < NoRows; i++) {
            String value = model.getValueAt(i, 4).toString().replace("£", "");
            subTotal += Double.valueOf(value);
        }

        return subTotal;
    }

    // Method for alllowing the user to add a new Item Category into the system
    public void addNewItemCategory() {
        String inputCategory = Utility.StringInputDialog("What should the name of the new category be?", "Add new category"); // Asks user for the name of the customer category
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
                    loadItemCategoriesIntoCB();                     // Refreshes Table

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

    // Method for loading all the customers currently in the system into the form
    public void loadCustomersIntoCB() {
        cbCustomers.removeAllItems();
        conn = sqlManager.openConnection();                         // Opens connection to the DB
        String query = "SELECT forename, surname FROM tblCustomers";
        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            String FullName = "";
            System.out.println("-------------------------------");
            while (rs.next()) {
                FullName = rs.getString(1) + " " + rs.getString(2);
                System.out.println(FullName);                       // For debugging
                cbCustomers.addItem(FullName);                      // Adds the customer to the combo box
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
        cbCustomers.addItem("Add a new customer...");               // Set one of the options to a new customer
    }

    // Method for loading all the item categories currently in the system into the form
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
                cbItemCategories.addItem(rs.getString(1));          // Ads the category to the combo box
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);                           // Closes connection to the DB
        cbItemCategories.addItem("Add a new category...");          // Set one of the options to a add a new category
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

        lblInvoiceID = new javax.swing.JLabel();
        lblCustomer = new javax.swing.JLabel();
        lblDateCreated = new javax.swing.JLabel();
        lblDateDeadline = new javax.swing.JLabel();
        txtInvoiceID = new javax.swing.JTextField();
        cbCustomers = new javax.swing.JComboBox<>();
        dcDateCreated = new com.toedter.calendar.JDateChooser();
        dcDateDeadline = new com.toedter.calendar.JDateChooser();
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
        lblAddItem = new javax.swing.JLabel();
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
        btnBack = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        btnFinish = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("New Invoice");

        lblInvoiceID.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvoiceID.setText("Invoice ID:");

        lblCustomer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCustomer.setText("Customer:");

        lblDateCreated.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateCreated.setText("Date Created:");

        lblDateDeadline.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDateDeadline.setText("Date Deadline:");

        cbCustomers.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        dcDateCreated.setDateFormatString("yyyy-MM-dd");

        dcDateDeadline.setDateFormatString("yyyy-MM-dd");

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

        lblAddItem.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblAddItem.setText("Add Item");

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

        btnAddItem.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnAddItem.setText("Add Item");
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnFinish.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnFinish.setText("Finish Invoice");
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblInvoiceID)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtInvoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(lblDateCreated)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(dcDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblCustomer)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(cbCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblDateDeadline)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(dcDateDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(25, 25, 25))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(10, Short.MAX_VALUE)
                        .addComponent(btnBack)
                        .addGap(823, 823, 823)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(222, 222, 222)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(14, 14, 14)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblAddItem)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblQuantity)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
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
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(20, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblCustomer)
                                    .addComponent(cbCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dcDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(dcDateDeadline, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(lblDateDeadline, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAddItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Add the item in the side view into the table if it is valid
    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        int checks = 0;
        if (!txtItem.getText().equals("")) {
            checks++;
        }
        if (Pattern.matches("^[0-9]+$", txtQuantity.getText())) {
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtUnitPrice.getText())) {
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtItemTotal.getText())) {
            checks++;
        }
        if (checks == 4) {
            conn = sqlManager.openConnection();
            model.addRow(new Object[]{txtItem.getText(), sqlManager.getCategory(conn, "tblItemCategories", "item_category_id", cbItemCategories.getSelectedIndex() + 1), txtQuantity.getText(), "£" + txtUnitPrice.getText().replace("£", ""), "£" + txtItemTotal.getText().replace("£", "")});
            sqlManager.closeConnection(conn);
            resetSideView();
            updateTableTotals();
        } else {
            System.out.println("Didn't pass checks - " + checks + "/4 checks passed");
        }
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back? All entered data will be lost", "Confirm going back", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
        if (YesNo == 0) {
            previousForm1.setVisible(true);                         // Makes main previous form visible
            this.dispose();                                         // Closes the document management form (current form)
        }
    }//GEN-LAST:event_btnBackActionPerformed

    // This button sets the invoice given all the inputs are valid and insert a row into the DB
    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        int checks = 0;
        if (cbCustomers.getSelectedIndex() != cbCustomers.getItemCount() - 1) {
            checks++;
        }
        if (dcDateCreated.getDate() != null) {
            checks++;
        }
        if (dcDateDeadline.getDate() != null) {
            checks++;
        }
        if (model.getRowCount() != 0) {
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtSubtotal.getText())) {
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$|^$", txtPayments.getText())) {
            checks++;
        }
        if (Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$", txtTotal.getText())) {
            checks++;
        }
        if (checks == 7) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDateCreated = dateFormat.format(dcDateCreated.getDate());
            String strDateDeadline = dateFormat.format(dcDateDeadline.getDate());

            conn = sqlManager.openConnection();
            String query = "INSERT INTO tblInvoices (invoice_id,customer_id,date_created,date_deadline,payments,employee_id) VALUES (?,?,?,?,?,?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(query);
                int new_invoiceID = sqlManager.getNextPKValue(conn, "tblInvoices", "invoice_id");   // Gets the next available value of the primary key
                pstmt.setInt(1, new_invoiceID);
                pstmt.setInt(2, cbCustomers.getSelectedIndex() + 1);
                pstmt.setString(3, strDateCreated);
                pstmt.setString(4, strDateDeadline);
                pstmt.setDouble(5, txtPayments.getText().equals("") ? 0.0 : Double.valueOf(txtPayments.getText().replace("£", "")));
                pstmt.setInt(6, EmployeeID);

                System.out.println(pstmt);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("-------------------------------");
                System.out.println(rowsAffected + " row inserted.");
                if (rowsAffected > 0) {
                    uploadInvoiceDetails(new_invoiceID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sqlManager.closeConnection(conn);
        } else {
            System.out.println("Didn't pass checks - " + checks + "/7 checks passed");
        }
    }//GEN-LAST:event_btnFinishActionPerformed

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        int selectedRow = jTable_InvoiceDetails.getSelectedRow();   // Gets the index of the selected row
        if (selectedRow != -1) {                                    // -1 = no row selected
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this item?", "Remove invoice item", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (YesNo == 0) {
                model.removeRow(selectedRow);
                updateTableTotals();
                txtItem.setText("");
                txtQuantity.setText("");
                txtUnitPrice.setText("");
                txtItemTotal.setText("");
                cbItemCategories.setSelectedIndex(0);
                btnRemoveItem.setEnabled(false);
                btnEditItem.setEnabled(false);
            }
        }
    }//GEN-LAST:event_btnRemoveItemActionPerformed

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

    // Method for resetting all the fields in the side view
    public void resetSideView() {
        txtItem.setText("");
        txtQuantity.setText("");
        txtUnitPrice.setText("");
        txtItemTotal.setText("");
        cbItemCategories.setSelectedIndex(0);
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
    private javax.swing.JButton btnEditItem;
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JComboBox<String> cbCustomers;
    private javax.swing.JComboBox<String> cbItemCategories;
    private com.toedter.calendar.JDateChooser dcDateCreated;
    private com.toedter.calendar.JDateChooser dcDateDeadline;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable jTable_InvoiceDetails;
    private javax.swing.JLabel lblAddItem;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblDateDeadline;
    private javax.swing.JLabel lblInvoiceID;
    private javax.swing.JLabel lblItemTotal;
    private javax.swing.JLabel lblPayments;
    private javax.swing.JLabel lblQuantity;
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
