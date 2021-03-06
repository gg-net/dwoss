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

import eu.ggnet.dwoss.core.widget.swing.ComboBoxController;
import eu.ggnet.dwoss.core.widget.swing.NamedEnumCellRenderer;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display.Ration;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display.Resolution;

/**
 *
 *
 * @author oliver.guenther
 */
public class DisplayPanel extends javax.swing.JPanel {

    private Display original;

    private final ComboBoxController<Display.Size> sizes;

    private final ComboBoxController<Display.Ration> rations;

    private final ComboBoxController<Resolution> resolutions;

    private final ButtonGroupController<Display.Type> typeGroup;

    /**
     * Creates new form PartsDisplayDataPanel
     */
    public DisplayPanel() {
        initComponents();
        displaySizeBox.setRenderer(new NamedEnumCellRenderer());
        sizes = new ComboBoxController<>(displaySizeBox, Display.Size.orderedValues());
        displayRatioBox.setRenderer(new NamedEnumCellRenderer());
        rations = new ComboBoxController<>(displayRatioBox, Display.Ration.orderedValues());
        displayMaxResBox.setRenderer(new NamedEnumCellRenderer());
        resolutions = new ComboBoxController<>(displayMaxResBox, Resolution.orderedValues());
        typeGroup = new ButtonGroupController<>(displayMatButton, displayCrystalBrightButton, Display.Type.MATT, Display.Type.CRYSTAL_BRIGHT);
        typeGroup.setSelected(Display.Type.CRYSTAL_BRIGHT);
        rations.setSelected(Ration.SIXTEEN_TO_NINE);
    }

    public void setDisplay(Display display) {
        if ( display == null ) return;
        rations.setSelected(display.getRation());
        sizes.setSelected(display.getSize());
        resolutions.setSelected(display.getResolution());
        typeGroup.setSelected(display.getType());
        if ( display.isLed() ) {
            displayLedCheck.setSelected(true);
        } else {
            displayLedCheck.setSelected(false);
        }
        this.original = display;
    }

    public Display getDisplay() {
        Display display = new Display();
        display.setRation(rations.getSelected());
        display.setResolution(resolutions.getSelected());
        display.setSize(sizes.getSelected());
        display.setType(typeGroup.getSelected());
        if ( displayLedCheck.isSelected() ) {
            display.setLed(true);
        } else {
            display.setLed(false);
        }
        if ( display.equalsContent(original) ) return original;
        else return display;
    }

    /**
     * This method is called DisplayPanel.java:82from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayTypeGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        displayMatButton = new javax.swing.JRadioButton();
        displayCrystalBrightButton = new javax.swing.JRadioButton();
        displayRatioBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        displaySizeBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        displayLedCheck = new javax.swing.JCheckBox();
        displayMaxResBox = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "Display Daten", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        displayTypeGroup.add(displayMatButton);
        displayMatButton.setText("Matt");
        displayMatButton.setNextFocusableComponent(displayCrystalBrightButton);

        displayTypeGroup.add(displayCrystalBrightButton);
        displayCrystalBrightButton.setText("Crystal Bright");
        displayCrystalBrightButton.setNextFocusableComponent(displayLedCheck);

        displayRatioBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        displayRatioBox.setNextFocusableComponent(displaySizeBox);

        jLabel2.setText("Max. Auflösung:");

        displaySizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        displaySizeBox.setNextFocusableComponent(displayMaxResBox);

        jLabel1.setText("Display Größe:");

        jLabel3.setText("Bildverhältniss:");

        displayLedCheck.setText("LED");

        displayMaxResBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        displayMaxResBox.setNextFocusableComponent(displayMatButton);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(displayMatButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(displayCrystalBrightButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(displayLedCheck))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(17, 17, 17)
                        .addComponent(displayRatioBox, 0, 183, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(displaySizeBox, 0, 183, Short.MAX_VALUE)
                            .addComponent(displayMaxResBox, 0, 183, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(displayRatioBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(displaySizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(displayMaxResBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(displayMatButton)
                    .addComponent(displayCrystalBrightButton)
                    .addComponent(displayLedCheck))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton displayCrystalBrightButton;
    private javax.swing.JCheckBox displayLedCheck;
    private javax.swing.JRadioButton displayMatButton;
    private javax.swing.JComboBox displayMaxResBox;
    private javax.swing.JComboBox displayRatioBox;
    private javax.swing.JComboBox displaySizeBox;
    private javax.swing.ButtonGroup displayTypeGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
