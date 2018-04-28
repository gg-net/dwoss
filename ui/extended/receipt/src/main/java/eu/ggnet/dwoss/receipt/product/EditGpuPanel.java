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

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import eu.ggnet.dwoss.common.ui.*;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu.Series;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu.Type;
import eu.ggnet.saft.Dl;

/**
 *
 * @author pascal.perau
 */
public class EditGpuPanel extends javax.swing.JPanel implements IPreClose {

    private Gpu gpu;

    List<Gpu> allgpu;

    private ProductProcessor productProcessor;

    private ComboBoxController<Gpu.Series> series;

    private ComboBoxController<Gpu.Manufacturer> manufacturers;

    public EditGpuPanel(List<Gpu> allgpu) {
        this(Dl.remote().lookup(ProductProcessor.class), allgpu);
    }

    /** Creates new form EditGpuPanel */
    public EditGpuPanel(ProductProcessor productProcessor, List<Gpu> allgpu) {
        this.productProcessor = Objects.requireNonNull(productProcessor, ProductProcessor.class.getSimpleName() + " must not be null");
        this.allgpu = allgpu;

        initComponents();
        manufacturers = new ComboBoxController<>(manufacturerBox, Gpu.Manufacturer.values());
        series = new ComboBoxController<>(seriesBox, Gpu.Series.values());
        seriesBox.setRenderer(new NamedEnumCellRenderer());
        filterSeries();
    }

    public void setDefaults(Gpu.Type type, Gpu.Series gpuSeries) {
        if ( type == Gpu.Type.MOBILE ) mobileCheckBox.setSelected(true);
        else desktopCheckBox.setSelected(true);
        manufacturers.setSelected(gpuSeries.getManufacturer());
        series.setSelected(gpuSeries);
    }

    public void setGpu(Gpu gpu) {
        manufacturers.setSelected(gpu.getManufacturer());
        if ( gpu.getTypes().contains(Gpu.Type.DESKTOP) ) desktopCheckBox.setSelected(true);
        if ( gpu.getTypes().contains(Gpu.Type.MOBILE) ) mobileCheckBox.setSelected(true);
        modelField.setText(gpu.getModel());
        series.setSelected(gpu.getSeries());
        nameField.setText(gpu.getName());
        this.gpu = gpu;
    }

    private Gpu updateGpu(Gpu newGpu) {
        if ( desktopCheckBox.isSelected() ) newGpu.addType(Gpu.Type.DESKTOP);
        else newGpu.removeType(Gpu.Type.DESKTOP);
        if ( mobileCheckBox.isSelected() ) newGpu.addType(Gpu.Type.MOBILE);
        else newGpu.removeType(Gpu.Type.MOBILE);
        newGpu.setSeries(series.getSelected());
        newGpu.setModel(modelField.getText());
        newGpu.setName(nameField.getText());
        return newGpu;
    }

    public Gpu getGpu() {
        return gpu;
    }

    private void filterSeries() {
        series.replaceElements(manufacturers.getSelected().getSeries());
    }

    @Override
    public boolean pre(CloseType type) {
        Gpu testGpu;
        if ( type == CloseType.CANCEL ) return true;
        if ( !(mobileCheckBox.isSelected() || desktopCheckBox.isSelected()) ) {
            JOptionPane.showMessageDialog(this, "Kein Type ausgewählt");
            return false;
        }
        try {
            testGpu = updateGpu(new Gpu());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Frequenz, Kerne oder wirtschaftl. Wert sind keine Zahlen");
            return false;
        }

        for (Gpu gpu1 : allgpu) {
            if ( gpu1.getModel().equals(testGpu.getModel()) && gpu1.getSeries() == testGpu.getSeries() ) {

                if ( (gpu != null && gpu1.getId() != gpu.getId()) || gpu == null ) {
                    String text = "";
                    text += "Die CPU ";
                    text += testGpu.getModel() + " " + testGpu.getSeries().getNote();
                    text += "exestiert schon mit einer anderen ID!\n";
                    text += "Momentane ID:" + (gpu != null ? gpu.getId() : testGpu.getId());
                    JOptionPane.showMessageDialog(this, text);
                    return false;
                }
            }
        }
        try {
            if ( gpu == null ) {
                gpu = productProcessor.create(updateGpu(new Gpu()));
            } else {
                if ( updateGpu(new Gpu()).equalsContent(gpu) ) return true;
                gpu = productProcessor.update(updateGpu(gpu));
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Cpu existiert schon " + e.getMessage());
            return false;
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

        seriesBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        manufacturerBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        modelField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        mobileCheckBox = new javax.swing.JCheckBox();
        desktopCheckBox = new javax.swing.JCheckBox();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), java.awt.Color.black), "GPU Daten bearbeiten / hinzufügen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        setMinimumSize(new java.awt.Dimension(400, 190));
        setPreferredSize(new java.awt.Dimension(400, 190));
        setLayout(new java.awt.GridBagLayout());

        seriesBox.setNextFocusableComponent(modelField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(seriesBox, gridBagConstraints);

        jLabel5.setText("Hersteller*:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel5, gridBagConstraints);

        manufacturerBox.setNextFocusableComponent(mobileCheckBox);
        manufacturerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manufacturerBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(manufacturerBox, gridBagConstraints);

        jLabel9.setText("Serie*:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel9, gridBagConstraints);

        modelField.setNextFocusableComponent(nameField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(modelField, gridBagConstraints);

        jLabel6.setText("Modell*:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel6, gridBagConstraints);

        mobileCheckBox.setText("Mobile");
        mobileCheckBox.setNextFocusableComponent(desktopCheckBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(mobileCheckBox, gridBagConstraints);

        desktopCheckBox.setText("Desktop");
        desktopCheckBox.setNextFocusableComponent(seriesBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(desktopCheckBox, gridBagConstraints);

        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(nameLabel, gridBagConstraints);

        nameField.setToolTipText("Nur ausfüllen, wenn das Model sehr stark vom Präsentationsnamen abweicht");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(nameField, gridBagConstraints);

        typeLabel.setText("Type*:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(typeLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void manufacturerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manufacturerBoxActionPerformed
        filterSeries();
    }//GEN-LAST:event_manufacturerBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox desktopCheckBox;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox manufacturerBox;
    private javax.swing.JCheckBox mobileCheckBox;
    private javax.swing.JTextField modelField;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JComboBox seriesBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Gpu gpu = new Gpu(Type.MOBILE, Series.GEFORCE_500, "580");

        EditGpuPanel view = new EditGpuPanel(new ProductProcessorStub(), new ArrayList<Gpu>());
        view.setGpu(gpu);
        OkCancelDialog<EditGpuPanel> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        System.out.println(view.getGpu());
        System.exit(0);

    }
}
