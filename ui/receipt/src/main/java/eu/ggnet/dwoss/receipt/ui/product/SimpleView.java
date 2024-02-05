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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import javax.swing.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.CreditMemoReason;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

@Dependent
@Title("Artikelkonfiguration")
public class SimpleView extends javax.swing.JPanel implements Consumer<SimpleView.CreateOrEdit>, ResultProducer<SpecAndModel> {

    public static class Enforce {

        private final TradeName brand;

        private final ProductGroup group;

        public Enforce(TradeName brand, ProductGroup group) {
            this.brand = Objects.requireNonNull(brand, "brand must not be null");
            this.group = Objects.requireNonNull(group, "group must not be null");
        }

        public TradeName brand() {
            return brand;
        }

        public ProductGroup group() {
            return group;
        }

        @Override
        public String toString() {
            return "EnforceGroupBrand{" + "brand=" + brand + ", group=" + group + '}';
        }

    }

    /**
     * Input for Create or Edit. (The SimpleView desides, if it is create or edit)
     */
    public static class CreateOrEdit {

        private final TradeName manufacturer;

        private final String partNo;

        private final Enforce enforce;

        /**
         * Input for Create or Edit. (The SimpleView desides, if it is create or edit)
         *
         * @param manufacturer the manufacturer, must not be null
         * @param partNo       the partNo, must not be blank
         * @throws NullPointerException if manufacturer or partNo is null
         * @throws UserInfoException    if partNo is black, manufacturer.isManufacturer() is false, the partNo does not match
         *                              an optional partNo support of the manufacturer or enforce is set and contains a brand which is not of manufacturer.
         */
        public CreateOrEdit(TradeName manufacturer, String partNo) throws UserInfoException, NullPointerException {
            this(manufacturer, partNo, null);
        }

        /**
         * Input for Create or Edit. (The SimpleView desides, if it is create or edit)
         *
         * @param manufacturer the manufacturer, must not be null
         * @param partNo       the partNo, must not be blank
         * @param enforce      optional enforcement, may be null
         * @throws NullPointerException if manufacturer or partNo is null
         * @throws UserInfoException    if partNo is black, manufacturer.isManufacturer() is false, the partNo does not match
         *                              an optional partNo support of the manufacturer or enforce is set and contains a brand which is not of manufacturer.
         */
        public CreateOrEdit(TradeName manufacturer, String partNo, Enforce enforce) throws UserInfoException, NullPointerException {
            this.manufacturer = Objects.requireNonNull(manufacturer, "manufacturer must not be null");
            this.partNo = Objects.requireNonNull(partNo, "partNo must not be null");
            this.enforce = enforce;
            if ( partNo.isBlank() ) throw new UserInfoException("Artikelnummer ist leer");
            if ( !manufacturer.isManufacturer() ) throw new UserInfoException(manufacturer + " ist kein Hersteller");
            if ( manufacturer.getPartNoSupport() != null && !manufacturer.getPartNoSupport().isValid(partNo) ) {
                throw new UserInfoException(manufacturer.getPartNoSupport().violationMessages(partNo));
            }
            if ( enforce != null && enforce.brand().getManufacturer() != manufacturer ) {
                throw new UserInfoException("Regel gebrocht: Brand " + enforce.brand() + " ist von " + enforce.brand().getManufacturer() + ", es wurde aber " + manufacturer + " ausgewählt");
            }
        }

        public TradeName manufacturer() {
            return manufacturer;
        }

        public String partNo() {
            return partNo;
        }

        public Optional<Enforce> enforce() {
            return Optional.ofNullable(enforce);
        }

        @Override
        public String toString() {
            return "CreateOrEdit{" + "manufacturer=" + manufacturer + ", partNo=" + partNo + ", enforce=" + enforce + '}';
        }

    }

