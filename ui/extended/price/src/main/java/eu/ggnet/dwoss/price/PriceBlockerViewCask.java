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
package eu.ggnet.dwoss.price;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import org.jdesktop.beansbinding.Converter;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.dwoss.util.OkCancelDialog;

/**
 * Ui for setting on price fixed.
 * <p/>
 * @author bastian.venz
 */
public class PriceBlockerViewCask extends javax.swing.JPanel implements IPreClose {

    private Converter<Double, String> stringConverter = new CurrencyConverter();

    private Converter<Double, String> taxedConverter = new CurrencyConverter(GlobalConfig.TAX);

    private class CurrencyConverter extends Converter<Double, String> {

        private double taxed;

        public CurrencyConverter() {
            this(0);
        }

        public CurrencyConverter(double tax) {
            this.taxed = 1 + tax;
        }

        @Override
        public String convertForward(Double s) {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(s * taxed);
        }

        @Override
        public Double convertReverse(String t) {
            try {
                return Math.round((Double.valueOf(t.replaceAll(",", "\\.")) / taxed) * 100) / 100d;
            } catch (NumberFormatException e) {
                return 0D;
            }
        }
    };

    private double retailerPrice;

    private double customerPrice;

    public static final String PROP_CUSTOMERPRICE = "customerPrice";

    public static final String PROP_RETAILERPRICE = "retailerPrice";

    /** Creates new form PriceBlockerPanel */
    public PriceBlockerViewCask(String unitName, String unitText, double customerPrice, double retailerPrice) {
        initComponents();
        setCustomerPrice(customerPrice);
        setRetailerPrice(retailerPrice);
        unitNameLabel.setText(unitName);
        unitDetailEditorPane.setText(unitText);

    }

    public Converter<Double, String> getTaxedConverter() {
        return taxedConverter;
    }

    public Converter<Double, String> getStringConverter() {
        return stringConverter;
    }

    /**
     * Get the value of retailerPrice
     *
     * @return the value of retailerPrice
     */
    public double getRetailerPrice() {
        return retailerPrice;
    }

    /**
     * Set the value of retailerPrice
     *
     * @param retailerPrice new value of retailerPrice
     */
    public final void setRetailerPrice(double retailerPrice) {
        double oldRetailerPriceBrutto = this.retailerPrice;
        this.retailerPrice = retailerPrice;
        firePropertyChange(PROP_RETAILERPRICE, oldRetailerPriceBrutto, retailerPrice);
    }

    /**
     * Get the value of customerPrice
     *
     * @return the value of customerPrice
     */
    public double getCustomerPrice() {
        return customerPrice;
    }

    /**
     * Set the value of customerPrice
     *
     * @param customerPrice new value of customerPrice
     */
    public final void setCustomerPrice(double customerPrice) {
        double oldcustomerPriceNetto = this.customerPrice;
        this.customerPrice = customerPrice;
        firePropertyChange(PROP_CUSTOMERPRICE, oldcustomerPriceNetto, customerPrice);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        customerPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        customerNetto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        customerBrutto = new javax.swing.JTextField();
        retailerPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        retailerNetto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        retailerBrutto = new javax.swing.JTextField();
        unitNameLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        unitDetailEditorPane = new javax.swing.JEditorPane();

        setAutoscrolls(true);
        setLayout(new java.awt.GridBagLayout());

        customerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Endkunden"));
        customerPanel.setPreferredSize(new java.awt.Dimension(528, 80));
        customerPanel.setRequestFocusEnabled(false);

        jLabel2.setText("Netto");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customerPrice}"), customerNetto, org.jdesktop.beansbinding.BeanProperty.create("text"), "CustomerNettoField");
        binding.setConverter(getStringConverter());
        bindingGroup.addBinding(binding);

