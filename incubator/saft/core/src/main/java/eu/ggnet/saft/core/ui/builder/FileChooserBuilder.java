/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.saft.core.ui.builder;

import java.io.File;
import java.util.Optional;

import javafx.stage.FileChooser;

import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.SwingCore;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class FileChooserBuilder {

    @Setter
    private String title;

    /**
     * Opens a file chooser and returns the selected file or empty.
     *
     * @return the selected file or empty.
     */
    // TODO: This is the only time we us a javafx component in all modes. It should be considered, that in the swing mode, the JFileChoser should be used.
    public Result<File> open() {
        SwingCore.ensurePlatformIsRunning();
        File file = FxSaft.dispatch(() -> {
            FileChooser fileChooser = new FileChooser();
            if ( title == null ) fileChooser.setTitle("Open File");
            else fileChooser.setTitle(title);
            return fileChooser.showOpenDialog(null);
        });
        return new Result<>(Optional.ofNullable(file));
    }

}
