/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Michal
 */
public class formReportTwo extends javax.swing.JFrame {

    /**
     * Creates new form formReportTwo
     */
    Connection conn = null;                                         // Shows the connection object to the DB
    formMainMenu previousForm = null;                               // Stores the previousForm object to make the Back button work
    int EmployeeID = 1;                                             // The employee id of the logged in employee

    public formReportTwo() {
        initComponents();
        this.setLocationRelativeTo(null);                           // Positions the form in the middle of the screen

        lblStart.setVisible(false);
        dcStart.setVisible(false);
        lblEnd.setVisible(false);
        dcEnd.setVisible(false);

        // ActionListener for when the users changes the selected item in the time combo box
        cbTime.addActionListener(new ActionListener() {        // When an action happens within the combo box - e.g. the selectedIndex changed
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbTime.getSelectedIndex() == cbTime.getItemCount() - 1) {   // If the user selected the last item ('Other')
                    lblStart.setVisible(true);
                    dcStart.setVisible(true);
                    lblEnd.setVisible(true);                        // Makes the date selectors for start and end date appear
                    dcEnd.setVisible(true);
                } else {
                    lblStart.setVisible(false);
                    dcStart.setVisible(false);
                    lblEnd.setVisible(false);                       // Makes the date selectors for start and end date disappear
                    dcEnd.setVisible(false);
                }
            }
        });
    }

    // Generates the dataset by first creating an empty LinkedHashmap so all data can first be added to that.
    private CategoryDataset getData(LocalDateTime start, LocalDateTime end, int CategoryCount) {
        DateTimeFormatter daymonth = DateTimeFormatter.ofPattern("dd/MM");      // For formatting dates into an appropriate format
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yy");
        conn = sqlManager.openConnection();                                     // Opens connection to DB

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();                          // the final output dataset
        LinkedHashMap<String, Double> dataArr_Invoice = new LinkedHashMap<String, Double>();    // Makes an empty hashmap with all the categories as the key
        LinkedHashMap<String, Double> dataArr_Quotation = new LinkedHashMap<String, Double>();  // Makes an empty hashmap with all the categories as the key
        LinkedHashMap<String, Double> total = new LinkedHashMap<String, Double>();              // Makes an empty hashmap with all the categories as the key

        //<editor-fold defaultstate="collapsed" desc="Populating the invoice hashmap with all the item category totals">
        try {
            String query = "SELECT invoice_id from tblInvoices WHERE date_created BETWEEN ? AND ?";
            PreparedStatement pstmt = conn.prepareStatement(query); // Gets all the invoices between two dates
            pstmt.setObject(1, start);
            pstmt.setObject(2, end);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int invID = rs.getInt(1);
                String query2 = "SELECT item_category_id, unit_price * quantity AS total FROM tblInvoiceDetails WHERE invoice_id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setInt(1, invID);

                ResultSet rs2 = pstmt2.executeQuery();
                while (rs2.next()) {
                    String category_name = sqlManager.getCategory(conn, "tblItemCategories", "item_category_id", rs2.getInt(1));
                    double item_total = rs2.getDouble(2);
                    if (dataArr_Invoice.containsKey(category_name)) {
                        dataArr_Invoice.put(category_name, dataArr_Invoice.get(category_name) + item_total);
                    } else {
                        dataArr_Invoice.put(category_name, item_total);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Populating the quotation hashmap with all the item category totals">
        try {
            String query = "SELECT quotation_id from tblQuotations WHERE date_created BETWEEN ? AND ?";
            PreparedStatement pstmt = conn.prepareStatement(query); // Gets all the quotations between two dates
            pstmt.setObject(1, start);
            pstmt.setObject(2, end);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int quotID = rs.getInt(1);
                String query2 = "SELECT item_category_id, unit_price * quantity AS total FROM tblQuotationDetails WHERE quotation_id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setInt(1, quotID);

                ResultSet rs2 = pstmt2.executeQuery();
                while (rs2.next()) {
                    String category_name = sqlManager.getCategory(conn, "tblItemCategories", "item_category_id", rs2.getInt(1));
                    double item_total = rs2.getDouble(2);
                    if (dataArr_Quotation.containsKey(category_name)) {
                        dataArr_Quotation.put(category_name, dataArr_Quotation.get(category_name) + item_total);
                    } else {
                        dataArr_Quotation.put(category_name, item_total);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        //</editor-fold>

        total = mergeHashMaps(dataArr_Invoice, dataArr_Quotation);
        
        return dataset;                                             // Returns the populated dataset
    }

    public static LinkedHashMap<String, Double> mergeHashMaps(LinkedHashMap<String, Double> map1, LinkedHashMap<String, Double> map2) {
        LinkedHashMap<String, Double> mergedMap = map1;

        for (Map.Entry<String, Double> i : map2.entrySet()) {  // Goes through each Entry in the hashmap
            if (mergedMap.containsKey(i.getKey())) {
                mergedMap.put(i.getKey(), mergedMap.get(i.getKey()) + i.getValue());
            } else {
                mergedMap.put(i.getKey(), i.getValue());
            }
        }
        return mergedMap;
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
        lblItemCategoryAnalysis = new javax.swing.JLabel();
        pParam = new javax.swing.JPanel();
        btnAnalyze = new javax.swing.JButton();
        lblDataToAnalyse = new javax.swing.JLabel();
        cbTime = new javax.swing.JComboBox<>();
        cbTimePeriod = new javax.swing.JLabel();
        lblStart = new javax.swing.JLabel();
        dcStart = new com.toedter.calendar.JDateChooser();
        dcEnd = new com.toedter.calendar.JDateChooser();
        lblEnd = new javax.swing.JLabel();
        spCategoryCount = new javax.swing.JSpinner();
        pOutput = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Item Category Analysis");

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblItemCategoryAnalysis.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblItemCategoryAnalysis.setText("Item Category Analysis");

        pParam.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pParam.setMinimumSize(new java.awt.Dimension(0, 200));

        btnAnalyze.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnAnalyze.setText("Analyze");
        btnAnalyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalyzeActionPerformed(evt);
            }
        });

        lblDataToAnalyse.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDataToAnalyse.setText("Categories to show:");

        cbTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Past month", "Past year", "This month", "This quarter", "This year", "This financial year", "All Time", "Other" }));

        cbTimePeriod.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbTimePeriod.setText("Time Period:");

        lblStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblStart.setText("Start Date: ");

        lblEnd.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEnd.setText("End Date: ");

        spCategoryCount.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        javax.swing.GroupLayout pParamLayout = new javax.swing.GroupLayout(pParam);
        pParam.setLayout(pParamLayout);
        pParamLayout.setHorizontalGroup(
            pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pParamLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pParamLayout.createSequentialGroup()
                        .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDataToAnalyse)
                            .addComponent(cbTimePeriod))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbTime, 0, 150, Short.MAX_VALUE)
                            .addComponent(spCategoryCount)))
                    .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAnalyze)
                        .addGroup(pParamLayout.createSequentialGroup()
                            .addComponent(lblStart)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(dcStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 240, Short.MAX_VALUE)
                            .addComponent(lblEnd)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(dcEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        pParamLayout.setVerticalGroup(
            pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pParamLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataToAnalyse)
                    .addComponent(spCategoryCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTimePeriod))
                .addGap(10, 10, 10)
                .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(dcStart, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(lblStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(dcEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(lblEnd)))
                .addGap(18, 18, 18)
                .addComponent(btnAnalyze)
                .addContainerGap())
        );

        pOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        pOutput.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack)
                        .addGap(200, 200, 200)
                        .addComponent(lblItemCategoryAnalysis)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pParam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pOutput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblItemCategoryAnalysis)
                    .addComponent(btnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        previousForm.setVisible(true);                              // Makes main previous form visible
        this.dispose();                                             // Closes the Sales Analysis (current form)
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAnalyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalyzeActionPerformed
        CategoryDataset data = null;                                // Empty dataset - about to be populated

        LocalDateTime start = null;                                 // Dates from which to get the results from
        LocalDateTime end = LocalDateTime.now();                    // end is always current datetime unless user specifies otherwise

        int categoryCount = (int) spCategoryCount.getValue();       // Gets the amount of categories the user wants to see
        if (categoryCount == 0) {                                   // All will be analyzed but only this amount will be shown
            categoryCount = 10;
        }

        boolean valid = true;                                       // boolean for input validity, assume always valid
        if (cbTime.getSelectedIndex() == 0) {                                           // Past month
            start = LocalDate.now().minusMonths(1).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 1) {                                    // Past year
            start = LocalDate.now().minusMonths(12).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 2) {                                    // This month
            start = LocalDate.now().withDayOfMonth(1).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 3) {                                    // This quarter
            start = Utility.getQuarterStart(LocalDate.now()).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 4) {                                    // This year
            start = LocalDate.now().withDayOfYear(1).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 5) {                                    // This financial year
            start = Utility.getFinancialYear(LocalDate.now()).atTime(0, 0, 0);
        } else if (cbTime.getSelectedIndex() == 6) {                                    // All time
            //<editor-fold defaultstate="collapsed" desc="Code for getting the earliest date of invoices or quotations or both">
            conn = sqlManager.openConnection();                     // Opens connection to the DB

            LocalDateTime inv = null;                               // Stores the date of the earliest invoice
            LocalDateTime quot = null;                              // and quotation
            inv = sqlManager.getEarliestDateTime(conn, "tblInvoices", "date_created");      // Gets the earliest dates
            quot = sqlManager.getEarliestDateTime(conn, "tblQuotations", "date_created");   //                                            // Otherwise
            if (inv.isAfter(quot)) {                            // if inv is the later date
                start = quot;                                   // sets quot as the earliest
            } else {
                start = inv;                                    // else inv is the earliest
            }
            sqlManager.closeConnection(conn);                       // Closes connection to the DB
            //</editor-fold>
        } else if (cbTime.getSelectedIndex() == 7) {                                    // Other
            //<editor-fold defaultstate="collapsed" desc="Code for verifying user input and setting start and end date">
            if (dcStart.getDate() == null || dcEnd.getDate() == null || dcEnd.getDate().before(dcStart.getDate())) {    // Checks if input is valid
                valid = false;
                System.out.println("Start date or end date missing or end date is after start date");
            } else {
                start = dcStart.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0); // Start of first date selected
                end = dcEnd.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);  // End of second date selected
            }
            //</editor-fold>
        }

        if (valid) {
            data = getData(start, end, categoryCount);                                  // Gets the CategoryDataset with all the data
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Categories Analysed",
                    "Category Name",
                    "Category count",
                    data,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false);

            CategoryPlot p = barChart.getCategoryPlot();
            p.setRangeGridlinePaint(Color.black);
            CategoryAxis axis = barChart.getCategoryPlot().getDomainAxis();
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);   // Makes the x axis labels vertical to conserve space

            ChartPanel barPanel = new ChartPanel(barChart);                 // chartPanel will hold the bar chart
            pOutput.removeAll();                                            // Clears the JPanel
            pOutput.add(barPanel, BorderLayout.CENTER);                     // Adds the chartPanel
            pOutput.validate();                                             // Validates the JPanel to make sure changes are visible

        }
    }//GEN-LAST:event_btnAnalyzeActionPerformed

    public formReportTwo getFrame() {
        return this;
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
            java.util.logging.Logger.getLogger(formReportTwo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formReportTwo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formReportTwo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formReportTwo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formReportTwo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalyze;
    private javax.swing.JButton btnBack;
    private javax.swing.JComboBox<String> cbTime;
    private javax.swing.JLabel cbTimePeriod;
    private com.toedter.calendar.JDateChooser dcEnd;
    private com.toedter.calendar.JDateChooser dcStart;
    private javax.swing.JLabel lblDataToAnalyse;
    private javax.swing.JLabel lblEnd;
    private javax.swing.JLabel lblItemCategoryAnalysis;
    private javax.swing.JLabel lblStart;
    private javax.swing.JPanel pOutput;
    private javax.swing.JPanel pParam;
    private javax.swing.JSpinner spCategoryCount;
    // End of variables declaration//GEN-END:variables
}
