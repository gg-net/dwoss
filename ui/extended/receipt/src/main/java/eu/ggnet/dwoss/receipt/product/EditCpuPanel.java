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

import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Dl;

/**
 *
 * @author pascal.perau
 */
public class EditCpuPanel extends javax.swing.JPanel implements IPreClose {

    private Cpu cpu;

    private List<Cpu> allCpus;

    private ProductProcessor productProcessor;

    private ComboBoxController<Cpu.Series> series;

    private ComboBoxController<Cpu.Manufacturer> manufacturers;

    public EditCpuPanel(List<Cpu> cpus) {
        this(Dl.remote().lookup(ProductProcessor.class), cpus);
    }

    /**
     * Creates new form editCpuPanel
     */
    public EditCpuPanel(ProductProcessor logic, List<Cpu> cpus) {
        this.productProcessor = Objects.requireNonNull(logic, ProductProcessor.class.getSimpleName() + " must not be null");
        this.allCpus = cpus;
        initComponents();
        manufacturers = new ComboBoxController<>(manufacturerBox, Cpu.Manufacturer.values());
        series = new ComboBoxController<>(seriesBox, Cpu.Series.values());
        seriesBox.setRenderer(new NamedEnumCellRenderer());
        filterSeries();
    }

    public void setDefaults(Cpu.Type type, Cpu.Series cpuSeries) {
        if ( type == Cpu.Type.MOBILE ) mobileCheckBox.setSelected(true);
        else desktopCheckBox.setSelected(true);
        if ( Cpu.Manufacturer.AMD.getSeries().contains(cpuSeries) ) manufacturers.setSelected(Cpu.Manufacturer.AMD);
        else manufacturers.setSelected(Cpu.Manufacturer.AMD);
        manufacturers.setSelected(cpuSeries.getManufacturer());
        series.setSelected(cpuSeries);
    }

    /**
     * @param cpu
     */
    public void setCpu(Cpu cpu) {
        manufacturers.setSelected(cpu.getManufacturer());
        if ( cpu.getTypes().contains(Cpu.Type.DESKTOP) ) desktopCheckBox.setSelected(true);
        if ( cpu.getTypes().contains(Cpu.Type.MOBILE) ) mobileCheckBox.setSelected(true);
        modelField.setText(cpu.getModel());
        series.setSelected(cpu.getSeries());
        nameField.setText(cpu.getName());
        this.cpu = cpu;
    }

    private Cpu updateCpu(Cpu newCpu) {
        if ( desktopCheckBox.isSelected() ) newCpu.addType(Cpu.Type.DESKTOP);
        else newCpu.removeType(Cpu.Type.DESKTOP);
        if ( mobileCheckBox.isSelected() ) newCpu.addType(Cpu.Type.MOBILE);
        else newCpu.removeType(Cpu.Type.MOBILE);
        newCpu.setSeries(series.getSelected());
        newCpu.setModel(modelField.getText());
        newCpu.setName(nameField.getText());
        return newCpu;
    }

    public Cpu getCpu() {
        return cpu;
    }

    @Override
    public boolean pre(CloseType type) {
        Cpu testCpu;

        if ( type == CloseType.CANCEL ) return true;
        if ( !(mobileCheckBox.isSelected() || desktopCheckBox.isSelected()) ) {
            JOptionPane.showMessageDialog(this, "Kein Type ausgewählt");
            return false;
        }

        try {
            testCpu = updateCpu(new Cpu());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Frequenz, Kerne oder wirtschaftl. Wert sind keine Zahlen");
            return false;
        }

        for (Cpu cpu1 : allCpus) {
            if ( cpu1.getModel().equals(testCpu.getModel()) && cpu1.getSeries().equals(testCpu.getSeries()) ) {
                if ( (cpu != null && cpu1.getId() != cpu.getId()) || cpu == null ) {
                    String text = "";
                    text += "Die CPU ";
                    text += testCpu.getModel() + " " + testCpu.getSeries().getNote();
                    text += "exestiert schon mit einer anderen ID!\n";
                    text += "Momentane ID:" + (cpu != null ? cpu.getId() : testCpu.getId());
                    text += " ID der schon existierenden:" + cpu1.getId();
                    JOptionPane.showMessageDialog(this, text);
                    return false;
                }
            }
        }
        try {
            if ( cpu == null ) {
                cpu = productProcessor.create(updateCpu(new Cpu()));
            } else {
                if ( updateCpu(new Cpu()).equalsContent(cpu) ) return true;
                cpu = productProcessor.update(updateCpu(cpu));
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Cpu existiert schon " + e.getMessage());
            return false;
        }
        return true;
    }

    private void filterSeries() {
        series.replaceElements(manufacturers.getSelected().getSeries());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        manufacturerBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        seriesBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        modelField = new javax.swing.JTextField();
        mobileCheckBox = new javax.swing.JCheckBox();
        desktopCheckBox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), java.awt.Color.black), "Cpu Daten bearbeiten / hinzufügen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        manufacturerBox.setNextFocusableComponent(seriesBox);
        manufacturerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manufacturerBoxActionPerformed(evt);
            }
        });

        jLabel5.setText("Hersteller*:");

        seriesBox.setNextFocusableComponent(modelField);

        jLabel9.setText("Serie*:");

        jLabel6.setText("Modell*:");

        modelField.setNextFocusableComponent(mobileCheckBox);

        mobileCheckBox.setText("Mobility");
        mobileCheckBox.setNextFocusableComponent(desktopCheckBox);

        desktopCheckBox.setText("Desktop");

        jLabel4.setText("Name:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(manufacturerBox, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mobileCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(desktopCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seriesBox, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(modelField, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(manufacturerBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seriesBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mobileCheckBox)
                    .addComponent(desktopCheckBox)
                    .addComponent(jLabel4)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void manufacturerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manufacturerBoxActionPerformed
        filterSeries();
    }//GEN-LAST:event_manufacturerBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox desktopCheckBox;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox manufacturerBox;
    private javax.swing.JCheckBox mobileCheckBox;
    private javax.swing.JTextField modelField;
    private javax.swing.JTextField nameField;
    private javax.swing.JComboBox seriesBox;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Cpu cpu = new Cpu(Cpu.Series.CORE_I7, "930", Cpu.Type.DESKTOP, 3.00, 4);

        EditCpuPanel view = new EditCpuPanel(new ProductProcessorStub(), new ArrayList<Cpu>());
        view.setCpu(cpu);
        OkCancelDialog<EditCpuPanel> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        if ( create.getCloseType() == CloseType.OK ) {
            System.out.println(view.getCpu());
        }
        System.exit(0);
    }
}
