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
package eu.ggnet.dwoss.redtapext.ui.cao;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

import jakarta.inject.Inject;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.spi.CustomerUiModifier;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition.Hint;
import eu.ggnet.dwoss.redtapext.ee.state.*;
import eu.ggnet.dwoss.redtapext.ui.cao.common.IDossierSelectionHandler;
import eu.ggnet.dwoss.redtapext.ui.cao.common.StringAreaView;
import eu.ggnet.dwoss.redtapext.ui.cao.dossierTable.DossierTableController;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.DocumentJasperViewAction;
import eu.ggnet.dwoss.redtapext.ui.cao.stateaction.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Swing;
import eu.ggnet.saft.core.ui.UiParent;
import eu.ggnet.statemachine.StateTransition;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ANNULATION_INVOICE;

/**
 * The RedTape main component controller handling all in/output as well as update actions provided by the {@link RedTapeView}.
 * <p>
 * @author pascal.perau
 */
@Dependent
public class RedTapeController implements IDossierSelectionHandler {

    @Inject
    private RemoteDl remote;

    @Inject
    private Saft saft;

    @Inject
    private DossierTableController dossierTableController;

    private RedTapeModel model;

    private RedTapeView view;

    private Set<Action> accessDependentActions;

    private Set<Long> viewOnlyCustomerIds;

    private SwingWorker<Void, Dossier> closedLoader;

    private Boolean shippingCostUiHelpEnabled = null;

    private static final Logger L = LoggerFactory.getLogger(RedTapeController.class);

    private boolean isShippingCostUiHelpEnabled() {
        if ( shippingCostUiHelpEnabled == null ) shippingCostUiHelpEnabled = remote.contains(ShippingCostService.class);
        return shippingCostUiHelpEnabled;
    }

    private Set<Long> getViewOnlyCustomerIds() {
        if ( viewOnlyCustomerIds == null ) viewOnlyCustomerIds = Dl.local().lookup(CachedMandators.class).loadSalesdata().viewOnlyCustomerIds();
        return viewOnlyCustomerIds;
    }

    public RedTapeModel getModel() {
        return model;
    }

    public RedTapeView getView() {
        return view;
    }

    public DossierTableController getDossierTableController() {
        return dossierTableController;
    }

