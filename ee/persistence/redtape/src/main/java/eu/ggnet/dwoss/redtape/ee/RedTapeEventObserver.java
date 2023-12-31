/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.ee;

import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.redtape.ee.emo.DossierEmo;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.stock.api.event.ScrapEvent;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApiLocal;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RedTapeEventObserver {

    private final static Logger L = LoggerFactory.getLogger(RedTapeEventObserver.class);

    @Inject
    private ScrapCustomers scrapCustomers;

    @Inject
    private UniqueUnitApiLocal api;

    @Inject
    private DossierEmo dossierEmo;

    public void onScrap(@Observes ScrapEvent event) {
        List<SimpleUniqueUnit> suus = api.findByIds(event.uniqueUnitIds());
        var byContractor = new HashMap<TradeName, List<SimpleUniqueUnit>>();
        for (SimpleUniqueUnit suu : suus) {
            if ( byContractor.get(suu.contractor()) == null ) byContractor.put(suu.contractor(), new ArrayList<>());
            byContractor.get(suu.contractor()).add(suu);
        }
        for (TradeName contractor : byContractor.keySet()) {
            long cid = scrapCustomers.get(contractor).orElse(0l);
            if ( cid == 0 ) {
                // Rare case, then no scrap customer exist.
                L.error("onScrap() no ScapeCustomer for contracor {}", contractor);
                continue;
            }
            Document doc = dossierEmo.requestActiveDocumentBlock((int)cid, "Blockaddresse KundenId " + cid, "Erzeugung durch Verschrottungsevent", event.arranger());
            for (SimpleUniqueUnit suu : byContractor.get(contractor)) {
                doc.append(Position.builder().type(PositionType.UNIT)
                        .amount(1)
                        .name(suu.shortDescription())
                        .description(suu.detailedDiscription())
                        .uniqueUnitId((int)suu.id())
                        .refurbishedId(suu.refurbishedId())
                        .uniqueUnitProductId(suu.productId()).build());
            }
            doc.append(Position.builder().type(PositionType.COMMENT).amount(1)
                    .name("Verschrottung").description(event.comment() + " durch " + event.arranger()).build());
            L.info("onScrap({}) observed and Dossier {} created/updated", event, doc.getDossier().getIdentifier());
        }
    }

}
