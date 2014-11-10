package eu.ggnet.dwoss.report.returns;

import java.awt.BorderLayout;
import java.util.List;

import eu.ggnet.dwoss.common.DesktopUtil;
import eu.ggnet.dwoss.report.TableLine;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.util.DateFormats;

/**
 *
 * @author bastian.venz
 */
public class ReturnsCask extends javax.swing.JPanel {

    private final TableAndSumViewCask reportTableViewCask;

    class ReturnsTableModel extends ReportTableModel {

        public ReturnsTableModel() {
            super(new Object[][]{
                {"Dossier", String.class}, {"Datum", String.class}, {"SopoNr.", String.class},
                {"ArtikelNr.", String.class}, {"Bezeichnung", String.class}, {"Seriennummer", String.class},
                {"MFGDate", String.class}, {"Reportet am", String.class}, {"Report", Boolean.class}
            });
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            switch (columnIndex) {
                case 0:
                    return lines.get(rowIndex).getReportLine().getDossierIdentifier();
                case 1:
                    return DateFormats.ISO.format(lines.get(rowIndex).getReportLine().getActual());
                case 2:
                    return lines.get(rowIndex).getReportLine().getRefurbishId();
                case 3:
                    return lines.get(rowIndex).getReportLine().getPartNo();
                case 4:
                    return lines.get(rowIndex).getReportLine().toName();
                case 5:
                    return lines.get(rowIndex).getReportLine().getSerial();
                case 6:
                    return DateFormats.ISO.format(lines.get(rowIndex).getReportLine().getMfgDate());
                case 7:
                    return DateFormats.ISO.format(lines.get(rowIndex).getReportLine().getReportingDate());
                case 8:
                    return lines.get(rowIndex).isShouldReported();
            }
            return "";
        }

    }

    /**
     * Create a panel for the export of ReportLines that are in the Return customer.
     * <p/>
     * @param lines the given ReportLines
     * @param start Starting Date of the Report
     */
    public ReturnsCask(List<ReportLine> lines) {
        initComponents();
        reportTableViewCask = new TableAndSumViewCask();
        reportTableViewCaskPanel.add(reportTableViewCask, BorderLayout.CENTER);
        ReturnsTableModel model = new ReturnsTableModel();
        for (ReportLine reportLine : lines) {
            model.add(new TableLine(reportLine));
        }
        reportTableViewCask.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exportButton = new javax.swing.JButton();
        reportTableViewCaskPanel = new javax.swing.JPanel();

        exportButton.setText("Exportieren");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        reportTableViewCaskPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(603, Short.MAX_VALUE)
                .addComponent(exportButton))
            .addComponent(reportTableViewCaskPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(reportTableViewCaskPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        ReturnsTableModel model = (ReturnsTableModel)reportTableViewCask.getModel();
        DesktopUtil.open(ReturnsExporter.returnsToXls(model.getSelectedLines()));
    }//GEN-LAST:event_exportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exportButton;
    private javax.swing.JPanel reportTableViewCaskPanel;
    // End of variables declaration//GEN-END:variables
}
