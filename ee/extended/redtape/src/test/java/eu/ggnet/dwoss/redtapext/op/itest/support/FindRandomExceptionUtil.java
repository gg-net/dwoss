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
package eu.ggnet.dwoss.redtapext.op.itest.support;

import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.rules.DocumentType;

import static org.junit.Assert.fail;

/**
 * Util, to finally discover the random exceptions, which must be based in the generator.
 *
 * @author oliver.guenther
 */
public class FindRandomExceptionUtil {

    public static Document order(Dossier dos) {
        try {
            return dos.getActiveDocuments(DocumentType.ORDER).get(0);
        } catch (RuntimeException e) {
            fail("Created Dossier has no order, which it must have. Document:\n" + dos.toMultiLine() + "\nOriginal Exception\n " + e.getMessage());
        }
        return null;
    }

}
