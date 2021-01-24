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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.core.widget.saft.VetoableOnOk;
import eu.ggnet.dwoss.core.widget.swing.PojoColumn;
import eu.ggnet.dwoss.core.widget.swing.PojoTableModel;
import eu.ggnet.dwoss.redtape.ee.entity.SalesProduct;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author pascal.perau
 */
public class SalesProductChooserCask extends javax.swing.JPanel implements Consumer<List<SalesProduct>>, ResultProducer<SalesProduct>, VetoableOnOk {

    private List<SalesProduct> products;

    private SalesProduct product;

    /** Creates new form SalesProductChooserCask */
    public SalesProductChooserCask() {
        initComponents();
    }

    @Override
    public void accept(List<SalesProduct> products) {
        this.products = Objects.requireNonNull(products, "products must not be null");

        PojoTableModel model = new PojoTableModel(products,
                new PojoColumn("ArtikelNr", false, 50, String.class, "partNo"),
                new PojoColumn("Name", false, 40, String.class, "name"));

        productTable.setModel(model);
        model.setTable(productTable);
    }

    @Override
    public SalesProduct getResult() {
        return product;
    }

    @Override
    public boolean mayClose() {
        if ( productTable.getSelectedRow() == -1 ) {
            Ui.build(this).title("Keine Auswahl").alert("Keinen Artikel ausgewählt");
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
        if ( evt.getClickCount() > 1 ) {
            Ui.build(this).title("Product: " + product.getPartNo()).fx().show(() -> Css.toHtml5WithStyle(product.toHtml()), () -> new HtmlPane());
        }
    }//GEN-LAST:event_productTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable productTable;
    // End of variables declaration//GEN-END:variables

}
