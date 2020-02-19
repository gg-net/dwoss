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
package eu.ggnet.dwoss.misc.ui.mc;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.saft.core.Ui;

import eu.ggnet.dwoss.core.widget.MainComponent;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = MainComponent.class)
public class FileListViewCask extends javax.swing.JPanel implements MainComponent {

    /** Creates new form FileListViewCask */
    public FileListViewCask() {
        initComponents();
        fileList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if ( value instanceof File ) {
                    File f = (File)value;
                    value = f.getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ( !Desktop.isDesktopSupported() || e.getClickCount() != 2 ) return;
                int index = fileList.locationToIndex(e.getPoint());
                ListModel model = fileList.getModel();
                Object value = model.getElementAt(index);
                if ( !(value instanceof File) ) return;
                Ui.osOpen((File)value);
            }
        });

        DirectoryMonitor dm = new DirectoryMonitor();
        dm.init(GlobalConfig.APPLICATION_PATH_OUTPUT);
        fileList.setModel(dm);
        dm.start();
    }

    @Override
    public String getLayoutHint() {
        return BorderLayout.CENTER;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList<>();

        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(fileList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<File> fileList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
