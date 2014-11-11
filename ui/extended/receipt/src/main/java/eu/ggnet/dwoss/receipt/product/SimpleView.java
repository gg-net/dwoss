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

import eu.ggnet.dwoss.spec.entity.ProductModel;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.entity.ProductSeries;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IView;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.util.IPreClose;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.*;

import eu.ggnet.dwoss.receipt.ProductProcessor;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.format.SpecFormater;

import lombok.Getter;

import static eu.ggnet.saft.core.Client.lookup;

public class SimpleView extends javax.swing.JPanel implements IPreClose, IView {

    @Getter
    private final TradeName manufacturer;

    private NamedComparator namedComparator = new NamedComparator();

    private ProductSpec spec;

    private List<ProductSeries> serieses;

    private List<ProductFamily> filteredFamilies;

    private List<ProductModel> filteredModels;

    private Window parent;

    private ProductProcessor receiptProductLogic;

    private SpecAgent specAgent;

    SimpleView(TradeName manufacturer) {
        this.manufacturer = Objects.requireNonNull(manufacturer, "Manufacturer must not be null");
        if ( !manufacturer.isManufacturer() ) throw new IllegalArgumentException("Manufacturer " + manufacturer + " is not a Manufacturer");
        initComponents();
        receiptProductLogic = lookup(ProductProcessor.class);
        specAgent = lookup(SpecAgent.class);
        this.serieses = specAgent.findAll(ProductSeries.class);
        seriesBox.setRenderer(new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value instanceof ProductSeries ) {
                    ((JLabel)component).setText(((ProductSeries)value).getName());
                } else if ( value == null ) {
                    ((JLabel)component).setText("");
                }
                return component;
            }
        });
        brandBox.setModel(new DefaultComboBoxModel(manufacturer.getBrands().toArray()));
        groupBox.setModel(new DefaultComboBoxModel(EnumSet.complementOf(EnumSet.of(ProductGroup.COMMENTARY)).toArray()));
        updateSeries();
        updateFamily();
        updateModel();
        familyBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkFamily();
            }
        });
        modelBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkModel();
            }
        });
    }

    /**
     * Creates new ProductSpec from the PartNo with some hints.
     * Usefull for existing SopoProducts.
     *
     */
    public SimpleView(TradeName manufacturer, String partNo) {
        this(manufacturer);
        partNoField.setText(partNo);
        editButton.setEnabled(false);
    }

    /**
     * Creates new ProductSpec from the PartNo, but with the special case, that brand and group are fixed.
     */
    public SimpleView(String partNo, TradeName brand, ProductGroup group) {
        this(brand.getManufacturer());
        partNoField.setText(partNo);
        editButton.setEnabled(false);
        brandBox.setEnabled(false);
        groupBox.setEnabled(false);
        brandBox.setSelectedItem(brand);
        groupBox.setSelectedItem(group);
        updateSeries();
        updateFamily();
        updateModel();
    }

    public SimpleView(ProductSpec spec) {
        this(spec.getModel().getFamily().getSeries().getBrand().getManufacturer());
        this.spec = spec;
        partNoField.setText(spec.getPartNo());
        editButton.setEnabled(true);
        brandBox.setEnabled(false);
        groupBox.setEnabled(false);
        ProductSeries series = spec.getModel().getFamily().getSeries();
        brandBox.setSelectedItem(series.getBrand());
        groupBox.setSelectedItem(series.getGroup());
        updateSeries();
        seriesBox.setSelectedItem(series);
        updateFamily();
        familyBox.setSelectedItem(spec.getModel().getFamily().getName());
        updateModel();
        modelBox.setSelectedItem(spec.getModel().getName());
        checkFamily();
        checkModel();
        htmlInfoPane.setText(SpecFormater.toHtml(spec));
    }

    /**
     * Returns a productSpec, which may if new miss the model.
     *
     * @return a ProductSpec.
     */
    public ProductSpec getProductSpec() {
        return spec;
    }

    // TODO: Describe somethere, that in the Editmode the Model is not set to the spec, but must be done in an active transaction.
    // merge productSpec. merge model. set Model. commit
    @Override
    public boolean pre(CloseType type) {
        if ( type != CloseType.OK ) return true;
        String msg = null;
        if ( getModel() == null ) {
            String modelName = (String)modelBox.getSelectedItem();
            if ( modelName == null || modelName.trim().equals("") ) return error("Kein Modell ausgewählt");
            return error("Modell " + modelName + " noch nicht hinzugefügt");
        }
        // The new ProductSpec Mode
        if ( spec == null ) {
            spec = ProductSpec.newInstance(getGroup());
            spec.setPartNo(partNoField.getText());
        }
        return true;
    }

    /**
     * Changes the SeriesBox contents in dependency of the brand and group.
     */
    private void updateSeries() {
        TradeName brand = (TradeName)brandBox.getSelectedItem();
        ProductGroup group = (ProductGroup)groupBox.getSelectedItem();
        List<ProductSeries> filteredSerieses = new ArrayList<ProductSeries>();
        for (ProductSeries series : serieses) {
            if ( series.getBrand().equals(brand) && series.getGroup().equals(group) ) {
                filteredSerieses.add(series);
            }
        }
        filteredSerieses.add(null); // For no Selection
        Collections.sort(filteredSerieses, namedComparator);
        seriesBox.setModel(new DefaultComboBoxModel(filteredSerieses.toArray()));
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
        ProductSeries selectedSeries = (ProductSeries)seriesBox.getSelectedItem();
        filteredFamilies = new ArrayList<ProductFamily>();
        filteredFamilies.add(null);
        if ( selectedSeries == null ) {
            TradeName brand = (TradeName)brandBox.getSelectedItem();
            ProductGroup group = (ProductGroup)groupBox.getSelectedItem();
            for (ProductSeries series : serieses) {
                if ( series.getBrand().equals(brand) && series.getGroup().equals(group) ) {
                    filteredFamilies.addAll(series.getFamilys());
                }
            }
        } else {
            filteredFamilies.addAll(selectedSeries.getFamilys());
        }
        Collections.sort(filteredFamilies, namedComparator);
        String[] names = new String[filteredFamilies.size()];
        for (int i = 0; i < filteredFamilies.size(); i++) {
            ProductFamily family = filteredFamilies.get(i);
            if ( family == null ) names[i] = null;
            else names[i] = family.getName();
        }
        familyBox.setModel(new DefaultComboBoxModel(names));
    }

    @Override
    public void setParent(Window parent) {
        this.parent = parent;
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
        filteredModels = new ArrayList<ProductModel>();
        filteredModels.add(null);
        if ( familyBox.getSelectedIndex() == -1 || filteredFamilies.get(familyBox.getSelectedIndex()) == null ) {
            if ( seriesBox.getSelectedItem() == null ) {
                TradeName brand = (TradeName)brandBox.getSelectedItem();
                ProductGroup group = (ProductGroup)groupBox.getSelectedItem();
                for (ProductSeries series : serieses) {
                    if ( series.getBrand().equals(brand) && series.getGroup().equals(group) ) {
                        for (ProductFamily family : series.getFamilys()) {
                            filteredModels.addAll(family.getModels());
                        }

                    }
                }
            } else {
                ProductSeries selectedSeries = (ProductSeries)seriesBox.getSelectedItem();
                for (ProductFamily family : selectedSeries.getFamilys()) {
                    filteredModels.addAll(family.getModels());
                }
            }
        } else {
            ProductFamily selectFamily = filteredFamilies.get(familyBox.getSelectedIndex());
            filteredModels.addAll(selectFamily.getModels());
        }
        Collections.sort(filteredModels, namedComparator);
        String[] names = new String[filteredModels.size()];
        for (int i = 0; i < filteredModels.size(); i++) {
            ProductModel model = filteredModels.get(i);
            if ( model == null ) names[i] = null;
            else names[i] = model.getName();
        }
        modelBox.setModel(new DefaultComboBoxModel(names));
    }

    private TradeName getBrand() {
        return (TradeName)brandBox.getSelectedItem();
    }

    private ProductGroup getGroup() {
        return (ProductGroup)groupBox.getSelectedItem();
    }

    private ProductSeries getSeries() {
        return (ProductSeries)seriesBox.getSelectedItem();
    }

    private ProductFamily getFamily() {
        if ( familyBox.getEditor().getItem() == null ) return null;
        for (ProductFamily family : filteredFamilies) {
            if ( family != null && family.getName() != null && family.getName().equals(familyBox.getEditor().getItem()) ) return family;
        }
        return null;
    }

    public ProductModel getModel() {
        if ( modelBox.getEditor().getItem() == null ) return null;
        for (ProductModel model : filteredModels) {
            if ( model == null ) continue;
            if ( model.getName() == null ) continue;
            if ( model.getName().equals(modelBox.getEditor().getItem()) ) return model;
        }
        return null;
    }

    private boolean error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void checkModel() {
        if ( getModel() != null ) addModelButton.setEnabled(false);
        else addModelButton.setEnabled(true);
    }

    private void checkFamily() {
        if ( getFamily() != null ) addFamilyButton.setEnabled(false);
        else addFamilyButton.setEnabled(true);
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
        modelBox = new javax.swing.JComboBox();
        familyBox = new javax.swing.JComboBox();
        seriesBox = new javax.swing.JComboBox();
        groupBox = new javax.swing.JComboBox();
        brandBox = new javax.swing.JComboBox();
        editButton = new javax.swing.JButton();
        addFamilyButton = new javax.swing.JButton();
        addModelButton = new javax.swing.JButton();
        partNoField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        htmlInfoPane = new javax.swing.JTextPane();

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
        modelBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
        familyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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

        seriesBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
    }// </editor-fold>//GEN-END:initComponents

    private void brandBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brandBoxActionPerformed
        updateSeries();
        updateFamily();
        updateModel();
    }//GEN-LAST:event_brandBoxActionPerformed

    private void groupBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupBoxActionPerformed
        updateSeries();
        updateFamily();
        updateModel();
    }//GEN-LAST:event_groupBoxActionPerformed

    private void seriesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seriesBoxActionPerformed
        updateFamily();
        updateModel();
    }//GEN-LAST:event_seriesBoxActionPerformed

    private void familyBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyBoxActionPerformed
        updateModel();
        checkFamily();
    }//GEN-LAST:event_familyBoxActionPerformed

    private void addModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelButtonActionPerformed
        String modelName = (String)modelBox.getSelectedItem();
        if ( modelName == null ) {
            error("Keine Modellname hinterlegt!");
            return;
        }
        if ( modelName.startsWith(" ") || modelName.endsWith(" ") ) {
            error("Model hat am Anfang oder Ende Freizeichen, nicht erlaubt");
            return;
        }
        for (ProductSeries series : serieses) {
            for (ProductFamily family : series.getFamilys()) {
                for (ProductModel model : family.getModels()) {
                    if ( model.getName().equals(modelName) ) {
                        error("Modell " + modelName + " existiert schon in " + series.getName() + "/" + family.getName());
                        return; // Found an equal, so nothing to do
                    }
                }
            }
        }
        if ( getSeries() == null ) {
            if ( !warn("Keine Serie und Familie ausgewählt, es werde Standartwerte verwendet.") ) return;
        } else if ( getFamily() == null ) {
            if ( !warn("Keine Familie ausgewählt, es wird ein Standartwert verwendet.") ) return;
        }
        ProductModel model = receiptProductLogic.create(getBrand(), getGroup(), getSeries(), getFamily(), modelName);
        // TODO: Add Model to local list in a better way
        // TODO: And show the active backgroundprogress.
        JOptionPane.showMessageDialog(this, "Modell " + model.getName() + " wurde hinzugefügt.\nAktualisiere Lokale Liste.");
        parent.setEnabled(false);
        serieses = specAgent.findAll(ProductSeries.class);
        parent.setEnabled(true);
        updateSeries();
        updateFamily();
        updateModel();
        seriesBox.setSelectedItem(model.getFamily().getSeries());
        familyBox.setSelectedItem(model.getFamily().getName());
        modelBox.setSelectedItem(model.getName());
        checkFamily();
        checkModel();
    }//GEN-LAST:event_addModelButtonActionPerformed

    private void addFamilyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFamilyButtonActionPerformed
        String familyName = (String)familyBox.getSelectedItem();
        if ( familyName == null ) {
            error("Keine Familienname hinterlegt!");
            return;
        }
        if ( familyName.startsWith(" ") || familyName.endsWith(" ") ) {
            error("Familie hat am Anfang oder Ende Freizeichen, nicht erlaubt");
            return;
        }
        for (ProductSeries series : serieses) {
            for (ProductFamily family : series.getFamilys()) {
                if ( family.getName().equals(familyName) ) {
                    error("Familie " + familyName + " existiert schon in " + series.getName());
                    return; // Found an equal, so nothing to do
                }
            }
        }
        if ( getSeries() == null ) {
            if ( !warn("Keine Serie ausgewählt, es wird ein Standartwert verwendet.") ) return;
        }
        ProductFamily family = receiptProductLogic.create(getBrand(), getGroup(), getSeries(), familyName);
        // TODO: Add Family to local list in a better way
        // TODO: And show the active backgroundprogress.
        JOptionPane.showMessageDialog(this, "Familie " + family.getName() + " wurde hinzugefügt.\nAktualisiere Lokale Liste.");
        parent.setEnabled(false);
        serieses = specAgent.findAll(ProductSeries.class);
        parent.setEnabled(true);
        updateSeries();
        updateFamily();
        updateModel();
        seriesBox.setSelectedItem(family.getSeries());
        familyBox.setSelectedItem(family.getName());
        checkFamily();
    }//GEN-LAST:event_addFamilyButtonActionPerformed

    private void modelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelBoxActionPerformed
        checkModel();
    }//GEN-LAST:event_modelBoxActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        String result = JOptionPane.showInputDialog(this, "Artikelnummer ändern:", spec.getPartNo());
        if ( result == null || result.trim().equals("") || result.equals(spec.getPartNo()) ) return; // Cancel and Nothing
        if ( result.startsWith(" ") || result.endsWith(" ") ) {
            error("ArtikelNummer hat am Anfang oder Ende Freizeichen, nicht erlaubt");
            return;
        }
        ProductSpec localSpec = specAgent.findProductSpecByPartNoEager(result);
        if ( localSpec != null ) {
            error("Artikel exitiert schon : " + localSpec.getModel().getName() + " (" + localSpec.getPartNo() + ")");
            return;
        }
        spec.setPartNo(result); // Will be persisted later on.
        partNoField.setText(result);
    }//GEN-LAST:event_editButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFamilyButton;
    private javax.swing.JButton addModelButton;
    private javax.swing.JComboBox brandBox;
    private javax.swing.JButton editButton;
    private javax.swing.JComboBox familyBox;
    private javax.swing.JComboBox groupBox;
    private javax.swing.JTextPane htmlInfoPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox modelBox;
    private javax.swing.JTextField partNoField;
    private javax.swing.JComboBox seriesBox;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SimpleView view = new SimpleView(TradeName.SAMSUNG, "LX.AAAAA.BBB");
        // SimpleView view = new SimpleView(receiptCos.spec().findAll().get(0), receiptCos);

        OkCancelDialog<SimpleView> dialog = new OkCancelDialog<>("Übersicht", view);
//        dialog.setSize(new Dimension(400, 200));
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            System.out.println(view.getProductSpec());
            System.out.println(view.getModel());
        }
        System.exit(0);
    }
}
