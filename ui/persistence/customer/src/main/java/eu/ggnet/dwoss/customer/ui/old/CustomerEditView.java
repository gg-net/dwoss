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
package eu.ggnet.dwoss.customer.ui.old;

import java.awt.Component;
import java.util.*;

import javax.swing.*;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.common.ui.*;
import eu.ggnet.dwoss.common.ui.table.CheckBoxTableNoteModel;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.dwoss.redtape.api.event.AddressChange;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.experimental.auth.Guardian;

import lombok.Getter;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;
import static eu.ggnet.dwoss.rights.api.AtomicRight.*;

/**
 *
 * @author pascal.perau
 */
public class CustomerEditView extends javax.swing.JPanel implements IPreClose {

    class LocaleComboBoxRendere extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) return label;
            if ( !(value instanceof Locale) ) return label;
            label.setText(((Locale)value).getDisplayCountry());
            return label;
        }
    }

    private Locale[] countries = new Locale[]{
        new Locale("de", "DE"),
        new Locale("de", "AT"),
        new Locale("de", "CH")};

    private CheckBoxTableNoteModel<CustomerFlag> customerFlagsModel = new CheckBoxTableNoteModel(new ArrayList<>(EnumSet.allOf(CustomerFlag.class)), "Kundeneigenschaften");

    private OldCustomer customer;

    private OldCustomer original;

    @Getter
    private Set<AddressChange> changedAdresses = new HashSet<>();

    private ShippingTerms shippingTerms;

    /** Creates new form EditCustomer
     * <p>
     */
    public CustomerEditView() {
        initComponents();

        shippingTerms = Dl.remote().lookup(Mandators.class).loadShippingTerms();

        shippingConditionBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value instanceof ShippingCondition && shippingTerms != null )
                    label.setText(shippingTerms.get((ShippingCondition)value).get().getNote());
                return label;
            }
        });
        paymentConditionBox.setRenderer(new NamedEnumCellRenderer());
        paymentMethodBox.setRenderer(new NamedEnumCellRenderer());
        payCountryBox.setRenderer(new LocaleComboBoxRendere());
        shipCountryBox.setRenderer(new LocaleComboBoxRendere());
        sourceComboBox.setRenderer(new NamedEnumCellRenderer());
        keyAccounterComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText("Nichts Ausgewählt");
                if ( value != null ) label.setText(value.toString());
                return label;
            }
        });

        reteilerChannelAllowedCheck.setActionCommand(SalesChannel.RETAILER.toString());
        endUserChannelAllowedCheck.setActionCommand(SalesChannel.CUSTOMER.toString());
        isSystemCustomerCheck.setActionCommand(CustomerFlag.SYSTEM_CUSTOMER.toString());

        customerFlagTable.setModel(customerFlagsModel);
        customerFlagsModel.setTable(customerFlagTable);
        customerFlagsModel.setFiltered(EnumSet.complementOf(EnumSet.of(CustomerFlag.SYSTEM_CUSTOMER))); // Systemcustomer is special.

        Guardian guardian = Dl.local().lookup(Guardian.class);

        guardian.add(new JComponentEnabler(UPDATE_CUSTOMER_TO_SYSTEM_CUSTOMER, isSystemCustomerCheck));
        guardian.add(new JComponentEnabler(UPDATE_CUSTOMER_PAYMENT_METHOD, paymentMethodBox));
        guardian.add(new JComponentEnabler(UPDATE_CUSTOMER_PAYMENT_CONDITION, paymentConditionBox));
        guardian.add(new JComponentEnabler(UPDATE_CUSTOMER_SHIPPING_CONDITION, shippingConditionBox));
    }

    public OldCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(OldCustomer customer) {
        OldCustomer oldCustomer = this.customer;
        this.customer = customer;
        this.original = new OldCustomer(customer);
        firePropertyChange("customer", oldCustomer, customer);
        reteilerChannelAllowedCheck.setSelected(customer.getAllowedSalesChannels().contains(SalesChannel.RETAILER));
        endUserChannelAllowedCheck.setSelected(customer.getAllowedSalesChannels().contains(SalesChannel.CUSTOMER));
        isSystemCustomerCheck.setSelected(customer.getFlags().contains(CustomerFlag.SYSTEM_CUSTOMER));
        titleBox.setSelectedItem(customer.getTitel() != null ? customer.getTitel() : null);
        payCountryBox.setSelectedItem(customer.getPayCountry());
        shipCountryBox.setSelectedItem(customer.getPayCountry());
        ledgerField.setText(Integer.toString(customer.getLedger()));
        taxIdField.setText(customer.getTaxId());
        customerFlagsModel.setMarked(customer.getFlags());
    }

    /**
     * Only used for ElProperty
     * <p/>
     * @return a list of PaymentMethods
     */
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    /**
     * Only used for ElProperty
     * <p/>
     * @return a list of PaymentConditions
     */
    public List<PaymentCondition> getPaymentCondition() {
        return Arrays.asList(PaymentCondition.values());
    }

    /**
     * Only used for ElProperty
     * <p/>
     * @return a list of ShippingConditions
     */
    public List<ShippingCondition> getShippingCondition() {
        return Arrays.asList(ShippingCondition.values());
    }

    public List<Source> getSources() {
        List<Source> asList = new ArrayList<>(Arrays.asList(Source.values()));
        asList.add(null);
        return asList;
    }

    public List<String> getAllUsernames() {
        List<String> allUsers = new ArrayList<>(Dl.local().lookup(Guardian.class).getAllUsernames());
        allUsers.add(null);
        return allUsers;
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.CANCEL ) {
            return true;
        }
        if ( titleBox.getSelectedItem() == null ) {
            JOptionPane.showMessageDialog(this, "Titel ist nicht gesetzt.");
            return false;
        }
        if ( !ValidationUtil.isValidOrShow(SwingUtilities.getWindowAncestor(this), customer) ) return false;
        customer.setTitel(titleBox.getSelectedItem().toString());
        customer.setFlags(customerFlagsModel.getMarked());

        if ( (PaymentCondition)paymentConditionBox.getSelectedItem() != PaymentCondition.CUSTOMER ) customer.setHaendler(true);
        // TODO: A Nullpointer here may be possible as I moved the Formater to the OldCustomer itself. If true add null check before.
        if ( !original.toInvoiceAddress().equals(customer.toInvoiceAddress()) ) {
            changedAdresses.add(new AddressChange(
                    original.getId(),
                    Dl.local().lookup(Guardian.class).getUsername(),
                    INVOICE,
                    original.toInvoiceAddress(),
                    customer.toInvoiceAddress()));
        }

        if ( !original.toShippingAddress().equals(customer.toShippingAddress()) ) {
            changedAdresses.add(new AddressChange(original.getId(),
                    Dl.local().lookup(Guardian.class).getUsername(),
                    SHIPPING,
                    original.toInvoiceAddress(),
                    customer.toInvoiceAddress()));
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
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        customerIdLabel = new javax.swing.JLabel();
        companyField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        titleBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        lastNameField = new javax.swing.JTextField();
        invoicePanel = new javax.swing.JPanel();
        invoiceStreetField = new javax.swing.JTextField();
        invoiceZipcodeField = new javax.swing.JTextField();
        InvoiceCityField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        payCountryBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        phoneNumberField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        mobileNumberField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        paymentMethodBox = new javax.swing.JComboBox();
        isSystemCustomerCheck = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        shippingConditionBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        paymentConditionBox = new javax.swing.JComboBox();
        commentAreaScrollPane = new javax.swing.JScrollPane();
        commentArea = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        shippingPanel = new javax.swing.JPanel();
        shippingStreetField = new javax.swing.JTextField();
        shippingZipcodeField = new javax.swing.JTextField();
        shippingCityField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        shipCountryBox = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        ledgerField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        taxIdField = new javax.swing.JTextField();
        customerFlagTableScrollPane = new javax.swing.JScrollPane();
        customerFlagTable = new javax.swing.JTable();
        salesChannelPanel = new javax.swing.JPanel();
        reteilerChannelAllowedCheck = new javax.swing.JCheckBox();
        endUserChannelAllowedCheck = new javax.swing.JCheckBox();
        addAdditionalCustomerIds = new javax.swing.JButton();
        sourceLabel = new javax.swing.JLabel();
        keyAccounterLabel = new javax.swing.JLabel();
        sourceComboBox = new javax.swing.JComboBox<>();
        keyAccounterComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("KID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel1, gridBagConstraints);

        customerIdLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.kundenID}"), customerIdLabel, org.jdesktop.beansbinding.BeanProperty.create("text"), "customerIdBinding");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(customerIdLabel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.firma}"), companyField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindCompany");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(companyField, gridBagConstraints);

        jLabel3.setText("Firma");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("Titel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel4, gridBagConstraints);

        titleBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Herr", "Frau" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(titleBox, gridBagConstraints);

        jLabel5.setText("Vorname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("Nachname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel6, gridBagConstraints);

        firstNameField.setMinimumSize(new java.awt.Dimension(125, 32));
        firstNameField.setPreferredSize(new java.awt.Dimension(125, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.vorname}"), firstNameField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindFirstName");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(firstNameField, gridBagConstraints);

        lastNameField.setMinimumSize(new java.awt.Dimension(125, 32));
        lastNameField.setPreferredSize(new java.awt.Dimension(125, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.nachname}"), lastNameField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindLastName");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(lastNameField, gridBagConstraints);

        invoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rechnungsadresse"));
        invoicePanel.setLayout(new java.awt.GridBagLayout());

        invoiceStreetField.setToolTipText("Straße (Rechnung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.REAdresse}"), invoiceStreetField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindInvoiceStreet");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        invoicePanel.add(invoiceStreetField, gridBagConstraints);

        invoiceZipcodeField.setToolTipText("PLZ (Rechnung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.REPlz}"), invoiceZipcodeField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindInvoiceZipCode");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 75;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        invoicePanel.add(invoiceZipcodeField, gridBagConstraints);

        InvoiceCityField.setToolTipText("Ort (Rechnung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.REOrt}"), InvoiceCityField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindInvoiceCity");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        invoicePanel.add(InvoiceCityField, gridBagConstraints);

        jLabel12.setText("RE Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        invoicePanel.add(jLabel12, gridBagConstraints);

        jLabel18.setText("RE Land:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        invoicePanel.add(jLabel18, gridBagConstraints);

        payCountryBox.setModel(new DefaultComboBoxModel(countries));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.payCountry}"), payCountryBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        invoicePanel.add(payCountryBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 3.0;
        add(invoicePanel, gridBagConstraints);

        jLabel13.setText("Telefon");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel13, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.telefonnummer}"), phoneNumberField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "bindPhoneNumber");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(phoneNumberField, gridBagConstraints);

        jLabel14.setText("Mobil");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel14, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.handynummer}"), mobileNumberField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "bindMobileNumber");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(mobileNumberField, gridBagConstraints);

        jLabel15.setText("e-Mail");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel15, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.email}"), emailField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindEmail");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        add(emailField, gridBagConstraints);

        jLabel7.setText("Zahlungsmodalität");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jLabel7, gridBagConstraints);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${paymentMethods}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, paymentMethodBox, "bindPaymentMethodElements");
        jComboBoxBinding.setSourceNullValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.paymentMethod}"), paymentMethodBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "bindPaymentMethod");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        add(paymentMethodBox, gridBagConstraints);

        isSystemCustomerCheck.setText("GG-Net Systemkunde");
        isSystemCustomerCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerFlagActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(isSystemCustomerCheck, gridBagConstraints);

        jLabel8.setText("Zahlungskondition");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jLabel8, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${shippingCondition}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, shippingConditionBox, "bindShippingConditionELements");
        jComboBoxBinding.setSourceNullValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.shippingCondition}"), shippingConditionBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "bindShippingCondition");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(shippingConditionBox, gridBagConstraints);

        jLabel9.setText("Lieferkondition");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jLabel9, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${paymentCondition}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, paymentConditionBox, "bindPaymentConditionElements");
        jComboBoxBinding.setSourceNullValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.paymentCondition}"), paymentConditionBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(paymentConditionBox, gridBagConstraints);

        commentArea.setColumns(20);
        commentArea.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.anmerkung}"), commentArea, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "bindComments");
        bindingGroup.addBinding(binding);

        commentAreaScrollPane.setViewportView(commentArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.1;
        add(commentAreaScrollPane, gridBagConstraints);

        jLabel10.setText("Anmerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel10, gridBagConstraints);

        shippingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Lieferadresse"));
        shippingPanel.setLayout(new java.awt.GridBagLayout());

        shippingStreetField.setToolTipText("Straße (Lieferung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.LIAdresse}"), shippingStreetField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindShippingStreet");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        shippingPanel.add(shippingStreetField, gridBagConstraints);

        shippingZipcodeField.setToolTipText("PLZ (Lieferung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.LIPlz}"), shippingZipcodeField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindShippingZipCode");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 75;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        shippingPanel.add(shippingZipcodeField, gridBagConstraints);

        shippingCityField.setToolTipText("Ort (Lieferung)");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.LIOrt}"), shippingCityField, org.jdesktop.beansbinding.BeanProperty.create("text"), "bindShippingCity");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        shippingPanel.add(shippingCityField, gridBagConstraints);

        jLabel17.setText("LI Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        shippingPanel.add(jLabel17, gridBagConstraints);

        jLabel19.setText("LI Land:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        shippingPanel.add(jLabel19, gridBagConstraints);

        shipCountryBox.setModel(new DefaultComboBoxModel(countries));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.shipCountry}"), shipCountryBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        shippingPanel.add(shipCountryBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(shippingPanel, gridBagConstraints);

        jLabel20.setText("FiBu Konto");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jLabel20, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.ledger}"), ledgerField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(ledgerField, gridBagConstraints);

        jLabel21.setText("USt-Nr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jLabel21, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.taxId}"), taxIdField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(taxIdField, gridBagConstraints);

        customerFlagTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        customerFlagTableScrollPane.setViewportView(customerFlagTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(customerFlagTableScrollPane, gridBagConstraints);

        salesChannelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Verkaufskanäle"));
        salesChannelPanel.setLayout(new java.awt.BorderLayout());

        reteilerChannelAllowedCheck.setText("Händlerkanal");
        reteilerChannelAllowedCheck.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                saleChannelAllowedActionPerformed(evt);
            }
        });
        salesChannelPanel.add(reteilerChannelAllowedCheck, java.awt.BorderLayout.CENTER);

        endUserChannelAllowedCheck.setText("Endkundenkanal");
        endUserChannelAllowedCheck.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                saleChannelAllowedActionPerformed(evt);
            }
        });
        salesChannelPanel.add(endUserChannelAllowedCheck, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(salesChannelPanel, gridBagConstraints);

        addAdditionalCustomerIds.setText("Kundennummern hinzufügen");
        addAdditionalCustomerIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAdditionalCustomerIdsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(addAdditionalCustomerIds, gridBagConstraints);

        sourceLabel.setText("DatenQuelle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(sourceLabel, gridBagConstraints);

        keyAccounterLabel.setText("Betreuer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(keyAccounterLabel, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sources}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, sourceComboBox);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.source}"), sourceComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(sourceComboBox, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${allUsernames}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, keyAccounterComboBox);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customer.keyAccounter}"), keyAccounterComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(keyAccounterComboBox, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void saleChannelAllowedActionPerformed(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_saleChannelAllowedActionPerformed
        if ( ((JCheckBox)evt.getSource()).isSelected() )
            customer.getAllowedSalesChannels().add(SalesChannel.valueOf(((JCheckBox)evt.getSource()).getActionCommand()));
        else customer.getAllowedSalesChannels().remove(SalesChannel.valueOf(((JCheckBox)evt.getSource()).getActionCommand()));
    }//GEN-LAST:event_saleChannelAllowedActionPerformed

    private void customerFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerFlagActionPerformed
        if ( ((JCheckBox)evt.getSource()).isSelected() )
            customer.getFlags().add(CustomerFlag.valueOf(((JCheckBox)evt.getSource()).getActionCommand()));
        else customer.getFlags().remove(CustomerFlag.valueOf(((JCheckBox)evt.getSource()).getActionCommand()));
    }//GEN-LAST:event_customerFlagActionPerformed

    private void addAdditionalCustomerIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAdditionalCustomerIdsActionPerformed
        Ui.exec(() -> {
            Ui.build(this).dialog().eval(() -> customer.getAdditionalCustomerIds(), () -> new AdditionalCustomerIdsView())
                    .opt().ifPresent(out -> {
                        customer.getAdditionalCustomerIds().clear();
                        customer.getAdditionalCustomerIds().putAll(out);
                    });
        });
    }//GEN-LAST:event_addAdditionalCustomerIdsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField InvoiceCityField;
    private javax.swing.JButton addAdditionalCustomerIds;
    private javax.swing.JTextArea commentArea;
    private javax.swing.JScrollPane commentAreaScrollPane;
    private javax.swing.JTextField companyField;
    private javax.swing.JTable customerFlagTable;
    private javax.swing.JScrollPane customerFlagTableScrollPane;
    private javax.swing.JLabel customerIdLabel;
    private javax.swing.JTextField emailField;
    private javax.swing.JCheckBox endUserChannelAllowedCheck;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JPanel invoicePanel;
    private javax.swing.JTextField invoiceStreetField;
    private javax.swing.JTextField invoiceZipcodeField;
    private javax.swing.JCheckBox isSystemCustomerCheck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox<String> keyAccounterComboBox;
    private javax.swing.JLabel keyAccounterLabel;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField ledgerField;
    private javax.swing.JTextField mobileNumberField;
    private javax.swing.JComboBox payCountryBox;
    private javax.swing.JComboBox paymentConditionBox;
    private javax.swing.JComboBox paymentMethodBox;
    private javax.swing.JTextField phoneNumberField;
    private javax.swing.JCheckBox reteilerChannelAllowedCheck;
    private javax.swing.JPanel salesChannelPanel;
    private javax.swing.JComboBox shipCountryBox;
    private javax.swing.JTextField shippingCityField;
    private javax.swing.JComboBox shippingConditionBox;
    private javax.swing.JPanel shippingPanel;
    private javax.swing.JTextField shippingStreetField;
    private javax.swing.JTextField shippingZipcodeField;
    private javax.swing.JComboBox<String> sourceComboBox;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JTextField taxIdField;
    private javax.swing.JComboBox titleBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
