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
package eu.ggnet.dwoss.misc.ui.toolbar;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import eu.ggnet.dwoss.core.system.OutputPath;
import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.saft.core.Ui;

/**
 * Button to start RedTape form the Toolbar.
 *
 * @author pascal.perau
 */
public class OpenDirectoryToolbarButton extends Button {

    @Inject
    @OutputPath
    private String outputPath;

    public OpenDirectoryToolbarButton() {
        super(null, new ImageView(loadLargeIcon().toExternalForm()));
        Tooltip tip = new Tooltip("Öffnet das Ausgabeverzeichnis");
        Tooltip.install(this, tip);
        setOnAction(e -> Ui.exec(() -> FileUtil.osOpen(new File(outputPath))));
    }

    static URL loadLargeIcon() {
        return OpenDirectoryToolbarButton.class.getResource("fileopen_large.png");
    }
}