    private final PropertyChangeListener redTapeViewListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            switch (evt.getPropertyName()) {
                case RedTapeModel.PROP_SELECTED_DOSSIER:
                    L.debug("PROP_SELECTED_DOSSIER: selected {}", model.getSelectedDossier());
                    //break if null selection
                    if ( model.getSelectedDossier() == null ) {
                        model.setDocuments(new ArrayList<>());
                        view.dossierCommentArea.setText("");
                        break;
                    }

                    Dossier selectedDossier = model.getSelectedDossier();

                    // Set null to ensure a fire for property change.
                    model.setSelectedDocument(null);
                    model.setDocuments(new ArrayList<>());
                    model.setDocuments(selectedDossier.getActiveDocuments());

                    model.setSelectedDocument(refreshDocumentSelection(selectedDossier));

                    view.documentList.setSelectedValue(model.getSelectedDocument(), true);
                    view.dossierCommentArea.setText(model.getSelectedDossier().getComment());
                    fillToolBar();
                    break;

                case RedTapeModel.PROP_SELECTED_DOCUMENT:
                    model.setPositions(new TreeSet<>());
                    if ( model.getSelectedDocument() == null ) {
                        view.priceSumLabel.setText(NumberFormat.getCurrencyInstance().format(0.));
                        view.afterTaxSumLabel.setText(NumberFormat.getCurrencyInstance().format(0.));
                        view.positionAmountLabel.setText("" + 0);
                    } else {
                        if ( model.getPurchaseCustomer() == null ) {
                            JOptionPane.showMessageDialog(null, "Kein Kunde gewählt");
                        } else {
                            CustomerDocument cdoc = new CustomerDocument(
                                    model.getPurchaseCustomer().flags(),
                                    model.getSelectedDocument(),
                                    model.getPurchaseCustomer().shippingCondition(),
                                    model.getPurchaseCustomer().paymentMethod());
                            List<StateTransition<CustomerDocument>> transitions = Dl.remote().lookup(RedTapeWorker.class).getPossibleTransitions(cdoc);
                            // Remove old Actions from the receiving of right changes.
                            for (Action accessDependent : accessDependentActions) {
                                if ( accessDependent instanceof AccessableAction ) {
                                    Dl.local().lookup(Guardian.class).remove((AccessableAction)accessDependent);
                                } else {
                                    Dl.local().lookup(Guardian.class).remove(accessDependent);
                                }
                            }
                            accessDependentActions.clear();
                            List<Action> stateActions = new ArrayList<>();
                            //check if selected document is order and invoice exist to restrict transitions
                            if ( model.getSelectedDocument().getDossier().getId() == 0 ) {
                                // Now this implies a legacy wrapped dossier, so no actions are possible.
                            } else if ( (model.getSelectedDocument().getType() == DocumentType.ORDER
                                         && !model.getSelectedDossier().getActiveDocuments(DocumentType.INVOICE).isEmpty())
                                    || getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().id()) ) {
                                //
                            } else {
                                for (StateTransition<CustomerDocument> originalStateTransition : transitions) {
                                    RedTapeStateTransition stateTransition = (RedTapeStateTransition)originalStateTransition;
                                    if ( RedTapeStateTransitions.ADD_SHIPPING_COSTS.contains(stateTransition) && isShippingCostUiHelpEnabled() ) {
                                        Action a = new ModifyShippingCostStateAction(parent(), RedTapeController.this, cdoc, stateTransition);
                                        stateActions.add(a);
                                    } else if ( RedTapeStateTransitions.REMOVE_SHIPPING_COSTS.contains(stateTransition) && isShippingCostUiHelpEnabled() ) {
                                        stateActions.add(new RemoveShippingCostStateAction(parent(), RedTapeController.this, cdoc, stateTransition));
                                    } else if ( stateTransition.getHints().contains(Hint.CREATES_CREDIT_MEMO) ) {
                                        CreditMemoAction creditMemoAction = new CreditMemoAction(parent(), RedTapeController.this, model.getSelectedDocument(), stateTransition);
                                        Dl.local().lookup(Guardian.class).add(creditMemoAction);
                                        accessDependentActions.add(creditMemoAction);
                                        stateActions.add(creditMemoAction);
                                    } else if ( stateTransition.getHints().contains(Hint.CREATES_COMPLAINT) ) {
                                        ComplaintAction action = new ComplaintAction(parent(), RedTapeController.this, model.getSelectedDocument(), stateTransition);
                                        stateActions.add(action);
                                    } else if ( stateTransition.getHints().contains(Hint.CREATES_ANNULATION_INVOICE) ) {
                                        AnnulationInvoiceAction action = new AnnulationInvoiceAction(parent(), RedTapeController.this, model.getSelectedDocument(), stateTransition);
                                        Dl.local().lookup(Guardian.class).add(action);
                                        accessDependentActions.add(action);
                                        stateActions.add(action);
                                    } else if ( stateTransition.getEnablingRight() != null && stateTransition.getEnablingRight().equals(AtomicRight.CREATE_ANNULATION_INVOICE) ) {
                                        DefaultStateTransitionAction action = new DefaultStateTransitionAction(parent(), RedTapeController.this, cdoc, stateTransition);
                                        Dl.local().lookup(Guardian.class).add(action, AtomicRight.CREATE_ANNULATION_INVOICE);
                                        accessDependentActions.add(action);
                                        stateActions.add(action);
                                    } else {
                                        stateActions.add(new DefaultStateTransitionAction(parent(), RedTapeController.this, cdoc, stateTransition));
                                    }
                                }
                                if ( model.getSelectedDocument().getType() == DocumentType.BLOCK ) {
                                    DossierDeleteAction action = new DossierDeleteAction(parent(), RedTapeController.this, model.getSelectedDossier());
                                    Dl.local().lookup(Guardian.class).add(action);
                                    accessDependentActions.add(action);
                                    stateActions.add(action);
                                }
                            }
                            view.setStateActions(stateActions);
                            model.setPositions(new TreeSet<>(model.getSelectedDocument().getPositions().values()));
                        }
                        view.priceSumLabel.setText(NumberFormat.getCurrencyInstance().format(model.getSelectedDocument().getPrice()));
                        view.afterTaxSumLabel.setText(NumberFormat.getCurrencyInstance().format(model.getSelectedDocument().toAfterTaxPrice()));
                        int i = model.getSelectedDocument().getPositions().size();
                        view.positionAmountLabel.setText("" + i);
                        view.positionAmountLabel.setForeground(i >= 20 ? Color.red : Color.black);
                        Font f = new Font(view.positionAmountLabel.getFont().getName(), i >= 20 ? Font.BOLD : Font.PLAIN, view.positionAmountLabel.getFont().getSize());
                        view.positionAmountLabel.setFont(f);
                        fillToolBar();
                    }
                    break;
                case RedTapeModel.PROP_SELECTED_SEARCH_RESULT:
                    reloadSelectionOnCustomerChange();
                    break;
                case RedTapeModel.PROP_SEARCH:
                    model.setSearchResult(Dl.remote().lookup(CustomerService.class).asUiCustomers(model.getSearch()));
                    break;
            }
        }
    };

    public RedTapeController() {
        this.accessDependentActions = new HashSet<>();
    }

    @PostConstruct
    private void initCdi() {
        dossierTableController.setSelectionHandler(this);
    }

    /**
     * Set the model to the controller
     * <p/>
     * @param model
     */
    public void setModel(RedTapeModel model) {
        if ( this.model != null ) this.model.removePropertyChangeListener(redTapeViewListener);
        this.model = model;
        dossierTableController.setModel(model.getDossierTableModel());
        this.model.addPropertyChangeListener(redTapeViewListener);
    }

    /**
     * Set the value of view
     *
     * @param view new value of view
     */
    public void setView(RedTapeView view) {
        if ( this.view != null ) this.view.removePropertyChangeListener(redTapeViewListener);
        this.view = view;
        this.view.addPropertyChangeListener(redTapeViewListener);
    }

    /**
     * Reload customer and all Dossiers.
     */
    private void reloadSelectionOnCustomerChange() {
        model.setSelectedDocument(null);
        model.setSelectedDossier(null);

        //ensure that even without selection a customer is found
        updateCustomer(model.getSelectedSearchResult() == 0 ? model.getPurchaseCustomer().id() : model.getSelectedSearchResult());

        view.dossierButtonPanel.removeAll();
        view.dossierButtonPanel.repaint();
        view.dossierCommentArea.setText("");
        if ( closedLoader != null && !closedLoader.isDone() ) {
            if ( !closedLoader.cancel(false) )
                JOptionPane.showMessageDialog(view, "Canceling of running loader not possible, call Olli!");
        }
        dossierTableController.getView().resetTableData((int)model.getPurchaseCustomer().id());
    }

    /**
     * Updates the selection in case of data change.
     *
     * @param dos
     */
    public void reloadSelectionOnStateChange(Dossier dos) {
        Dossier oldDos = model.getSelectedDossier();
        dossierTableController.getModel().update(oldDos, dos);
        model.setSelectedDossier(null);
        model.setSelectedDossier(dos);
    }

    /**
     * Updates the selection in case of dossier deletion.
     * <p>
     * @param dos
     */
    public void reloadOnDelete(Dossier dos) {
        dossierTableController.getModel().delete(dos);
        model.setSelectedDocument(null);
        model.setSelectedDossier(null);
        view.dossierButtonPanel.removeAll();
        view.dossierButtonPanel.repaint();
    }

    /**
     * Opens a dialog to create a Customer.
     */
    public void openCreateCustomer() {
        Dl.local().lookup(CustomerUiModifier.class).createCustomer(UiParent.of(view), (custId) -> {

            if ( custId == 0 ) {
                Ui.exec(() -> {
                    Ui.build().alert("Customer with Id 0 createt. Not possible. Either create error or we are running on a stub.");
                });
                return;
            }
            updateCustomer(custId);
            //reset search to avoid wrong customer selections
            model.setSearch(String.valueOf(custId));
            view.searchResultList.setSelectedIndex(0);
        });
    }

    /**
     * Opens a dialog to edit a Customer.
     * <p/>
     * @param recentCustomerId The customer that shall be edited
     */
    public void openUpdateCustomer(long recentCustomerId) {
        Dl.local().lookup(CustomerUiModifier.class).updateCustomer(UiParent.of(view), recentCustomerId, () -> {
            model.setSearch(String.valueOf(recentCustomerId));
            view.searchResultList.setSelectedIndex(0);
            reloadSelectionOnCustomerChange();
        });
    }

    /**
     * Opens a dialog to edit a dossiers comment.
     * <p/>
     * If Dossier.id equals 0, a wrapped Dossier from an old Sopo Auftrag is assumed and no change can be made.
     * <p/>
     * @param dossier the dossier from wich the comment will be changed
     */
    public void openEditComment(Dossier dossier) {
        if ( model.getSelectedDossier() == null ) {
            JOptionPane.showMessageDialog(view, "Kein Auftrag ausgewählt");
            return;
        }
        StringAreaView sav = new StringAreaView(dossier.getComment());
        OkCancelDialog<StringAreaView> dialog = new OkCancelDialog<>(parent(), Dialog.ModalityType.DOCUMENT_MODAL, "Bemerkungen editieren", sav);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            try {
                Dossier dos = Dl.remote().lookup(RedTapeWorker.class).updateComment(model.getSelectedDossier(), sav.getText());
                reloadSelectionOnStateChange(dos);
            } catch (UserInfoException ex) {
                Ui.handle(ex);
            }
        }
    }

    /**
     * Opens a dialog with detailed information of a {@link Dossier}.
     * <p/>
     * @param dos the {@link Dossier} entity.
     */
    public void openDossierDetailViewer(Dossier dos) {
        Ui.build(view).fx().show(() -> Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(dos.getId()), () -> new HtmlPane());
    }

    /**
     * Opens a dialog with detailed information of a {@link Document}.
     * <p/>
     * @param doc the {@link Document} entity.
     */
    public void openDocumentViewer(Document doc) {
        saft.build(view).title("Vorgang: " + doc.getIdentifier()).fx().show(() -> Css.toHtml5WithStyle(
                DocumentFormater.toHtmlDetailedWithPositions(doc)
                + "<br />"
                + remote.lookup(CustomerService.class).asHtmlHighDetailed(model.getPurchaseCustomer().id())
        ), () -> new HtmlPane());
    }

    @Override
    public void selected(Dossier dossier) {
        L.debug("Triggered Dossier selection for {}", dossier);
        model.setSelectedDossier(dossier);
    }

    /**
     * Fills the toolbar as well as organizing the popupmenus.
     * <p/>
     */
    public void fillToolBar() {
        view.actionBar.removeAll();
        view.actionBar.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
        view.actionBar.add(new JButton(new AbstractAction("Kunden bearbeiten") {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUpdateCustomer(model.getPurchaseCustomer().id());
                view.customerDetailArea.setText(Dl.remote().lookup(CustomerService.class).asHtmlHighDetailed(model.getPurchaseCustomer().id()));
            }
        }));

        //build customer dependant actions.
        if ( model.getPurchaseCustomer().violationMessage().isPresent() ) {
            Ui.build(view).alert("Kunde ist invalid: " + model.getPurchaseCustomer().violationMessage().get());
        } else if ( getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().id()) ) {
            // Don't allow anything here.
        } else if ( model.getPurchaseCustomer().flags().contains(CustomerFlag.SYSTEM_CUSTOMER) ) {
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), false, RedTapeController.this, model.getPurchaseCustomer().id())));
        } else {
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), false, RedTapeController.this, model.getPurchaseCustomer().id())));
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), true, RedTapeController.this, model.getPurchaseCustomer().id())));
        }

        JToolBar.Separator sep = new JToolBar.Separator();
        sep.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        view.actionBar.add(sep);

        if ( model.getSelectedDocument() != null && !getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().id()) ) {
            Document selDocument = model.getSelectedDocument();
            DossierUpdateAction modifyDocumentAction = new DossierUpdateAction(parent(), this, model.getPurchaseCustomer().id(), model.getSelectedDocument());
            view.actionBar.add(new JButton(modifyDocumentAction));

            //Deactivate Button if a Update isn't possible or allowed.
            if ( !isSelectedDocumentEditable() ) {
                modifyDocumentAction.setEnabled(false);
            }

            if ( selDocument.getType().equals(DocumentType.CREDIT_MEMO) ) {
                Dl.local().lookup(Guardian.class).add(modifyDocumentAction, CREATE_ANNULATION_INVOICE);
                accessDependentActions.add(modifyDocumentAction);
            }
            Action detailsAction = new AbstractAction("Details") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openDocumentViewer(model.getSelectedDocument());
                }
            };

            Action changeSettlementAction = new AbstractAction("Bezahlung ändern") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saft.build(view).fx().eval(SettlementPane.class).cf()
                            .thenAccept(ps -> {
                                Document d = model.getSelectedDocument();
                                d.clearPaymentSettlements();
                                d.add(ps);
                                d = remote.lookup(RedTapeWorker.class).update(d, null, Dl.local().lookup(Guardian.class).getUsername());
                                reloadSelectionOnStateChange(d.getDossier());
                            }).handle(saft.handler(view));
                }
            };

            if (model.getSelectedDocument().isClosed()) {
                changeSettlementAction.setEnabled(false);
            }
            
            view.setDocumentPopupActions(modifyDocumentAction, changeSettlementAction, detailsAction);

            view.actionBar.add(new JButton(new DocumentJasperViewAction(selDocument, DocumentViewType.DEFAULT, this, model.getPurchaseCustomer().id())));
            if ( selDocument.getType() == DocumentType.ORDER )
                view.actionBar.add(new JButton(new DocumentJasperViewAction(selDocument, DocumentViewType.RESERVATION, this, model.getPurchaseCustomer().id())));
            if ( !EnumSet.of(DocumentType.ANNULATION_INVOICE, DocumentType.COMPLAINT, DocumentType.CREDIT_MEMO).contains(selDocument.getType()) ) {
                view.actionBar.add(new DocumentJasperViewAction(selDocument, DocumentViewType.SHIPPING, this, model.getPurchaseCustomer().id()));
            }
        }
        for (Component component : view.actionBar.getComponents()) {
            if ( component instanceof JButton ) {
                ((JButton)component).setBorder(new BevelBorder(SoftBevelBorder.LOWERED, Color.lightGray, Color.DARK_GRAY, Color.DARK_GRAY, Color.lightGray));
            }
        }
        view.actionBar.revalidate();
        view.actionBar.repaint();
    }

    private Document refreshDocumentSelection(Dossier dos) {
        for (Document document : dos.getActiveDocuments()) {
            if ( document.getDirective() != Directive.NONE ) return dos.getActiveDocuments(document.getType()).get(0);
        }
        if ( !dos.getActiveDocuments(DocumentType.INVOICE).isEmpty() ) {
            return dos.getActiveDocuments(DocumentType.INVOICE).get(0);
        } else {
            return dos.getActiveDocuments().get(dos.getActiveDocuments().size() - 1);
        }
    }

    /**
     * Return weither the selected Document is editable or not.
     * <p/>
     * @return true if its possible to fire the {@link DossierUpdateAction}.
     */
    private boolean isSelectedDocumentEditable() {
        //Document is canceled.
        if ( model.getSelectedDocument().getConditions().contains(Condition.CANCELED) ) return false;

        //Dossier is a wrapped SopoAuftrag
        if ( model.getSelectedDossier().getId() == 0 ) return false;

        //INVOICE is the hightst document in the hierarchy.
        if ( !model.getSelectedDossier().getActiveDocuments(DocumentType.INVOICE).isEmpty() && model.getSelectedDocument().getType() == DocumentType.ORDER )
            return false;

        //COMPLAINT is the hightst document in the hierarchy.
        if ( !model.getSelectedDossier().getActiveDocuments(DocumentType.COMPLAINT).isEmpty() && model.getSelectedDocument().getType() == DocumentType.INVOICE )
            return false;

        //CREDIT_MEMO is the hightst document in the hierarchy.
        if ( !model.getSelectedDossier().getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty()
                && (model.getSelectedDocument().getType() == DocumentType.COMPLAINT
                    || model.getSelectedDocument().getType() == DocumentType.INVOICE) ) return false;

        //ANNULATION_INVOICE is the hightst document in the hierarchy.
        if ( !model.getSelectedDossier().getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty()
                && (model.getSelectedDocument().getType() == DocumentType.COMPLAINT
                    || model.getSelectedDocument().getType() == DocumentType.INVOICE) ) return false;

        return true;
    }

    private Window parent() {
        return UiCore.global().core(Swing.class).unwrap(UiParent.of(view)).orElse(null);
    }

    private void updateCustomer(long id) {
        CustomerService cs = Dl.remote().lookup(CustomerService.class);
        model.setPurchaseCustomer(cs.asCustomerMetaData(id));
    }

}
