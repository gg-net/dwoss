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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.saft.*;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

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

    private TradeName manufacturer;

    private final NamedComparator INAMED_COMPARATOR = new NamedComparator();

    private ProductSpec spec;

    private List<ProductSeries> allSeries;

    private boolean edit = false;

    private boolean cancel = true;

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    // Inject
    private RemoteDl remote = Dl.remote();

    private final KeyAdapter ENABLE_ADD_BUTTONS = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            enableAddButtons();
        }
    };

    public SimpleView() {
        initComponents();
        groupBox.setModel(new DefaultComboBoxModel<>(EnumSet.complementOf(EnumSet.of(ProductGroup.COMMENTARY)).toArray()));
        seriesBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
        familyBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
        modelBox.getEditor().getEditorComponent().addKeyListener(ENABLE_ADD_BUTTONS);
    }

    @Override
    public void accept(CreateOrEdit in) {
        Objects.requireNonNull(in, "in must not be null");

        this.manufacturer = in.manufacturer();
        // TODO: put in some background thread.
        allSeries = remote.lookup(SpecAgent.class).findAll(ProductSeries.class);
        brandBox.setModel(new DefaultComboBoxModel<>(manufacturer.getBrands().toArray()));
        partNoField.setText(in.partNo());

        // TODO: Put in background.
        final ProductSpec spec = remote.lookup(SpecAgent.class).findProductSpecByPartNoEager(in.partNo());
        if ( spec != null ) { // Edit Case detected.
            in.enforce().ifPresent(e -> { // Enforce Edit Case.
                if ( spec.getModel().getFamily().getSeries().getGroup() != e.group() ) { // Nicht schön, aber geht erstmal. Info ist halt für den Nutzer.
                    throw new CompletionException(new UserInfoException("Erlaubte Warengruppe ist " + e.group() + ", Artikel ist aber " + SpecFormater.toDetailedName(spec)));
                } else if ( spec.getModel().getFamily().getSeries().getBrand() != e.brand() ) {
                    throw new CompletionException(new UserInfoException("Ausgewählte Marke ist " + e.brand() + ", Artikel ist aber " + SpecFormater.toDetailedName(spec)));
                }
            });
            this.edit = true;
            this.spec = spec;
            editButton.setEnabled(true);
            brandBox.setEnabled(false);
            groupBox.setEnabled(false);
            ProductSeries series = spec.getModel().getFamily().getSeries();
            brandBox.setSelectedItem(series.getBrand());
            groupBox.setSelectedItem(series.getGroup());
            updateSeries();
            seriesBox.setSelectedItem(series.getName());
            updateFamily();
            familyBox.setSelectedItem(spec.getModel().getFamily().getName());
            updateModel();
            modelBox.setSelectedItem(spec.getModel().getName());
            enableAddButtons();
            htmlInfoPane.setText(SpecFormater.toHtml(spec));
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
            spec = ProductSpec.newInstance(getGroup());
            spec.setPartNo(partNoField.getText());
        }
        // TODO: The gtin should be on the product spec, not on the product. thats why we need this workaround here.
        // Load gtin if in edit mode.
        long gtin = spec.getId() > 0 ? Dl.remote().lookup(UniqueUnitAgent.class).findById(Product.class, spec.getProductId()).getGtin() : 0;
        return new SpecAndModel(spec, getSelectedModel().get(), gtin);
    }

    private TradeName getBrand() {
        return (TradeName)brandBox.getSelectedItem();
    }

    private ProductGroup getGroup() {
        return (ProductGroup)groupBox.getSelectedItem();
    }

    private boolean error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Displays a Warning Dialog.
     *
     * @param msg the message to display
     *
     * @return ture if ok is presses, false if cancel
     */
    private boolean warn(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Warnung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private TradeName getSelectedBrand() {
        return (TradeName)brandBox.getSelectedItem();
    }

    private ProductGroup getSelectedGroup() {
        return (ProductGroup)groupBox.getSelectedItem();
    }

    private List<ProductSeries> getFilteredSeries() {
        List<ProductSeries> filteredSerieses = new ArrayList<>();
        for (ProductSeries series : allSeries) {
            if ( series.getBrand().equals(getSelectedBrand()) && series.getGroup().equals(getSelectedGroup()) ) {
                filteredSerieses.add(series);
            }
        }
        Collections.sort(filteredSerieses, INAMED_COMPARATOR);
        return filteredSerieses;
    }

    private Optional<ProductSeries> getSelectedSeries() {
        return getFilteredSeries().stream().filter(s -> s.getName().equals(seriesBox.getEditor().getItem())).findAny();
    }

    private Optional<ProductFamily> getSelectedFamily() {
        return getFilteredFamilies().stream().filter(s -> s.getName().equals(familyBox.getEditor().getItem())).findAny();
    }

    public Optional<ProductModel> getSelectedModel() {
        return getFilteredModels().stream().filter(s -> s.getName().equals(modelBox.getEditor().getItem())).findAny();
    }

    private List<ProductFamily> getFilteredFamilies() {
        return getSelectedSeries()
                .map(ProductSeries::getFamilys)
                .orElseGet(() -> getFilteredSeries().stream().flatMap((ProductSeries s) -> s.getFamilys().stream()).collect(Collectors.toSet()))
                .stream()
                .sorted(INAMED_COMPARATOR)
                .collect(Collectors.toList());
    }

    private List<ProductModel> getFilteredModels() {
        return getSelectedFamily()
                .map(ProductFamily::getModels)
                .orElseGet(() -> getFilteredFamilies().stream().flatMap((ProductFamily f) -> f.getModels().stream()).collect(Collectors.toSet()))
                .stream()
                .sorted(INAMED_COMPARATOR)
                .collect(Collectors.toList());
    }

    private String[] toNamesAndNull(List<? extends INamed> inamed) {
        return inamed.stream().map(INamed::getName).collect(Collectors.toCollection(() -> new ArrayList<>(Arrays.asList((String)null)))).toArray(new String[]{});
    }

    /**
     * Changes the SeriesBox contents in dependency of the brand and group.
     */
    private void updateSeries() {
        seriesBox.setModel(new DefaultComboBoxModel<>(toNamesAndNull(getFilteredSeries())));
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
        familyBox.setModel(new DefaultComboBoxModel<>(toNamesAndNull(getFilteredFamilies())));
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
        modelBox.setModel(new DefaultComboBoxModel<>(toNamesAndNull(getFilteredModels())));
    }

    private void enableAddButtons() {
        addModelButton.setEnabled(!getSelectedModel().isPresent());
        addFamilyButton.setEnabled(!getSelectedFamily().isPresent());
        addSeriesButton.setEnabled(!getSelectedSeries().isPresent());
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

        modelBox.setEditable(true);
        modelBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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

        familyBox.setEditable(true);
        familyBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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

        seriesBox.setEditable(true);
        seriesBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
        String modelName = (String)modelBox.getSelectedItem();
        if ( StringUtils.isBlank(modelName) ) {
            error("Keine Modellname hinterlegt!");
            return;
        }
        for (ProductSeries series : allSeries) {
            for (ProductFamily family : series.getFamilys()) {
                for (ProductModel model : family.getModels()) {
                    if ( model.getName().equals(modelName) ) {
                        error("Modell " + modelName + " existiert schon in " + series.getName() + "/" + family.getName());
                        return; // Found an equal, so nothing to do
                    }
                }
            }
        }
        if ( !getSelectedSeries().isPresent() ) {
            if ( !warn("Keine Serie und Familie ausgewählt, es werde Standartwerte verwendet.") ) return;
        } else if ( !getSelectedFamily().isPresent() ) {
            if ( !warn("Keine Familie ausgewählt, es wird ein Standartwert verwendet.") ) return;
        }
        ProductModel model = remote.lookup(ProductProcessor.class).create(getBrand(), getGroup(), getSelectedSeries().orElse(null), getSelectedFamily().orElse(null), modelName);
        // TODO: Add Model to local list in a better way
        // TODO: And show the active backgroundprogress.
        JOptionPane.showMessageDialog(this, "Modell " + model.getName() + " wurde hinzugefügt.\nAktualisiere Lokale Liste.");
        // TODO: Put in background and add loadingblock
        allSeries = remote.lookup(SpecAgent.class).findAll(ProductSeries.class);
        updateSeries();
        updateFamily();
        updateModel();
        seriesBox.setSelectedItem(model.getFamily().getSeries().getName());
        familyBox.setSelectedItem(model.getFamily().getName());
        modelBox.setSelectedItem(model.getName());
        enableAddButtons();
    }//GEN-LAST:event_addModelButtonActionPerformed

    private void addFamilyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFamilyButtonActionPerformed
        String familyName = (String)familyBox.getSelectedItem();
        if ( StringUtils.isBlank(familyName) ) {
            error("Keine Familienname hinterlegt!");
            return;
        }
        for (ProductSeries series : allSeries) {
            for (ProductFamily family : series.getFamilys()) {
                if ( family.getName().equals(familyName) ) {
                    error("Familie " + familyName + " existiert schon in " + series.getName());
                    return; // Found an equal, so nothing to do
                }
            }
        }
        if ( !getSelectedSeries().isPresent() ) {
            if ( !warn("Keine Serie ausgewählt, es wird ein Standartwert verwendet.") ) return;
        }
        ProductFamily family = remote.lookup(ProductProcessor.class).create(getBrand(), getGroup(), getSelectedSeries().orElse(null), familyName);
        // TODO: Add Family to local list in a better way
        // TODO: And show the active backgroundprogress.
        JOptionPane.showMessageDialog(this, "Familie " + family.getName() + " wurde hinzugefügt.\nAktualisiere Lokale Liste.");
        // TODO: Put in background and add loadingblock
        allSeries = remote.lookup(SpecAgent.class).findAll(ProductSeries.class);
        updateSeries();
        updateFamily();
        updateModel();
        seriesBox.setSelectedItem(family.getSeries().getName());
        familyBox.setSelectedItem(family.getName());
        enableAddButtons();
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
        final String seriesName = (String)seriesBox.getSelectedItem();
        if ( StringUtils.isBlank(seriesName) ) {
            error("Keinen Serienname hinterlegt!");
            return;
        }

        if ( allSeries.stream().map(ProductSeries::getName).anyMatch(n -> seriesName.trim().equals(n)) ) {
            error("Serie " + seriesName + " existiert schon");
            return; // Found an equal, so nothing to do
        }
        Reply<ProductSeries> reply = ReplyUtil.wrap(() -> remote.lookup(ProductProcessor.class).create(getBrand(), getGroup(), seriesName));
        if ( !Failure.handle(reply) ) return;
        ProductSeries series = reply.getPayload();
        JOptionPane.showMessageDialog(this, "Serie " + series.getName() + " wurde hinzugefügt.\nAktualisiere Lokale Liste.");
        // TODO: Put in background and add loadingblock
        allSeries = remote.lookup(SpecAgent.class).findAll(ProductSeries.class);
        updateSeries();
        updateFamily();
        updateModel();
        seriesBox.setSelectedItem(series.getName());
        enableAddButtons();
    }//GEN-LAST:event_addSeriesButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if ( !getSelectedModel().isPresent() ) {
            Ui.build(this).alert("Model nicht ausgewählt oder nicht hinzugefügt");
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
    private javax.swing.JComboBox<String> familyBox;
    private javax.swing.JComboBox groupBox;
    private javax.swing.JTextPane htmlInfoPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> modelBox;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField partNoField;
    private javax.swing.JComboBox<String> seriesBox;
    // End of variables declaration//GEN-END:variables

}
