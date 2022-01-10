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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(formNewInvoice.class.getName());
    int InvoiceID = 1;

    // The previous forms the user could have navigated from
    formMainMenu previousForm1 = null;
    formManageInvoices previousForm2 = null;
    formOneCustomer previousForm3 = null;

    // Init
    DefaultTableModel model;
    boolean CurrentlyAddingCustomer = false;
    int selectedItem = 0;
    boolean CurrentlyEditing = false;

    public formNewInvoice() {
        initComponents();
        this.setLocationRelativeTo(null);

        // Fetches Table model and makes table non-editable
        model = (DefaultTableModel) jTable_InvoiceDetails.getModel();
        jTable_InvoiceDetails.setDefaultEditor(Object.class, null);

        // Sets up the table header to be a bit larger
        JTableHeader header = jTable_InvoiceDetails.getTableHeader();
        header.setFont(new Font("Dialog", Font.PLAIN, 14));

        // Gets the next available invoiceID
        InvoiceID = sqlManager.getNextPKValue("tblInvoice", "invoice_id");
        txtInvoiceID.setText(String.valueOf(InvoiceID));

        // Loads all the customers and item categories into the ComboBoxes
        loadCustomersIntoCB();
        loadItemCategoriesIntoCB();

        // When the user clicks on a row in the table
        jTable_InvoiceDetails.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // If the user is not editing another row already
                if (!CurrentlyEditing) {
                    // Gets the id of the invoice item which is currently selected in the table
                    int selectedRow = jTable_InvoiceDetails.getSelectedRow();
                    if (selectedRow != -1) {

                        // Loads the row into the side view
                        selectedItem = selectedRow;
                        txtItem.setText(model.getValueAt(selectedRow, 0).toString());
                        cbCategory.setSelectedItem(model.getValueAt(selectedRow, 1).toString());
                        txtQuantity.setText(model.getValueAt(selectedRow, 2).toString());
                        txtUnitPrice.setText(model.getValueAt(selectedRow, 3).toString());
                        txtItemTotal.setText(model.getValueAt(selectedRow, 4).toString());

                        // Makes some of the fields uneditable since a row was loaded into side view
                        JTextField[] fields = {txtQuantity, txtUnitPrice};
                        Utility.setEditable(fields, false);
                        txtItem.setEditable(false);

                        // Makes all the item management rows available
                        btnRemoveItem.setEnabled(true);
                        btnEditItem.setEnabled(true);
                        btnAddItem.setEnabled(true);

                    } else {
                        logger.log(Level.WARNING, "No row selected");
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

        // Listens for a change in the selectedIndex
        cbCustomers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // NEA OBJECTIVE 1.2: The user should be able to select an existing customer or be prompted
                // to add a new customer in a separate window when they are creating a new receipt.

                // Checks if the user selected the last item ('Add a new customer...')
                if (cbCustomers.getSelectedIndex() == cbCustomers.getItemCount() - 1) {
                    if (!CurrentlyAddingCustomer) {

                        // Opens formAddCustomer
                        formAddCustomer form = new formAddCustomer().getFrame();
                        form.setVisible(true);
                        form.previousForm2 = formNewInvoice.this;

                        // Boolean updated since a new customer is being added
                        CurrentlyAddingCustomer = true;
                        // ComboBox is reset temporarily in case the new customer form is closed
                        cbCustomers.setSelectedIndex(0);
                    }
                }
            }
        });

        // Listens for a change in the selectedIndex
        cbCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // NEA OBJECTIVE 1.4: The user must be able to select an item category for each item being added to the receipt.

                // If the user selected the last item ('Add a new category...')
                if (cbCategory.getSelectedIndex() == cbCategory.getItemCount() - 1) {

                    // NEA OBJECTIVE 5.1: Allow the user to create a new item category.
                    // Prompts user to a add a new category
                    String addedCategory = sqlManager.addNewItemCategory();

                    if (addedCategory != null) {
                        // If the user successfully added a new category then that category is selected
                        loadItemCategoriesIntoCB();
                        cbCategory.setSelectedItem(addedCategory);
                    } else {
                        cbCategory.setSelectedIndex(0);
                    }
                }
            }
        });

        // Updates the totals for the item currently if the values in txtQuantity or txtUnitPrice are changed
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

        // Calculates the initial totals and resets the side view. Puts current date as the date
        updateTableTotals();
        resetSideView();
        dcDateCreated.setDate(new Date());

        // Adjusting the header widths
        jTable_InvoiceDetails = Utility.setColumnWidths(jTable_InvoiceDetails, new int[]{300, 100, 60, 90, 90});
    }

    private void goBack() {
        // If the user came from the Main Menu
        if (previousForm1 != null) {
            previousForm1.setVisible(true);
            this.dispose();
        }
        // If the user came from the Invoice management form
        if (previousForm2 != null) {
            previousForm2.setVisible(true);
            previousForm2.loadInvoices();
            this.dispose();
        }
        // If the user came from a specific customer
        if (previousForm3 != null) {
            previousForm3.setVisible(true);
            previousForm3.previousForm.setVisible(true);
            this.dispose();
        }
    }

    // Selects a specific customer for the invoice if the user came from formOneCustomer
    public void selectCustomer(int customerID) {
        // Gets the customer name for a specific customerID
        String customer = sqlManager.getCustomerFullName(customerID);

        // Selects the customer in the ComboBox
        cbCustomers.setSelectedItem(customer);
    }

    // Calculates the item total if the quantity and unit price are valid
    private void updateItemTotals() {
        String sQuantity = txtQuantity.getText();
        // Gets rid of the £ sign and any commas
        String sUnitPrice = txtUnitPrice.getText().replace("£", "").replace(",", "");

        // If the quantity is a valid int and unit price is valid double
        if (isQuantityValid() && isUnitPriceValid()) {

            // Calculates the item total
            int quantity = Utility.StringToInt(sQuantity);
            double unit_price = Double.valueOf(sUnitPrice);
            double item_subtotal = quantity * unit_price;

            // Updates the Item Total JTextField
            txtItemTotal.setText(Utility.formatCurrency(item_subtotal));
        }
    }

    // Calculates the Subtotal and Total if the values are valid
    private void updateTableTotals() {
        // Gets the subtotal
        double subtotal = calculateSubtotal();
        txtSubtotal.setText(Utility.formatCurrency(subtotal));

        // Gets rid of the £ sign and any commas
        String sPayments = txtPayments.getText().replace("£", "").replace(",", "");
        double payments = 0;
        // If the payments value is a valid double
        if (Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sPayments)) {
            payments = Double.valueOf(sPayments);
        }

        //Updates the total
        double total = subtotal - payments;
        txtTotal.setText(Utility.formatCurrency(total));
    }

    // Calculates the subtotal of the invoice by summing all item totals in the invoice
    private double calculateSubtotal() {
        // Init
        double subTotal = 0.0;
        int NoRows = model.getRowCount();

        // Goes through each invoice item
        for (int i = 0; i < NoRows; i++) {
            // Gets rid of the £ sign and any commas and adds to the subtotal
            String value = model.getValueAt(i, 4).toString().replace("£", "").replace(",", "");
            subTotal += Double.valueOf(value);
        }

        return subTotal;
    }

    // Used when the form is opened from within another forms
    public formNewInvoice getFrame() {
        return this;
    }

    // Method for loading all the customers currently in the system into the ComboBox
    public void loadCustomersIntoCB() {
        // Clears ComboBox
        cbCustomers.removeAllItems();

        try (Connection conn = sqlManager.openConnection()) {
            // Query Setup & Execution
            String query = "SELECT CONCAT(forename,' ', surname) as customerFullName FROM tblCustomer ORDER BY customerFullName";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Adds the customer to the ComboBox
                cbCustomers.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        cbCustomers.addItem("Add a new customer...");
    }

    // Method for loading all the item categories into the ComboBox
    private void loadItemCategoriesIntoCB() {
        // Clears ComboBox
        cbCategory.removeAllItems();

        try (Connection conn = sqlManager.openConnection()) {
            // Query Setup & Execution
            String query = "SELECT category_name FROM tblItemCategory";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Adds category to the ComboBox
                cbCategory.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        cbCategory.addItem("Add a new category...");
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
        jSeparator = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        lblSideView = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtItem = new javax.swing.JTextArea();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        lblUnitPrice = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        lblItemTotal = new javax.swing.JLabel();
        txtItemTotal = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        lblCategory = new javax.swing.JLabel();
        cbCategory = new javax.swing.JComboBox<>();
        btnRemoveItem = new javax.swing.JButton();
        btnEditItem = new javax.swing.JButton();
        btnAddItem = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        btnFinish = new javax.swing.JButton();

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

        txtInvoiceID.setEditable(false);

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

        txtSubtotal.setEditable(false);

        txtTotal.setEditable(false);

        lblSideView.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblSideView.setText("Side View");

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

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

        txtItemTotal.setEditable(false);

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
                .addComponent(jSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                    .addComponent(cbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jSeparator2)))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(btnEditItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(jSeparator)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(cbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAddItem)
                                .addComponent(btnEditItem)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Adds the item from the side view into the table if it is valid
    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        if (isItemValid()) {
            // NEA OBJECTIVE 1.3: The user must be able to enter each new item in the receipt as a row in a table.
            // If all inputs passed the validity checks then item is added to the table
            model.addRow(new Object[]{txtItem.getText(), cbCategory.getSelectedItem().toString(), txtQuantity.getText(),
                "£" + txtUnitPrice.getText().replace("£", ""), "£" + txtItemTotal.getText().replace("£", "")});

            // Resets the side view and updates invoice total
            resetSideView();

            // NEA OBJECTIVE 1.6: The values for subtotal and total should be recalculated each time 
            //a new item is added to the receipt so the user can see what the current totals are.
            updateTableTotals();
        }
    }//GEN-LAST:event_btnAddItemActionPerformed

    // Goes back to the previous form
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // Checks if the user has entered anything into the invoice
        if (model.getRowCount() != 0) {
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back? All entered data will be lost",
                    "Confirm going back", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

            // If the user still wants to go back to the previous form
            if (YesNo == 0) {
                goBack();
            }
        } else {
            goBack();
        }
    }//GEN-LAST:event_btnBackActionPerformed

    // This button finishes the invoice (given all the inputs are valid) and adds it to the DB
    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed

        if (isInvoiceValid()) {
            // NEA OBJECTIVE 1.5: The user must be given the option to review their receipt before submitting it.

            // Asks user whether they really want to finish this invoice
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to finish this invoice?", "Finish Invoice",
                    JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

            // If the response is yes
            if (YesNo == 0) {
                // Gets the date created of the invoice
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDateCreated = dateFormat.format(dcDateCreated.getDate());

                // NEA OBJECTIVE 1.1: Search for last receipt ID and increment for a new receipt.
                // Gets the next available invoice id
                int new_invoiceID = sqlManager.getNextPKValue("tblInvoice", "invoice_id");

                try (Connection conn = sqlManager.openConnection()) {
                    // Query Setup & Execution
                    String query = "INSERT INTO tblInvoice (invoice_id,customer_id,date_created,payments,employee_id) VALUES (?,?,?,?,?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, new_invoiceID);
                    pstmt.setInt(2, sqlManager.getIDofCustomer(cbCustomers.getSelectedItem().toString()));
                    pstmt.setString(3, strDateCreated);
                    pstmt.setDouble(4, txtPayments.getText().isEmpty() ? 0.0 : Double.valueOf(txtPayments.getText().replace("£", "")));
                    pstmt.setInt(5, LoggedInUser.getID());

                    int rowsAffected = pstmt.executeUpdate();
                    logger.log(Level.INFO, rowsAffected + " row(s) inserted.");

                    // Begins inserting all invoice items into the DB
                    uploadInvoiceDetails(new_invoiceID);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQLException");
                }

                // Goes back to the previous form
                goBack();
            }
        }
    }//GEN-LAST:event_btnFinishActionPerformed

    private boolean isInvoiceValid() {
        if (cbCustomers.getSelectedIndex() == cbCustomers.getItemCount() - 1) {
            ErrorMsg.throwError(ErrorMsg.DEFAULT_ERROR, "The 'Add new customer' option is not a valid customer");

        } else if (dcDateCreated.getDate() == null) {
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "Date cannot be empty");

        } else if (model.getRowCount() == 0) {
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "Invoice must have at least one item");

        } else if (!Pattern.matches("^£?[0-9]+(.[0-9])?[0-9]*$|^$", txtPayments.getText())) {
            ErrorMsg.throwError(ErrorMsg.NUMBER_FORMAT_ERROR, "The entered payments value is not a valid decimal");

        } else {
            return true;
        }

        return false;
    }

    // Uploads each row of the invoice to tblInvoiceDetail in the DB
    private void uploadInvoiceDetails(int invoiceID) {
        int NoRows = model.getRowCount();

        for (int i = 0; i < NoRows; i++) {
            // Item Pre-processing
            String Item = model.getValueAt(i, 0).toString();
            int quantity = Utility.StringToInt(model.getValueAt(i, 2).toString());
            double unit_price = Double.valueOf(model.getValueAt(i, 3).toString().replace("£", ""));
            int category = sqlManager.getIDofCategory("tblItemCategory", model.getValueAt(i, 1).toString());

            // Gets the next available row_id for the invoice item
            int new_rowID = sqlManager.getNextPKValue("tblInvoiceDetail", "row_id");

            String query = "INSERT INTO tblInvoiceDetail"
                    + " (row_id,invoice_id,description,quantity,unit_price,category_id)"
                    + " VALUES (?,?,?,?,?,?)";

            try (Connection conn = sqlManager.openConnection()) {
                // Query Setup & Execution
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, new_rowID);
                pstmt.setInt(2, invoiceID);
                pstmt.setString(3, Item);
                pstmt.setInt(4, quantity);
                pstmt.setDouble(5, unit_price);
                pstmt.setInt(6, category);

                int rowsAffected = pstmt.executeUpdate();
                logger.log(Level.INFO, rowsAffected + " row(s) inserted.");

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException");
            }
        }
    }

    // Remove button to remove the selected item in the table
    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        int selectedRow = jTable_InvoiceDetails.getSelectedRow();

        // Checks if a row is selected
        if (selectedRow != -1) {
            // Asks user if they really want to remove this item
            int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this item?",
                    "Remove invoice item", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

            // If the response is yes
            if (YesNo == 0) {
                // Removes the item, updates the invoice totals and resets the side view
                model.removeRow(selectedRow);
                updateTableTotals();
                resetSideView();

                // Since no item is selected
                selectedItem = 0;
            }
        }
    }//GEN-LAST:event_btnRemoveItemActionPerformed

    private void btnEditItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditItemActionPerformed
        // If the user is not yet editing
        if (!CurrentlyEditing) {
            startEdit();
        } else {
            confirmEdit();
        }
    }//GEN-LAST:event_btnEditItemActionPerformed

    private void startEdit() {
        // Flips the boolean
        CurrentlyEditing = true;

        // Makes the item fields editable
        txtItem.setEditable(true);
        txtQuantity.setEditable(true);
        txtUnitPrice.setEditable(true);

        // Changes the item management buttons so the user can only confirm the edit
        btnEditItem.setText("Confirm Edit");
        btnRemoveItem.setEnabled(false);
        btnAddItem.setEnabled(false);
    }

    private void confirmEdit() {
        if (isItemValid()) {
            // Changes the values of the item which is being edited
            model.setValueAt(txtItem.getText(), selectedItem, 0);
            model.setValueAt(cbCategory.getSelectedItem(), selectedItem, 1);
            model.setValueAt(txtQuantity.getText(), selectedItem, 2);
            model.setValueAt("£" + txtUnitPrice.getText().replace("£", ""), selectedItem, 3);
            model.setValueAt("£" + txtItemTotal.getText().replace("£", ""), selectedItem, 4);

            // Recalculates the invoice totals
            updateTableTotals();

            // Flips the boolean since editing is finished
            CurrentlyEditing = false;

            // Makes the item fields uneditable
            txtItem.setEditable(false);
            txtQuantity.setEditable(false);
            txtUnitPrice.setEditable(false);

            // Changes the item management buttons so the user can once again remove, add or edit
            btnEditItem.setText("Edit Item");
            btnRemoveItem.setEnabled(true);
            btnAddItem.setEnabled(true);
        }
    }

    private boolean isItemValid() {

        if (txtItem.getText().isEmpty()) {
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "Description of the item cannot be empty");

        } else if (!isQuantityValid()) {
            ErrorMsg.throwError(ErrorMsg.NUMBER_FORMAT_ERROR, "Quantity must be an integer greater than 0");

        } else if (!isUnitPriceValid()) {
            ErrorMsg.throwError(ErrorMsg.NUMBER_FORMAT_ERROR, "Unit Price must be a double greater than 0");

        } else if (txtItem.getText().length() > sqlManager.getMaxColumnLength("tblInvoiceDetail", "description")) {
            ErrorMsg.throwError(ErrorMsg.INPUT_LENGTH_ERROR_LONG, "item description");

        } else {
            return true;
        }
        return false;
    }

    private boolean isQuantityValid() {
        String sQuantity = txtQuantity.getText();

        if (sQuantity.isEmpty()) {
            // Quantity field empty

        } else if (!Pattern.matches("^[0-9]+$", sQuantity)) {
            // Quantity in the wrong format

        } else if (Utility.StringToInt(sQuantity) == 0) {
            // Quantity equal to 0

        } else {
            return true;
        }
        return false;
    }

    private boolean isUnitPriceValid() {
        String sUnitPrice = txtUnitPrice.getText().replace("£", "").replace(",", "");;

        if (sUnitPrice.isEmpty()) {
            // Unit Price field empty

        } else if (!Pattern.matches("^[0-9]+(.[0-9])?[0-9]*$", sUnitPrice)) {
            // Unit |Price in the wrong format

        } else if (Double.valueOf(sUnitPrice) == 0) {
            // Unit Price equal to 0

        } else {
            return true;
        }
        return false;
    }

    // Clear button to clear all the fields in the side view and some other variables
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // Asks user for confirmation
        int YesNo = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the side view?", "Clear side view",
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

        if (YesNo == 0) {
            // If the response is yes
            resetSideView();
            selectedItem = 0;
            jTable_InvoiceDetails.clearSelection();
        }
    }//GEN-LAST:event_btnClearActionPerformed

    // Method for resetting all the fields and buttons in the side view to their original state
    private void resetSideView() {
        // Item detail fields
        txtItem.setText("");
        txtItem.setEditable(true);
        txtQuantity.setText("");
        txtQuantity.setEditable(true);
        txtUnitPrice.setText("");
        txtUnitPrice.setEditable(true);

        // Item metadata fields
        txtItemTotal.setText("");
        cbCategory.setSelectedIndex(0);

        // Item management buttons
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
    private javax.swing.JComboBox<String> cbCategory;
    private javax.swing.JComboBox<String> cbCustomers;
    private com.toedter.calendar.JDateChooser dcDateCreated;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
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
