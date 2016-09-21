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
package eu.ggnet.dwoss.redtape.position;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.*;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.saft.api.ui.OnOk;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.authorisation.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_POSITION_WITH_EXISTING_DOCUMENT;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRICE_OF_UNITS_AND_PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.PositionType.*;

import eu.ggnet.dwoss.util.*;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
@Title("Position bearbeiten")
public class PositionUpdateCask extends javax.swing.JPanel implements OnOk, Consumer<Position> {

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
            double value = MathUtil.roundedApply(s, taxed - 1, 0.);
            return df.format(value);
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

    private Converter<Double, String> stringConverter = new CurrencyConverter();

    private Converter<Double, String> taxedConverter = new CurrencyConverter(GlobalConfig.TAX);

    private Position position;

    private double price;

    private String description;

    private int preDecimal;

    private int postDecimal;

    private double amount;

    private String positionName;

    private int bookingAccount;

    public static final String PROP_BOOKINGACCOUNT = "bookingAccount";

    public static final String PROP_POSITIONNAME = "positionName";

    public static final String PROP_AMOUNT = "amount";

    public static final String PROP_PREDECIMAL = "preDecimal";

    public static final String PROP_DESCRIPTION = "description";

    public static final String PROP_PRICE = "price";

    public static final String PROP_POSTDECIMAL = "postDecimal";

    private Guardian accessCos;

    /**
     * Creates new form PositionUpdateCask.
     */
    public PositionUpdateCask() {
        initComponents();
        ((JSpinner.DefaultEditor)postDecimalSpinner.getEditor()).getTextField().setEditable(false);
//        ((JSpinner.DefaultEditor)preDecimalSpinner.getEditor()).getTextField().setEditable(false);
        PostLedger postLedger = lookup(MandatorSupporter.class).loadPostLedger();
        List bookingAccounts = new ArrayList();
        bookingAccounts.add(postLedger.get(SERVICE).orElse(-1));
        bookingAccounts.addAll(postLedger.getPossible(SERVICE).orElse(Collections.EMPTY_LIST));
        bookingAccountBox.setModel(new DefaultComboBoxModel(bookingAccounts.toArray()));

    }

    public Position getPosition() {
        return position;
    }

    @Override
    public void accept(Position position) {
        if ( position == null ) return;
        this.position = position;
        this.setPositionName(position.getName());
        this.setPrice(position.getPrice());
        this.setDescription(position.getDescription());
        this.setAmount(position.getAmount());
        this.setPreDecimal((int)(position.getAmount() - (position.getAmount() % 1)));
        this.setPostDecimal((int)((position.getAmount() % 1) * 100));
        this.setBookingAccount(position.getBookingAccount());

        PostLedger postLedger = lookup(MandatorSupporter.class).loadPostLedger();
        List bookingAccounts = new ArrayList();
        bookingAccounts.add(postLedger.get(position.getType()).orElse(-1));
        bookingAccounts.addAll(postLedger.getPossible(position.getType()).orElse(Collections.EMPTY_LIST));
        bookingAccountBox.setModel(new DefaultComboBoxModel(bookingAccounts.toArray()));

        System.out.println("Pre: " + this.getPreDecimal());
        System.out.println("Post: " + this.getPostDecimal());

        this.accessCos = lookup(Guardian.class);

        if ( position.getDocument() != null && EnumSet.of(DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO).contains(position.getDocument().getType()) ) {
            disableComponents(preDecimalSpinner, postDecimalSpinner, nameArea, bookingAccountBox, priceField, afterTaxPriceField, descriptionArea);
            accessCos.add(priceSumField, UPDATE_POSITION_WITH_EXISTING_DOCUMENT);
            accessCos.add(afterTaxSumField, UPDATE_POSITION_WITH_EXISTING_DOCUMENT);
            accessCos.add(priceField, UPDATE_POSITION_WITH_EXISTING_DOCUMENT);
            accessCos.add(afterTaxPriceField, UPDATE_POSITION_WITH_EXISTING_DOCUMENT);
        } else {
            if ( position.getType() == PRODUCT_BATCH || position.getType() == UNIT ) {
                disableComponents(postDecimalSpinner);
                accessCos.add(priceField, UPDATE_PRICE_OF_UNITS_AND_PRODUCT_BATCH);
                accessCos.add(afterTaxPriceField, UPDATE_PRICE_OF_UNITS_AND_PRODUCT_BATCH);
            }
            if ( position.getType() == UNIT ) {
                disableComponents(preDecimalSpinner);
                bookingAccountBox.setEnabled(true);
            }
            if ( position.getType() == COMMENT ) {
                disableComponents(priceField, priceSumField, afterTaxPriceField, afterTaxSumField, preDecimalSpinner, postDecimalSpinner);
            }
            if ( position.getType() == SHIPPING_COST ) {
                disableComponents(nameArea, afterTaxPriceField, afterTaxSumField, preDecimalSpinner, postDecimalSpinner);
            }

            if ( EnumSet.of(COMMENT, SERVICE, PRODUCT_BATCH).contains(position.getType()) ) {
                nameArea.setEditable(true);
                if ( position.getType() == SERVICE ) bookingAccountBox.setEnabled(true);
            }
        }
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
    public double getPrice() {
        return price;
    }

    /**
     * Set the value of retailerPrice
     *
     * @param price new value of retailerPrice
     */
    public void setPrice(double price) {
        double oldPrice = this.price;
        this.price = price;
        firePropertyChange(PROP_PRICE, oldPrice, price);
    }

    public int getPreDecimal() {
        return preDecimal;
    }

    public void setPreDecimal(int preDecimal) {
        int oldPreDecimal = this.preDecimal;
        this.preDecimal = preDecimal;
        firePropertyChange(PROP_PREDECIMAL, oldPreDecimal, preDecimal);
        this.setAmount(preDecimal + (amount % 1));
    }

    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
    }

