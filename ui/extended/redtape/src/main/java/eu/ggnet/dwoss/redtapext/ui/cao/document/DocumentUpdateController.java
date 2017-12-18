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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import eu.ggnet.dwoss.redtapext.ui.cao.common.ShippingCostHelper;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.PositionUpdateCask;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.SalesProductChooserCask;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.ServiceViewCask;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.CommentCreateCask;

import java.awt.Dialog;
import java.awt.Window;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.redtape.RedTapeWorker.Addresses;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.core.swing.OkCancel;

import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DocumentUpdateController {

    private final DocumentUpdateView view;

    private final Window parent;

    private final Document document;

    public DocumentUpdateController(DocumentUpdateView view, Document model) {
        this.view = view;
        this.document = model;
        this.parent = SwingUtilities.getWindowAncestor(view);
    }

    /**
     * This method creates a position from a specified type.
     * Values set to Position are:<br>
     * <p>
     * @param dossierId   the dossier id
     * @param type        the position type
     * @param refurbishId the refurbishId if a type is unit.
     * @param forceAdd    passed to the unit creation to force conditioned behaviour
     *                    ({@link DocumentUpdateController#createUnitPostion(long, java.lang.String, boolean force)})
     * @throws eu.ggnet.dwoss.util.UserInfoException
     */
    public void addPosition(long dossierId, PositionType type, String refurbishId, boolean forceAdd) throws UserInfoException {
        switch (type) {
            case UNIT:
                List<Position> result = lookup(UnitOverseer.class)
                        .createUnitPosition(refurbishId, document.getId()).request(new SwingInteraction(view));
                for (Position p : result) {
                    if ( p.getType() == UNIT ) lookup(UnitOverseer.class).lockStockUnit(dossierId, p.getRefurbishedId());
                }
                document.appendAll(result);

                break;
            case SERVICE:
                document.append(createServicePosition());
                break;
            case PRODUCT_BATCH:
                SalesProduct pb = createProductBatchPosition(lookup(RedTapeAgent.class).findAll(SalesProduct.class));
                if ( pb != null ) {
                    Position p = new PositionBuilder().setType(type).setDescription(pb.getDescription()).
                            setName(pb.getName()).setUniqueUnitProductId(pb.getUniqueUnitProductId()).createPosition();
                    p.setPrice((pb.getPrice() == null) ? 0. : pb.getPrice());
                    p.setBookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(p.getType()).orElse(-1));
                    document.append(editPosition(p));
                }
                break;
            case COMMENT:
                document.append(createCommentPosition());
                break;
            case SHIPPING_COST:
                ShippingCostHelper.modifyOrAddShippingCost(document, lookup(CustomerService.class).asCustomerMetaData(view.getCustomerId()).getShippingCondition());
                break;
        }
    }

    /**
     * Edit a Position.
     * <p/>
     * @param pos the Position
     * @return the updated Position.
     */
    public Position editPosition(final Position pos) {
        return Ui.swing().parent(view).title("Position bearbeiten")
                .eval(() -> pos, () -> OkCancel.wrap(new PositionUpdateCask()))
                .map(Reply::getPayload)
                .orElse(null);
    }

    public eu.ggnet.dwoss.redtape.entity.Position createCommentPosition() {
        eu.ggnet.dwoss.redtape.entity.Position p = new PositionBuilder().setType(PositionType.COMMENT).createPosition();
        CommentCreateCask commentView = new CommentCreateCask(p);
        OkCancelDialog<CommentCreateCask> dialog = new OkCancelDialog<>(parent, Dialog.ModalityType.DOCUMENT_MODAL, "Comment hinzufügen", commentView);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            p = commentView.getPosition();
            return p;
        }
        return null;
    }

    public Position createServicePosition() {
        // Hint: Unusual usage, but works if we need a return type and use null for cancel.
        return Ui.swing().parent(view).title("Diensleistung/Kleinteil hinzufügen o. bearbeiten")
                .eval(() -> Position.builder().type(PositionType.SERVICE).build(), () -> OkCancel.wrap(new ServiceViewCask()))
                .map(Reply::getPayload)
                .orElse(null);
    }

    public SalesProduct createProductBatchPosition(List<SalesProduct> products) {
        SalesProductChooserCask spcView = new SalesProductChooserCask(products);
        OkCancelDialog<SalesProductChooserCask> dialog = new OkCancelDialog<>(parent, Dialog.ModalityType.DOCUMENT_MODAL, "Artikel hinzufügen", spcView);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            return spcView.getProduct();
        }
        return null;
    }

    public void editDocumentInvoiceAddress() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        DocumentAdressUpdateView dauv = new DocumentAdressUpdateView(view.getCustomerId(), document.getInvoiceAddress().getDescription(), true);
        OkCancelDialog<DocumentAdressUpdateView> dialog = new OkCancelDialog<>(parent, Dialog.ModalityType.DOCUMENT_MODAL, "Adressen ändern", dauv);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            if ( document.getInvoiceAddress() == document.getShippingAddress() ) {
                document.setInvoiceAddress(lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
                document.setShippingAddress(document.getInvoiceAddress());
            }
            document.setInvoiceAddress(lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
            view.refreshAddressArea();
        }
    }

    public void editDocumentShippingAddress() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        DocumentAdressUpdateView dauv = new DocumentAdressUpdateView(view.getCustomerId(), document.getShippingAddress().getDescription(), false);
        OkCancelDialog<DocumentAdressUpdateView> dialog = new OkCancelDialog<>(parent, Dialog.ModalityType.DOCUMENT_MODAL, "Adressen ändern", dauv);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            document.setShippingAddress(lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
            view.refreshAddressArea();
        }
    }

    public void resetAddressesToCustomerData() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        Addresses addresses = lookup(RedTapeWorker.class).requestAdressesByCustomer(view.getCustomerId());
        document.setInvoiceAddress(addresses.getInvoice());
        document.setShippingAddress(addresses.getShipping());
        view.refreshAddressArea();
    }

    /**
     * Asks the User if he/she wants to Recalculate the ShippingCosts.
     *
     * @param document
     * @param shippingCondition
     * @return weither the shipping costs are recalculated or not.
     */
    boolean optionalRecalcShippingCost() {
        if ( document.getType() != DocumentType.ORDER && document.getType() != DocumentType.INVOICE ) return true;
        String msg;
        String titel;
        if ( document.getPositions().size() == 1 && document.containsPositionType(PositionType.SHIPPING_COST) ) {
            JOptionPane.showMessageDialog(parent, "Der Vorgang enthält nur Versandkosten.", "Nur Versandkosten?", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if ( document.getDossier().isDispatch() ) {
            msg = "Sie haben einen Versandauftrag geändert.\nSollen die Versandkosten automatisch berechnet werden?";
            titel = "Automatisches Setzten der Versandkosten";
        } else if ( !document.getDossier().isDispatch() && document.containsPositionType(PositionType.SHIPPING_COST) ) {
            msg = "Sie haben einen Abholauftrag, der Versandkosten enthält.\nSollen die Versandkosten entfernt werden?";
            titel = "Automatisches entfernen der Versandkosten";
        } else {
            return true;
        }

        int confirmDialog = JOptionPane.showConfirmDialog(parent, msg, titel, JOptionPane.YES_NO_CANCEL_OPTION);
        switch (confirmDialog) {
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.YES_OPTION:
                if ( document.getDossier().isDispatch() ) {
                    ShippingCostHelper.modifyOrAddShippingCost(document, lookup(CustomerService.class).asCustomerMetaData(view.getCustomerId()).getShippingCondition());
                } else {
                    ShippingCostHelper.removeShippingCost(document);
                }
            case JOptionPane.NO_OPTION:
            default:
                return true;
        }
    }
}
