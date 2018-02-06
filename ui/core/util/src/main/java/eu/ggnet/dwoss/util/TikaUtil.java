/*
 * Copyright (C) 2018 GG-Net GmbH
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

import eu.ggnet.saft.api.Reply;

/**
 * Util to check for a file type.
 *
 * @author oliver.guenther
 */
public class TikaUtil {

    public static String EXCEL_MIME_TYPE = "application/vnd.ms-excel";

    public static String OPENOFFICE_SPREADSHEET_MIME_TYPE = "application/vnd.oasis.opendocument.spreadsheet";

    private static final Tika TIKA = new Tika();

    /**
     * Returns a reply representing the contents of the file
     *
     * @param file the file test
     * @return success if an xls file content, else failure.
     */
    public static Reply<File> isExcel(File file) {
        if ( file == null || !file.canRead() ) Reply.failure("Datei kann nicht gelesen werden.");
        try {
            if ( !TIKA.detect(file).equals(EXCEL_MIME_TYPE) ) Reply.failure("Datei ist keine Excel Datei.\n"
                        + "Umbennenen hilft nicht das eine OpenOffice Datei eine Excel Datei wird!!!");
            return Reply.success(file);
        } catch (IOException ex) {
            return Reply.failure(ex.getClass().getName() + " bei Dateiprüfung " + ex.getLocalizedMessage());
        }
    }

    /**
     * Returns a reply representing the contents of the file
     *
     * @param file the file test
     * @return success if an odc file content, else failure.
     */
    public static Reply<File> isOpenCalc(File file) {
        if ( file == null || !file.canRead() ) Reply.failure("Datei kann nicht gelesen werden.");
        try {
            if ( !TIKA.detect(file).equals(OPENOFFICE_SPREADSHEET_MIME_TYPE) ) Reply.failure("Datei ist keine OpenOffice Spreadsheet Datei.\n"
                        + "Umbennenen hilft nicht das eine Excel Datei eine OpenOffice Datei wird!!!");
            return Reply.success(file);
        } catch (IOException ex) {
            return Reply.failure(ex.getClass().getName() + " bei Dateiprüfung " + ex.getLocalizedMessage());
        }
    }

}
