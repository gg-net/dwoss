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
package eu.ggnet.dwoss.misc.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.util.MetawidgetConfig;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ShowMandatorAction extends AbstractAction {

    public ShowMandatorAction() {
        super("Aktiver Mandant");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(false, 2, TradeName.class);
        mw.setReadOnly(true);
        mw.setToInspect(lookup(MandatorSupporter.class).loadMandator());
        JDialog dialog = new JDialog(lookup(Workspace.class).getMainFrame(), "Aktiver Mandant");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(mw);
        dialog.pack();
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
    }
}
