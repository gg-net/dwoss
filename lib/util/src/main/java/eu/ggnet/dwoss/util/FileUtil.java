/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.util;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;

/**
 * This Class contains methods to handle, validate or work with Files.
 * <p>
 * @author bastian.venz
 */
public class FileUtil {

    public static String EXCEL_MIME_TYPE = "application/vnd.ms-excel";

    public static String OPENOFFICE_SPREADSHEET_MIME_TYPE = "application/vnd.oasis.opendocument.spreadsheet";

    private static final Tika TIKA = new Tika();

    public static void checkIfExcelFile(File file) throws UserInfoException {
        if ( file == null || !file.canRead() ) throw new UserInfoException("Datei kann nicht gelesen werden.");
        try {
            if ( !TIKA.detect(file).equals(EXCEL_MIME_TYPE) ) throw new UserInfoException("Datei ist keine Excel Datei.\n"
                        + "Umbennenen hilft nicht das eine OpenOffice Datei eine Excel Datei wird!!!");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void checkIfOdsFile(File file) throws UserInfoException {
        if ( file == null || !file.canRead() ) throw new UserInfoException("Datei kann nicht gelesen werden.");
        try {
            if ( !TIKA.detect(file).equals(OPENOFFICE_SPREADSHEET_MIME_TYPE) ) throw new UserInfoException("Datei ist keine OpenOffice Spreadsheet Datei.\n"
                        + "Umbennenen hilft nicht das eine Excel Datei eine OpenOffice Datei wird!!!");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
