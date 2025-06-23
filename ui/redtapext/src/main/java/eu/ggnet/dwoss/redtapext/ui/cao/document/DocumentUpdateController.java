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

import java.awt.Dialog;
import java.awt.Window;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker.Addresses;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.ui.cao.common.ShippingCostHelper;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.*;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.impl.Swing;

import static eu.ggnet.dwoss.core.common.values.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.core.common.values.PositionType.UNIT;
import static eu.ggnet.saft.core.ui.UiParent.of;

/**
 *
 * @author pascal.perau
 */
public class DocumentUpdateController {

    private final DocumentUpdateView view;

    private final Optional<Window> parent;

    private final Document document;

    public DocumentUpdateController(DocumentUpdateView view, Document model) {
        this.view = view;
        this.document = model;
        parent = UiCore.global().core(Swing.class).unwrap(of(view));
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
     * @throws eu.ggnet.dwoss.core.common.UserInfoException
     */
    public void addPosition(long dossierId, PositionType type, String refurbishId, boolean forceAdd) throws UserInfoException {
        switch (type) {
            case UNIT -> {
                List<Position> result = Dl.remote().lookup(UnitOverseer.class)
                        .createUnitPosition(refurbishId, document.getId()).request(new SwingInteraction(view));
                for (Position p : result) {
                    if ( p.getType() == UNIT ) Dl.remote().lookup(UnitOverseer.class).lockStockUnit(dossierId, p.getRefurbishedId());
                }
                document.appendAll(result);
            }
            case SERVICE -> createServicePosition();
            case PRODUCT_BATCH -> Ui.build(view).title("Artikel hinzufügen").swing()
                        .eval(() -> Dl.remote().lookup(RedTapeAgent.class).findAll(SalesProduct.class), () -> OkCancelWrap.consumerVetoResult(new SalesProductChooserCask())).cf()
                        .thenAccept(pb -> {
                            Position p = Position.builder()
                                    .amount(1)
                                    .type(type)
                                    .tax(document.getTaxType().tax())
                                    .description(pb.getDescription())
                                    .name(pb.getName())
                                    .uniqueUnitProductId(pb.getUniqueUnitProductId())
                                    .price((pb.getPrice() == null) ? 0. : pb.getPrice())
                                    .bookingAccount(Dl.local().lookup(CachedMandators.class).loadPostLedger().get(type, document.getTaxType()).orElse(null))
                                    .build();
                            document.append(editPosition(p));
                            view.refreshAll();
                        });
            case COMMENT -> document.append(createCommentPosition());
            case SHIPPING_COST -> ShippingCostHelper.modifyOrAddShippingCost(document, Dl.remote().lookup(CustomerService.class).asCustomerMetaData(view.getCustomerId()).shippingCondition());
        }
    }

    /**
     * Edit a Position.
     * <p/>
     * @param pos the Position
     * @return the updated Position.
     */
    public Position editPosition(final Position pos) {
        return Ui.build().parent(view).title("Position bearbeiten").swing()
                .eval(() -> new PositionAndTaxType(pos, document.getTaxType()), () -> OkCancelWrap.consumerVetoResult(new PositionUpdateCask()))
                .opt()
                .orElse(null);
    }

    public Position createCommentPosition() {
        Position p = Position.builder().amount(1).type(PositionType.COMMENT).build();
        CommentCreateCask commentView = new CommentCreateCask(p);
        OkCancelDialog<CommentCreateCask> dialog = new OkCancelDialog<>(parent.orElse(null), Dialog.ModalityType.DOCUMENT_MODAL, "Comment hinzufügen", commentView);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            p = commentView.getPosition();
            return p;
        }
        return null;
    }

    public void createServicePosition() {
        document.append(Ui.build().parent(view).title("Diensleistung/Kleinteil hinzufügen o. bearbeiten").swing()
                .eval(() -> OkCancelWrap.consumerVetoResult(new ServiceViewCask(document.getTaxType())))
                .opt()
                .orElse(null));
    }

    public void editDocumentInvoiceAddress() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        DocumentAdressUpdateView dauv = new DocumentAdressUpdateView(view.getCustomerId(), document.getInvoiceAddress().getDescription(), true);
        OkCancelDialog<DocumentAdressUpdateView> dialog = new OkCancelDialog<>(parent.orElse(null), Dialog.ModalityType.DOCUMENT_MODAL, "Adressen ändern", dauv);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            if ( document.getInvoiceAddress() == document.getShippingAddress() ) {
                document.setInvoiceAddress(Dl.remote().lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
                document.setShippingAddress(document.getInvoiceAddress());
            }
            document.setInvoiceAddress(Dl.remote().lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
            view.refreshAddressArea();
        }
    }

    public void editDocumentShippingAddress() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        DocumentAdressUpdateView dauv = new DocumentAdressUpdateView(view.getCustomerId(), document.getShippingAddress().getDescription(), false);
        OkCancelDialog<DocumentAdressUpdateView> dialog = new OkCancelDialog<>(parent.orElse(null), Dialog.ModalityType.DOCUMENT_MODAL, "Adressen ändern", dauv);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            document.setShippingAddress(Dl.remote().lookup(RedTapeWorker.class).requestAddressByDescription(dauv.getAddress()));
            view.refreshAddressArea();
        }
    }

    public void resetAddressesToCustomerData() {
        if ( view.getCustomerId() <= 0 ) {
            JOptionPane.showMessageDialog(view, "Ein Kunde muss ausgewählt sein.");
            return;
        }
        Addresses addresses = Dl.remote().lookup(RedTapeWorker.class).requestAdressesByCustomer(view.getCustomerId());
        document.setInvoiceAddress(addresses.invoice);
        document.setShippingAddress(addresses.shipping);
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
            JOptionPane.showMessageDialog(parent.orElse(null), "Der Vorgang enthält nur Versandkosten.", "Nur Versandkosten?", JOptionPane.INFORMATION_MESSAGE);
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

        int confirmDialog = JOptionPane.showConfirmDialog(parent.orElse(null), msg, titel, JOptionPane.YES_NO_CANCEL_OPTION);
        switch (confirmDialog) {
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.YES_OPTION:
                if ( document.getDossier().isDispatch() ) {
                    ShippingCostHelper.modifyOrAddShippingCost(document, Dl.remote().lookup(CustomerService.class).asCustomerMetaData(view.getCustomerId()).shippingCondition());
                } else {
                    ShippingCostHelper.removeShippingCost(document);
                }
            case JOptionPane.NO_OPTION:
            default:
                return true;
        }
    }
}
