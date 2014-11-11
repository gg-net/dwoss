/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.dossier;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import eu.ggnet.saft.core.UserPreferences;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.entity.Document.Directive;

import eu.ggnet.dwoss.redtape.format.DossierFormater;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import eu.ggnet.dwoss.util.ComboBoxController;
import eu.ggnet.dwoss.util.HtmlDialog;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author bastian.venz
 */
public class DossierFilterView extends javax.swing.JFrame {

    private static DossierFilterView instance;

    /**
     * Returns a single Instance of this view, initalizing and showing it.
     */
    public static void showSingleInstance() {
        if ( instance == null ) {
            instance = new DossierFilterView();
            DossierFilterModel model = new DossierFilterModel();
            DossierFilterController controller = new DossierFilterController();
            instance.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
            lookup(UserPreferences.class).loadLocation(instance);
            instance.setController(controller);
            controller.setView(instance);
            instance.setModel(model);
            controller.setModel(model);
            instance.setVisible(true);
        } else {
            instance.toFront();
            if ( instance.getState() == JFrame.ICONIFIED ) instance.setState(JFrame.NORMAL);
        }
    }

    private final ComboBoxController<String> directiveFilter;

    private final Map<String, Directive> directivNames = new HashMap<>();

    private final ComboBoxController<String> documentTypeFilter;

    private final Map<String, DocumentType> documentTypeNames = new HashMap<>();

    private final ComboBoxController<String> paymentMethodFilter;

    private final Map<String, PaymentMethod> paymentTypeNames = new HashMap<>();

    private DossierFilterController controller;

    private final JPopupMenu dossierPopup;

    private DossierFilterModel filterModel;

    /** Creates new form DossierFilterDialog */
    public DossierFilterView() {
        initComponents();
        for (Directive directive : Directive.values()) {
            directivNames.put(directive.getName(), directive);
        }
        directiveFilter = new ComboBoxController<>(directiveComboBox, directivNames.keySet());

        for (DocumentType type : DocumentType.values()) {
            documentTypeNames.put(type.getName(), type);
        }
        documentTypeFilter = new ComboBoxController<>(documentTypeComboBox, documentTypeNames.keySet());

        for (PaymentMethod method : PaymentMethod.values()) {
            paymentTypeNames.put(method.getNote(), method);
        }
        paymentMethodFilter = new ComboBoxController<>(paymentComboBox, paymentTypeNames.keySet());
        this.pack();
        this.revalidate();

        dossierPopup = buildDossierPopup();
    }

    public void setController(DossierFilterController controller) {
        if ( controller == null ) return;
        this.controller = controller;
    }

