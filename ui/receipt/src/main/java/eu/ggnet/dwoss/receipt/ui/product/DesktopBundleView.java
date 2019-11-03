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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.swing.IPreClose;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ui.UiProductSupport;
import eu.ggnet.dwoss.receipt.ui.unit.UnitModel;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.receipt.ui.unit.chain.Chains;
import eu.ggnet.dwoss.receipt.ui.unit.chain.partno.ProductSpecMatches;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 * Ui for the Desktop Bundle.
 * <p/>
 * @author pascal.perau, oliver.guenther
 */
public class DesktopBundleView extends AbstractView<DesktopBundle> implements IPreClose {

    private final static Logger L = LoggerFactory.getLogger(DesktopBundleView.class);

    private final UiProductSupport productSupport;

    private final SpecAgent specAgent;

    private final UnitModel.MetaValue<String> partNo1 = new UnitModel.MetaValue<>();

    private final UnitModel.MetaValue<String> partNo2 = new UnitModel.MetaValue<>();

    private final List<ChainLink<String>> product1Chain;

    private final List<ChainLink<String>> product2Chain;

    private final TradeName mustBrand;

    private final ProductGroup mustGroup1;

    private final ProductGroup mustGroup2;

    private DesktopBundle spec;

    public DesktopBundleView(TradeName mode,
                             TradeName mustBrand, ProductGroup mustGroup1, ProductGroup mustGroup2) {
        this(Dl.local().lookup(CachedMandators.class), Dl.remote().lookup(SpecAgent.class), Dl.remote().lookup(ProductProcessor.class), mode, mustBrand, mustGroup1, mustGroup2);
    }

    public DesktopBundleView(Mandators mandatorSupporter, SpecAgent specAgent, ProductProcessor productProcessor,
                             TradeName mode,
                             TradeName mustBrand, ProductGroup mustGroup1, ProductGroup mustGroup2) {
        initComponents();
        this.productSupport = new UiProductSupport();
        this.specAgent = specAgent;
        this.mustBrand = mustBrand;
        this.mustGroup1 = mustGroup1;
        this.mustGroup2 = mustGroup2;
        // The Load here is so wrong. But what actually is right in Bunldes :-(
        product1Chain = new ArrayList<>(Chains.getInstance(mode).newPartNoChain(specAgent, mandatorSupporter.loadContractors().allowedBrands()));
        product2Chain = new ArrayList<>(product1Chain);
        // Adding a extra enforcer of brand and product group.
        product1Chain.add(new ProductSpecMatches(specAgent, mustBrand, mustGroup1));
        product2Chain.add(new ProductSpecMatches(specAgent, mustBrand, mustGroup2));
        partNo1.setChain(product1Chain);
        partNo2.setChain(product2Chain);
    }

    @Override
    public void setSpec(DesktopBundle bundle) {
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
    public DesktopBundle getSpec() {
        return spec;
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

    private ProductSpec validatePartNoAndLoad(UnitModel.MetaValue<String> partNo) {
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
                        .addComponent(desktopPartNoField, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
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
            .addComponent(bundleMonitorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bundleDesktopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bundleMonitorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void desktopEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopEditButtonActionPerformed
        try {
            partNo1.setValue(desktopPartNoField.getText());
            productSupport.createOrEditPart(mustBrand.getManufacturer(), partNo1.getValue(), mustBrand, mustGroup1, parent);
            validateAndUpdateDesktop();
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
    }//GEN-LAST:event_desktopEditButtonActionPerformed

    private void monitorEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monitorEditButtonActionPerformed
        try {
            partNo2.setValue(monitorPartNoField.getText());
            productSupport.createOrEditPart(mustBrand.getManufacturer(), partNo2.getValue(), mustBrand, mustGroup2, parent);
            validateAndUpdateMonitor();
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
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

    @Override
    public long getGtin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGtin(long gtin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
