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
package eu.ggnet.dwoss.misc.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.spec.ee.SpecExporter;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_PRODUCT_SPEC_FOR_XML_EXPORT;

/**
 * Action to Export the ProductSpecs to XML.
 * <p/>
 * @author oliver.guenther
 */
public class ProductSpecExportAction extends AccessableAction {

    public ProductSpecExportAction() {
        super(READ_PRODUCT_SPEC_FOR_XML_EXPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = JOptionPane.showInputDialog(UiCore.getMainFrame(), "Grenze der zu exportierenden ProductSpecs eingeben", 100);
        if ( input == null ) return;
        try {
            final int amount = Integer.parseInt(input);
            Ui.exec(() -> {
                Ui.progress().wrap(() -> Dl.remote().lookup(SpecExporter.class).toXml(amount).toFile(GlobalConfig.APPLICATION_PATH_OUTPUT));
            });

        } catch (NumberFormatException ex) {
            Ui.exec(() -> {
                Ui.build().alert("Die Eingabe '" + input + "' ist keine Zahl");
            });
            return;
        }
    }
}
