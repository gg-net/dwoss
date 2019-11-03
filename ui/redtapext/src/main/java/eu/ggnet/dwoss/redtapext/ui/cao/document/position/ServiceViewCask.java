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
package eu.ggnet.dwoss.redtapext.ui.cao.document.position;

import java.util.function.Consumer;

import eu.ggnet.dwoss.redtape.ee.api.PositionService;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.core.common.values.TaxType;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.dwoss.core.widget.saft.VetoableOnOk;

import static eu.ggnet.dwoss.core.common.values.PositionType.SERVICE;


/**
 *
 * @author pascal.perau
 */
public class ServiceViewCask extends javax.swing.JPanel implements Consumer<PositionAndTaxType>, ResultProducer<Position>, VetoableOnOk {

    private Position position;

    private final PositionUpdateCask positionView;

    private TaxType taxType;

    public ServiceViewCask(TaxType taxType) {
        initComponents();
        this.taxType = taxType;
        positionView = new PositionUpdateCask();
        positionPanel.add(positionView);

        templateList.setCellRenderer(new Tuple2PositionRenderer());
        if ( Dl.remote().contains(PositionService.class) ) {
            templateList.setListData(Dl.remote().lookup(PositionService.class).servicePositionTemplates().toArray());
        } else {
            templateList.setListData(new Position[]{Position.builder().name("ServicePosition").type(SERVICE).description("").price(0.).build()});
        }
    }

    @Override
    public void accept(PositionAndTaxType posAndTax) {
        if ( posAndTax == null ) return;
        this.position = posAndTax.getPosition();
        this.taxType = posAndTax.getTaxType();
        positionView.accept(posAndTax);
    }

    @Override
    public Position getResult() {
        return position;
    }

    @Override
    public boolean mayClose() {
        return positionView.mayClose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        positionPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        templateList = new javax.swing.JList();

        positionPanel.setMinimumSize(new java.awt.Dimension(328, 572));
        positionPanel.setPreferredSize(new java.awt.Dimension(328, 572));
        positionPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        templateList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        templateList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        templateList.setToolTipText("");
        templateList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                templateListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(templateList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(positionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(positionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void templateListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_templateListMouseClicked
        if ( evt.getClickCount() == 2 ) {
            Position template = (Position)templateList.getSelectedValue();
            position = Position.builder()
                    .amount(template.getAmount())
                    .bookingAccount(template.getBookingAccount().orElse(null))
                    .description(template.getDescription())
                    .name(template.getName())
                    .price(template.getPrice())
                    .tax(taxType.getTax())
                    .type(template.getType())
                    .uniqueUnitProductId(template.getUniqueUnitProductId())
                    .build();
            positionView.accept(new PositionAndTaxType(position, taxType));
        }
    }//GEN-LAST:event_templateListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel positionPanel;
    private javax.swing.JList templateList;
    // End of variables declaration//GEN-END:variables

}