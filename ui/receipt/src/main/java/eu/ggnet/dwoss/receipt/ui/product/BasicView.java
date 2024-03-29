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

import javax.swing.DefaultComboBoxModel;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.swing.NamedEnumCellRenderer;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.ui.CheckBoxTableNoteModel;
import eu.ggnet.dwoss.receipt.ui.SwingTraversalUtil;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.Color;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.VideoPort;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.api.ShopCategory;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class BasicView extends AbstractView {

    CheckBoxTableNoteModel<Extra> extrasModel = new CheckBoxTableNoteModel(Arrays.asList(Extra.class.getEnumConstants()), "Ausstattung");

    CheckBoxTableNoteModel<VideoPort> videoPortModel = new CheckBoxTableNoteModel(Arrays.asList(VideoPort.class.getEnumConstants()), "Ausstattung");

    private BasicSpec basicSpec;

    private long modelId;

    /** Creates new form BasicView */
    public BasicView() {
        initComponents();

        extrasTable.setModel(extrasModel);
        extrasModel.setTable(extrasTable);
        videoPortTable.setModel(videoPortModel);
        videoPortModel.setTable(videoPortTable);

        Color[] colors = Arrays.copyOf(Color.values(), Color.values().length + 1);

        NamedEnumCellRenderer renderer = new NamedEnumCellRenderer();
        colorBox.setModel(new DefaultComboBoxModel(colors));
        colorBox.setRenderer(renderer);
        colorBox.setSelectedItem(null);
        
        List<ShopCategory> categories = new ArrayList<>(Dl.remote().lookup(UniqueUnitApi.class).findAllShopCategories());
        categories.add(0,null);    
        shopCategoryComboBox.setRenderer(new ShopCategoryCellRenderer());
        shopCategoryComboBox.setModel(new DefaultComboBoxModel<>(categories.toArray(ShopCategory[]::new)));
        shopCategoryComboBox.setSelectedItem(null);

        SwingTraversalUtil.spaceSelection(extrasTable);
        SwingTraversalUtil.spaceSelection(videoPortTable);
    }

    @Override
    public void accept(SpecAndModel sam) {
        BasicSpec inSpec = (BasicSpec)Objects.requireNonNull(sam, "sam must not be null").spec();
        modelId = sam.modelId();
        gtinTextField.setText(Long.toString(sam.gtin()));        
        shopCategoryComboBox.setSelectedItem(sam.nullableShopCategory());
        rchCheckBox.setSelected(sam.rch());
        Set<Extra> extras = inSpec.getDefaultExtras();
        extras.addAll(inSpec.getExtras());
        extrasModel.setFiltered(extras);
        extrasModel.setMarked(inSpec.getExtras());
        colorBox.setSelectedItem(inSpec.getColor());
        videoPortModel.setMarked(inSpec.getVideoPorts());
        noteArea.setText(inSpec.getComment());
        this.basicSpec = inSpec;
    }

    @Override
    public SpecAndModel getResult() {
        if ( basicSpec == null ) throw new IllegalStateException("SecondState Ui, Consumer.accept() must be called first");
        basicSpec.setColor((Color)colorBox.getSelectedItem());
        basicSpec.setExtras(extrasModel.getMarked());
        basicSpec.setVideoPorts(videoPortModel.getMarked());
        basicSpec.setComment(noteArea.getText());
        return new SpecAndModel(basicSpec, modelId, getGtin(),shopCategoryComboBox.getItemAt(shopCategoryComboBox.getSelectedIndex()),rchCheckBox.isSelected());
    }

    // only internal use
    private long getGtin() {
        if ( StringUtils.isBlank(gtinTextField.getText()) ) return 0;
        return Long.parseLong(gtinTextField.getText());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colorBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        noteArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        videoPortTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        extrasTable = new javax.swing.JTable();
        gtinLabel = new javax.swing.JLabel();
        rchCheckBox = new javax.swing.JCheckBox();
        gtinTextField = new javax.swing.JFormattedTextField();
        shopCategoryLabel = new javax.swing.JLabel();
        shopCategoryComboBox = new javax.swing.JComboBox<>();

        colorBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        colorBox.setMinimumSize(new java.awt.Dimension(72, 28));
        colorBox.setPreferredSize(new java.awt.Dimension(72, 28));

        jLabel1.setText("Farbe:");

        noteArea.setColumns(20);
        noteArea.setLineWrap(true);
        noteArea.setRows(5);
        noteArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(noteArea);

        jLabel2.setText("Bemerkung/Beschreibung");

        videoPortTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(videoPortTable);

        extrasTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(extrasTable);

        gtinLabel.setText("GTIN/EAN:");

        rchCheckBox.setText("Reverse Charge Kandidat");

        gtinTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("0000000000000"))));

        shopCategoryLabel.setText("Shop Kartegory: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colorBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gtinLabel)
                        .addGap(2, 2, 2)
                        .addComponent(gtinTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(rchCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(shopCategoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(shopCategoryComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rchCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(shopCategoryLabel)
                            .addComponent(shopCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(gtinLabel)
                            .addComponent(gtinTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(colorBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox colorBox;
    private javax.swing.JTable extrasTable;
    private javax.swing.JLabel gtinLabel;
    private javax.swing.JFormattedTextField gtinTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea noteArea;
    private javax.swing.JCheckBox rchCheckBox;
    private javax.swing.JComboBox<ShopCategory> shopCategoryComboBox;
    private javax.swing.JLabel shopCategoryLabel;
    private javax.swing.JTable videoPortTable;
    // End of variables declaration//GEN-END:variables

}