    /**
     * Get the value of decimal
     *
     * @return the value of decimal
     */
    public int getPostDecimal() {
        return postDecimal;
    }

    /**
     * Set the value of decimal
     *
     * @param postDecimal new value of decimal
     */
    public void setPostDecimal(int postDecimal) {
        int oldPostDecimal = this.postDecimal;
        this.postDecimal = postDecimal;
        firePropertyChange(PROP_POSTDECIMAL, oldPostDecimal, postDecimal);

        this.setAmount((amount - (amount % 1)) + postDecimal / 100d);
    }

    /**
     * Get the value of amount
     *
     * @return the value of amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the value of amount
     *
     * @param amount new value of amount
     */
    public void setAmount(double amount) {
        double oldAmount = this.amount;
        this.amount = amount;
        firePropertyChange(PROP_AMOUNT, oldAmount, amount);
    }

    /**
     * Get the value of positionName
     *
     * @return the value of positionName
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Set the value of positionName
     *
     * @param positionName new value of positionName
     */
    public void setPositionName(String positionName) {
        String oldPositionName = this.positionName;
        this.positionName = positionName;
        firePropertyChange(PROP_POSITIONNAME, oldPositionName, positionName);
    }

    public int getBookingAccount() {
        return bookingAccount;
    }

    /**
     * Set the value of bookingAccount
     *
     * @param bookingAccount new value of bookingAccount
     */
    public void setBookingAccount(int bookingAccount) {
        int oldBookingAccount = this.bookingAccount;
        this.bookingAccount = bookingAccount;
        firePropertyChange(PROP_BOOKINGACCOUNT, oldBookingAccount, bookingAccount);
    }

    private void disableComponents(Component... c) {
        for (Component component : c) {
            component.setEnabled(false);
        }
    }

