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
package eu.ggnet.dwoss.redtapext.ui.cao.document.position;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.HtmlDialog;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoTableModel;

import static eu.ggnet.dwoss.util.CloseType.*;

/**
 *
 * @author pascal.perau
 */
public class SalesProductChooserCask extends javax.swing.JPanel implements IPreClose {

    private List<SalesProduct> products;

    private SalesProduct product;

    /** Creates new form SalesProductChooserCask */
    public SalesProductChooserCask(List<SalesProduct> products) {
        initComponents();

        this.products = products;

        PojoTableModel model = new PojoTableModel(products,
                new PojoColumn("ArtikelNr", false, 50, String.class, "partNo"),
                new PojoColumn("Name", false, 40, String.class, "name"));

        productTable.setModel(model);
        model.setTable(productTable);
    }

    public SalesProduct getProduct() {
        return product;
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == OK && productTable.getSelectedRow() == -1 ) {
            JOptionPane.showMessageDialog(this, "Kein Artikel gewählt");
            return false;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void productTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTableMouseClicked
        String s = (String)productTable.getValueAt(productTable.getSelectedRow(), 0);
        for (SalesProduct p : products) {
            if ( p.getPartNo().equals(s) ) product = p;
        }
        if ( evt.getClickCount()>1) {
            HtmlDialog dialog = new HtmlDialog(SwingUtilities.getWindowAncestor(this), Dialog.ModalityType.MODELESS);
            dialog.setText(product.toHtml());
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_productTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable productTable;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        SalesProduct salesProduct1 = new SalesProduct("AS.ASASD.ASD", "Test SalesProduct", 10D, 1, "SalesProduct Test Descritpion");
        SalesProduct salesProduct2 = new SalesProduct("AS.1234.ASD", "Test SalesProduct2", 1D, 2, "SalesProduct Test Descritpion2");

        List<SalesProduct> products = new ArrayList<>();
        products.add(salesProduct1);
        products.add(salesProduct2);
        SalesProductChooserCask view = new SalesProductChooserCask(products);
        OkCancelDialog<SalesProductChooserCask> dialog = new OkCancelDialog<>("Sample", view);
        dialog.setVisible(true);
        System.out.println(view.getProduct());
    }
}