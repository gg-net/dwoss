/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc;

import java.io.File;
import java.util.Objects;

/**
 * A simple CalcDocument supported by a file, overrides any existing file with the same name
 */
public class FileCalcDocument extends CCalcDocument {

    private File file;

    private String fileName;

    public FileCalcDocument(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "Filename must not be null");
    }

    @Override
    public File getFile() {
        if ( file == null ) {
            file = new File(fileName);
            if ( file.exists() ) {
                file.delete();
            }
        }
        return file;
    }
}
