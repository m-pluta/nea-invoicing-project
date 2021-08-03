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
public class formReportThree extends javax.swing.JFrame {

    /**
     * Creates new form formReportThree
     */
    Connection conn = null;                                         // Shows the connection object to the DB
    formMainMenu previousForm = null;                               // Stores the previousForm object to make the Back button work
    int EmployeeID = 1;                                             // The employee id of the logged in employee

    public formReportThree() {
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
        int NoEmployees = 1;
        conn = sqlManager.openConnection();
        try {
            String query = "SELECT COUNT(employee_id) FROM tblEmployees";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();                                              // Gets the next result from query
            NoEmployees = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }

        int preferredAmount = 5;                                    // The preferred amount of categories to display
        SpinnerModel sm = new SpinnerNumberModel(NoEmployees < preferredAmount ? NoEmployees : preferredAmount, 1, NoEmployees, 1); // Default, LB, UB, Increment
        spEmployeeCount.setModel(sm);
        //</editor-fold>
    }

    // Generates the dataset by first creating an empty LinkedHashmap so all data can first be added to that.
    private CategoryDataset getData(LocalDateTime start, LocalDateTime end, int EmployeeCount) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();                          // the final output dataset

        // Raw SQL query: https://pastebin.com/RXQdWpH1
        
        String queryInvoiceTotals = "SELECT i.employee_id, SUM(iD.quantity * iD.unit_price) AS invoiceSubtotal"
                + " FROM tblInvoices AS i"
                + " INNER JOIN tblInvoiceDetails AS iD ON i.invoice_id = iD.invoice_id"
                + " WHERE i.date_created BETWEEN ? AND ?"
                + " GROUP BY i.invoice_id";

        String queryQuotationTotals = "SELECT q.employee_id, SUM(qD.quantity * qD.unit_price) AS quotationSubtotal"
                + " FROM tblQuotations AS q"
                + " INNER JOIN tblQuotationDetails AS qD ON q.quotation_id = qD.quotation_id"
                + " WHERE q.date_created BETWEEN ? AND ?"
                + " GROUP BY q.quotation_id";

        String queryEmployeeInvoiceTotals = "SELECT e.employee_id, COALESCE(SUM(iT.invoiceSubtotal), 0) AS eInvoiceTotal"
                + " FROM tblEmployees AS e"
                + " INNER JOIN (" + queryInvoiceTotals + ") AS iT ON e.employee_id = iT.employee_id"
                + " GROUP BY e.employee_id";

        String queryEmployeeQuotationTotals = "SELECT e.employee_id, COALESCE(SUM(qT.quotationSubtotal), 0) AS eQuotationTotal"
                + " FROM tblEmployees AS e"
                + " INNER JOIN (" + queryQuotationTotals + ") AS qT ON e.employee_id = qT.employee_id"
                + " GROUP BY e.employee_id";

        String mainQuery = "SELECT CONCAT(e.forename, ' ', e.surname) AS Fullname,"
                + " COALESCE(eITs.eInvoiceTotal, 0) AS invoiceTotal,"
                + " COALESCE(eQTs.eQuotationTotal, 0) AS quotationTotal,"
                + " COALESCE(eITs.eInvoiceTotal, 0) + COALESCE(eQTs.eQuotationTotal, 0) AS overallTotal"
                + " FROM tblEmployees AS e"
                + " LEFT JOIN (" + queryEmployeeInvoiceTotals + ") AS eITs ON e.employee_id = eITs.employee_id"
                + " LEFT JOIN (" + queryEmployeeQuotationTotals + ") AS eQTs ON e.employee_id = eQTs.employee_id"
                + " ORDER BY overallTotal DESC"
                + " LIMIT ?";

        conn = sqlManager.openConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(mainQuery);
            pstmt.setObject(1, start);
            pstmt.setObject(2, end);
            pstmt.setObject(3, start);
            pstmt.setObject(4, end);
            pstmt.setInt(5, EmployeeCount);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String category_name = rs.getString(1);
                dataset.addValue(rs.getDouble(2), "Invoices", category_name);
                dataset.addValue(rs.getDouble(3), "Quotations", category_name);
                dataset.addValue(rs.getDouble(4), "Both", category_name);
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
            e.printStackTrace();
        }
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
        spEmployeeCount = new javax.swing.JSpinner();
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
        lblItemCategoryAnalysis.setText("Employee Analysis");

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
        lblDataToAnalyse.setText("Employees to show:");

        cbTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Past month", "Past year", "This month", "This quarter", "This year", "This financial year", "All Time", "Other" }));

        cbTimePeriod.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbTimePeriod.setText("Time Period:");

        lblStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblStart.setText("Start Date: ");

        lblEnd.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEnd.setText("End Date: ");

        spEmployeeCount.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

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
                            .addComponent(spEmployeeCount)))
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
                    .addComponent(spEmployeeCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(222, 222, 222)
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
        //</editor-fold>

        int categoryCount = (int) spEmployeeCount.getValue();       // Gets the amount of employees the user wants to see

        if (valid) {
            data = getData(start, end, categoryCount);                      // Gets the CategoryDataset with all the data
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Value invoiced/quoted per employee",
                    "Employee name",
                    "Value invoiced/quoted",
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

    public formReportThree getFrame() {
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
            java.util.logging.Logger.getLogger(formReportThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formReportThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formReportThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formReportThree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formReportThree().setVisible(true);
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
    private javax.swing.JSpinner spEmployeeCount;
    // End of variables declaration//GEN-END:variables
}