    @Override
    public boolean onOk() {
        if ( StringUtils.isBlank(description) ) {
            Alert.show(this, "Beschreibung darf nich leer sein.");
            return false;
        }
        if ( StringUtils.isBlank(positionName) ) {
            Alert.show(this, "Name darf nich leer sein.");
            return false;
        }
        position.setDescription(description);
        position.setName(positionName);
        position.setAmount(amount);
        position.setTax(GlobalConfig.TAX);
        position.setBookingAccount(bookingAccount);
        try {
            position.setPrice(Double.valueOf(priceField.getText().replace(",", ".")));
            position.setAfterTaxPrice(Double.valueOf(afterTaxPriceField.getText().replace(",", ".")));
        } catch (NumberFormatException e) {
            Alert.show(this, "Preisformat ist nicht lesbar");
        }
        for (Binding binding : bindingGroup.getBindings()) {
            binding.save();
        }
        if ( position.getPrice() == 0 && position.getType() != PositionType.COMMENT ) {
            // TODO: We need something like Alert. e.g. Question.ask
            return JOptionPane.showConfirmDialog(this, "Preis ist 0, trotzdem fortfahren?", "Position bearbeiten", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0;
        }
        for (Component component : this.getComponents()) {
            accessCos.remove(component);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        preDecimalSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        bookingAccountBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        afterTaxPriceField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        nameArea = new javax.swing.JTextArea();
        priceSumField = new javax.swing.JTextField();
        afterTaxSumField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        postDecimalSpinner = new javax.swing.JSpinner();

        setMinimumSize(new java.awt.Dimension(328, 572));

        jLabel1.setText("Positionsname/Überschrift:");

        jLabel2.setText("Positionstyp:");

        jLabel3.setText("Menge / Zeit:");
        jLabel3.setToolTipText("<html>Zeit in decimalformat <br /> z.B. 1.35 = 1 stunde 35 minuten</html>");

        preDecimalSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 0, null, 1));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${preDecimal}"), preDecimalSpinner, org.jdesktop.beansbinding.BeanProperty.create("value"), "amountBinding");
        bindingGroup.addBinding(binding);

        jLabel4.setText("Buchungskonto:");

        bookingAccountBox.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${bookingAccount}"), bookingAccountBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "bookingAccountBinding");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        bookingAccountBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookingAccountBoxActionPerformed(evt);
            }
        });

        jLabel5.setText("Nettopreis:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${price}"), priceField, org.jdesktop.beansbinding.BeanProperty.create("text"), "priceBinding");
        binding.setConverter(getStringConverter());
        bindingGroup.addBinding(binding);

        jLabel7.setText("Bruttopreis:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${price}"), afterTaxPriceField, org.jdesktop.beansbinding.BeanProperty.create("text"), "afterTaxPriceBinding");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        jLabel8.setText("Beschreibung:");

        descriptionArea.setColumns(20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(5);
        descriptionArea.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${description}"), descriptionArea, org.jdesktop.beansbinding.BeanProperty.create("text"), "positionDescriptionBinding");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(descriptionArea);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${position.type.name}"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("text"), "positionTypeBinding");
        bindingGroup.addBinding(binding);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        nameArea.setEditable(false);
        nameArea.setColumns(20);
        nameArea.setLineWrap(true);
        nameArea.setRows(5);
        nameArea.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${positionName}"), nameArea, org.jdesktop.beansbinding.BeanProperty.create("text"), "nameBinding");
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(nameArea);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${amount * price  }"), priceSumField, org.jdesktop.beansbinding.BeanProperty.create("text"), "priceSumBinding");
        binding.setConverter(getStringConverter());
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${amount *price }"), afterTaxSumField, org.jdesktop.beansbinding.BeanProperty.create("text"), "afterTaxSumBinding");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setToolTipText("");

        jLabel6.setText("Summe");

        jLabel10.setText(",");

        postDecimalSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 75, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${postDecimal}"), postDecimalSpinner, org.jdesktop.beansbinding.BeanProperty.create("value"), "decimalBounding");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(bookingAccountBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(109, 109, 109))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(preDecimalSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(postDecimalSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addGap(68, 68, 68))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(priceField)
                                    .addComponent(afterTaxPriceField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(priceSumField, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(afterTaxSumField)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(bookingAccountBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(preDecimalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(postDecimalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(priceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(afterTaxPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(priceSumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(afterTaxSumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void bookingAccountBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_bookingAccountBoxActionPerformed
        setBookingAccount((int)bookingAccountBox.getSelectedItem());
    }//GEN-LAST:event_bookingAccountBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JTextField afterTaxPriceField;
    javax.swing.JTextField afterTaxSumField;
    javax.swing.JComboBox bookingAccountBox;
    javax.swing.JTextArea descriptionArea;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel10;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel4;
    javax.swing.JLabel jLabel5;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JLabel jLabel8;
    javax.swing.JLabel jLabel9;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JSeparator jSeparator1;
    javax.swing.JTextArea nameArea;
    javax.swing.JSpinner postDecimalSpinner;
    javax.swing.JSpinner preDecimalSpinner;
    javax.swing.JTextField priceField;
    javax.swing.JTextField priceSumField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public String toString() {
        return "PositionUpdateCask{" + "price=" + price + ", description=" + description + ", preDecimal=" + preDecimal + ", postDecimal=" + postDecimal + ", amount=" + amount + ", positionName=" + positionName + '}';
    }
}