    public void setModel(DossierFilterModel model) {
        filterModel = model;
        dossierTable.setModel(model);
        model.setTable(dossierTable);
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

        yearButtonGroup = new javax.swing.ButtonGroup();
        customerFilter = new javax.swing.ButtonGroup();
        exactlyBriefedGroup = new javax.swing.ButtonGroup();
        bookingClosedGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        filterPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        dossierIdField = new javax.swing.JTextField();
        dossierIdBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        directiveCheckBox = new javax.swing.JCheckBox();
        directiveComboBox = new javax.swing.JComboBox();
        sysCustomerButton = new javax.swing.JRadioButton();
        normalCustomerButton = new javax.swing.JRadioButton();
        wayneCustomer = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        documentTypeCheckbox = new javax.swing.JCheckBox();
        documentTypeComboBox = new javax.swing.JComboBox();
        invertTypeBox = new javax.swing.JCheckBox();
        isExactlyBriefdButton = new javax.swing.JRadioButton();
        isNotExactlyBriefdButton = new javax.swing.JRadioButton();
        wayneExactlyBriefdButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        isBookingClosedButton = new javax.swing.JRadioButton();
        isNotBookingClosedButton = new javax.swing.JRadioButton();
        wayneBookingClosedButton = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        paymentComboBox = new javax.swing.JComboBox();
        paymentCheckBox = new javax.swing.JCheckBox();
        loadButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        invertFilterBox = new javax.swing.JCheckBox();
        conditionPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        dossierTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Aufträge nach Status");
        setMinimumSize(new java.awt.Dimension(400, 200));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 12), java.awt.Color.black)); // NOI18N
        filterPanel.setMinimumSize(new java.awt.Dimension(600, 500));
        filterPanel.setPreferredSize(new java.awt.Dimension(600, 500));

        dossierIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dossierIdFieldActionPerformed(evt);
            }
        });
        dossierIdField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dossierIdFieldKeyReleased(evt);
            }
        });

        dossierIdBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dossierIdBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Vorgangs Id");

        jLabel1.setText("Directive");

        directiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directiveCheckBoxActionPerformed(evt);
            }
        });

        directiveComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        directiveComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directiveComboBoxActionPerformed(evt);
            }
        });

        customerFilter.add(sysCustomerButton);
        sysCustomerButton.setText("ja");
        sysCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerFilterActionPerfomed(evt);
            }
        });

        customerFilter.add(normalCustomerButton);
        normalCustomerButton.setText("nein");
        normalCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerFilterActionPerfomed(evt);
            }
        });

        customerFilter.add(wayneCustomer);
        wayneCustomer.setSelected(true);
        wayneCustomer.setText("egal");

        jLabel4.setText("Dokumenten Typ");

        documentTypeCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                documentTypeCheckboxActionPerformed(evt);
            }
        });

        documentTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        documentTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                documentTypeComboBoxActionPerformed(evt);
            }
        });

        invertTypeBox.setText("enthält nicht");
        invertTypeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertTypeBoxActionPerformed(evt);
            }
        });

        exactlyBriefedGroup.add(isExactlyBriefdButton);
        isExactlyBriefdButton.setText("ja");
        isExactlyBriefdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactlyBriefedButtonAction(evt);
            }
        });

        exactlyBriefedGroup.add(isNotExactlyBriefdButton);
        isNotExactlyBriefdButton.setText("nein");
        isNotExactlyBriefdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactlyBriefedButtonAction(evt);
            }
        });

        exactlyBriefedGroup.add(wayneExactlyBriefdButton);
        wayneExactlyBriefdButton.setSelected(true);
        wayneExactlyBriefdButton.setText("egal");
        wayneExactlyBriefdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactlyBriefedButtonAction(evt);
            }
        });

        jLabel2.setText("Liegt dem Kunden das Dokument vor");

        jLabel5.setText("Ist es buchhalterisch abgeschlossen?");

        bookingClosedGroup.add(isBookingClosedButton);
        isBookingClosedButton.setText("ja");
        isBookingClosedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBookingClosedButtonAction(evt);
            }
        });

        bookingClosedGroup.add(isNotBookingClosedButton);
        isNotBookingClosedButton.setText("nein");
        isNotBookingClosedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBookingClosedButtonAction(evt);
            }
        });

        bookingClosedGroup.add(wayneBookingClosedButton);
        wayneBookingClosedButton.setSelected(true);
        wayneBookingClosedButton.setText("egal");
        wayneBookingClosedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBookingClosedButtonAction(evt);
            }
        });

        jLabel6.setText("Sollen nur Systemkunden angezeigt werden?");

        jLabel7.setText("Zahlungsbedingung");

        paymentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        paymentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentComboBoxActionPerformed(evt);
            }
        });

        paymentCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentCheckBoxActionPerformed(evt);
            }
        });

        loadButton.setFont(new java.awt.Font("DejaVu Sans", 2, 12)); // NOI18N
        loadButton.setText("Die nächsten 100 Vorgänge laden");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3)
            .addComponent(jLabel1)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(dossierIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dossierIdBox))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(directiveComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(directiveCheckBox))
            .addComponent(jLabel4)
            .addComponent(invertTypeBox)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(isExactlyBriefdButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isNotExactlyBriefdButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wayneExactlyBriefdButton))
            .addComponent(jLabel2)
            .addComponent(jLabel5)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(isBookingClosedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isNotBookingClosedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wayneBookingClosedButton))
            .addComponent(jLabel6)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(sysCustomerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(normalCustomerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wayneCustomer))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(paymentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(paymentCheckBox))
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(documentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(documentTypeCheckbox)))
            .addComponent(jLabel7)
            .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dossierIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dossierIdBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(directiveComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directiveCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isExactlyBriefdButton)
                    .addComponent(isNotExactlyBriefdButton)
                    .addComponent(wayneExactlyBriefdButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isBookingClosedButton)
                    .addComponent(isNotBookingClosedButton)
                    .addComponent(wayneBookingClosedButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sysCustomerButton)
                    .addComponent(normalCustomerButton)
                    .addComponent(wayneCustomer))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(documentTypeCheckbox)
                    .addComponent(documentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invertTypeBox)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(paymentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paymentCheckBox))
                .addGap(18, 18, 18)
                .addComponent(loadButton)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Konditionen"));

        invertFilterBox.setText("Filter invertieren");
        invertFilterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertFilterBoxActionPerformed(evt);
            }
        });

        conditionPanel.setLayout(new javax.swing.BoxLayout(conditionPanel, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(conditionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(invertFilterBox)
                        .addGap(0, 132, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(invertFilterBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(conditionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filterPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filterPanel, gridBagConstraints);

        dossierTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        dossierTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dossierTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dossierTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(dossierTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1082, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1082, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dossierTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dossierTableMouseClicked

        if ( !filterModel.getLines().isEmpty() && SwingUtilities.isRightMouseButton(evt) ) {
            int row = dossierTable.rowAtPoint(evt.getPoint());
            dossierTable.getSelectionModel().setSelectionInterval(row, row);
            dossierPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        if ( evt.getClickCount() == 2 && !filterModel.getLines().isEmpty() && SwingUtilities.isLeftMouseButton(evt) ) {
            int row = dossierTable.rowAtPoint(evt.getPoint());
            dossierTable.getSelectionModel().setSelectionInterval(row, row);
            controller.openDossierDetailViewer(filterModel.getSelected());
        }

    }//GEN-LAST:event_dossierTableMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ( controller != null ) controller.cancelLoader();
        lookup(UserPreferences.class).storeLocation(instance);
        instance = null;
    }//GEN-LAST:event_formWindowClosing

    private void directiveComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directiveComboBoxActionPerformed
        if ( controller == null ) return;
        if ( !directiveCheckBox.isSelected() ) return;
        controller.filterDirective(directivNames.get(directiveFilter.getSelected()), directiveCheckBox.isSelected());
    }//GEN-LAST:event_directiveComboBoxActionPerformed

    private void directiveCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directiveCheckBoxActionPerformed
        if ( controller == null ) return;
        controller.filterDirective(directivNames.get(directiveFilter.getSelected()), directiveCheckBox.isSelected());
    }//GEN-LAST:event_directiveCheckBoxActionPerformed

    private void dossierIdBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dossierIdBoxActionPerformed
        if ( controller == null ) {
            return;
        }
        controller.filterDossierId(dossierIdField.getText(), dossierIdBox.isSelected());
    }//GEN-LAST:event_dossierIdBoxActionPerformed

    private void dossierIdFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dossierIdFieldKeyReleased
        dossierIdBoxActionPerformed(null);
    }//GEN-LAST:event_dossierIdFieldKeyReleased

    private void dossierIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dossierIdFieldActionPerformed
        dossierIdBoxActionPerformed(null);
    }//GEN-LAST:event_dossierIdFieldActionPerformed

    private void invertFilterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertFilterBoxActionPerformed
        if ( controller == null ) return;
        controller.filterInvers(invertFilterBox.isSelected());

    }//GEN-LAST:event_invertFilterBoxActionPerformed

    private void customerFilterActionPerfomed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerFilterActionPerfomed
        if ( controller == null ) return;

        if ( wayneCustomer.isSelected() ) {
            controller.filterCustomer(true, true);
        } else {
            controller.filterCustomer(sysCustomerButton.isSelected(), normalCustomerButton.isSelected());
        }

    }//GEN-LAST:event_customerFilterActionPerfomed

    private void documentTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentTypeComboBoxActionPerformed
        if ( controller == null ) return;
        controller.filterType(documentTypeNames.get(documentTypeFilter.getSelected()), documentTypeCheckbox.isSelected());
    }//GEN-LAST:event_documentTypeComboBoxActionPerformed

    private void documentTypeCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentTypeCheckboxActionPerformed
        documentTypeComboBoxActionPerformed(null);
    }//GEN-LAST:event_documentTypeCheckboxActionPerformed

    private void invertTypeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertTypeBoxActionPerformed
        if ( controller == null ) return;
        controller.filterInverseType(invertTypeBox.isSelected());
    }//GEN-LAST:event_invertTypeBoxActionPerformed

    private void exactlyBriefedButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exactlyBriefedButtonAction
        if ( controller == null ) return;

        if ( wayneExactlyBriefdButton.isSelected() ) {
            controller.filterExcatlyBriefed(true, true);
        } else {
            controller.filterExcatlyBriefed(isExactlyBriefdButton.isSelected(), isNotExactlyBriefdButton.isSelected());
        }

    }//GEN-LAST:event_exactlyBriefedButtonAction

    private void isBookingClosedButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isBookingClosedButtonAction
        if ( controller == null ) return;

        if ( wayneBookingClosedButton.isSelected() ) {
            controller.filterExcatlyBriefed(true, true);
        } else {
            controller.filterExcatlyBriefed(isBookingClosedButton.isSelected(), isNotBookingClosedButton.isSelected());
        }
    }//GEN-LAST:event_isBookingClosedButtonAction

    private void paymentCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paymentCheckBoxActionPerformed

    private void paymentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentComboBoxActionPerformed
        if ( controller == null ) return;
        controller.filterPaymentMethod(paymentTypeNames.get(paymentMethodFilter.getSelected()), paymentCheckBox.isSelected());
    }//GEN-LAST:event_paymentComboBoxActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        controller.loadNextHundred();
    }//GEN-LAST:event_loadButtonActionPerformed

    private JPopupMenu buildDossierPopup() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem detailsItem = new JMenuItem(new AbstractAction("Details") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openDossierDetailViewer(filterModel.getSelected());
            }
        });
        detailsItem.setText("Details");

        JMenuItem historyItem = new JMenuItem(new AbstractAction("Verlauf") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HtmlDialog dialog = new HtmlDialog(DossierFilterView.this, Dialog.ModalityType.MODELESS);

                dialog.setText(DossierFormater.toHtmlHistory(filterModel.getSelected()));
                dialog.setVisible(true);
            }
        });
        historyItem.setText("Verlauf");

        menu.add(detailsItem);
        menu.add(historyItem);
        return menu;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.ButtonGroup bookingClosedGroup;
    public javax.swing.JPanel conditionPanel;
    javax.swing.ButtonGroup customerFilter;
    javax.swing.JCheckBox directiveCheckBox;
    javax.swing.JComboBox directiveComboBox;
    javax.swing.JCheckBox documentTypeCheckbox;
    javax.swing.JComboBox documentTypeComboBox;
    javax.swing.JCheckBox dossierIdBox;
    javax.swing.JTextField dossierIdField;
    javax.swing.JTable dossierTable;
    javax.swing.ButtonGroup exactlyBriefedGroup;
    javax.swing.JPanel filterPanel;
    javax.swing.JCheckBox invertFilterBox;
    javax.swing.JCheckBox invertTypeBox;
    javax.swing.JRadioButton isBookingClosedButton;
    javax.swing.JRadioButton isExactlyBriefdButton;
    javax.swing.JRadioButton isNotBookingClosedButton;
    javax.swing.JRadioButton isNotExactlyBriefdButton;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel4;
    javax.swing.JLabel jLabel5;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JPanel jPanel4;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JButton loadButton;
    javax.swing.JRadioButton normalCustomerButton;
    javax.swing.JCheckBox paymentCheckBox;
    javax.swing.JComboBox paymentComboBox;
    javax.swing.JProgressBar progressBar;
    javax.swing.JRadioButton sysCustomerButton;
    javax.swing.JRadioButton wayneBookingClosedButton;
    javax.swing.JRadioButton wayneCustomer;
    javax.swing.JRadioButton wayneExactlyBriefdButton;
    javax.swing.ButtonGroup yearButtonGroup;
    // End of variables declaration//GEN-END:variables
}
