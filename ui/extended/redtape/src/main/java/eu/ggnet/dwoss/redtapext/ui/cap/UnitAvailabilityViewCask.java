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
package eu.ggnet.dwoss.redtapext.ui.cap;

import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.experimental.Ops;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.common.ui.HtmlPane;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.stock.upi.StockUpi;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.dwoss.common.ui.MainComponent;
import eu.ggnet.saft.experimental.ops.SelectionEnhancer;
import eu.ggnet.saft.experimental.ops.Selector;

/**
 * View that is used to quickly check availability.
 * <p/>
 * @author pascal.perau, oliver.guenther
 */
@ServiceProvider(service = MainComponent.class)
public class UnitAvailabilityViewCask extends javax.swing.JPanel implements MainComponent {

    public static class UnitShardRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( !(value instanceof UnitShard) ) return label;
            UnitShard us = (UnitShard)value;
            label.setText(us.getHtmlDescription());
            label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            label.setForeground(Color.black);
            label.setBackground(getColor(us));

            if ( cellHasFocus ) {
                label.setBackground(SystemColor.textHighlight);
                label.setForeground(SystemColor.textHighlightText);
            }
            return label;
        }

        private Color getColor(UnitShard us) {
            if ( us.getAvailable() == null ) return Color.YELLOW;
            if ( us.getAvailable() == false ) return Color.RED;
            // now we are available
            return Dl.local().optional(StockUpi.class)
                    .map(StockUpi::getActiveStock)
                    .map(ps -> Objects.equals(ps.getId(), us.getStockId()) ? Color.GREEN : Color.CYAN)
                    .orElse(Color.CYAN);
        }
    }

    private final DefaultListModel<UnitShard> model = new DefaultListModel<>();

    private final Selector<UnitShard> selector; // No clear needed. This Panel is in the MainFrame.

    /** Creates new form UnitAvailability */
    public UnitAvailabilityViewCask() {
        initComponents();
        resultList.setModel(model);
        resultList.setCellRenderer(new UnitShardRenderer());
        SelectionEnhancer<UnitShard> selectionEnhancer = (eu.ggnet.dwoss.uniqueunit.api.UnitShard selected) -> {
            if ( selected != null && selected.getAvailable() != null )
                return Arrays.asList(new PicoUnit(selected.getUniqueUnitId(), "SopoNr:" + selected.getRefurbishedId()));
            return Collections.EMPTY_LIST;
        };
        selector = Ops.seletor(UnitShard.class, selectionEnhancer);

    }

    @Override
    public String getLayoutHint() {
        return BorderLayout.WEST;
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

        jLabel1 = new javax.swing.JLabel();
        searchCommand = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList<>();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("SopoNr.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(jLabel1, gridBagConstraints);

        searchCommand.setPreferredSize(new java.awt.Dimension(100, 30));
        searchCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(searchCommand, gridBagConstraints);

        searchButton.setText("Liste leeren");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(searchButton, gridBagConstraints);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        resultList.setMaximumSize(new java.awt.Dimension(1000, 1000));
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultListMouseClicked(evt);
            }
        });
        resultList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                resultListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(resultList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void resultListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultListMouseClicked
        if ( evt.getClickCount() != 2 ) return;
        UnitShard us = resultList.getSelectedValue();
        if ( us == null || us.getAvailable() == null ) return;
        Ui.exec(() -> {
            Ui.build().id(us.getRefurbishedId()).fx().show(() -> Css.toHtml5WithStyle(Dl.remote().lookup(UnitOverseer.class).toDetailedHtml(us.getRefurbishedId(), Dl.local().lookup(Guardian.class).getUsername())), () -> new HtmlPane());
        });
    }//GEN-LAST:event_resultListMouseClicked

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        String refurbishedId = searchCommand.getText().trim();
        UnitShard us = Dl.remote().lookup(UnitOverseer.class).find(refurbishedId);
        model.add(0, us);
        searchCommand.setText("");
    }//GEN-LAST:event_searchActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        model.clear();
    }//GEN-LAST:event_clearActionPerformed

    private void resultListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_resultListValueChanged
        selector.selected(resultList.getSelectedValue());
    }//GEN-LAST:event_resultListValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<UnitShard> resultList;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCommand;
    // End of variables declaration//GEN-END:variables

}
