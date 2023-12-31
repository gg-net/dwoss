/*
 * Copyright (C) 2020 GG-Net GmbH
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

import java.util.TreeSet;

import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.CustomerApiLocal;
import eu.ggnet.dwoss.redtape.api.DossierViewer;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.PositionEao;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.format.DossierFormater;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class DossierViewerBean implements DossierViewer {

    @Inject
    private Instance<CustomerApiLocal> instance;

    @Inject
    @RedTapes
    private EntityManager em;

    @Override
    public String findByUniqueUnitIdAsHtml(long uniqueUnitId) {
        TreeSet<Dossier> dossiers = new TreeSet<>(Dossier.ORDER_INVERSE_ACTIVE_ACTUAL);
        for (Position pos : new PositionEao(em).findByUniqueUnitId((int)uniqueUnitId)) {
            if ( !pos.getDocument().isActive() ) continue; // For now we ignore all Dossiers which just had the unit in the history
            dossiers.add(pos.getDocument().getDossier());
        }

        String re = "<ul>";
        if ( dossiers.isEmpty() ) re += "<li>Keine Vorgänge vorhanden</li>";
        for (Dossier dossier : dossiers) {
            re += "<li>";
            if ( instance.isResolvable() ) {
                re += instance.get().asUiCustomer(dossier.getCustomerId()).toNameCompanyLine();
            }
            re += DossierFormater.toHtmlSimpleWithDocument(dossier) + "<br /></li>";
        }
        re += "</ul>";
        return re;
    }

}
