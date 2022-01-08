/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

/**
 *
 * @author Michal
 */
public class formFormatIntoWord extends javax.swing.JFrame {

    // Init Date formatters
    DateTimeFormatter year_short = DateTimeFormatter.ofPattern("yy");
    DateTimeFormatter full_short = DateTimeFormatter.ofPattern("dd/MM/yy");

    /**
     * If the receipt about to be formatted is an invoice
     */
    public static final int INVOICE = 0;
    /**
     * If the receipt about to be formatted is a quotation
     */
    public static final int QUOTATION = 1;

    // What type of receipt is being formatted
    int RECEIPT_TYPE;

    // ID of the receipt that is about to be formatted into a Word XWPFDocument
    int receiptID = 0;
    private static final Logger logger = Logger.getLogger(formFormatIntoWord.class.getName());

    // Filepaths to input template and output file
    static String templateFilePath = null;
    static String outputFilePath = null;

    public formFormatIntoWord() {
        initComponents();
        this.setLocationRelativeTo(null);
        // Don't close the entire program if the AddEmployee window is closed
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // MouseListener for when the user clicks the template selection JTextField
        txtTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    // Sets up the file chooser to a default location
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    int option = fileChooser.showOpenDialog(new JFrame());
                    // If the user selected a file
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        // Gets the template's filepath and displays name of file in JTextField
                        txtTemplate.setText(file.getName());
                        templateFilePath = file.getAbsolutePath();

                        logger.log(Level.INFO, templateFilePath);
                    } else {
                        // If the user closed the file chooser window
                        logger.log(Level.INFO, "Open command cancelled");
                    }
                }
            }
        });

        // MouseListener for when the user clicks the output selection JTextField
        txtOutput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    // Sets up the file chooser to a default location
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    int option = fileChooser.showOpenDialog(new JFrame());
                    // If the user selected a file
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        // Gets the output's filepath and displays name of file in JTextField
                        txtOutput.setText(file.getName());
                        outputFilePath = file.getAbsolutePath();

                        logger.log(Level.INFO, outputFilePath);
                    } else {
                        // If the user closed the file chooser window
                        logger.log(Level.INFO, "Open command cancelled");
                    }
                }
            }
        });
    }

    // Updates JTextField txtDocument to show user which document is about to be formatted
    public void setDocument() {
        txtDocument.setText(((RECEIPT_TYPE == INVOICE) ? "Invoice #" : "Quotation #") + receiptID);
    }

    // Used when the form is opened from within another form
    public formFormatIntoWord getFrame() {
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

        lblDocument = new javax.swing.JLabel();
        lblTemplate = new javax.swing.JLabel();
        lblOutput = new javax.swing.JLabel();
        lblFileName = new javax.swing.JLabel();
        txtDocument = new javax.swing.JTextField();
        txtTemplate = new javax.swing.JTextField();
        txtOutput = new javax.swing.JTextField();
        txtFileName = new javax.swing.JTextField();
        btnGenerateDocument = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generate Word Document");

        lblDocument.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblDocument.setText("Document:");

        lblTemplate.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTemplate.setText("Template Location:");

        lblOutput.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblOutput.setText("Output Location:");

        lblFileName.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblFileName.setText("File Name:");

        txtDocument.setEditable(false);

        txtTemplate.setEditable(false);

        txtOutput.setEditable(false);

        btnGenerateDocument.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnGenerateDocument.setText("Generate Document");
        btnGenerateDocument.setToolTipText("");
        btnGenerateDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateDocumentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDocument)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblFileName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtDocument)
                            .addComponent(txtTemplate)
                            .addComponent(txtFileName))))
                .addContainerGap(10, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(191, Short.MAX_VALUE)
                .addComponent(btnGenerateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDocument)
                    .addComponent(txtDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTemplate)
                    .addComponent(txtTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOutput)
                    .addComponent(txtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFileName))
                .addGap(18, 18, 18)
                .addComponent(btnGenerateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerateDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateDocumentActionPerformed
        if (templateFilePath == null) {
            // If the person has not supplied a filepath to the template
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "No template filepath supplied");

        } else if (outputFilePath == null) {
            // If the person has not supplied a filepath to the output folder
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "No output filepath supplied");

        } else {
            // Opens the template
            File f = new File(templateFilePath);
            if (f.exists()) {
                beginFormat();
            } else {
                ErrorMsg.throwError(ErrorMsg.DOES_NOT_EXIST_ERROR);
            }
        }
    }//GEN-LAST:event_btnGenerateDocumentActionPerformed

    private void beginFormat() {
        // Gets the table rows of the receipt and calculates the subtotal of the receipt
        ArrayList<tableRow> receiptRows = new ArrayList<>();
        double subtotal = 0.0;

        // Gets the subtotal and receipt items from the appropriate table
        if (RECEIPT_TYPE == INVOICE) {
            subtotal = sqlManager.getReceiptTotal("tblInvoiceDetail", "invoice_id", receiptID);
            receiptRows = getReceiptItems("tblInvoiceDetail", "invoice_id", receiptID);
        } else if (RECEIPT_TYPE == QUOTATION) {
            subtotal = sqlManager.getReceiptTotal("tblQuotationDetail", "quotation_id", receiptID);
            receiptRows = getReceiptItems("tblQuotationDetail", "quotation_id", receiptID);
        }

        // Gets metadata about the specific receipt
        LinkedHashMap<String, String> receiptMetaData = new LinkedHashMap<>();
        if (RECEIPT_TYPE == INVOICE) {
            receiptMetaData = getInvoiceMetaData(subtotal);
        } else if (RECEIPT_TYPE == QUOTATION) {
            receiptMetaData = getQuotationMetaData(subtotal);
        }

        // Gets metadata about the receipt's customer
        LinkedHashMap<String, String> customerData = getCustomerMetaData();

        try {
            // Resizes the template to the amount of rows in the receipt and saves to a temp location
            XWPFDocument doc = new XWPFDocument(OPCPackage.open(templateFilePath));
            doc = resizeDocumentTable(doc, receiptRows.size());
            saveDocument(doc, outputFilePath, "Temp", false);

            doc = new XWPFDocument(OPCPackage.open(outputFilePath + "\\Temp.docx"));

            // Inserting the customer and receipt data into the XWPFDocument
            doc = insertCustomerData(doc, customerData, receiptMetaData);
            doc = insertReceiptData(doc, receiptRows, receiptMetaData);

            String outputFileName = "Output";
            // Checks if the outfile file should have a custom filename
            if (!txtFileName.getText().isEmpty()) {
                if (Pattern.matches("^\\w+$", txtFileName.getText())) {
                    outputFileName = txtFileName.getText();
                }
            }

            // Saves the final formatted Word document
            saveDocument(doc, outputFilePath, outputFileName, true);

            // Removing the temporary file
            removeFile(outputFilePath + "\\Temp.docx");
            this.dispose();

        } catch (InvalidFormatException e) {
            logger.log(Level.SEVERE, "InvalidFormatException");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException");
        }

    }

    private ArrayList<tableRow> getReceiptItems(String tableName, String key, int receipt_id) {
        ArrayList<tableRow> output = new ArrayList<>();

        try (Connection conn = sqlManager.openConnection()) {
            // Query Setup & Execution
            String query = String.format("SELECT description, quantity, unit_price FROM %s WHERE %s = ?", tableName, key);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, receipt_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Pre-processing item values
                int quantity = rs.getInt(2);
                double unit_price = rs.getDouble(3);
                double itemCost = quantity * unit_price;

                // Converting value to strings
                String s_quantity = "" + quantity;
                String s_unit_price = Utility.formatCurrency(unit_price);
                String s_itemCost = Utility.formatCurrency(itemCost);

                tableRow row = new tableRow(rs.getString(1), s_quantity, s_unit_price, s_itemCost);

                // Adds the tableRow into output ArrayList
                output.add(row);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return output;
    }

    private LinkedHashMap<String, String> getInvoiceMetaData(double subtotal) {
        LinkedHashMap<String, String> output = new LinkedHashMap<>();

        try (Connection conn = sqlManager.openConnection()) {
            // Query Setup & Execution
            String query = "SELECT payments, date_created FROM tblInvoice WHERE invoice_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, receiptID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Adds receipt metadata to output hashmap
                output.put("$subtotal", Utility.formatCurrency(subtotal));
                output.put("$payments", Utility.formatCurrency(rs.getDouble(1)));
                output.put("$total", Utility.formatCurrency(subtotal - rs.getDouble(1)));
                output.put("$date", rs.getDate(2).toLocalDate().format(full_short));

                // Calculates the invoice number in the current financial year
                int InvoiceNoThisFinYear = sqlManager.getReceiptNoThisFinancialYear("tblInvoice", "invoice_id", rs.getTimestamp(2).toLocalDateTime());
                String currentYear = Utility.getFinancialYear(rs.getDate(2).toLocalDate()).format(year_short);
                output.put("$No", InvoiceNoThisFinYear + "/" + currentYear);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return output;
    }

    private LinkedHashMap<String, String> getQuotationMetaData(double subtotal) {
        LinkedHashMap<String, String> output = new LinkedHashMap<>();

        try (Connection conn = sqlManager.openConnection()) {
            // Query Setup & Execution
            String query = "SELECT date_created FROM tblQuotation WHERE quotation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, receiptID);
            ResultSet rs = pstmt.executeQuery();

            output.put("$total", Utility.formatCurrency(subtotal));
            if (rs.next()) {
                output.put("$date", rs.getDate(1).toLocalDate().format(full_short));

                // Calculates the quotation number in the current financial year
                int QuotationNoThisFinYear = sqlManager.getReceiptNoThisFinancialYear("tblQuotation", "quotation_id", rs.getTimestamp(1).toLocalDateTime());
                String currentYear = Utility.getFinancialYear(rs.getDate(1).toLocalDate()).format(year_short);
                output.put("$No", QuotationNoThisFinYear + "/" + currentYear);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return output;
    }

    private LinkedHashMap<String, String> getCustomerMetaData() {
        LinkedHashMap<String, String> output = new LinkedHashMap<>();

        try (Connection conn = sqlManager.openConnection()) {
            // Sets up query's SELECT clause
            String query = "SELECT CONCAT(c.forename, ' ', c.surname) AS customerFullName,"
                    + " COALESCE(c.address1, '') AS address1,"
                    + " COALESCE(c.address2, '') AS address2,"
                    + " COALESCE(c.address3, '') AS address3,"
                    + " COALESCE(c.county, '') AS county,"
                    + " COALESCE(c.postcode, '') AS postcode";

            // This sets up the query's FROM, JOIN and WHERE clause depending on receipt type
            if (RECEIPT_TYPE == INVOICE) {
                query += " FROM tblInvoice AS i";
                query += " INNER JOIN tblCustomer AS c ON i.customer_id = c.customer_id";
                query += " WHERE invoice_id = ?";
            } else {
                query += " FROM tblQuotation AS q";
                query += " INNER JOIN tblCustomer AS c ON q.customer_id = c.customer_id";
                query += " WHERE quotation_id = ?";
            }

            // Query Setup & Execution
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, receiptID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Adds the customer data into the hashmap
                output.put("$customerFullName", rs.getString(1));
                output.put("$address1", rs.getString(2));
                output.put("$address2", rs.getString(3));
                output.put("$address3", rs.getString(4));
                output.put("$county", rs.getString(5));
                output.put("$postcode", rs.getString(6));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }

        return output;
    }

    // Method for removing a file under a given filepath
    public static void removeFile(String filepath) {
        File temp = new File(filepath);
        if (temp.delete()) {
            logger.log(Level.INFO, temp.getName() + " deleted successfully");
        } else {
            logger.log(Level.INFO, "Failed to delete " + temp.getName());
        }

    }

    // Method for inserting the customer's data, the ReceiptNo and date into the XWPFDocument
    public XWPFDocument insertCustomerData(XWPFDocument doc, LinkedHashMap<String, String> customerData, LinkedHashMap<String, String> receiptMetaData) {

        // Gets the pointer to the first table in the Word document
        List<XWPFTable> tables = doc.getTables();
        XWPFTable table = tables.get(0);

        if (customerData.get("$address3").isEmpty()) {
            // If the customer has no 3rd address line then this row in the table is removed
            table.removeRow(3);
        }
        if (customerData.get("$address2").isEmpty()) {
            // If the customer has no 2nd address line then this row in the table is removed
            table.removeRow(2);
        }

        // See How To section (4.3) for explanation
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph p : cell.getParagraphs()) {
                    for (XWPFRun r : p.getRuns()) {

                        // Checks if each of these fields are found in the XWPFDocument
                        // If it finds one then it inserts the appropriate data into the template
                        if (r.getText(0).equals("$customerFullName")) {
                            r.setText(customerData.get("$customerFullName"), 0);
                        }
                        if (r.getText(0).equals("$address1")) {
                            r.setText(customerData.get("$address1"), 0);
                        }
                        if (r.getText(0).equals("$address2")) {
                            r.setText(customerData.get("$address2"), 0);
                        }
                        if (r.getText(0).equals("$address3")) {
                            r.setText(customerData.get("$address3"), 0);
                        }
                        if (r.getText(0).equals("$other")) {
                            r.setText(customerData.get("$county") + ", " + customerData.get("$postcode"), 0);
                        }
                        if (r.getText(0).equals("$No")) {
                            r.setText(receiptMetaData.get("$No"), 0);
                        }
                        if (r.getText(0).equals("$date")) {
                            r.setText(receiptMetaData.get("$date"), 0);
                        }
                    }
                }
            }
        }

        return doc;
    }

    // Method for inserting the receipt rows and some of the receipt's metadata (subtotal, payments, total) into the Word document
    public XWPFDocument insertReceiptData(XWPFDocument doc, ArrayList<tableRow> receiptRows, LinkedHashMap<String, String> receiptMetaData) {

        // Gets info about the table and gets the pointer to the second table in the Word Document
        int NoRows = receiptRows.size();
        List<XWPFTable> tables = doc.getTables();
        XWPFTable table = tables.get(1);

        // Goes through each item in the receipt
        for (int i = 0; i < NoRows; i++) {
            // Goes through each column in the table
            for (int j = 0; j < 4; j++) {
                XWPFTableCell cell = table.getRows().get(i + 1).getTableCells().get(j);

                // A paragraph is added to the cell if there was not already one in the cell
                if (cell.getParagraphs().isEmpty()) {
                    cell.addParagraph();
                }

                // Gets the first paragraph in the cell
                XWPFParagraph p = cell.getParagraphs().get(0);

                // Creates a new run and inserts the receipt item into the cell
                XWPFRun run = p.insertNewRun(0);
                run.setText(receiptRows.get(i).data[j]);

            }
        }

        // Calculates position of the final row in the table
        // The quotation template has 2 less rows as the payments and total row is not present
        int FinalRow = (RECEIPT_TYPE == INVOICE) ? NoRows + 4 : NoRows + 2;

        // Inserting the subtotal, payments, and total values
        for (int i = NoRows + 1; i < FinalRow; i++) {
            for (XWPFTableCell cell : table.getRow(i).getTableCells()) {
                for (XWPFParagraph p : cell.getParagraphs()) {
                    for (XWPFRun r : p.getRuns()) {

                        // Checks if each of these fields are found in the XWPFDocument
                        // If it finds one then it inserts the appropriate data into the template
                        if (r.getText(0).equals("$subtotal")) {
                            r.setText(receiptMetaData.get("$subtotal"), 0);
                        }
                        if (r.getText(0).equals("$payments")) {
                            r.setText(receiptMetaData.get("$payments"), 0);
                        }
                        if (r.getText(0).equals("$total")) {
                            r.setText(receiptMetaData.get("$total"), 0);
                        }
                    }
                }
            }
        }

        return doc;
    }

    // Method for resizing the table inside the template depending on the number of rows in the receipt
    public XWPFDocument resizeDocumentTable(XWPFDocument doc, int amtRows) {

        // Context: The template contains three empty rows in the table
        // Gets the pointer to the second table in the Word Document
        List<XWPFTable> tables = doc.getTables();
        XWPFTable table = tables.get(1);

        // The third row in the template is the blank row
        XWPFTableRow blankRow = table.getRows().get(2);

        // Removes the default buffer rows if the receipt has less than 3 items in it
        if (amtRows < 3) {
            for (int i = 3; i >= 1 + amtRows; i--) {
                table.removeRow(i);
                logger.log(Level.INFO, "Row " + i + "removed from template");
            }

        } else if (amtRows == 3) {
            logger.log(Level.INFO, "No change to amount of rows in template");

        } else if (amtRows > 3) {
            // How many more empty rows need to be inserted into the table
            int newRowsNeeded = amtRows - 3;

            try {
                for (int i = 0; i < newRowsNeeded; i++) {
                    // Takes the blankRow and converts it to a CTRow
                    CTRow ctrow = CTRow.Factory.parse(blankRow.getCtRow().newInputStream());
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

                    // Adds the empty row to the table
                    table.addRow(newRow, 2);
                    logger.log(Level.INFO, "Row " + i + " added to template");
                }
            } catch (XmlException e) {
                logger.log(Level.SEVERE, "XmlException");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOException");
            }
        }

        return doc;
    }

    // Method for saving the XWPFDocument to a given location with the option of a counter for overwrite protection
    public void saveDocument(XWPFDocument doc, String destination, String fileName, boolean withCounter) {

        // The directory where the file will be saved
        String savingDestination = destination + "\\" + fileName + ".docx";

        if (withCounter) {
            // Checks if the destination the user wanted is already occupied by another file
            File f = new File(savingDestination);
            if (f.exists()) {
                // Init counters
                boolean found = false;
                int counter = 1;

                // Checks all the files in the directory in order 
                // e.g. Output1, Output2, Output3... until an unused name is found
                while (!found) {
                    savingDestination = destination + "\\" + fileName + counter + ".docx";
                    File t = new File(savingDestination);
                    if (t.exists()) {
                        counter++;
                    } else {
                        // The filepath is not occupied so this is set as the filepath
                        found = true;
                        String msg = String.format("%s already exists. The file was saved as %s%s instead", fileName, fileName, counter);
                        ErrorMsg.throwCustomError(msg, "Already Exists Error");
                    }
                }
            }
        }

        try {
            // Saves the XWPFDocument using the output stream
            FileOutputStream out;
            out = new FileOutputStream(savingDestination);
            doc.write(out);
            out.close();

            logger.log(Level.INFO, "Document successfully saved to: " + savingDestination);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException");
        }
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
            java.util.logging.Logger.getLogger(formFormatIntoWord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formFormatIntoWord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formFormatIntoWord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formFormatIntoWord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formFormatIntoWord().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateDocument;
    private javax.swing.JLabel lblDocument;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblTemplate;
    private javax.swing.JTextField txtDocument;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JTextField txtOutput;
    private javax.swing.JTextField txtTemplate;
    // End of variables declaration//GEN-END:variables
}
