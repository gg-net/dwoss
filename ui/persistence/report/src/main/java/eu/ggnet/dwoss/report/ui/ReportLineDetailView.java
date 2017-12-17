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
package eu.ggnet.dwoss.report.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.MetawidgetConfig;

/**
 * Displays all oft the details of the ReportLine.
 *
 * @author oliver.guenther
 */
public class ReportLineDetailView {

    // TODO: Make this an embedded panel and use it also in the SimpleReportLinePane and else
    public static void show(Component parent, ReportLine rl) {
        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(true, 2, ProductGroup.class, TradeName.class, SalesChannel.class);
        mw.setReadOnly(true);
        mw.setToInspect(rl);
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Details für Reportline(" + rl.getId() + ")");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(mw);
        dialog.pack();
        dialog.setSize(dialog.getSize().width, dialog.getSize().height + 50);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(parent));
        dialog.setVisible(true);
    }

}
