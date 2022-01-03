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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(formReportThree.class.getName());
    Connection conn = null;
    formMainMenu previousForm = null;

    public formReportThree() {
        initComponents();
        this.setLocationRelativeTo(null);

        // Makes the option to set a custom start date and end date invisible temporarily
        lblStart.setVisible(false);
        dcStart.setVisible(false);
        lblEnd.setVisible(false);
        dcEnd.setVisible(false);

        // Listens for a change in the selectedIndex
        cbTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbTime.getSelectedIndex() == cbTime.getItemCount() - 1) {
                    // If the user selected the last item i.e. 'Other'
                    // Makes the date selectors for start and end date visible
                    lblStart.setVisible(true);
                    dcStart.setVisible(true);
                    lblEnd.setVisible(true);
                    dcEnd.setVisible(true);
                } else {
                    // Makes the date selectors for start and end date invisible
                    lblStart.setVisible(false);
                    dcStart.setVisible(false);
                    lblEnd.setVisible(false);
                    dcEnd.setVisible(false);
                }
            }
        });

        // Init
        int NoEmployees = 1;
        conn = sqlManager.openConnection();

        try {
            // Query Setup & Execution
            String query = "SELECT COUNT(employee_id) FROM tblEmployee";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                NoEmployees = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
        }
        sqlManager.closeConnection(conn);

        // The preferred amount of categories to display
        int preferredAmount = 5;
        int default_val = NoEmployees < preferredAmount ? NoEmployees : preferredAmount;

        // Default, Lower Bound, Upper Bound, Increment
        SpinnerModel sm = new SpinnerNumberModel(default_val, 1, NoEmployees, 1);
        spEmployeeCount.setModel(sm);
    }

    // Generates the dataset by fetching the data from the DB
    private CategoryDataset getData(LocalDateTime start, LocalDateTime end, int EmployeeCount) {
        conn = sqlManager.openConnection();

        // The final output dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Raw SQL query: https://pastebin.com/RXQdWpH1
        String queryInvoiceTotals = "SELECT i.employee_id, SUM(iD.quantity * iD.unit_price) AS invoiceSubtotal"
                + " FROM tblInvoice AS i"
                + " INNER JOIN tblInvoiceDetail AS iD ON i.invoice_id = iD.invoice_id"
                + " WHERE i.date_created BETWEEN ? AND ?"
                + " GROUP BY i.invoice_id";

        String queryQuotationTotals = "SELECT q.employee_id, SUM(qD.quantity * qD.unit_price) AS quotationSubtotal"
                + " FROM tblQuotation AS q"
                + " INNER JOIN tblQuotationDetail AS qD ON q.quotation_id = qD.quotation_id"
                + " WHERE q.date_created BETWEEN ? AND ?"
                + " GROUP BY q.quotation_id";

        String queryEmployeeInvoiceTotals = "SELECT e.employee_id, COALESCE(SUM(iT.invoiceSubtotal), 0) AS eInvoiceTotal"
                + " FROM tblEmployee AS e"
                + " INNER JOIN (" + queryInvoiceTotals + ") AS iT ON e.employee_id = iT.employee_id"
                + " GROUP BY e.employee_id";

        String queryEmployeeQuotationTotals = "SELECT e.employee_id, COALESCE(SUM(qT.quotationSubtotal), 0) AS eQuotationTotal"
                + " FROM tblEmployee AS e"
                + " INNER JOIN (" + queryQuotationTotals + ") AS qT ON e.employee_id = qT.employee_id"
                + " GROUP BY e.employee_id";

        String mainQuery = "SELECT CONCAT(e.forename, ' ', e.surname) AS Fullname,"
                + " COALESCE(eITs.eInvoiceTotal, 0) AS invoiceTotal,"
                + " COALESCE(eQTs.eQuotationTotal, 0) AS quotationTotal,"
                + " COALESCE(eITs.eInvoiceTotal, 0) + COALESCE(eQTs.eQuotationTotal, 0) AS overallTotal"
                + " FROM tblEmployee AS e"
                + " LEFT JOIN (" + queryEmployeeInvoiceTotals + ") AS eITs ON e.employee_id = eITs.employee_id"
                + " LEFT JOIN (" + queryEmployeeQuotationTotals + ") AS eQTs ON e.employee_id = eQTs.employee_id"
                + " ORDER BY overallTotal DESC"
                + " LIMIT ?";

        try {
            // Query Setup & Execution
            PreparedStatement pstmt = conn.prepareStatement(mainQuery);
            pstmt.setObject(1, start);
            pstmt.setObject(2, end);
            pstmt.setObject(3, start);
            pstmt.setObject(4, end);
            pstmt.setInt(5, EmployeeCount);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Adds the data for each employee to the dataset
                String employee_name = rs.getString(1);
                dataset.addValue(rs.getDouble(2), "Invoices", employee_name);
                dataset.addValue(rs.getDouble(3), "Quotations", employee_name);

                // If the employee has set both invoices and quotations then a value for both is calculated
                if (rs.getDouble(2) > 0 && rs.getDouble(3) > 0) {
                    dataset.addValue(rs.getDouble(4), "Both", employee_name);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException");
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
        lblEmployeeAnalysis = new javax.swing.JLabel();
        pParam = new javax.swing.JPanel();
        btnAnalyze = new javax.swing.JButton();
        lblEmployeesToShow = new javax.swing.JLabel();
        cbTime = new javax.swing.JComboBox<>();
        lblTime = new javax.swing.JLabel();
        lblStart = new javax.swing.JLabel();
        dcStart = new com.toedter.calendar.JDateChooser();
        dcEnd = new com.toedter.calendar.JDateChooser();
        lblEnd = new javax.swing.JLabel();
        spEmployeeCount = new javax.swing.JSpinner();
        pOutput = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Analysis");

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblEmployeeAnalysis.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblEmployeeAnalysis.setText("Employee Analysis");

        pParam.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pParam.setMinimumSize(new java.awt.Dimension(0, 200));

        btnAnalyze.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btnAnalyze.setText("Analyze");
        btnAnalyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalyzeActionPerformed(evt);
            }
        });

        lblEmployeesToShow.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblEmployeesToShow.setText("Employees to show:");

        cbTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Past month", "Past year", "This month", "This quarter", "This year", "This financial year", "All Time", "Other" }));

        lblTime.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTime.setText("Time Period:");

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
                            .addComponent(lblEmployeesToShow)
                            .addComponent(lblTime))
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
                    .addComponent(lblEmployeesToShow)
                    .addComponent(spEmployeeCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTime))
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
                        .addComponent(lblEmployeeAnalysis)
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
                    .addComponent(lblEmployeeAnalysis)
                    .addComponent(btnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Goes back to the previous form
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        previousForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    // When the user clicks the Analyze button
    private void btnAnalyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalyzeActionPerformed
        // Creates an empty dataset
        CategoryDataset data = null;

        // Dates from which to get the results from
        // End date is always the current date unless user specifies otherwise
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();

        // Code for assigning the start date for each choice in cbTime
        // Boolean for input validity, assume always valid
        boolean valid = false;
        switch (cbTime.getSelectedIndex()) {
            case 0:// Past month
                start = LocalDate.now().minusMonths(1).atTime(0, 0, 0);
                valid = true;
                break;
            case 1:// Past year
                start = LocalDate.now().minusMonths(12).atTime(0, 0, 0);
                valid = true;
                break;
            case 2:// This month
                start = LocalDate.now().withDayOfMonth(1).atTime(0, 0, 0);
                valid = true;
                break;
            case 3:// This quarter
                start = Utility.getQuarterStart(LocalDate.now()).atTime(0, 0, 0);
                valid = true;
                break;
            case 4:// This year
                start = LocalDate.now().withDayOfYear(1).atTime(0, 0, 0);
                valid = true;
                break;
            case 5:// This financial year
                start = Utility.getFinancialYear(LocalDate.now()).atTime(0, 0, 0);
                valid = true;
                break;
            case 6:// All time
                conn = sqlManager.openConnection();

                // Init
                LocalDateTime inv;
                LocalDateTime quot;

                // Gets the dates of first invoice and quotation 
                inv = sqlManager.getEarliestDateTime(conn, "tblInvoice", "date_created");
                quot = sqlManager.getEarliestDateTime(conn, "tblQuotation", "date_created");

                // Sets the date_created of the first ever receipt
                if (inv.isBefore(quot)) {
                    start = inv;
                } else {
                    start = quot;
                }

                valid = true;
                sqlManager.closeConnection(conn);
                break;
            case 7:// Other

                if (dcStart.getDate() == null) {
                    ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "Start date cannot be empty");
                    valid = false;

                } else if (dcEnd.getDate() == null) {
                    ErrorMsg.throwError(ErrorMsg.EMPTY_INPUT_FIELD_ERROR, "End date cannot be empty");
                    valid = false;

                } else if (dcEnd.getDate().before(dcStart.getDate())) {
                    ErrorMsg.throwCustomError("Start Date should be before the end date", "Invalid Input Error");
                    valid = false;

                } else {
                    // If the date inputs pass the obove checks then these are set as the start and end date
                    start = dcStart.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0);
                    end = dcEnd.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);
                    valid = true;
                }
                break;
        }

        // Gets the amount of employees the user wants to see
        int employeeCount = (int) spEmployeeCount.getValue();

        if (valid) {
            data = getData(start, end, employeeCount);
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Value invoiced/quoted per employee",
                    "Employee name",
                    "Value invoiced/quoted",
                    data,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            // Adds horizontal grid lines to the plot
            CategoryPlot p = barChart.getCategoryPlot();
            p.setRangeGridlinePaint(Color.black);

            // Makes the x axis labels vertical to conserve space
            CategoryAxis axis = barChart.getCategoryPlot().getDomainAxis();
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

            // Clears the JPanel and adds the ChartPanel which holds the barChart graphic
            ChartPanel barPanel = new ChartPanel(barChart);
            pOutput.removeAll();
            pOutput.add(barPanel, BorderLayout.CENTER);

            // Validates the JPanel to make sure changes are visible
            pOutput.validate();

        }
    }//GEN-LAST:event_btnAnalyzeActionPerformed

    // Used when the form is opened from within another form
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
    private com.toedter.calendar.JDateChooser dcEnd;
    private com.toedter.calendar.JDateChooser dcStart;
    private javax.swing.JLabel lblEmployeeAnalysis;
    private javax.swing.JLabel lblEmployeesToShow;
    private javax.swing.JLabel lblEnd;
    private javax.swing.JLabel lblStart;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel pOutput;
    private javax.swing.JPanel pParam;
    private javax.swing.JSpinner spEmployeeCount;
    // End of variables declaration//GEN-END:variables
}
