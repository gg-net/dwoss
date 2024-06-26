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
package eu.ggnet.dwoss.redtapext.ui.cao.document.annulation;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import eu.ggnet.dwoss.core.common.values.CreditMemoReason;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.IPreClose;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.core.widget.Dl;

import static eu.ggnet.dwoss.core.common.values.PositionType.COMMENT;

/**
 *
 * @author pascal.perau
 */
public class CreditMemoView extends javax.swing.JPanel implements IPreClose, TableModelListener {

    private final List<AfterInvoicePosition> creditPositions;

    private List<Position> positions;

    private Stock selectedStock;

    private final CreditMemoTableModel tableModel;

    /** Creates new form CreditMemoView
     * <p>
     * @param creditPositions
     */
    public CreditMemoView(List<AfterInvoicePosition> creditPositions) {
        initComponents();
        stockSelectionBox.setModel(new DefaultComboBoxModel(Dl.remote().lookup(StockAgent.class).findAll(Stock.class).toArray(Stock[]::new)));
        stockSelectionBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value instanceof Stock ) {
                    label.setText(((Stock)value).getName());
                }
                return label;
            }
        });

        selectedStock = (Stock)stockSelectionBox.getSelectedItem();
        this.creditPositions = creditPositions;

        //set up position panel
        tableModel = new CreditMemoTableModel(creditPositions);
        afterInvoiceTable.setModel(tableModel);
        tableModel.addTableModelListener(this);

        reasonComboBox.setRenderer(new CreditMemoReasonCellRenderer());

        var list = new ArrayList<>(EnumSet.allOf(CreditMemoReason.class));
        list.add(null);
        reasonComboBox.setModel(new DefaultComboBoxModel<>(list.toArray(CreditMemoReason[]::new)));
        reasonComboBox.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED && e.getItem().equals(CreditMemoReason.RETRACTION)) {
                reasonTextArea.setText("Widerruf des Kaufvertrages gemäß Fernabsatzgesetz");
            }
        });
        reasonComboBox.setSelectedItem(null);
    }

    public List<Position> getPositions() {
        return positions;
    }

    public CreditMemoReason getReason() {
        return reasonComboBox.getItemAt(reasonComboBox.getSelectedIndex());
    }

    private List<Position> extractPositions() {
        List<Position> positionList = new ArrayList<>();
        for (AfterInvoicePosition creditMemoPosition : creditPositions) {
            if ( creditMemoPosition.isParticipant() ) {
                if ( creditMemoPosition.isPartialCredit() && creditMemoPosition.getPosition().getType() == PositionType.UNIT ) {
                    creditMemoPosition.getPosition().setType(PositionType.UNIT_ANNEX);
                }
                positionList.add(creditMemoPosition.getPosition());
            }
        }
        return positionList;
    }

    public int getStockLocation() {
        return selectedStock.getId();
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.OK ) {
            for (AfterInvoicePosition afterInvoicePosition : creditPositions) {
                if ( afterInvoicePosition.getPosition().getPrice() == 0 ) {
                    JOptionPane.showMessageDialog(this, "Eine Position hat keinen Preis.\nPosition entfernen oder Preis hinzufügen.");
                    return false;
                }
                if ( afterInvoicePosition.isPartialCredit() && afterInvoicePosition.getOriginalPrice() == afterInvoicePosition.getPosition().getPrice() ) {
                    String[] options = new String[]{"Nein, kommt nicht wieder vor."};
                    JOptionPane.showOptionDialog(this, "Soll der Kunde die Position:\n" + afterInvoicePosition.getPosition().getName()
                            + "\nwirklich behalten und den gesamten Betrag erhalten?", null, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                    return false;
                }
            }
            positions = extractPositions();
            if ( positions.isEmpty() ) {
                JOptionPane.showMessageDialog(this, "keine Positionen zur Gutschrift gewählt");
                return false;
            }
            if ( reasonTextArea.getText() == null || reasonTextArea.getText().trim().isEmpty() ) {
                JOptionPane.showMessageDialog(this, "Bitte Storno/Gutschriftsgrund angeben");
                return false;
            }
            positions.add(Position.builder()
                    .amount(1)
                    .type(COMMENT)
                    .name("Grund/Beschreibung")
                    .description(reasonTextArea.getText() + "\n\n" + balancingBox.getSelectedItem().toString())
                    .build());
            if ( selectedStock == null ) {
                JOptionPane.showMessageDialog(this, "bitte Standort für die Gutschrift auswählen");
                return false;
            }
        }
        return true;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        double nettoSum = 0;
        double bruttoSum = 0;
        for (AfterInvoicePosition cmp : tableModel.getDataModel()) {
            if ( cmp.isParticipant() ) {
                nettoSum += cmp.getPosition().getPrice();
                bruttoSum += (cmp.getPosition().getPrice() * (cmp.getPosition().getTax() + 1));
            }
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nettoValue.setText(nf.format(nettoSum));
        bruttoValue.setText(nf.format(bruttoSum));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        afterInvoiceScrollPane = new javax.swing.JScrollPane();
        afterInvoiceTable = new javax.swing.JTable();
        reasonScrollPane = new javax.swing.JScrollPane();
        reasonTextArea = new javax.swing.JTextArea();
        reasonLabel = new javax.swing.JLabel();
        reasonComboBox = new javax.swing.JComboBox<>();
        stockLabel = new javax.swing.JLabel();
        stockSelectionBox = new javax.swing.JComboBox();
        balancingBox = new javax.swing.JComboBox();
        nettoLabel = new javax.swing.JLabel();
        nettoValue = new javax.swing.JLabel();
        bruttoLabel = new javax.swing.JLabel();
        bruttoValue = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(800, 500));
        setLayout(new java.awt.GridBagLayout());

        afterInvoiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        afterInvoiceScrollPane.setViewportView(afterInvoiceTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(afterInvoiceScrollPane, gridBagConstraints);

        reasonScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Grund / Beschreibung"));

        reasonTextArea.setColumns(20);
        reasonTextArea.setRows(5);
        reasonScrollPane.setViewportView(reasonTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(reasonScrollPane, gridBagConstraints);

        reasonLabel.setText("Grund:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(reasonLabel, gridBagConstraints);

        reasonComboBox.setMinimumSize(new java.awt.Dimension(100, 22));
        reasonComboBox.setPreferredSize(new java.awt.Dimension(100, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(reasonComboBox, gridBagConstraints);

        stockLabel.setText("Standort:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(stockLabel, gridBagConstraints);

        stockSelectionBox.setMinimumSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.setPreferredSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockSelectionBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(stockSelectionBox, gridBagConstraints);

        balancingBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rechnungsbetrag bar ausgezahlt", "Rechnungsbetrag wird überwiesen", "Rechnungsbetrag wird verrechnet" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        add(balancingBox, gridBagConstraints);

        nettoLabel.setText("Nettosumme: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(nettoLabel, gridBagConstraints);

        nettoValue.setText("0,- €");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(nettoValue, gridBagConstraints);

        bruttoLabel.setText("Bruttosumme: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(bruttoLabel, gridBagConstraints);

        bruttoValue.setText("0,- €");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(bruttoValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void stockSelectionBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockSelectionBoxActionPerformed
        if ( stockSelectionBox.getSelectedItem() != null ) selectedStock = (Stock)stockSelectionBox.getSelectedItem();
    }//GEN-LAST:event_stockSelectionBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane afterInvoiceScrollPane;
    private javax.swing.JTable afterInvoiceTable;
    private javax.swing.JComboBox balancingBox;
    private javax.swing.JLabel bruttoLabel;
    private javax.swing.JLabel bruttoValue;
    private javax.swing.JLabel nettoLabel;
    private javax.swing.JLabel nettoValue;
    private javax.swing.JComboBox<CreditMemoReason> reasonComboBox;
    private javax.swing.JLabel reasonLabel;
    private javax.swing.JScrollPane reasonScrollPane;
    private javax.swing.JTextArea reasonTextArea;
    private javax.swing.JLabel stockLabel;
    private javax.swing.JComboBox stockSelectionBox;
    // End of variables declaration//GEN-END:variables

}
