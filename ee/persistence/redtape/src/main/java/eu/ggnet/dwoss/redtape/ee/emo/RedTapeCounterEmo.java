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
package eu.ggnet.dwoss.redtape.ee.emo;

import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.eao.RedTapeCounterEao;
import eu.ggnet.dwoss.redtape.ee.entity.RedTapeCounter;

import eu.ggnet.dwoss.core.common.values.DocumentType;

/**
 * Emo for RedTapeCounter.
 *
 * @author oliver.guenther
 */
public class RedTapeCounterEmo {

    private EntityManager em;

    public RedTapeCounterEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests a RedTapeCounter and increments the result.
     *
     * @param type   the type of counter.
     * @param prefix the prefix of the counter, typically YY
     * @param initialValue the initialValue if a new counter is startet.
     * @return a RedTapeCounter with incremented Result
     */
    public RedTapeCounter requestNext(DocumentType type, String prefix, long initialValue) {
        RedTapeCounterEao eao = new RedTapeCounterEao(em);
        RedTapeCounter singleResult = eao.findByCompositeKey(type, prefix);
        if ( singleResult == null ) {
            singleResult = new RedTapeCounter(type, prefix);
            singleResult.setValue(initialValue);
            em.persist(singleResult);
        }
        singleResult.setValue(singleResult.getValue() + 1);
        return singleResult;
    }
}
