package eu.ggnet.dwoss.redtape.document.creditmemo;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.mandator.MandatorSupporter;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;

import eu.ggnet.dwoss.rules.PositionType;

import eu.ggnet.dwoss.redtape.document.AfterInvoicePosition;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rules.PositionType.COMMENT;

/**
 *
 * @author pascal.perau
 */
public class CreditMemoView extends javax.swing.JPanel implements IPreClose {

    private List<AfterInvoicePosition> creditPositions;

    List<Position> positions;

    Stock selectedStock;

    /** Creates new form CreditMemoView
     * <p>
     * @param creditPositions
     */
    public CreditMemoView(List<AfterInvoicePosition> creditPositions) {
        initComponents();
        stockSelectionBox.setModel(new DefaultComboBoxModel(lookup(StockAgent.class).findAll(Stock.class).toArray(new Stock[0])));
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
        CreditMemoTableModel model = new CreditMemoTableModel(creditPositions);
        tablePanel.setTableModel(model);
        sumPanel.setModel(model);
        model.addTableModelListener(sumPanel);

    }

    public List<Position> getPositions() {
        return positions;
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
            if ( tablePanel.getComment() == null || tablePanel.getComment().trim().isEmpty() ) {
                JOptionPane.showMessageDialog(this, "Bitte Storno/Gutschriftsgrund angeben");
                return false;
            }
            positions.add(new PositionBuilder()
                    .setType(COMMENT)
                    .setName("Grund/Beschreibung")
                    .setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1))
                    .setDescription(tablePanel.getComment() + "\n\n" + balancingBox.getSelectedItem().toString())
                    .createPosition());
            if ( selectedStock == null ) {
                JOptionPane.showMessageDialog(this, "bitte Standort für die Gutschrift auswählen");
                return false;
            }
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePanel = new eu.ggnet.dwoss.redtape.document.AfterInvoiceTablePanel();
        sumPanel = new eu.ggnet.dwoss.redtape.document.AfterInvoicePositionPriceSumPanel();
        afterInvoiceInfoPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        stockSelectionBox = new javax.swing.JComboBox();
        balancingBox = new javax.swing.JComboBox();

        jLabel3.setText("Standort:");

        stockSelectionBox.setMinimumSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.setPreferredSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockSelectionBoxActionPerformed(evt);
            }
        });

        balancingBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rechnungsbetrag bar ausgezahlt", "Rechnungsbetrag wird überwiesen", "Rechnungsbetrag wird verrechnet" }));

        javax.swing.GroupLayout afterInvoiceInfoPanelLayout = new javax.swing.GroupLayout(afterInvoiceInfoPanel);
        afterInvoiceInfoPanel.setLayout(afterInvoiceInfoPanelLayout);
        afterInvoiceInfoPanelLayout.setHorizontalGroup(
            afterInvoiceInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(afterInvoiceInfoPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stockSelectionBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(balancingBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        afterInvoiceInfoPanelLayout.setVerticalGroup(
            afterInvoiceInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(afterInvoiceInfoPanelLayout.createSequentialGroup()
                .addGroup(afterInvoiceInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(stockSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(balancingBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(afterInvoiceInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sumPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sumPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(afterInvoiceInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void stockSelectionBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockSelectionBoxActionPerformed
        if ( stockSelectionBox.getSelectedItem() != null ) selectedStock = (Stock)stockSelectionBox.getSelectedItem();
    }//GEN-LAST:event_stockSelectionBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel afterInvoiceInfoPanel;
    private javax.swing.JComboBox balancingBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox stockSelectionBox;
    private eu.ggnet.dwoss.redtape.document.AfterInvoicePositionPriceSumPanel sumPanel;
    private eu.ggnet.dwoss.redtape.document.AfterInvoiceTablePanel tablePanel;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        Position p1 = new PositionBuilder().setName("P1").setPrice(12.).setTax(1.19).createPosition();
        Position p2 = new PositionBuilder().setName("P2").setPrice(20.).setTax(1.19).createPosition();
        Position p3 = new PositionBuilder().setName("P3").setPrice(13.24).setTax(1.19).createPosition();
        Position p4 = new PositionBuilder().setName("P4").setPrice(400.).setTax(1.19).createPosition();
        Position p5 = new PositionBuilder().setName("P5").setPrice(1234.).setTax(1.19).createPosition();

        List<AfterInvoicePosition> positions = new ArrayList<>();
        positions.add(new AfterInvoicePosition(p1));
        positions.add(new AfterInvoicePosition(p2));
        positions.add(new AfterInvoicePosition(p3));
        positions.add(new AfterInvoicePosition(p4));
        positions.add(new AfterInvoicePosition(p5));

        CreditMemoView view = new CreditMemoView(positions);
        OkCancelDialog<CreditMemoView> dialog = new OkCancelDialog<>("Test", view);
        dialog.setVisible(true);
        System.out.println(view.getPositions());
        System.exit(0);
    }
}
