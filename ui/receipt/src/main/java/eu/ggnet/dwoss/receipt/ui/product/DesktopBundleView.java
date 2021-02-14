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
package eu.ggnet.dwoss.receipt.ui.product;

import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.ui.ProductUiBuilder;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.receipt.ui.unit.chain.Chains;
import eu.ggnet.dwoss.receipt.ui.unit.chain.partno.ProductSpecMatches;
import eu.ggnet.dwoss.receipt.ui.unit.model.MetaValue;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.saft.core.Saft;

import static eu.ggnet.dwoss.core.common.values.ProductGroup.DESKTOP;
import static eu.ggnet.dwoss.core.common.values.ProductGroup.MONITOR;
import static eu.ggnet.saft.core.ui.UiParent.of;

/**
 * Ui for the Desktop Bundle.
 * <p/>
 * @author pascal.perau, oliver.guenther
 */
public class DesktopBundleView extends AbstractView {

    private final static Logger L = LoggerFactory.getLogger(DesktopBundleView.class);

    private final ProductUiBuilder productSupport;

    private final SpecAgent specAgent;

    private final MetaValue<String> partNo1;

    private final MetaValue<String> partNo2;

    private final List<ChainLink<String>> product1Chain;

    private final List<ChainLink<String>> product2Chain;

    private final ProductGroup mustGroup1;

    private final ProductGroup mustGroup2;

    private TradeName brand;

    private DesktopBundle spec;

    private ProductModel model;

    private long gtin;

    @Inject
    private Saft saft;

    @Inject
    private ProductUiBuilder productUiBuilder;

    public DesktopBundleView() {
        initComponents();
        this.productSupport = new ProductUiBuilder();
        this.specAgent = Dl.remote().lookup(SpecAgent.class);
        this.mustGroup1 = DESKTOP;
        this.mustGroup2 = MONITOR;
        product1Chain = new ArrayList<>();
        product2Chain = new ArrayList<>();
        partNo1 = new MetaValue<>();
        partNo2 = new MetaValue<>();
        partNo1.setChain(product1Chain);
        partNo2.setChain(product2Chain);
    }

    @Override
    public void accept(SpecAndModel sam) {

        brand = sam.model().getFamily().getSeries().getBrand();
        model = sam.model();
        gtin = sam.gtin();

        // TODO: verify, that this works. If the partNo validator works, we are fine.
        product1Chain.clear();
        product2Chain.clear();
        product1Chain.addAll(Chains.getInstance(brand.getManufacturer())
                .newPartNoChain(specAgent, Dl.local().lookup(CachedMandators.class).loadContractors().allowedBrands()));
        product2Chain.addAll(Chains.getInstance(brand.getManufacturer())
                .newPartNoChain(specAgent, Dl.local().lookup(CachedMandators.class).loadContractors().allowedBrands()));
        // Adding a extra enforcer of brand and product group.
        product1Chain.add(new ProductSpecMatches(specAgent, brand, mustGroup1));
        product2Chain.add(new ProductSpecMatches(specAgent, brand, mustGroup2));

        DesktopBundle bundle = (DesktopBundle)Objects.requireNonNull(sam, "sam must not be null").spec();
        this.spec = Objects.requireNonNull(bundle);
        if ( bundle.getDesktop() != null ) {
            partNo1.setValue(bundle.getDesktop().getPartNo());
            validateAndUpdateDesktop();
        }
        if ( bundle.getMonitor() != null ) {
            partNo2.setValue(bundle.getMonitor().getPartNo());
            validateAndUpdateMonitor();
        }
    }

    @Override
    public SpecAndModel getResult() {
        return new SpecAndModel(spec, model, gtin);
    }

    private void updateActions() {
        // TODO: Disable/Enable the OK Button based on the Validation.
    }

    private void updateValidationStatus() {
        desktopPartNoField.setForeground(partNo1.getSurvey().getStatus().color);
        desktopPartNoField.setToolTipText(partNo1.getSurvey().getMessage());
        monitorPartNoField.setForeground(partNo2.getSurvey().getStatus().color);
        monitorPartNoField.setToolTipText(partNo2.getSurvey().getMessage());
    }

    private void updateView() {
        if ( spec == null ) throw new NullPointerException("The DesktopBundleSpec instace is null, impossible");
        if ( !Objects.equals(desktopPartNoField.getText(), partNo1.getValue()) ) desktopPartNoField.setText(partNo1.getValue());
        if ( !Objects.equals(monitorPartNoField.getText(), partNo2.getValue()) ) monitorPartNoField.setText(partNo2.getValue());
        desktopField.setText(SpecFormater.toName(spec.getDesktop()));
        monitorField.setText(SpecFormater.toName(spec.getMonitor()));
    }

    private void validateAndUpdateDesktop() {
        spec.setDesktop(validatePartNoAndLoad(partNo1));
        updateActions();
        updateView();
    }

    private void validateAndUpdateMonitor() {
        spec.setMonitor(validatePartNoAndLoad(partNo2));
        updateActions();
        updateView();
    }

