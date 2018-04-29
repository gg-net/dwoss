/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.report.ui.returns;

import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Consumer;

import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author bastian.venz
 */
public class ReturnsReportView extends javax.swing.JPanel implements Consumer<List<ReportLine>> {

    private final ReturnsReportViewComponent reportTableViewCask;

    class ReturnsTableModel extends ReturnsReportTableModel {

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
    public ReturnsReportView() {
        initComponents();
        reportTableViewCask = new ReturnsReportViewComponent();
        reportTableViewCaskPanel.add(reportTableViewCask, BorderLayout.CENTER);
    }

    @Override
    public void accept(List<ReportLine> lines) {
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
        Ui.osOpen(ReturnsExporter.returnsToXls(model.getSelectedLines()));
    }//GEN-LAST:event_exportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exportButton;
    private javax.swing.JPanel reportTableViewCaskPanel;
    // End of variables declaration//GEN-END:variables
}
