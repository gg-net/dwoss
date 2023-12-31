/*
 * Copyright (C) 2022 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.itest.emo;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.assist.gen.RedTapeDeleteUtils;
import eu.ggnet.dwoss.redtape.ee.emo.RedTapeCounterEmo;
import eu.ggnet.dwoss.redtape.ee.entity.RedTapeCounter;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeCounterEmoIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private final String PREFIX = "2000";
    
    private final long VALUE = 10000;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        RedTapeDeleteUtils.deleteAll(em);
        assertThat(RedTapeDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void findAndCreateNewCounter() throws Exception {
        var counterEmo = new RedTapeCounterEmo(em);
        utx.begin();
        em.joinTransaction();
        RedTapeCounter counter = counterEmo.requestNext(DocumentType.ORDER, PREFIX, VALUE);
        assertThat(counter).as("Counter 1 of Order").isNotNull().extracting(RedTapeCounter::getValue).isEqualTo(VALUE + 1);
        counter = counterEmo.requestNext(DocumentType.ORDER, PREFIX, VALUE);
        assertThat(counter).as("Counter 2 of Order").isNotNull().extracting(RedTapeCounter::getValue).isEqualTo(VALUE + 2);
        counter = counterEmo.requestNext(DocumentType.INVOICE, PREFIX, 0);
        assertThat(counter).as("Counter 1 of Invoice").isNotNull().extracting(RedTapeCounter::getValue).isEqualTo(1L);
        utx.commit();
    }

}
