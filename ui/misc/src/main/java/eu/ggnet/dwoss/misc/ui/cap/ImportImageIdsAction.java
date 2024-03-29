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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.misc.ee.ImageIdHandler;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_IMAGE_IDS;

/**
 *
 *
 * @author oliver.guenther
 */
@Dependent
public class ImportImageIdsAction extends AccessableAction {

    public ImportImageIdsAction() {
        super(IMPORT_IMAGE_IDS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileUtil.open(null).cf()
                .thenCompose(f -> Ui.build().dialog().eval(() -> new ConfirmationDialog<>("ImageId Import", "ImageIds aus der Datei: " + f.getPath() + " importieren ?", f)).cf())
                .thenApply(f -> TikaUtil.verifyExcel(f))
                .thenAccept(f -> Progressor.global().run(() -> Dl.remote().lookup(ImageIdHandler.class).importMissing(new FileJacket("in", ".xls", f))))
                .handle(Ui.handler());
    }
}
