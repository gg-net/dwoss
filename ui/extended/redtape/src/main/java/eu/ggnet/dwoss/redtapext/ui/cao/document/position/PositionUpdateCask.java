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

import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.*;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.util.TwoDigits;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.VetoableOnOk;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_POSITION_WITH_EXISTING_DOCUMENT;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRICE_OF_UNITS_AND_PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.PositionType.*;
import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class PositionUpdateCask extends javax.swing.JPanel implements Consumer<PositionAndTaxType>, ResultProducer<Position>, VetoableOnOk {

    public class CurrencyConverter extends Converter<Double, String> {

        private double taxed = 0; // 19% are 1.19

        @Override
        public String convertForward(Double s) {
            DecimalFormat df = new DecimalFormat("#.00");
            double value = TwoDigits.roundedApply(s, taxed - 1, 0.);
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

    private final static DecimalFormat TAX = new DecimalFormat("##0 %");

    private final CurrencyConverter stringConverter = new CurrencyConverter();

    private final CurrencyConverter taxedConverter = new CurrencyConverter();

    private Position position;

    private double price;

    private String description;

    private int preDecimal;

    private int postDecimal;

    private double amount;

    private String positionName;

    private Ledger bookingAccount;

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
        bookingAccountBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value instanceof Ledger ) {
                    Ledger l = (Ledger)value;
                    label.setText(l.getValue() + ":" + l.getDescription());
                }
                return label;
            }
        });
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public void accept(PositionAndTaxType posAndTax) {
        if ( posAndTax == null ) return;
        this.position = posAndTax.getPosition();
        PostLedger postLedger = lookup(MandatorSupporter.class).loadPostLedger();
        final List<Ledger> ledgers = postLedger.getAlternatives(position.getType(), posAndTax.getTaxType());
        bookingAccountBox.setModel(new DefaultComboBoxModel(ledgers.toArray()));
        this.positionTypeField.setText(position.getType() != null ? position.getType().getName() : "Nicht angegeben");
        this.taxedConverter.taxed = position.getTax() + 1;
        this.taxField.setText(TAX.format(position.getTax()));
        this.setPositionName(position.getName());
        this.setPrice(position.getPrice());
        this.setDescription(position.getDescription());
        this.setAmount(position.getAmount());
        this.setPreDecimal((int)(position.getAmount() - (position.getAmount() % 1)));
        this.setPostDecimal((int)((position.getAmount() % 1) * 100));

        if ( position.getBookingAccount().isPresent() ) { // Remapping of the ledger.
            if ( !ledgers.contains(position.getBookingAccount().get()) ) {
                UiAlert.show(this, "Buchungskonto " + position.getBookingAccount().get() + " nicht für diese Position und Steuer konfiguriert. Wurde auf Standard gesetzt");
                if ( !ledgers.isEmpty() ) this.setBookingAccount(ledgers.get(0));
            } else {
                this.setBookingAccount(ledgers.get(ledgers.indexOf(position.getBookingAccount().get())));
            }
        }

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

    public Ledger getBookingAccount() {
        return bookingAccount;
    }

    /**
     * Set the value of bookingAccount
     *
     * @param bookingAccount new value of bookingAccount
     */
    public void setBookingAccount(Ledger bookingAccount) {
        Ledger oldBookingAccount = this.bookingAccount;
        this.bookingAccount = bookingAccount;
        firePropertyChange(PROP_BOOKINGACCOUNT, oldBookingAccount, bookingAccount);
    }

    private void disableComponents(Component... c) {
        for (Component component : c) {
            component.setEnabled(false);
        }
    }

    @Override
    public boolean mayClose() {
        if ( StringUtils.isBlank(description) ) {
            UiAlert.show(this, "Beschreibung darf nich leer sein.");
            return false;
        }
        if ( StringUtils.isBlank(positionName) ) {
            UiAlert.show(this, "Name darf nich leer sein.");
            return false;
        }
        try {
            position.setPrice(Double.valueOf(priceField.getText().replace(",", ".")));
        } catch (NumberFormatException e) {
            UiAlert.show(this, "Preisformat ist nicht lesbar");
        }
        for (Binding binding : bindingGroup.getBindings()) {
            binding.save();
        }
        position.setDescription(description);
        position.setName(positionName);
        position.setAmount(amount);
        position.setBookingAccount(bookingAccount);
        if ( position.getPrice() == 0 && position.getType() != PositionType.COMMENT ) {
            // TODO: We need something like Alert. e.g. Question.ask
            return JOptionPane.showConfirmDialog(this, "Preis ist 0, trotzdem fortfahren?", "Position bearbeiten", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0;
        }
        for (Component component : this.getComponents()) {
            accessCos.remove(component);
        }
        return true;
    }

    @Override
    public Position getResult() {
        return position;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new BindingGroup();

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        preDecimalSpinner = new JSpinner();
        jLabel4 = new JLabel();
        bookingAccountBox = new JComboBox();
        jLabel5 = new JLabel();
        priceField = new JTextField();
        jLabel7 = new JLabel();
        afterTaxPriceField = new JTextField();
        jLabel8 = new JLabel();
        jScrollPane1 = new JScrollPane();
        descriptionArea = new JTextArea();
        positionTypeField = new JLabel();
        jScrollPane2 = new JScrollPane();
        nameArea = new JTextArea();
        priceSumField = new JTextField();
        afterTaxSumField = new JTextField();
        jSeparator1 = new JSeparator();
        jLabel6 = new JLabel();
        jLabel10 = new JLabel();
        postDecimalSpinner = new JSpinner();
        taxLabel = new JLabel();
        taxField = new JLabel();

        setMinimumSize(new Dimension(328, 572));

        jLabel1.setText("Positionsname/Überschrift:");

        jLabel2.setText("Positionstyp:");

        jLabel3.setText("Menge / Zeit:");
        jLabel3.setToolTipText("<html>Zeit in decimalformat <br /> z.B. 1.35 = 1 stunde 35 minuten</html>");

        preDecimalSpinner.setModel(new SpinnerNumberModel(1, 0, null, 1));

        Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${preDecimal}"), preDecimalSpinner, BeanProperty.create("value"), "amountBinding");
        bindingGroup.addBinding(binding);

        jLabel4.setText("Buchungskonto:");

        bookingAccountBox.setEnabled(false);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${bookingAccount}"), bookingAccountBox, BeanProperty.create("selectedItem"), "bookingAccountBinding");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        jLabel5.setText("Nettopreis:");

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${price}"), priceField, BeanProperty.create("text"), "priceBinding");
        bindingGroup.addBinding(binding);

        jLabel7.setText("Bruttopreis:");

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${price}"), afterTaxPriceField, BeanProperty.create("text"), "afterTaxPriceBinding");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        jLabel8.setText("Beschreibung:");

        descriptionArea.setColumns(20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(5);
        descriptionArea.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${description}"), descriptionArea, BeanProperty.create("text"), "positionDescriptionBinding");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(descriptionArea);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${position.type.name}"), positionTypeField, BeanProperty.create("text"), "positionTypeBinding");
        bindingGroup.addBinding(binding);

        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        nameArea.setEditable(false);
        nameArea.setColumns(20);
        nameArea.setLineWrap(true);
        nameArea.setRows(5);
        nameArea.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${positionName}"), nameArea, BeanProperty.create("text"), "nameBinding");
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(nameArea);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${amount * price  }"), priceSumField, BeanProperty.create("text"), "priceSumBinding");
        bindingGroup.addBinding(binding);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${amount * price }"), afterTaxSumField, BeanProperty.create("text"), "afterTaxSumBinding");
        binding.setConverter(getTaxedConverter());
        bindingGroup.addBinding(binding);

        jSeparator1.setOrientation(SwingConstants.VERTICAL);
        jSeparator1.setToolTipText("");

        jLabel6.setText("Summe");

        jLabel10.setText(",");

        postDecimalSpinner.setModel(new SpinnerNumberModel(0, 0, 75, 25));

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${postDecimal}"), postDecimalSpinner, BeanProperty.create("value"), "decimalBounding");
        bindingGroup.addBinding(binding);

        taxLabel.setText("Steuer:");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, Alignment.LEADING)
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(bookingAccountBox, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(preDecimalSpinner)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(postDecimalSpinner, GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addGap(68, 68, 68))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(priceField)
                                    .addComponent(afterTaxPriceField))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(priceSumField, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(afterTaxSumField)))
                            .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(positionTypeField, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(taxLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(taxField)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(jLabel1, Alignment.LEADING)
                            .addComponent(jLabel8, Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(positionTypeField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(bookingAccountBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(preDecimalSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(postDecimalSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(priceField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(afterTaxPriceField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(priceSumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(afterTaxSumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(taxLabel)
                            .addComponent(taxField))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JTextField afterTaxPriceField;
    JTextField afterTaxSumField;
    JComboBox bookingAccountBox;
    JTextArea descriptionArea;
    JLabel jLabel1;
    JLabel jLabel10;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    JLabel jLabel6;
    JLabel jLabel7;
    JLabel jLabel8;
    JScrollPane jScrollPane1;
    JScrollPane jScrollPane2;
    JSeparator jSeparator1;
    JTextArea nameArea;
    JLabel positionTypeField;
    JSpinner postDecimalSpinner;
    JSpinner preDecimalSpinner;
    JTextField priceField;
    JTextField priceSumField;
    JLabel taxField;
    JLabel taxLabel;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public String toString() {
        return "PositionUpdateCask{" + "price=" + price + ", description=" + description + ", preDecimal=" + preDecimal + ", postDecimal=" + postDecimal + ", amount=" + amount + ", positionName=" + positionName + '}';
    }
}
