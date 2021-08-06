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
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
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

        //<editor-fold defaultstate="collapsed" desc="Code for setting the min and max value for the JSpinner">
        int NoCategories = 1;
        conn = sqlManager.openConnection();
        try {
            String query = "SELECT COUNT(category_id) FROM tblItemCategories";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();                                              // Gets the next result from query
            NoCategories = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);

        int preferredAmount = 5;                                    // The preferred amount of categories to display
        SpinnerModel sm = new SpinnerNumberModel(NoCategories < preferredAmount ? NoCategories : preferredAmount, 1, NoCategories, 1); // Default, LB, UB, Increment
        spCategoryCount.setModel(sm);
        //</editor-fold>
    }

    // Generates the dataset by first creating an empty LinkedHashmap so all data can first be added to that.
    private CategoryDataset getData(LocalDateTime start, LocalDateTime end, int CategoryCount) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();                          // the final output dataset

        // Raw SQL query: https://pastebin.com/5n2Pi2qn
        String queryRawInvoiceCategoryCosts = "SELECT a.category_id, (b.quantity * b.unit_price) as itemCost"
                + " FROM tblitemcategories as a"
                + " INNER JOIN tblinvoicedetails as b ON a.category_id = b.category_id"
                + " INNER JOIN tblinvoices as c ON b.invoice_id = c.invoice_id"
                + " WHERE c.date_created BETWEEN ? AND ?";

        String queryRawQuotationCategoryCosts = "SELECT a.category_id, (b.quantity * b.unit_price) as itemCost"
                + " FROM tblitemcategories as a"
                + " INNER JOIN tblquotationdetails as b ON a.category_id = b.category_id"
                + " INNER JOIN tblquotations as c ON b.quotation_id = c.quotation_id"
                + " WHERE c.date_created BETWEEN ? AND ?";

        String queryInvoiceCategoryTotals = "SELECT sq.category_id, SUM(sq.itemCost) as cT"
                + " FROM (" + queryRawInvoiceCategoryCosts + ") as sq"
                + " GROUP BY sq.category_id";

        String queryQuotationCategoryTotals = "SELECT sq.category_id, SUM(sq.itemCost) as cT"
                + " FROM (" + queryRawQuotationCategoryCosts + ") as sq"
                + " GROUP BY sq.category_id";

        String mainQuery = "SELECT a.category_name, COALESCE(sq1.cT, 0) AS invoiceTotal, COALESCE(sq2.cT, 0) AS quotationTotal, (COALESCE(sq1.cT, 0) + COALESCE(sq2.cT, 0)) as combinedTotal"
                + " FROM tblitemcategories as a"
                + " LEFT JOIN (" + queryInvoiceCategoryTotals + ") as sq1"
                + " ON a.category_id = sq1.category_id"
                + " LEFT JOIN (" + queryQuotationCategoryTotals + ") as sq2"
                + " ON a.category_id = sq2.category_id"
                + " ORDER BY combinedTotal DESC"
                + " LIMIT ?";

        conn = sqlManager.openConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(mainQuery);
            pstmt.setObject(1, start);
            pstmt.setObject(2, end);
            pstmt.setObject(3, start);
            pstmt.setObject(4, end);
            pstmt.setInt(5, CategoryCount);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String category_name = rs.getString(1);
                dataset.addValue(rs.getDouble(2), "Invoices", category_name);
                dataset.addValue(rs.getDouble(3), "Quotations", category_name);
                dataset.addValue(rs.getDouble(4), "Both", category_name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlManager.closeConnection(conn);
        return dataset;
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

        //<editor-fold defaultstate="collapsed" desc="Code for assigning start date values for each choice in cbTime">
        boolean valid = false;                                       // boolean for input validity, assume always valid
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
            conn = sqlManager.openConnection();

            LocalDateTime inv = null;                               // Stores the date of the earliest invoice
            LocalDateTime quot = null;                              // and quotation
            inv = sqlManager.getEarliestDateTime(conn, "tblInvoices", "date_created");      // Gets the earliest dates
            quot = sqlManager.getEarliestDateTime(conn, "tblQuotations", "date_created");   //                                            // Otherwise
            if (inv.isAfter(quot)) {                            // if inv is the later date
                start = quot;                                   // sets quot as the earliest
            } else {
                start = inv;                                    // else inv is the earliest
            }
            sqlManager.closeConnection(conn);
            //</editor-fold>
        } else if (cbTime.getSelectedIndex() == 7) {                                    // Other
            //<editor-fold defaultstate="collapsed" desc="Code for verifying user input and setting start and end date">
            if (dcStart.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Start date input is missing", "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Start date null");

            } else if (dcEnd.getDate() == null) {
                JOptionPane.showMessageDialog(null, "End date input is missing", "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("End date null");

            } else if (dcEnd.getDate().before(dcStart.getDate())) {
                JOptionPane.showMessageDialog(null, "Start Date should be before the end date", "Invalid Input Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("-------------------------------");
                System.out.println("Start date > End date");

            } else {
                start = dcStart.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0); // Start of first date selected
                end = dcEnd.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);  // End of second date selected
                valid = true;
            }
            //</editor-fold>
        }
        //</editor-fold>

        int categoryCount = (int) spCategoryCount.getValue();       // Gets the amount of categories the user wants to see

        if (valid) {
            data = getData(start, end, categoryCount);                      // Gets the CategoryDataset with all the data
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Value per category in each type of document",
                    "Category name",
                    "Value in category",
                    data,
                    PlotOrientation.VERTICAL,
                    true,
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
