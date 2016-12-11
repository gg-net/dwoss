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
import java.io.IOException;

/**
 * Represents a temporary calc document, which will be created using File.createTempFile()
 */
public class TempCalcDocument extends CCalcDocument {

    private File file;

    public TempCalcDocument(String prefix) {
        this.prefix = prefix;
    }

    public TempCalcDocument() {
        this("aaa");
    }

    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    @Override
    public File getFile() throws RuntimeException {
        if ( file == null ) {
            try {
                file = File.createTempFile(prefix, ".xls");
            } catch (IOException ex) {
                throw new RuntimeException("Execption during Tempfile creation", ex);
            }
        }
        return file;
    }
}
