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
package eu.ggnet.dwoss.receipt.product;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.UIManager;

import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.rules.ProductGroup;

import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.DisplayAbleDesktop;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.util.OkCancelDialog;

public class DisplayAbleView extends AbstractView<DisplayAbleDesktop> {

    private DesktopView desktopView;

    private DisplayPanel displayView;

    public DisplayAbleView(DesktopView desktopView, DisplayPanel displayView) {
        this.desktopView = desktopView;
        this.displayView = displayView;
        initComponents();
        desktopView.getDisplayViewPanel().add(displayView, BorderLayout.CENTER);
        desktopPlace.add(desktopView, BorderLayout.CENTER);
        desktopPlace.setPreferredSize(desktopView.getPreferredSize());
    }

    /** Creates new form DesktopView */
    public DisplayAbleView(ProductGroup group) {
        this(new DesktopView(group), new DisplayPanel(group));
    }

    public DisplayAbleView(SpecAgent specAgent, ProductGroup group) {
        this(new DesktopView(specAgent, group), new DisplayPanel(group));
    }

    @Override
    public void setParent(Window parent) {
        super.setParent(parent);
        desktopView.setParent(parent);
    }

    @Override
    public void setSpec(DisplayAbleDesktop dad) {
        desktopView.setSpec(dad);
        displayView.setDisplay(dad.getDisplay());
    }

    @Override
    public DisplayAbleDesktop getSpec() {
        DisplayAbleDesktop displayAbleDesktop = (DisplayAbleDesktop)desktopView.getSpec();
        displayAbleDesktop.setDisplay(displayView.getDisplay());
        return displayAbleDesktop;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPlace = new javax.swing.JPanel();

        desktopPlace.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        desktopPlace.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPlace, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPlace, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel desktopPlace;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ProductProcessorStub receipt = new ProductProcessorStub();
        DisplayAbleView view = new DisplayAbleView(receipt.getSpecAgentStub(), ProductGroup.NOTEBOOK);
        view.setSpec(receipt.allInOne);
        OkCancelDialog<DisplayAbleView> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        System.out.println(SpecFormater.toSingleLine(view.getSpec()));
        System.exit(0);
    }

    @Override
    public long getGtin() {
        return desktopView.getGtin();
    }

    @Override
    public void setGtin(long gtin) {
        desktopView.setGtin(gtin);
    }
}
