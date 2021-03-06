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

import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.IPreClose;
import eu.ggnet.dwoss.redtape.ee.api.PositionService;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

/**
 *
 * @author pascal.perau
 */
public class CommentCreateCask extends javax.swing.JPanel implements IPreClose {

    private final List<Position> templates = new ArrayList<>();

    private Position position;

    public CommentCreateCask() {
        this(null);
    }

    /** Creates new form CommentCreateCask */
    public CommentCreateCask(Position position) {
        initComponents();

        Position templateComment = new Position();
        templateComment.setName("UPS Versand am " + SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(new Date()));
        templateComment.setDescription("");
        templateComment.setType(PositionType.COMMENT);
        templates.add(templateComment);

        templateComment = new Position();
        templateComment.setName("Ihre Bestellung");
        templateComment.setDescription("Diese Geräte konnten für Sie reserviert werden.");
        templateComment.setType(PositionType.COMMENT);
        templates.add(templateComment);

        templateComment = new Position();
        templateComment.setName("Verrechnung");
        templateComment.setDescription("Betrag aus Stornorechnung/Gutschrift XXXX verrechnet.");
        templateComment.setType(PositionType.COMMENT);
        templates.add(templateComment);

        templateComment = new Position();
        templateComment.setName("Vorab-Überweisung");
        templateComment.setDescription("Der Versand der Ware erfolgt nach Zahlungseingang. \n"
                + "Bei Zahlungseingang bis 11:00 Uhr (Montag bis Freitag) erfolgt der Versand tagesgleich.");
        templateComment.setType(PositionType.COMMENT);
        templates.add(templateComment);

        templateComment = new Position();
        templateComment.setName("Information, " + SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(new Date()));
        templateComment.setDescription("Wir konnten bis heute keinen Zahlungseingang feststellen.\n"
                + "Wir müssen annehmen, Sie haben kein Interesse mehr haben und geben das Gerät in 48 Stunden wieder zum Verkauf frei.");
        templateComment.setType(PositionType.COMMENT);
        templates.add(templateComment);

        if ( Dl.remote().contains(PositionService.class) ) {
            templates.addAll(Dl.remote().lookup(PositionService.class).commentPositionTemplates());
        }

        if ( position != null ) setPosition(position);
        else setPosition(Position.builder().type(PositionType.COMMENT).amount(1).build());
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

}