    public static class TradeNameRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) {
                label.setText("");
                return label;
            }
            if ( value instanceof TradeName sc ) {
                label.setText(sc.getDescription());
            } else {
                label.setText(value.toString());
            }
            return label;
        }
    }

    public static class ProductGroupRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) {
                label.setText("");
                return label;
            }
            if ( value instanceof ProductGroup sc ) {
                label.setText(sc.getName());
            } else {
                label.setText(value.toString());
            }
            return label;
        }
    }

    public static class NameIdRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) {
                label.setText("");
                return label;
            }
            if ( value instanceof SpecApi.NameId ni ) {
                label.setText(ni.name());
            } else {
                label.setText(value.toString());
            }
            return label;
        }
    }

    private TradeName manufacturer;

    private ProductSpec spec;

    private boolean edit = false;

    private boolean cancel = true;

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    @Inject
    private RemoteDl remote;

    @Inject
    private Saft saft;

//    private final KeyAdapter ENABLE_ADD_BUTTONS = new KeyAdapter() {
//        @Override
//        public void keyReleased(KeyEvent e) {
//            enableAddButtons();
//        }
//    };
    public SimpleView() {
        initComponents();
        groupBox.setModel(new DefaultComboBoxModel<>(EnumSet.complementOf(EnumSet.of(ProductGroup.COMMENTARY)).toArray()));
        groupBox.setRenderer(new ProductGroupRenderer());
        brandBox.setRenderer(new TradeNameRenderer());
        seriesBox.setRenderer(new NameIdRenderer());
        familyBox.setRenderer(new NameIdRenderer());
        modelBox.setRenderer(new NameIdRenderer());
//        seriesBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
//        familyBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
//        modelBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
    }

    @Override
    public void accept(CreateOrEdit in) {
        Objects.requireNonNull(in, "in must not be null");

        this.manufacturer = in.manufacturer();
        brandBox.setModel(new DefaultComboBoxModel<>(manufacturer.getBrands().toArray()));
        partNoField.setText(in.partNo());

        // TODO: Put in background and consider Speed optimastion. fetcheager is bad.
        final ProductSpec editSpec = remote.lookup(SpecAgent.class).findProductSpecByPartNoEager(in.partNo());
        if ( editSpec != null ) { // Edit Case detected.
            in.enforce().ifPresent(e -> { // Enforce Edit Case.
                if ( editSpec.getModel().getFamily().getSeries().getGroup() != e.group() ) { // Nicht schön, aber geht erstmal. Info ist halt für den Nutzer.
                    throw new CompletionException(new UserInfoException("Erlaubte Warengruppe ist " + e.group() + ", Artikel ist aber " + SpecFormater.toDetailedName(editSpec)));
                } else if ( editSpec.getModel().getFamily().getSeries().getBrand() != e.brand() ) {
                    throw new CompletionException(new UserInfoException("Ausgewählte Marke ist " + e.brand() + ", Artikel ist aber " + SpecFormater.toDetailedName(editSpec)));
                }
            });
            this.edit = true;
            this.spec = editSpec;
            editButton.setEnabled(true);
            brandBox.setEnabled(false);
            groupBox.setEnabled(false);

            ProductSeries series = editSpec.getModel().getFamily().getSeries();
            SpecApi.NameId selectSeries = new SpecApi.NameId(editSpec.getModel().getFamily().getSeries().getId(), editSpec.getModel().getFamily().getSeries().getName());
            SpecApi.NameId selectFamily = new SpecApi.NameId(editSpec.getModel().getFamily().getId(), editSpec.getModel().getFamily().getName());
            SpecApi.NameId selectModel = new SpecApi.NameId(editSpec.getModel().getId(), editSpec.getModel().getName());
            brandBox.setSelectedItem(series.getBrand());
            groupBox.setSelectedItem(series.getGroup());
            updateSeries();
            seriesBox.setSelectedItem(selectSeries);
            updateFamily();
            familyBox.setSelectedItem(selectFamily);
            updateModel();
            modelBox.setSelectedItem(selectModel);
            enableAddButtons();
            htmlInfoPane.setText(SpecFormater.toHtml(editSpec));
        } else { // Create case.
            editButton.setEnabled(false);
            in.enforce().ifPresent(e -> {
                brandBox.setEnabled(false);
                groupBox.setEnabled(false);
                brandBox.setSelectedItem(e.brand());
                groupBox.setSelectedItem(e.group());
            });
            updateSeries();
            updateFamily();
            updateModel();
        }
    }

    public boolean isEdit() {
        return edit;
    }

    /**
     * Returns a productSpec, which may if new miss the model.
     *
     * @return a ProductSpec.
     */
    // Resultproducer
    public ProductSpec getProductSpec() {
        return spec;
    }

    @Override
    public SpecAndModel getResult() {
        if ( cancel ) return null;

        if ( spec == null ) {
            spec = ProductSpec.newInstance(getSelectedGroup());
            spec.setPartNo(partNoField.getText());
        }
        // Loading Product specific details.

        return Optional.ofNullable(spec.getId() > 0 ? spec.getId() : null)
                .map((specId) -> {
                    Product p = Dl.remote().lookup(UniqueUnitAgent.class).findById(Product.class, spec.getProductId());
                    return new SpecAndModel(spec, getSelectedModel().get().id(), p.getGtin(),
                            Optional.ofNullable(p.getShopCategory()).map(t -> t.toApi()).orElse(null), p.isRch());
                })
                .orElse(new SpecAndModel(spec, getSelectedModel().get().id(), 0, null, false));
    }

    private boolean error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private TradeName getSelectedBrand() {
        return (TradeName)brandBox.getSelectedItem();
    }

    private ProductGroup getSelectedGroup() {
        return (ProductGroup)groupBox.getSelectedItem();
    }

    private Optional<SpecApi.NameId> getSelectedSeries() {
        Object o = seriesBox.getSelectedItem();
        if ( o == null ) return Optional.empty();
        return Optional.of((SpecApi.NameId)o);
    }

    private Optional<SpecApi.NameId> getSelectedFamily() {
        Object o = familyBox.getSelectedItem();
        if ( o == null ) return Optional.empty();
        return Optional.of((SpecApi.NameId)o);
    }

    private Optional<SpecApi.NameId> getSelectedModel() {
        Object o = modelBox.getSelectedItem();
        if ( o == null ) return Optional.empty();
        return Optional.of((SpecApi.NameId)o);
    }

    /**
     * Changes the SeriesBox contents in dependency of the brand and group.
     */
    private void updateSeries() {
        seriesBox.setModel(new DefaultComboBoxModel<>(remote.lookup(SpecApi.class)
                .findProductSeries(getSelectedBrand(), getSelectedGroup())
                .toArray(SpecApi.NameId[]::new)));
    }

    /**
     * Changes the FamilyBox contents.
     * This filtering is done in dependency of two cases:
     * <ul>
     * <li>series box has valid selection, take this as parent</li>
     * <li>series box has invalid selection, take brand and group as parent</li>
     * </ul>
     */
    private void updateFamily() {
        getSelectedSeries().ifPresentOrElse(s -> {
            familyBox.setModel(new DefaultComboBoxModel<>(remote.lookup(SpecApi.class)
                    .findProductFamilies(s.id())
                    .toArray(SpecApi.NameId[]::new)));
        }, () -> familyBox.setModel(new DefaultComboBoxModel<>()));
    }

    /**
     * Changes the ModelBox contents. This filtering is done in dependency of two cases:
     * <ul>
     * <li>family box has valid selection, take selection as parent</li>
     * <li>family box has invalid selection, but series box has valid selection, take series selection as parent</li>
     * <li>series box and family box have invalid selection, take brand and group as parent</li>
     * </ul>
     */
    private void updateModel() {
        getSelectedFamily().ifPresentOrElse(f -> {
            modelBox.setModel(new DefaultComboBoxModel<>(remote.lookup(SpecApi.class)
                    .findProductModels(f.id())
                    .toArray(SpecApi.NameId[]::new)));
        }, () -> modelBox.setModel(new DefaultComboBoxModel<SpecApi.NameId>()));
    }

    private void enableAddButtons() {
        addModelButton.setEnabled(getSelectedFamily().isPresent());
        addFamilyButton.setEnabled(getSelectedSeries().isPresent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        modelBox = new javax.swing.JComboBox<>();
        familyBox = new javax.swing.JComboBox<>();
        seriesBox = new javax.swing.JComboBox<>();
        groupBox = new javax.swing.JComboBox();
        brandBox = new javax.swing.JComboBox();
        editButton = new javax.swing.JButton();
        addFamilyButton = new javax.swing.JButton();
        addModelButton = new javax.swing.JButton();
        partNoField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        htmlInfoPane = new javax.swing.JTextPane();
        addSeriesButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("ArtikelNr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel1, gridBagConstraints);

        jLabel6.setText("Modell");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel6, gridBagConstraints);

        jLabel5.setText("Familie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel5, gridBagConstraints);

        jLabel4.setText("Serie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel4, gridBagConstraints);

        jLabel2.setText("Marke");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("Warengruppe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel3, gridBagConstraints);

        modelBox.setMinimumSize(new java.awt.Dimension(150, 25));
        modelBox.setPreferredSize(new java.awt.Dimension(150, 25));
        modelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(modelBox, gridBagConstraints);

        familyBox.setMinimumSize(new java.awt.Dimension(150, 25));
        familyBox.setPreferredSize(new java.awt.Dimension(150, 25));
        familyBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(familyBox, gridBagConstraints);

        seriesBox.setMinimumSize(new java.awt.Dimension(72, 25));
        seriesBox.setPreferredSize(new java.awt.Dimension(150, 25));
        seriesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seriesBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(seriesBox, gridBagConstraints);

        groupBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        groupBox.setMinimumSize(new java.awt.Dimension(72, 25));
        groupBox.setPreferredSize(new java.awt.Dimension(150, 25));
        groupBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(groupBox, gridBagConstraints);

        brandBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        brandBox.setMinimumSize(new java.awt.Dimension(72, 25));
        brandBox.setPreferredSize(new java.awt.Dimension(150, 25));
        brandBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brandBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(brandBox, gridBagConstraints);

        editButton.setText("Ändern");
        editButton.setPreferredSize(new java.awt.Dimension(75, 23));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(editButton, gridBagConstraints);

        addFamilyButton.setText("hinzufügen");
        addFamilyButton.setPreferredSize(new java.awt.Dimension(75, 23));
        addFamilyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFamilyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(addFamilyButton, gridBagConstraints);

        addModelButton.setText("hinzufügen");
        addModelButton.setPreferredSize(new java.awt.Dimension(75, 23));
        addModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(addModelButton, gridBagConstraints);

        partNoField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(partNoField, gridBagConstraints);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(360, 300));

        htmlInfoPane.setEditable(false);
        htmlInfoPane.setContentType("text/html"); // NOI18N
        htmlInfoPane.setMaximumSize(new java.awt.Dimension(100, 100));
        htmlInfoPane.setPreferredSize(new java.awt.Dimension(100, 100));
        jScrollPane1.setViewportView(htmlInfoPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jScrollPane1, gridBagConstraints);

        addSeriesButton.setText("hinzufügen");
        addSeriesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSeriesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 14;
        add(addSeriesButton, gridBagConstraints);

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(okButton, gridBagConstraints);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        add(cancelButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void brandBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brandBoxActionPerformed
        updateSeries();
        updateFamily();
        updateModel();
        enableAddButtons();
    }//GEN-LAST:event_brandBoxActionPerformed

    private void groupBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupBoxActionPerformed
        updateSeries();
        updateFamily();
        updateModel();
        enableAddButtons();
    }//GEN-LAST:event_groupBoxActionPerformed

    private void seriesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seriesBoxActionPerformed
        updateFamily();
        updateModel();
        enableAddButtons();
    }//GEN-LAST:event_seriesBoxActionPerformed

    private void familyBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyBoxActionPerformed
        updateModel();
        enableAddButtons();
    }//GEN-LAST:event_familyBoxActionPerformed

    private void addModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelButtonActionPerformed
        saft.build().parent(this).title("Model").dialog()
                .eval(() -> {
                    TextInputDialog d = new TextInputDialog();
                    d.setTitle("Model");
                    d.setHeaderText("Neues Model hinzufügen");
                    return d;
                }).cf()
                .thenAccept(n -> {
                    if ( n.isBlank() ) throw new CompletionException(new UserInfoException("Modelname ist leer"));
                    if ( getSelectedSeries().isEmpty() ) throw new CompletionException(new UserInfoException("Keine Serie ausgewählt"));
                    if ( getSelectedFamily().isEmpty() ) throw new CompletionException(new UserInfoException("Keine Family ausgewählt"));
                    ProductModel createdModel = UiUtil.exceptionRun(() -> remote.lookup(ProductProcessor.class)
                            .createModel(getSelectedFamily().get().id(), n));
                    updateModel();
                    modelBox.setSelectedItem(new SpecApi.NameId(createdModel.getId(), createdModel.getName()));
                    enableAddButtons();
                })
                .handle(saft.handler(this));
    }//GEN-LAST:event_addModelButtonActionPerformed

    private void addFamilyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFamilyButtonActionPerformed
        saft.build().parent(this).title("Family").dialog()
                .eval(() -> {
                    TextInputDialog d = new TextInputDialog();
                    d.setTitle("Family");
                    d.setHeaderText("Neue Family hinzufügen");
                    return d;
                }).cf()
                .thenAccept(n -> {
                    if ( n.isBlank() ) throw new CompletionException(new UserInfoException("Familyname ist leer"));
                    if ( getSelectedSeries().isEmpty() ) throw new CompletionException(new UserInfoException("Keine Serie ausgewählt"));
                    ProductFamily createdFamily = UiUtil.exceptionRun(() -> remote.lookup(ProductProcessor.class)
                            .createFamily(getSelectedSeries().get().id(), n));
                    updateFamily();
                    updateModel();
                    familyBox.setSelectedItem(new SpecApi.NameId(createdFamily.getId(), createdFamily.getName()));
                    enableAddButtons();
                })
                .handle(saft.handler(this));
    }//GEN-LAST:event_addFamilyButtonActionPerformed

    private void modelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelBoxActionPerformed
        enableAddButtons();
    }//GEN-LAST:event_modelBoxActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        String result = JOptionPane.showInputDialog(this, "Artikelnummer ändern:", spec.getPartNo());
        if ( result == null || result.trim().equals("") || result.equals(spec.getPartNo()) ) return; // Cancel and Nothing
        if ( result.startsWith(" ") || result.endsWith(" ") ) {
            error("ArtikelNummer hat am Anfang oder Ende Freizeichen, nicht erlaubt");
            return;
        }
        ProductSpec localSpec = remote.lookup(SpecAgent.class).findProductSpecByPartNoEager(result);
        if ( localSpec != null ) {
            error("Artikel exitiert schon : " + localSpec.getModel().getName() + " (" + localSpec.getPartNo() + ")");
            return;
        }
        spec.setPartNo(result); // Will be persisted later on.
        partNoField.setText(result);
    }//GEN-LAST:event_editButtonActionPerformed

    private void addSeriesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSeriesButtonActionPerformed
        saft.build().parent(this).title("Serie").dialog()
                .eval(() -> {
                    TextInputDialog d = new TextInputDialog();
                    d.setTitle("Serie");
                    d.setHeaderText("Neue Serie hinzufügen");
                    return d;
                }).cf()
                .thenAccept(n -> {
                    System.out.println("Seriesn: " + n);
                    if ( n.isBlank() ) throw new CompletionException(new UserInfoException("Serienname ist leer"));
                    ProductSeries createdSeries = UiUtil.exceptionRun(() -> remote.lookup(ProductProcessor.class)
                            .createSeries(getSelectedBrand(), getSelectedGroup(), n));
                    updateSeries();
                    updateFamily();
                    updateModel();
                    seriesBox.setSelectedItem(new SpecApi.NameId(createdSeries.getId(), createdSeries.getName()));
                    enableAddButtons();
                })
                .handle(saft.handler(this));
    }//GEN-LAST:event_addSeriesButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if ( !getSelectedModel().isPresent() ) {
            saft.build(this).alert("Model nicht ausgewählt oder nicht hinzugefügt");
        } else {
            cancel = false;
            showingProperty.set(false);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        showingProperty.set(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFamilyButton;
    private javax.swing.JButton addModelButton;
    private javax.swing.JButton addSeriesButton;
    private javax.swing.JComboBox brandBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton editButton;
    private javax.swing.JComboBox<SpecApi.NameId> familyBox;
    private javax.swing.JComboBox groupBox;
    private javax.swing.JTextPane htmlInfoPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<SpecApi.NameId> modelBox;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField partNoField;
    private javax.swing.JComboBox<SpecApi.NameId> seriesBox;
    // End of variables declaration//GEN-END:variables

}
