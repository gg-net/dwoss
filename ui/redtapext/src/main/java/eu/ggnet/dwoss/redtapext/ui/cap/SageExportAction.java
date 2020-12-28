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

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.redtapext.ee.sage.SageExporter;
import eu.ggnet.saft.core.Ui;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOCUMENTS_FOR_SAGE_IN_XML;

/**
 * Action to create the GsOfficeXml.
 *
 * @author pascal.perau
 */
public class SageExportAction extends AccessableAction {

    public SageExportAction() {
        super(EXPORT_DOCUMENTS_FOR_SAGE_IN_XML);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new DateRangeChooserView()).opt().ifPresent(r -> {
                Progressor.global()
                        .run("Sage Export", () -> Dl.remote().lookup(SageExporter.class).toXml(r.startAsDate(), r.endAsDate()).toFile(GlobalConfig.APPLICATION_PATH_OUTPUT));
            });
        });
    }
}
