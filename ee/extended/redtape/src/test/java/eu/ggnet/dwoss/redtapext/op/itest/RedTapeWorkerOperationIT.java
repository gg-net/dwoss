/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.op.itest;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.core.common.UserInfoException;

import static eu.ggnet.dwoss.common.api.values.Warranty.NO_B2B_WARRANTY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class RedTapeWorkerOperationIT extends ArquillianProjectArchive {

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private UniqueUnitEao uuEao;

    @Inject
    private CustomerGeneratorOperation cgo;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
    public void testUpdateWarrantyPositive() throws UserInfoException {
        long customerId = cgo.makeCustomer();
        List<UniqueUnit> uu1 = receiptGenerator.makeUniqueUnits(10, true, true);
        Dossier dos = setupDossier(customerId, uu1);

        assertThat("Crucial document is not of type order", dos.getCrucialDocument().getType(), is(DocumentType.ORDER));
        assertThat("10 Units should be on the dossier", dos.getCrucialDocument().getPositions(PositionType.UNIT).size(), is(10));

        dos = redTapeWorker.updateWarranty(dos.getId(), NO_B2B_WARRANTY, "RedTapeWorkerOperationIT");

        assertTrue("Every position should have the warranty name present in its description",
                dos.getCrucialDocument().getPositions(PositionType.UNIT).values().stream().allMatch(p -> p.getDescription().contains(NO_B2B_WARRANTY.getName())));

        List<UniqueUnit> units = uuEao.findByIds(new ArrayList<>(dos.getCrucialDocument().getPositionsUniqueUnitIds()));
        assertTrue("Every unit should have the warranty updated", units.stream().allMatch(u -> u.getWarranty() == NO_B2B_WARRANTY));

    }

    @Test(expected = UserInfoException.class)
    public void testUpdateWarrantyNoUnits() throws UserInfoException {
        long customerId = cgo.makeCustomer();
        Dossier dos = setupDossier(customerId, new ArrayList<>());

        assertThat("Crucial document is not of type order", dos.getCrucialDocument().getType(), is(DocumentType.ORDER));
        assertThat("Zero Units should be on the dossier", dos.getCrucialDocument().getPositions(PositionType.UNIT).size(), is(0));
        
        Document crucialDocument = dos.getCrucialDocument();
        crucialDocument.append(Position.builder().name("TestComment").type(PositionType.COMMENT).description("TestComment").build());
        dos = redTapeWorker.update(crucialDocument, null, "RedTapeWorkerOperationIT").getDossier();

        dos = redTapeWorker.updateWarranty(dos.getId(), NO_B2B_WARRANTY, "RedTapeWorkerOperationIT");
    }

    private Dossier setupDossier(long customerId, Collection<UniqueUnit> uu1) throws UserInfoException {
        Dossier dos = redTapeWorker.create(customerId, true, "RedTapeWorkerOperationIT");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);

        for (UniqueUnit uniqueUnit : uu1) {
            doc.append(NaivBuilderUtil.unit(uniqueUnit));
        }

        return redTapeWorker.update(doc, null, "RedTapeWorkerOperationIT").getDossier();
    }

}
