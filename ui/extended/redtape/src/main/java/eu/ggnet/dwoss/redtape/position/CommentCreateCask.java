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
package eu.ggnet.dwoss.redtape.position;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.mandator.MandatorSupporter;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;

import eu.ggnet.dwoss.rules.PositionType;

import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.dwoss.rules.PositionType.COMMENT;

/**
 *
 * @author pascal.perau
 */
public class CommentCreateCask extends javax.swing.JPanel implements IPreClose {

    private final List<Position> templates = new ArrayList<>();

    {
        Position commPosition = new Position();
        commPosition.setName("UPS Versand am " + SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(new Date()));
        commPosition.setDescription("");
        commPosition.setType(PositionType.COMMENT);
        commPosition.setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1));
        templates.add(commPosition);

        commPosition = new Position();
        commPosition.setName("Ihre Bestellung");
        commPosition.setDescription("Diese Geräte konnten für Sie reserviert werden.");
        commPosition.setType(PositionType.COMMENT);
        commPosition.setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1));
        templates.add(commPosition);

        commPosition = new Position();
        commPosition.setName("Verrechnung");
        commPosition.setDescription("Betrag aus Stornorechnung/Gutschrift XXXX verrechnet.");
        commPosition.setType(PositionType.COMMENT);
        commPosition.setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1));
        templates.add(commPosition);

        commPosition = new Position();
        commPosition.setName("Information, " + SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(new Date()));
        commPosition.setDescription("Wir konnten bis heute keinen Zahlungseingang feststellen.\n"
                + "Wir müssen annehmen, Sie haben kein Interesse mehr und geben das Gerät in 48 Stunden wieder zum Verkauf frei.");
        commPosition.setType(PositionType.COMMENT);
        commPosition.setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1));
        templates.add(commPosition);

    }

    private Position position;

    public CommentCreateCask() {
        this(null);
    }

    /** Creates new form CommentCreateCask */
    public CommentCreateCask(Position position) {
        initComponents();
        if ( position != null ) setPosition(position);
        else setPosition(new PositionBuilder().setType(PositionType.COMMENT)
                    .setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1))
                    .createPosition());
        positionTamplateList.setCellRenderer(new Tuple2PositionRenderer());
        positionTamplateList.setListData(templates.toArray());
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Set the value of position
     *
     * @param p new value of position
     */
    public void setPosition(Position p) {
        this.position = p;
        titleField.setText(p.getName() != null ? p.getName() : "");
        commentContentArea.setText(p.getDescription() != null ? p.getDescription() : "");
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type != CloseType.OK ) return true;
        position.setName(titleField.getText());
        position.setDescription(commentContentArea.getText());
        position.setBookingAccount(-1);
        // TODO: use validator.validate(A,DefaultUi)
        if ( position.getName() == null || position.getDescription() == null || position.getName().trim().equals("") || position.getDescription().trim().equals("") ) {
            JOptionPane.showMessageDialog(this, "Überschrift und/oder Inhalt sind leer");
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

        jLabel1 = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentContentArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        positionTamplateList = new javax.swing.JList();

        jLabel1.setText("Überschrift:");

        jLabel2.setText("Text:");

        commentContentArea.setColumns(20);
        commentContentArea.setRows(5);
        jScrollPane1.setViewportView(commentContentArea);

        positionTamplateList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        positionTamplateList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                positionTamplateListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(positionTamplateList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(titleField)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void positionTamplateListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_positionTamplateListMouseClicked
        if ( evt.getClickCount() == 2 && positionTamplateList.getSelectedIndex() != -1 ) {
            setPosition((Position)positionTamplateList.getSelectedValue());
        }
    }//GEN-LAST:event_positionTamplateListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea commentContentArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList positionTamplateList;
    private javax.swing.JTextField titleField;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        Position pos = new PositionBuilder().createPosition();
        pos.setName("blarg");
        pos.setDescription("blubbadiblub");
        pos.setBookingAccount(-1);
        pos.setType(PositionType.COMMENT);
        CommentCreateCask ccc = new CommentCreateCask(null);
        OkCancelDialog<CommentCreateCask> dialog = new OkCancelDialog<>("Comment Creation", ccc);
        dialog.setVisible(true);
        System.out.println(ccc.getPosition());
        System.exit(0);
    }
}