    private ProductSpec validatePartNoAndLoad(MetaValue<String> partNo) {
        L.debug("Validating partNo : {}", partNo.getValue());
        partNo.getSurvey().validating("Wert wird geprüft");
        updateValidationStatus();

        ChainLink.Result<String> result = Chains.execute(partNo.getChain(), partNo.getValue());

        L.debug("After Chain : {}", result);
        partNo.getSurvey().setStatus(result.valid, result.message);
        updateValidationStatus();

        partNo.setValue(result.value);
        L.debug("After Chain2 : {}", result);

        ProductSpec spec = null;
        if ( partNo.getSurvey().isOkOrWarn() ) {
            L.debug("Loading Details for PartNo: {}", partNo.getValue());
            spec = specAgent.findProductSpecByPartNoEager(partNo.getValue());
        }
        L.debug("Completing, after update : {}", partNo.getValue());
        return spec;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bundleDesktopPanel = new javax.swing.JPanel();
        desktopEditButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        desktopPartNoField = new javax.swing.JTextField();
        desktopField = new javax.swing.JTextField();
        bundleMonitorPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        monitorPartNoField = new javax.swing.JTextField();
        monitorEditButton = new javax.swing.JButton();
        monitorField = new javax.swing.JTextField();

        bundleDesktopPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), java.awt.Color.black), "Bundle: Desktop"));
        bundleDesktopPanel.setRequestFocusEnabled(false);

        desktopEditButton.setText("ändern");
        desktopEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopEditButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Art.-Nr:");

        desktopPartNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                desktopPartNoFieldFocusLost(evt);
            }
        });

        desktopField.setEditable(false);

        javax.swing.GroupLayout bundleDesktopPanelLayout = new javax.swing.GroupLayout(bundleDesktopPanel);
        bundleDesktopPanel.setLayout(bundleDesktopPanelLayout);
        bundleDesktopPanelLayout.setHorizontalGroup(
            bundleDesktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bundleDesktopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bundleDesktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bundleDesktopPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(38, 38, 38)
                        .addComponent(desktopPartNoField, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(desktopEditButton))
                    .addComponent(desktopField))
                .addContainerGap())
        );
        bundleDesktopPanelLayout.setVerticalGroup(
            bundleDesktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bundleDesktopPanelLayout.createSequentialGroup()
                .addGroup(bundleDesktopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(desktopPartNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(desktopEditButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(desktopField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bundleMonitorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), java.awt.Color.black), "Bundle: Monitor"));
        bundleMonitorPanel.setPreferredSize(new java.awt.Dimension(400, 150));

        jLabel4.setText("Art.-Nr:");

        monitorPartNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                monitorPartNoFieldFocusLost(evt);
            }
        });

        monitorEditButton.setText("ändern");
        monitorEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monitorEditButtonActionPerformed(evt);
            }
        });

        monitorField.setEditable(false);

        javax.swing.GroupLayout bundleMonitorPanelLayout = new javax.swing.GroupLayout(bundleMonitorPanel);
        bundleMonitorPanel.setLayout(bundleMonitorPanelLayout);
        bundleMonitorPanelLayout.setHorizontalGroup(
            bundleMonitorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bundleMonitorPanelLayout.createSequentialGroup()
                .addGroup(bundleMonitorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bundleMonitorPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(40, 40, 40)
                        .addComponent(monitorPartNoField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(monitorEditButton))
                    .addGroup(bundleMonitorPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(monitorField)))
                .addContainerGap())
        );
        bundleMonitorPanelLayout.setVerticalGroup(
            bundleMonitorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bundleMonitorPanelLayout.createSequentialGroup()
                .addGroup(bundleMonitorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monitorPartNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monitorEditButton)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(monitorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bundleDesktopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bundleMonitorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bundleDesktopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bundleMonitorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void desktopEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopEditButtonActionPerformed
        partNo1.setValue(desktopPartNoField.getText());
        productUiBuilder.createOrEditPart(() -> new SimpleView.CreateOrEdit(brand.getManufacturer(), partNo1.getValue(), new SimpleView.Enforce(brand, mustGroup1)), of(this))
                .thenAccept(p -> validateAndUpdateDesktop())
                .handle(saft.handler(this));
    }//GEN-LAST:event_desktopEditButtonActionPerformed

    private void monitorEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monitorEditButtonActionPerformed
        partNo2.setValue(monitorPartNoField.getText());
        productUiBuilder.createOrEditPart(() -> new SimpleView.CreateOrEdit(brand.getManufacturer(), partNo2.getValue(), new SimpleView.Enforce(brand, mustGroup2)), of(this))
                .thenAccept(p -> validateAndUpdateMonitor())
                .handle(saft.handler(this));
    }//GEN-LAST:event_monitorEditButtonActionPerformed

    private void desktopPartNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_desktopPartNoFieldFocusLost
        partNo1.setValue(desktopPartNoField.getText());
        validateAndUpdateDesktop();
    }//GEN-LAST:event_desktopPartNoFieldFocusLost

    private void monitorPartNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_monitorPartNoFieldFocusLost
        partNo2.setValue(monitorPartNoField.getText());
        validateAndUpdateMonitor();
    }//GEN-LAST:event_monitorPartNoFieldFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bundleDesktopPanel;
    private javax.swing.JPanel bundleMonitorPanel;
    private javax.swing.JButton desktopEditButton;
    private javax.swing.JTextField desktopField;
    private javax.swing.JTextField desktopPartNoField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton monitorEditButton;
    private javax.swing.JTextField monitorField;
    private javax.swing.JTextField monitorPartNoField;
    // End of variables declaration//GEN-END:variables

}
