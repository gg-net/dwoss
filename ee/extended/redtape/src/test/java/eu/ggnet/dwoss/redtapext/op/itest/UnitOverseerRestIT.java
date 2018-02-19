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
package eu.ggnet.dwoss.redtapext.op.itest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseerRest;
import eu.ggnet.dwoss.redtapext.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class UnitOverseerRestIT extends ArquillianProjectArchive {

    private final static String ROOT_URL = "http://localhost:4204";

    /*
     TODO: Find out why:
     We assumed, that this should be " dwoss-ee-extended-redtape-1.0-SNAPSHOT" as in the sample or local client.
     But it's only "redtape".
     */
    private final static String MAIN_PATH = "redtape";

    private List<UniqueUnit> uniqueUnits;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Ignore // WebClient an Aquillian are a little more difficult, if there is time .....
    @Test
    public void testUnit() throws InterruptedException {
        uniqueUnits = receiptGenerator.makeUniqueUnits(4, true, true);
        String refurbishId = uniqueUnits.get(0).getRefurbishId();
        UnitShard unitShard = getWebClient().path(UnitOverseerRest.FIND_PATH).path(refurbishId).get(UnitShard.class);
        assertThat(unitShard).isNotNull();
        assertThat(unitShard.getRefurbishedId()).isEqualTo(refurbishId);
        assertThat(unitShard.isAvailable()).isTrue();
    }

    private WebClient getWebClient() {
        return WebClient.create(ROOT_URL + "/" + MAIN_PATH).type(MediaType.APPLICATION_XML);
    }

}
