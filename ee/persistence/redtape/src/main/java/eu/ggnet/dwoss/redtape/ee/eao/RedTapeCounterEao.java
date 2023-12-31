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
package eu.ggnet.dwoss.redtape.ee.eao;

import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.entity.RedTapeCounter;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.redtape.ee.entity.QRedTapeCounter.redTapeCounter;

/**
 * This is the EAO for the {@link RedTapeCounter}.
 * <p>
 * @author bastian.venz
 */
public class RedTapeCounterEao extends AbstractEao<RedTapeCounter> {

    private final EntityManager em;

    public RedTapeCounterEao(EntityManager em) {
        super(RedTapeCounter.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public RedTapeCounter findByCompositeKey(DocumentType type, String prefixString) {
        return new JPAQuery<RedTapeCounter>(em)
                .from(redTapeCounter)
                .where(redTapeCounter.type.eq(type).and(redTapeCounter.prefix.eq(prefixString)))
                .fetchOne();
    }
}