        jLabel3.setText("Brutto");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customerPrice}"), customerBrutto, org.jdesktop.beansbinding.BeanProperty.create("text"), "CustomerBruttoField");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout customerPanelLayout = new javax.swing.GroupLayout(customerPanel);
        customerPanel.setLayout(customerPanelLayout);
        customerPanelLayout.setHorizontalGroup(
            customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(customerNetto, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customerBrutto)
                    .addGroup(customerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 215, Short.MAX_VALUE))))
        );
        customerPanelLayout.setVerticalGroup(
            customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerPanelLayout.createSequentialGroup()
                .addGroup(customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerNetto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerBrutto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(customerPanel, gridBagConstraints);

        retailerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Händler"));
        retailerPanel.setPreferredSize(new java.awt.Dimension(528, 80));

        jLabel4.setText("Netto");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${retailerPrice}"), retailerNetto, org.jdesktop.beansbinding.BeanProperty.create("text"), "RetailerNettoField");
        binding.setConverter(getStringConverter());
        bindingGroup.addBinding(binding);

        jLabel5.setText("Brutto");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${retailerPrice}"), retailerBrutto, org.jdesktop.beansbinding.BeanProperty.create("text"), "RetailerBruttoField");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout retailerPanelLayout = new javax.swing.GroupLayout(retailerPanel);
        retailerPanel.setLayout(retailerPanelLayout);
        retailerPanelLayout.setHorizontalGroup(
            retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(retailerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(retailerNetto, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(retailerBrutto)
                    .addGroup(retailerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 215, Short.MAX_VALUE))))
        );
        retailerPanelLayout.setVerticalGroup(
            retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(retailerPanelLayout.createSequentialGroup()
                .addGroup(retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(retailerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(retailerNetto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(retailerBrutto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(retailerPanel, gridBagConstraints);

        unitNameLabel.setText("unitName");
        add(unitNameLabel, new java.awt.GridBagConstraints());

        unitDetailEditorPane.setContentType("text/html"); // NOI18N
        unitDetailEditorPane.setPreferredSize(new java.awt.Dimension(100, 200));
        jScrollPane2.setViewportView(unitDetailEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.CANCEL ) return true;
        if ( retailerBrutto.getText().trim().equals("") || retailerNetto.getText().trim().equals("")
                || customerBrutto.getText().trim().equals("") || customerNetto.getText().trim().equals("") ) {

            JOptionPane.showMessageDialog(this, "Ein Preis ist nicht gesetzt!", "Fehler beim Verifizieren der Preise", JOptionPane.ERROR_MESSAGE);
            return false;

        }
        StringBuilder confirmString = new StringBuilder("Die Momentane Preise sind: (Netto / Brutto)\nEndkunde: ");
        confirmString.append(customerNetto.getText());
        confirmString.append(" / ");
        confirmString.append(customerBrutto.getText());
        confirmString.append("\nHändler: ");

        confirmString.append(retailerNetto.getText());
        confirmString.append(" /");
        confirmString.append(retailerBrutto.getText());

        confirmString.append("\n\nStimmen die Preise so?");

        if ( JOptionPane.showConfirmDialog(this, confirmString.toString(), "Überprüfung der Preise", JOptionPane.YES_NO_OPTION) == 0 ) {
            return true;
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField customerBrutto;
    private javax.swing.JTextField customerNetto;
    private javax.swing.JPanel customerPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField retailerBrutto;
    private javax.swing.JTextField retailerNetto;
    private javax.swing.JPanel retailerPanel;
    private javax.swing.JEditorPane unitDetailEditorPane;
    private javax.swing.JLabel unitNameLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * Testmain
     * <p/>
     * @param args
     */
    public static void main(String[] args) {

        PriceBlockerViewCask pbp = new PriceBlockerViewCask("TestUnit des Testens", "Hier wird getestets\n<b>BLARG</b>", 10d, 15d);
        OkCancelDialog<PriceBlockerViewCask> cancelDialog = new OkCancelDialog<>("Test", pbp);

        cancelDialog.setMinimumSize(pbp.getMinimumSize());
        cancelDialog.setPreferredSize(pbp.getPreferredSize());
        cancelDialog.setVisible(true);
        System.out.println(cancelDialog.getSubContainer().getCustomerPrice());
        System.out.println(cancelDialog.getSubContainer().getRetailerPrice());
        System.exit(0);
    }
}
