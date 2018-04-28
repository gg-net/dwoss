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
package eu.ggnet.dwoss.redtapext.ee;

import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.ee.api.LegacyLocalBridge;
import eu.ggnet.dwoss.redtape.ee.api.LegacyUnit;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.redtape.ee.format.DossierFormater;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.Tuple2;

/**
 *
 * @author pascal.perau
 */
//TODO: Total screwup, see http://overload.ahrensburg.gg-net.de/jira/browse/DW-1215
@Stateless
public class UniversalSearcherOperation implements UniversalSearcher {

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private Mandator mandator;

    @Inject
    private Instance<LegacyLocalBridge> bridgeInstance;

    @Inject
    private CustomerServiceBean customerService;

    /**
     * Returns a List of short descriptions of Customers in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param search a search string.
     * @return a List of short descriptions of Customers in Html.
     */
    // misc and redTape.
    @Override
    public List<Tuple2<Long, String>> searchCustomers(String search) {
        List<UiCustomer> customers = customerService.asUiCustomers(search);
        List<Tuple2<Long, String>> result = new ArrayList<>(customers.size());
        for (UiCustomer customer : customers) {
            result.add(new Tuple2((long)customer.getId(), customer.getSimpleHtml()));
        }
        return result;
    }

    /**
     * Returns a List of short descriptions of Customers in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param firma    the firma or null
     * @param vorname  the vorname or null
     * @param nachname the nachname or null
     * @param email    the email or null
     * @return a List of short descriptions of Customers in Html.
     */
    // RedTape createCustomer Contorller.
    @Override
    public List<Tuple2<Long, String>> findCustomers(String firma, String vorname, String nachname, String email) {
        List<UiCustomer> customers = customerService.asUiCustomers(firma, vorname, nachname, email, true);
        List<Tuple2<Long, String>> result = new ArrayList<>(customers.size());
        for (UiCustomer customer : customers) {
            result.add(new Tuple2(customer.getId(), customer.getSimpleHtml()));
        }
        return result;
    }

    /**
     * Find a Customer and return a html formated String representing it.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param id CustomerId to search for
     * @return a html formated String representing a Customer.
     */
    @Override
    public String findCustomer(int id) {
        List<Dossier> dossiers = new DossierEao(redTapeEm).findOpenByCustomerId(id);
        String res = "";
        res += customerService.asHtmlHighDetailed(Integer.valueOf(id).longValue());
        res += "<p><h3>Offene Vorgänge:</h3><ol>";
        for (Dossier dossier : dossiers) {
            res += "<li>" + DossierFormater.toHtmlSimpleWithDocument(dossier) + "</li>";
        }
        res += "</ol></p>";
        return res;
    }

    /**
     * Returns a List of short descriptions of Units in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param search a search string.
     * @return a List of short descriptions of Units in Html.
     */
    // Used in Misc. Unversal Search
    @Override
    public List<Tuple2<Long, String>> searchUnits(String search) {
        List<UniqueUnit> uus = new UniqueUnitEao(uuEm).find(search);

        List<Tuple2<Long, String>> result = new ArrayList<>(uus.size());
        Set<Long> uurefurbishIds = new HashSet<>();

        for (UniqueUnit uu : uus) {
            // TODO: WARNING, if we start using letters in refurbhisIds, the search will not show any results.
            try {
                long sopoNr = Long.parseLong(uu.getRefurbishId());
                uurefurbishIds.add(sopoNr);
                result.add(new Tuple2(sopoNr, UniqueUnitFormater.toSimpleHtml(uu)));
            } catch (NumberFormatException e) {
                continue;
            }
        }

        if ( bridgeInstance.isUnsatisfied() ) return result;
        for (LegacyUnit unit : bridgeInstance.get().findUnit(search)) {
            try {
                if ( uurefurbishIds.contains(Long.parseLong(unit.getUnitIdentifier())) ) continue;
                String re = "<b>" + unit.getUnitIdentifier() + "</b> - ";
                re += unit.getSerial() + "<br />";
                re += unit.getManufacturerPartNo() + "<br />";
                re += "Status: <i>Nicht verfügbar (Legacy System:" + bridgeInstance.get().localName() + ")</i><br />";
                result.add(new Tuple2<>(Long.parseLong(unit.getUnitIdentifier()), re));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return result;
    }

    /**
     * Search for Dossiers where the search matches the identifier.
     * This method will search for any partial matches from the beginning of a identifier if a wildcard is used.
     * <p/>
     * @param identifier the identifier to search for
     * @return a List of Tuple2 containing dossier.id and string representation
     */
    // Used in Misc. Unversal Search
    @Override
    public List<Tuple2<Long, String>> searchDossiers(String identifier) {
        List<Tuple2<Long, String>> result = new ArrayList<>();
        List<Dossier> dossiers = new DossierEao(redTapeEm).findByIdentifier(identifier);
        for (Dossier dossier : dossiers) {
            String s = DossierFormater.toHtmlSimple(dossier);
            Tuple2<Long, String> tuple = new Tuple2<>(dossier.getId(), s);
            result.add(tuple);
        }
        return result;
    }

    /**
     * Generates a html formated string containing information about the Dossier.
     * <p/>
     * @param id the dossier id
     * @return a html formated string containing information about the Dossier
     */
    // Used in Misc. Unversal Search
    @Override
    public String findDossier(long id) {
        return DossierFormater.toHtmlDetailed(new DossierEao(redTapeEm).findById(id));
    }

    /**
     * Search for Documents where the search matches the identifier.
     * This method will search for any partial matches from the beginning of a identifier if a wildcard is used.
     * <p/>
     * @param identifier the identifier to search for
     * @param type
     * @return a List of Tuple2 containing document.id and string representation
     */
    // Used in Misc. Unversal Search
    @Override
    public List<Tuple2<Long, String>> searchDocuments(String identifier, DocumentType type) {
        List<Tuple2<Long, String>> result = new ArrayList<>();
        List<Document> documents = new DocumentEao(redTapeEm).findByIdentifierAndType(identifier, type);
        for (Document document : documents) {
            String s = DocumentFormater.toHtmlSimple(document);
            Tuple2<Long, String> tuple = new Tuple2<>(document.getId(), s);
            result.add(tuple);
        }
        return result;
    }

    /**
     * Generates a html formated string containing information about the Document.
     * <p/>
     * @param id the dossier id
     * @return a html formated string containing information about the Document
     */
    // Used in Misc. Unversal Search
    @Override
    public String findDocument(long id) {
        return DocumentFormater.toHtmlDetailedWithPositions(new DocumentEao(redTapeEm).findById(id));
    }
}
