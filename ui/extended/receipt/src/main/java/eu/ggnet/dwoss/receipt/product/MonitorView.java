/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.receipt.product;

import java.awt.BorderLayout;

import javax.swing.UIManager;

import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.rules.ProductGroup;

import eu.ggnet.dwoss.spec.entity.Monitor;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.dwoss.util.OkCancelDialog;

public class MonitorView extends AbstractView<Monitor> implements IPreClose {

    private BasicView basicView;

    private DisplayPanel displayView;

    /**
     * Creates new form PartsMonitorPanel
     */
    public MonitorView() {
        initComponents();
        displayView = new DisplayPanel(ProductGroup.MONITOR);
        displayPanel.add(displayView, BorderLayout.CENTER);

        basicView = new BasicView();
        basicViewPanel.add(basicView, BorderLayout.CENTER);
        basicViewPanel.setPreferredSize(basicView.getPreferredSize());
    }

    @Override
    public void setSpec(Monitor monitor) {
        displayView.setDisplay(monitor.getDisplay());
        basicView.setSpec(monitor);
    }

    @Override
    public Monitor getSpec() {
        Monitor monitor = (Monitor)basicView.getSpec();
        monitor.setDisplay(displayView.getDisplay());
        return monitor;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayPanel = new javax.swing.JPanel();
        basicViewPanel = new javax.swing.JPanel();

        displayPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        displayPanel.setLayout(new java.awt.BorderLayout());

        basicViewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        basicViewPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(displayPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
            .addComponent(basicViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(basicViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basicViewPanel;
    private javax.swing.JPanel displayPanel;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        ProductProcessorStub stub = new ProductProcessorStub();
        MonitorView view = new MonitorView();
        view.setSpec(stub.monitor);
        OkCancelDialog<MonitorView> create = new OkCancelDialog("Display Details", view);
        create.setVisible(true);
        System.out.println(create.getSubContainer().getSpec());
        System.exit(0);
    }
}
