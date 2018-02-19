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
package eu.ggnet.dwoss.report.ui.cap.support;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.report.ee.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.Report.ViewMode;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.NamedEnumCellRenderer;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.swing.VetoableOnOk;

/**
 * View to create new sales report based on mandator contractors.
 * <p>
 * @author oliver.guenther
 */
public class CreateNewReportView extends javax.swing.JPanel implements VetoableOnOk, ResultProducer<CreateNewReportView> {

    public CreateNewReportView() {
        initComponents();
        contractorComboBox.setModel(new DefaultComboBoxModel(Dl.local().lookup(CachedMandators.class).loadContractors().all().toArray()));
        contractorComboBox.setRenderer(new NamedEnumCellRenderer());
        viewModeComboBox.setModel(new DefaultComboBoxModel(Report.ViewMode.values()));
    }

    @Override
    public boolean mayClose() {
        if ( !StringUtils.isNotBlank(nameTextField.getText()) ) {
            JOptionPane.showMessageDialog(this, "Der Name ist leer.");
            return false;
        }
        if ( endDateChooser.getDate() == null ) {
            JOptionPane.showMessageDialog(this, "Das Enddatum wurde nicht ausgewählt.");
            return false;
        }
        if ( startDateChooser.getDate() == null ) {
            JOptionPane.showMessageDialog(this, "Das Startdatum wurde nicht ausgewählt.");
            return false;
        }
        if ( startDateChooser.getDate().after(endDateChooser.getDate()) ) {
            JOptionPane.showMessageDialog(this, "Das Startdatum ist nach dem Enddatum.");
            return false;
        }
        return true;
    }

    @Override
    public CreateNewReportView getResult() {
        return this;
    }

    private void prepareTemplateTextOnce() {
        if ( !StringUtils.isBlank(nameTextField.getText())
                || endDateChooser.getDate() == null
                || startDateChooser.getDate() == null
                || contractorComboBox.getSelectedItem() == null ) return;
        String text = ((TradeName)contractorComboBox.getSelectedItem()).getName() + " Report";
        text += " - " + DateFormats.ISO.format(startDateChooser.getDate());
        text += " bis " + DateFormats.ISO.format(endDateChooser.getDate());
        nameTextField.setText(text);
    }

    public boolean loadUnreported() {
        return unreportedCheckBox.isSelected();
    }

    public ReportParameter getParameter() {
        return ReportParameter.builder()
                .contractor((TradeName)contractorComboBox.getSelectedItem())
                .viewMode((ViewMode)viewModeComboBox.getSelectedItem())
                .reportName(nameTextField.getText())
                .start(startDateChooser.getDate())
                .end(endDateChooser.getDate())
                .build();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        contractorComboBox = new javax.swing.JComboBox();
        startDateChooser = new com.toedter.calendar.JDateChooser();
        endDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        unreportedCheckBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        viewModeComboBox = new javax.swing.JComboBox();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Lieferant:");

        contractorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        contractorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractorComboBoxActionPerformed(evt);
            }
        });

        startDateChooser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startDateChooserMouseClicked(evt);
            }
        });

        endDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                endDateChooserPropertyChange(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Ende:");

        jLabel3.setText("Name:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Begin:");

        unreportedCheckBox.setText("Noch nicht gemeldete Elemente hinzufügen");

        jLabel5.setText("ViewMode:");

        viewModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nameTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(endDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 48, Short.MAX_VALUE)
                        .addComponent(unreportedCheckBox)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contractorComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewModeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(contractorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(viewModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unreportedCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void contractorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractorComboBoxActionPerformed
        prepareTemplateTextOnce();
    }//GEN-LAST:event_contractorComboBoxActionPerformed

    private void startDateChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startDateChooserMouseClicked
        prepareTemplateTextOnce();
    }//GEN-LAST:event_startDateChooserMouseClicked

    private void endDateChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_endDateChooserPropertyChange
        prepareTemplateTextOnce();
    }//GEN-LAST:event_endDateChooserPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox contractorComboBox;
    public com.toedter.calendar.JDateChooser endDateChooser;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JTextField nameTextField;
    public com.toedter.calendar.JDateChooser startDateChooser;
    public javax.swing.JCheckBox unreportedCheckBox;
    public javax.swing.JComboBox viewModeComboBox;
    // End of variables declaration//GEN-END:variables

}
