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
package eu.ggnet.dwoss.redtape;

import java.util.*;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class UnitOverseerRestIT {

    private final static String ROOT_URL = "http://localhost:4204";

    /*
     TODO: Find out why:
     We assumed, that this should be " dwoss-ee-extended-redtape-1.0-SNAPSHOT" as in the sample or local client.
     But it's only "redtape".
     */
    private final static String MAIN_PATH = "redtape";

    private EJBContainer container;

    private List<UniqueUnit> uniqueUnits;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING_WITHOUT_JPA);
        c.put("openejb.embedded.remotable", "true");
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
        uniqueUnits = receiptGenerator.makeUniqueUnits(4, true, true);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Ignore // Enable with Arquilian. Test should work, but some deps are not ok.
    @Test
    public void testUnit() throws InterruptedException {
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
