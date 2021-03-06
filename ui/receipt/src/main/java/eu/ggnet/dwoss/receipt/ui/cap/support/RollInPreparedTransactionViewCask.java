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
package eu.ggnet.dwoss.receipt.ui.cap.support;

import java.util.List;
import java.util.function.Consumer;

import eu.ggnet.dwoss.core.widget.saft.VetoableOnOk;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.format.StockTransactionFormater;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author oliver.guenther
 */
public class RollInPreparedTransactionViewCask extends javax.swing.JPanel implements Consumer<List<StockTransaction>>, ResultProducer<List<StockTransaction>>, VetoableOnOk {

    private List<StockTransaction> stockTransactions;

    /** Creates new form HtmlPanel */
    public RollInPreparedTransactionViewCask() {
        initComponents();
    }

    public List<StockTransaction> getStockTransactions() {
        return stockTransactions;
    }
    
    @Override
    public void accept(List<StockTransaction> sts) {
        this.stockTransactions = sts;
        htmlPane.setText(StockTransactionFormater.toHtml(sts));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        htmlPane = new javax.swing.JTextPane();

        htmlPane.setContentType("text/html"); // NOI18N
        htmlPane.setEditable(false);
        jScrollPane2.setViewportView(htmlPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane htmlPane;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public List<StockTransaction> getResult() {
        return stockTransactions;
    }

    @Override
    public boolean mayClose() {
        return true;
    }

}
