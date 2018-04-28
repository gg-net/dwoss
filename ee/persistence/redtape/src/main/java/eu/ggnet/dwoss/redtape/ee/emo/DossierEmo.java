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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;

/**
 * Entity Manipulation Object for Dossier.
 *
 * @author oliver.guenther
 */
public class DossierEmo {

    private final static NumberFormat _00000_ = new DecimalFormat("00000");

    private EntityManager em;

    public DossierEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests an active Block for the supplied customerId, if none found creates one with the supplied parameters.
     *
     * @param customerId the customerId to find, or used.
     * @param address    address as string, only used on creation
     * @param comment    comment, only used on creation.
     * @param arranger   arranger, only used on creation.
     * @return an active Block for the supplied customerId, if none found creates one with the supplied parameters.
     */
    public Document requestActiveDocumentBlock(long customerId, String address, String comment, String arranger) {
        Document doc = new DocumentEao(em).findActiveAndOpenByCustomerId(DocumentType.BLOCK, customerId);
        if ( doc != null ) return doc;
        Dossier dossier = new Dossier();
        dossier.setPaymentMethod(PaymentMethod.INVOICE);
        dossier.setDispatch(false);
        dossier.setCustomerId(customerId);

        doc = new Document();
        doc.setType(DocumentType.BLOCK);
        doc.setActive(true);
        doc.setDirective(Directive.NONE);
        doc.setHistory(new DocumentHistory(arranger, comment));

        Address addressEntity = new AddressEmo(em).request(address);
        doc.setInvoiceAddress(addressEntity);
        doc.setShippingAddress(addressEntity);
        dossier.add(doc);
        em.persist(dossier);
        dossier.setIdentifier("DW" + _00000_.format(dossier.getId()));
        return doc;
    }

    /**
     * Removes the history (all Documents, which are no longer active) from a dossier, which is a Block.
     *
     * @param dossierId the dossierId
     */
    public void removeHistoryFromBlock(long dossierId) {
        em.createNativeQuery("update document set predecessor_id = null where Dossier_id = ?1").setParameter(1, dossierId).executeUpdate();
        em.createNativeQuery("delete from position where Document_id in (select id from document where Dossier_id = ?1 and active <> 1)").setParameter(1, dossierId).executeUpdate();
        em.createNativeQuery("delete from document where Dossier_id = ?1 and active <> 1").setParameter(1, dossierId).executeUpdate();
    }
}
