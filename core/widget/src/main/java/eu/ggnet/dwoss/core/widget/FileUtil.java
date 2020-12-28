/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.core.widget;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.stage.FileChooser;

import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.builder.Result;

/**
 *
 * @author mirko.schulze
 */
public class FileUtil {
    
    public static Result<File> open(String title) {
        return new Result<>(UiCore.global(), CompletableFuture.supplyAsync(() -> {
            FileChooser fileChooser = new FileChooser();
            if ( title == null ) fileChooser.setTitle("Open File");
            else fileChooser.setTitle(title);
            File result = fileChooser.showOpenDialog(null);
            if ( result == null ) throw new CancellationException();
            return result;
        }, Platform::runLater).thenApplyAsync(r -> r, UiCore.global().executorService())); // the last Apply is for the thread change only
    }

    /**
     * Wrapper for Desktop.getDesktop().open() with UI Exception handling
     *
     * @param file a file to open via ui.
     * @return true if operation was successful, otherwise false. Can be used if the following operations should happen.
     */
    public static boolean osOpen(File file) {
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (IOException e) {
            UiCore.global().handle(e);
        }
        return false;
    }
    
}
