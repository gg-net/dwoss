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
package eu.ggnet.dwoss.receipt;

import java.awt.Component;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.mandator.api.service.ClientLocation;
import eu.ggnet.dwoss.mandator.api.service.MandatorService;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.saft.core.ToolbarComponent;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ToolbarComponent.class)
public class LocationSelectorViewCask extends javax.swing.JPanel implements ToolbarComponent {

    /** Creates new form LocationSelectorViewCask */
    public LocationSelectorViewCask() {
        initComponents();
        List<Stock> allStocks = lookup(StockAgent.class).findAll(Stock.class);
        Stock stock = allStocks.stream().findFirst().get();
        locationBox.setModel(new DefaultComboBoxModel(allStocks.toArray()));
        locationBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value == null ) return label;
                label.setText(((Stock)value).getName());
                return label;
            }

        });
        if ( Client.hasFound(MandatorService.class) ) {

            try {
                Set<InetAddress> adresses = Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                        .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream()).collect(Collectors.toCollection(HashSet::new));
                ClientLocation cl = new ClientLocation(adresses);
                stock = allStocks.stream().filter((t) -> t.getId() == lookup(MandatorService.class).getLocationStockId(cl)).findFirst().get();
            } catch (SocketException ex) {
                Logger.getLogger(LocationSelectorViewCask.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        locationBox.setSelectedItem(stock);
        lookup(Workspace.class).setValue(stock);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        locationBox = new javax.swing.JComboBox();

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        jLabel1.setText("Standort");

        locationBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addComponent(locationBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(locationBox, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void locationBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationBoxActionPerformed
        lookup(Workspace.class).setValue((Stock)locationBox.getSelectedItem());
    }//GEN-LAST:event_locationBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox locationBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getOrder() {
        return 2;
    }
}
