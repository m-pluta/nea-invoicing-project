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

    /**
     * Creates new form formFormatIntoWord
     */
    /**
     * If the document about to be formatted is an invoice
     */
    static int INVOICE = 0;
    /**
     * If the document about to be formatted is a quotation
     */
    static int QUOTATION = 1;
    /**
     * What type of document is being formatted
     */
    static int DOCUMENT_TYPE;

    static Connection conn = null;                                  // Stores the connection object                         
    static int documentID = 0;                                      // The ID of the document that is about to be formatted into a Word XWPFDocument

    static String templateFilePath = null;                          // The filepath to the input template 
    static String outputFilePath = null;                            // The filepath to where the output file should go

    public formFormatIntoWord() {
        initComponents();
        this.setLocationRelativeTo(null);                           // Positions form in the centre of the screen
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);     // Makes sure the program won't close if the document formatting window is closed

        txtTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));  // The starting directory
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);              // So the user can only select a file
                    int option = fileChooser.showOpenDialog(new JFrame());
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        txtTemplate.setText(file.getName());                                // Sets the text box to whatever file the user selected
                        templateFilePath = file.getAbsolutePath();
                        System.out.println(templateFilePath);
                    } else {
                        System.out.println("-------------------------------");
                        System.out.println("Open command cancelled");
                    }
                }
            }
        });

        txtOutput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));  // The starting directory
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        // So the user can only select a directory (folder)
                    int option = fileChooser.showOpenDialog(new JFrame());
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        txtOutput.setText(file.getName());                                  // Sets the text box to whatever directory the user selected
                        outputFilePath = file.getAbsolutePath();
                        System.out.println(outputFilePath);
                    } else {
                        System.out.println("-------------------------------");
                        System.out.println("Open command cancelled");
                    }
                }
            }
        });
    }

    // Since txtDocument is static this has be done
    // Sets the Textfield to show which document is being formatted
    public void setDocument() {
        txtDocument.setText(((DOCUMENT_TYPE == INVOICE) ? "Invoice #" : "Quotation #") + documentID);
    }

    
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

        lblTemplate = new javax.swing.JLabel();
        lblOutput = new javax.swing.JLabel();
        txtTemplate = new javax.swing.JTextField();
        txtOutput = new javax.swing.JTextField();
        btnGenerateDocument = new javax.swing.JButton();
        lblDocument = new javax.swing.JLabel();
        txtDocument = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generate Word Document");

        lblTemplate.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTemplate.setText("Template Location:");

        lblOutput.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblOutput.setText("Output Location:");

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

        lblDocument.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblDocument.setText("Document:");

        txtDocument.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDocument)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnGenerateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDocument)
                                .addComponent(txtTemplate)))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
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
                .addGap(24, 24, 24)
                .addComponent(btnGenerateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerateDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateDocumentActionPerformed
        DateTimeFormatter year_short = DateTimeFormatter.ofPattern("yy");           // Date formatters
        DateTimeFormatter full_short = DateTimeFormatter.ofPattern("dd/MM/yy");     //

        if (templateFilePath == null) {                             // If the person has not supplied a filepath to the template
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "No template filepath supplied");

        } else if (outputFilePath == null) {                        // If the person has not supplied a filepath to the output folder
            ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "No output filepath supplied");

        } else {
            File f = new File(templateFilePath);                                        // Opens the template
            if (f.exists()) {                                                           // The template must exist

                conn = sqlManager.openConnection();
                double subtotal = 0.0;                                                  // The subtotal of the document

                ArrayList<tableRow> documentRows = new ArrayList<tableRow>();           // Stores each row of the document
                //<editor-fold defaultstate="collapsed" desc="Gets the table rows of the document and calculates the subtotal of the document">
                String query = null;
                if (DOCUMENT_TYPE == INVOICE) {
                    query = "SELECT description, quantity, unit_price, unit_price * quantity as itemCost FROM tblInvoiceDetails WHERE invoice_id = ?";
                } else if (DOCUMENT_TYPE == QUOTATION) {
                    query = "SELECT description, quantity, unit_price, unit_price * quantity as itemCost FROM tblQuotationDetails WHERE quotation_id = ?";
                }
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, documentID);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {                             // While there are more rows
                        tableRow row = new tableRow(rs.getString(1), rs.getString(2), Utility.formatCurrency(rs.getDouble(3)), Utility.formatCurrency(rs.getDouble(4)));    // Creates a table row object
                        subtotal += rs.getDouble(4);                // Adds to the subtotal
                        documentRows.add(row);                      // Adds the tableRow object into an arraylist
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //</editor-fold>

                LinkedHashMap<String, String> documentMetaData = new LinkedHashMap<>();  // Hashmap for storing metadata about the document
                //<editor-fold defaultstate="collapsed" desc="Gets metadata about the specific document and puts it in the hashmap">
                query = null;
                if (DOCUMENT_TYPE == INVOICE) {
                    try {
                        query = "SELECT payments, date_created FROM tblInvoices WHERE invoice_id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setInt(1, documentID);
                        ResultSet rs = pstmt.executeQuery();

                        documentMetaData.put("$subtotal", Utility.formatCurrency(subtotal));
                        if (rs.next()) {
                            documentMetaData.put("$payments", Utility.formatCurrency(rs.getDouble(1)));
                            documentMetaData.put("$total", Utility.formatCurrency(subtotal - rs.getDouble(1)));
                            documentMetaData.put("$date", rs.getDate(2).toLocalDate().format(full_short));

                            int InvoiceNoThisFinYear = sqlManager.getInvoiceNoThisFinancialYear(conn, rs.getTimestamp(2).toLocalDateTime());    // The invoice number in the current financial year
                            String currentYear = Utility.getFinancialYear(rs.getDate(2).toLocalDate()).format(year_short);                      // The current year in short format
                            documentMetaData.put("$No", InvoiceNoThisFinYear + "/" + currentYear);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (DOCUMENT_TYPE == QUOTATION) {
                    try {
                        query = "SELECT date_created FROM tblQuotations WHERE quotation_id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setInt(1, documentID);
                        ResultSet rs = pstmt.executeQuery();

                        documentMetaData.put("$total", Utility.formatCurrency(subtotal));
                        if (rs.next()) {
                            documentMetaData.put("$date", rs.getDate(1).toLocalDate().format(full_short));

                            int DocumentNoThisFinYear = sqlManager.getQuotationNoThisFinancialYear(conn, rs.getTimestamp(1).toLocalDateTime()); // The quotation number in the current financial year
                            String currentYear = Utility.getFinancialYear(rs.getDate(1).toLocalDate()).format(year_short);                      // The current year in short format
                            documentMetaData.put("$No", DocumentNoThisFinYear + "/" + currentYear);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //</editor-fold>

                LinkedHashMap<String, String> customerData = new LinkedHashMap<>();
                //<editor-fold defaultstate="collapsed" desc="Gets data about the customer and puts it in the hashmap">
                try {
                    String FROM = (DOCUMENT_TYPE == INVOICE) ? " FROM tblInvoices AS i" : " FROM tblQuotations AS q";
                    String JOIN = (DOCUMENT_TYPE == INVOICE) ? " INNER JOIN tblCustomers AS c ON i.customer_id = c.customer_id" : " INNER JOIN tblCustomers AS c ON q.customer_id = c.customer_id";
                    String WHERE = (DOCUMENT_TYPE == INVOICE) ? " WHERE invoice_id = ?" : " WHERE quotation_id = ?";

                    query = "SELECT CONCAT(c.forename, ' ', c.surname) AS customerFullName,"
                            + " COALESCE(c.address1, '') AS address1,"
                            + " COALESCE(c.address2, '') AS address2,"
                            + " COALESCE(c.address3, '') AS address3,"
                            + " COALESCE(c.county, '') AS county,"
                            + " COALESCE(c.postcode, '') AS postcode"
                            + FROM
                            + JOIN
                            + WHERE;
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, documentID);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        customerData.put("$customerFullName", rs.getString(1)); //
                        customerData.put("$address1", rs.getString(2));         //
                        customerData.put("$address2", rs.getString(3));         // Adds the customer data into the hashmap
                        customerData.put("$address3", rs.getString(4));         //
                        customerData.put("$county", rs.getString(5));           //
                        customerData.put("$postcode", rs.getString(6));         //
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //</editor-fold>

                sqlManager.closeConnection(conn);

                //<editor-fold defaultstate="collapsed" desc="Debug">
                System.out.println("-------------------------------");
                System.out.println("documentRows: ");
                for (tableRow row : documentRows) {
                    System.out.println(row.toString());
                }
                System.out.println("-------------------------------");
                System.out.println("documentMetaData" + ": ");
                System.out.println(documentMetaData);
                System.out.println("-------------------------------");
                System.out.println("customerData: ");
                System.out.println(customerData);
                //</editor-fold>

                XWPFDocument doc = null;                                        // Declares a variable to store an XWPFDocument
                try {
                    doc = new XWPFDocument(OPCPackage.open(templateFilePath));  //
                    doc = resizeDocumentTable(doc, documentRows.size());        // Resizing the amount of rows in the 
                    saveDocument(doc, outputFilePath, "Temp", false);           //

                    doc = new XWPFDocument(OPCPackage.open(outputFilePath + "\\Temp.docx"));    //
                    doc = insertCustomerData(doc, customerData, documentMetaData);              // Inserting the customer's data into the XWPFDocument
                    doc = insertDocumentData(doc, documentRows, documentMetaData);              // Inserting the document's details into the XWPFDocument
                    saveDocument(doc, outputFilePath, "Output", true);                          //

                    removeFile(outputFilePath + "\\Temp.docx");                 // Removing the temporary file
                    this.dispose();
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ErrorMsg.throwError(ErrorMsg.DOES_NOT_EXIST_ERROR);
            }
        }
    }//GEN-LAST:event_btnGenerateDocumentActionPerformed

    // Method for removing a file under a given filepath
    public static void removeFile(String filepath) {
        File temp = new File(filepath);
        if (temp.delete()) {                                        // Returns true if the file was deleted successfully
            System.out.println("-------------------------------");
            System.out.println(temp.getName() + " deleted successfully");
        } else {
            System.out.println("-------------------------------");
            System.out.println("Failed to delete " + temp.getName());
        }

    }

    // Method for inserting the customer's data and the DocumentNo and date into the XWPFDocument
    public static XWPFDocument insertCustomerData(XWPFDocument document, LinkedHashMap<String, String> customerData, LinkedHashMap<String, String> documentMD) {

        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(0);                            // The first table is where the customer's data is inserted

        if (customerData.get("$address3").isEmpty()) {              // If the customer has no 3rd address line then this row in the table is removed
            table.removeRow(3);
        }
        if (customerData.get("$address2").isEmpty()) {              // If the customer has no 2nd address line then this row in the table is removed
            table.removeRow(2);
        }
        for (XWPFTableRow row : table.getRows()) {                  // Goes through each row in the table
            for (XWPFTableCell cell : row.getTableCells()) {        // Goes trough each cell in the individual row
                for (XWPFParagraph p : cell.getParagraphs()) {      // Goes through each parahraph in the cell - a paragraph is a set of runs seperated by line seperators
                    for (XWPFRun r : p.getRuns()) {                 // Goes through each run in the parahraph - a run is a string with characters that have the same formatting settings applied to them

                        // Checks if each of these fields are found in the XWPFDocument
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
                            r.setText(documentMD.get("$No"), 0);
                        }
                        if (r.getText(0).equals("$date")) {
                            r.setText(documentMD.get("$date"), 0);
                        }
                    }
                }
            }
        }

        return document;
    }

    // Method for inserting the document rows and some of the document's meta data (*subtotal, *payments, total) into the Word document
    public static XWPFDocument insertDocumentData(XWPFDocument document, ArrayList<tableRow> documentRows, LinkedHashMap<String, String> documentMD) {

        int NoRows = documentRows.size();                           // The number of rows in the document table
        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(1);                            // Gets the second table which is the table that stores details about the document

        for (int i = 0; i < NoRows; i++) {                          // Each row in the documentRows ArrayList
            for (int j = 0; j < 4; j++) {                           // 0-3 is the index of the cells in each table row
                XWPFTableCell cell = table.getRows().get(i + 1).getTableCells().get(j); // Gets the cell, (i+1) is used to avoid inserting into the table headers
                if (cell.getParagraphs().size() == 0) {
                    cell.addParagraph();                            // Paragraph is added to the cell if there was not already one in the cell
                }
                XWPFParagraph p = cell.getParagraphs().get(0);      // Gets the first paragraph in the cell

                XWPFRun run = p.insertNewRun(0);                    // Adds a new run to the paragraph
                run.setText(documentRows.get(i).data[j]);           // Adds the datum from the documentRows ArrayList

            }
        }

        int FinalRow = (DOCUMENT_TYPE == INVOICE) ? NoRows + 4 : NoRows + 2;

        // Inserting the *subtotal, *payments, total fields
        for (int i = NoRows + 1; i < FinalRow; i++) {
            for (XWPFTableCell cell : table.getRow(i).getTableCells()) {    //
                for (XWPFParagraph p : cell.getParagraphs()) {              // Goes through every run in this part of the table
                    for (XWPFRun r : p.getRuns()) {                         //

                        // If it finds a run with the target name then it replaces it with the document's data
                        if (r.getText(0).equals("$subtotal")) {
                            r.setText(documentMD.get("$subtotal"), 0);
                        }
                        if (r.getText(0).equals("$payments")) {
                            r.setText(documentMD.get("$payments"), 0);
                        }
                        if (r.getText(0).equals("$total")) {
                            r.setText(documentMD.get("$total"), 0);
                        }
                    }
                }
            }
        }

        return document;
    }

    // Method for resizing the table inside the template depending on the amount of rows in the document
    public static XWPFDocument resizeDocumentTable(XWPFDocument document, int amtRows) {

        // Context: The template should contain three empty rows in the table
        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(1);                            // Gets the second table which stores details about the document

        XWPFTableRow blankRow = table.getRows().get(2);             // The third row in the document template is the blank row

        if (amtRows < 3) {
            for (int i = 3; i >= 1 + amtRows; i--) {                // Removes the buffer rows if the document has less than 3 rows
                table.removeRow(i);
            }

        } else if (amtRows == 3) {
            System.out.println("No change to amount of rows");

        } else if (amtRows > 3) {
            int newRowsNeeded = amtRows - 3;                        // How many more empty rows need to be inserted into the table
            try {
                for (int i = 0; i < newRowsNeeded; i++) {       // Goes through the index of all the new rows that are going to be added
                    CTRow ctrow = CTRow.Factory.parse(blankRow.getCtRow().newInputStream());    // Takes the blankRow and converts it to a CTRow
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                    table.addRow(newRow, 2);                                                    // Adds the empty row to the table
                }
            } catch (XmlException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return document;
    }

    // Method for saving the XWPFDocument to a given location with the option of a counter for overwrite protection
    public static void saveDocument(XWPFDocument document, String destination, String fileName, boolean withCounter) {

        String savingDestination = destination + "\\" + fileName + ".docx";     // The directory where the file will be saved
        if (withCounter) {
            File f = new File(savingDestination);                               // Checks if the destination the user wanted is already occupied by another file
            if (f.exists()) {

                boolean found = false;
                int counter = 1;
                while (!found) {                                                            // Checks all the files in the directory in order...
                    savingDestination = destination + "\\" + fileName + counter + ".docx";  // e.g. Output1, Output2, Output3... until an used name is found
                    File t = new File(savingDestination);
                    if (t.exists()) {                               // If it already exists
                        counter++;                                  // Increment the counter
                    } else {
                        found = true;                               // The filepath is not occupied so this is set as the filepath
                        ErrorMsg.throwCustomError(fileName + " already exists. The file was saved as " + fileName + counter + " instead", "Already Exists Error");
                    }
                }
            }
        }

        FileOutputStream out = null;                                // Empty FileOutputStream
        try {
            out = new FileOutputStream(savingDestination);          // FileOutputStream with a set destination directory
            document.write(out);                                    // Saves the XWPFDocument using the stream
            out.close();                                            // Closes the OutputStream
            System.out.println("-------------------------------");
            System.out.println("Document successfully saved to: " + savingDestination);

        } catch (IOException e) {
            e.printStackTrace();
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
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblTemplate;
    private javax.swing.JTextField txtDocument;
    private javax.swing.JTextField txtOutput;
    private javax.swing.JTextField txtTemplate;
    // End of variables declaration//GEN-END:variables
}
