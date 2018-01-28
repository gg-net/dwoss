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
package eu.ggnet.dwoss.customer.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.priv.SearchSingleton;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author olive
 */
@RunWith(Arquillian.class)

public class CustomerSearchIndexerIT extends ArquillianProjectArchive {

    private Logger L = LoggerFactory.getLogger(CustomerSearchIndexerIT.class);

    @Inject
    private CustomerGeneratorOperation gen;

    @Inject
    private CustomerEao customerEao;

    @EJB
    private SearchSingleton search;

    @Test
    public void testIndexer() throws InterruptedException {
        gen.makeCustomers(10);
        assertThat(customerEao.count()).isEqualTo(10);
        search.reindexSearch();
        while (search.isActive()) {
            L.info("Waiting for Search to reindex");
            Thread.sleep(1000);
        }
        assertThat(search.isActive()).isFalse();

        L.info("Thread Count={}", Thread.activeCount());
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getName().startsWith("Hibernate Search sync consumer thread")).forEach(t -> {
            L.info("IndexThread: {}, clazz={}", t.getName(), t.getClass().getName());
        });
        // TODO: If someone is interesed: find out which class which contains the search sync consumer an shut it down safly.

    }

}
