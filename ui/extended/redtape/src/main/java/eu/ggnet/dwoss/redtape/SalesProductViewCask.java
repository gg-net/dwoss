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
package eu.ggnet.dwoss.redtape;

import java.awt.Dialog;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoTableModel;
import eu.ggnet.saft.Ui;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * A UI to add a {@link SalesProduct} for Sale. <br />
 * They are used in {@link DocumentUpdateView} to add {@link Position}s of type PRODUCT_BATCH to a {@link Document}.
 * <p/>
 * @author bastian.venz
 */
public class SalesProductViewCask extends javax.swing.JPanel {

    public class SalesProductTableModel extends PojoTableModel<SalesProduct> {

        public SalesProductTableModel() {

            super(new ArrayList<SalesProduct>(),
                    new PojoColumn<SalesProduct>(
                            "PartNo", false, 10, String.class, "partNo"),
                    new PojoColumn<SalesProduct>("Name", false, 100, String.class, "name"),
                    new PojoColumn<SalesProduct>("Preis (netto)", false, 10, Double.class, "price"));
        }
    }

    private final RedTapeAgent redTapeAgent;

    private final RedTapeWorker redTapeWorker;

    private SalesProductTableModel salesProductTableModel;

    private List<SalesProduct> salesProducts;

    public SalesProductViewCask(RedTapeAgent salesProductOperation, RedTapeWorker redTapeWorker) {
        initComponents();
        this.redTapeAgent = salesProductOperation;
        this.redTapeWorker = redTapeWorker;
        salesProductTableModel = new SalesProductTableModel();
        reloadListData();

    }

    public SalesProductViewCask() {
        this(lookup(RedTapeAgent.class), lookup(RedTapeWorker.class));
    }

    private void reloadListData() {
        salesProducts = redTapeAgent.findAll(SalesProduct.class);
        salesProductTableModel = new SalesProductTableModel();
        for (SalesProduct salesProduct : salesProducts) {
            salesProductTableModel.add(salesProduct);
        }
        productTable.setModel(salesProductTableModel);
        salesProductTableModel.setTable(productTable);
    }

    /** Creates new form SalesProductViewCask */
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        createButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        createButton.setText("Erstellen");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        editButton.setText("Preis Editieren");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Löschen");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteButton)
                .addContainerGap())
        );

        productTable.setModel(new javax.swing.table.DefaultTableModel(
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
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(productTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        try {
            String showInputDialog = JOptionPane.showInputDialog("Bitte Artikelnummer zum erstellen eines Produktes eingeben.");
            if ( showInputDialog == null || showInputDialog.trim().isEmpty() ) return;
            redTapeWorker.createSalesProduct(showInputDialog);
            reloadListData();
        } catch (HeadlessException | UserInfoException ex) {
            Ui.handle(ex);
        }

    }//GEN-LAST:event_createButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        if ( productTable.getSelectedRow() == -1 ) return;

        SalesProduct sp = salesProductTableModel.getLines().get(productTable.getSelectedRow());

        SalesProductUpdateCask pbuc = new SalesProductUpdateCask(sp);

        OkCancelDialog<SalesProductUpdateCask> dialog = new OkCancelDialog<>(SwingUtilities.getWindowAncestor(this), "Editieren eines  SalesProduct", pbuc);
        dialog.setVisible(true);

        if ( dialog.isCancel() ) return;
        sp.setPrice(dialog.getSubContainer().getSalesProduct().getPrice());
        redTapeAgent.merge(sp);
        reloadListData();

    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if ( productTable.getSelectedRow() == -1 ) return;
        redTapeAgent.remove(salesProductTableModel.getLines().get(productTable.getSelectedRow()));
        reloadListData();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void productTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTableMouseClicked
        String s = (String)productTable.getValueAt(productTable.getSelectedRow(), 0);
        SalesProduct salesProduct = null;
        for (SalesProduct p : salesProducts) {
            if ( p.getPartNo().equals(s) ) salesProduct = p;
        }
        if ( evt.getClickCount() > 1 ) {
            HtmlDialog dialog = new HtmlDialog(SwingUtilities.getWindowAncestor(this), Dialog.ModalityType.MODELESS);
            dialog.setText(salesProduct.toHtml());
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_productTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable productTable;
    // End of variables declaration//GEN-END:variables
}
