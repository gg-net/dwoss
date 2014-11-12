/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.customer.priv;

import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.search.MassIndexer;
import org.hibernate.search.impl.SimpleIndexingProgressMonitor;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.Customers;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

/**
 * This Singleton is to create and recreate the SearchIndex in Memory.
 * <p>
 * @author bastian.venz
 */
@Singleton
@Startup
public class SearchSingletonBean implements SearchSingleton {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSingletonBean.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    @Schedule(hour = "2")
    @Override
    public void reindexSearch() {
        final SubMonitor m = monitorFactory.newSubMonitor("Recreationg Searchindex");
        m.start();
        try {
            MassIndexer createIndexer = Search.getFullTextEntityManager(em).createIndexer();
            createIndexer.progressMonitor(new SimpleIndexingProgressMonitor() {

                private final AtomicLong documentsDoneCounter = new AtomicLong();

                private final AtomicLong totalCounter = new AtomicLong();

                private final AtomicLong entitiesLoaded = new AtomicLong();

                @Override
                public void entitiesLoaded(int size) {
                    entitiesLoaded.set(size);
                }

                @Override
                public void addToTotalCount(long count) {
                    super.addToTotalCount(count);
                    totalCounter.addAndGet(count);
                }

                @Override
                public void documentsAdded(long increment) {
                    super.documentsAdded(increment);
                    long current = documentsDoneCounter.addAndGet(increment);
                    if ( current % getStatusMessagePeriod() == 0 ) {
                        m.message("Prozess " + current + "/" + totalCounter.get()
                                + " | Entities loaded: " + entitiesLoaded.get());
                    }
                }

            });
            // Values still not optimal, but the mysql db holds.
            createIndexer
                    .batchSizeToLoadObjects(10000)
                    .threadsToLoadObjects(3)
                    .startAndWait();
        } catch (InterruptedException ex) {
            LOG.error("Error on Reindex Search.", ex);
        }
        m.finish();
    }

}
