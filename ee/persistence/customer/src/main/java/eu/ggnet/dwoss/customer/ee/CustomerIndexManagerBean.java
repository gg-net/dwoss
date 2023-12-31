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
package eu.ggnet.dwoss.customer.ee;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.pojo.massindexing.MassIndexingMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;

/**
 * This Singleton is to create and recreate the SearchIndex in Memory.
 * <p>
 * @author bastian.venz
 */
@Singleton
@Startup
public class CustomerIndexManagerBean implements CustomerIndexManager {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerIndexManagerBean.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    private boolean active = false;

    @Override
    public boolean isActive() {
        return active;
    }

    @Schedule(hour = "2",persistent = false)
    @Override
    public void reindexSearch() {
        active = true;
        final SubMonitor m = monitorFactory.newSubMonitor("Recreationg Searchindex");
        m.start();
        try {
            MassIndexer indexer = Search.session(em).massIndexer();
            indexer.monitor(new MassIndexingMonitor() {

                private final AtomicLong documentsDoneCounter = new AtomicLong();

                private final AtomicLong totalCounter = new AtomicLong();

                private final AtomicLong entitiesLoaded = new AtomicLong();

                @Override
                public void addToTotalCount(long count) {
                    totalCounter.addAndGet(count);
                }

                @Override
                public void documentsAdded(long increment) {
                    long current = documentsDoneCounter.addAndGet(increment);
//                    if ( current % getStatusMessagePeriod() == 0 ) {
                        m.message("Prozess " + current + "/" + totalCounter.get()
                                + " | Entities loaded: " + entitiesLoaded.get());
  //                  }
                }

                @Override
                public void documentsBuilt(long l) {
                    // ingnore
                }

                @Override
                public void entitiesLoaded(long l) {
                    entitiesLoaded.set(l);
                }

                @Override
                public void indexingCompleted() {
                    m.finish();
                }

            });
            // Values still not optimal, but the mysql db holds.
            indexer
                    .batchSizeToLoadObjects(10000)
                    .threadsToLoadObjects(3)
                    .startAndWait();
        } catch (InterruptedException ex) {
            LOG.error("Error on Reindex Search.", ex);
        } finally {
            active = false;
        }
        m.finish();
    }

}
