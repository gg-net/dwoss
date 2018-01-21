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

import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.dwoss.redtapext.ui.cao.common.StringAreaView;
import eu.ggnet.dwoss.redtapext.ui.cao.common.IDossierSelectionHandler;
import eu.ggnet.dwoss.redtapext.ui.LegacyBridgeUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.customer.api.CustomerCos;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtapext.ui.cao.dossierTable.DossierTableController;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;
import eu.ggnet.dwoss.redtape.state.RedTapeStateTransition.Hint;
import eu.ggnet.dwoss.redtape.state.*;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.DocumentPrintAction;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.JRViewerCask;
import eu.ggnet.dwoss.redtapext.ui.cao.stateaction.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;

import lombok.Getter;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ANNULATION_INVOICE;
import static eu.ggnet.saft.Client.lookup;

/**
 * The RedTape main component controller handling all in/output as well as update actions provided by the {@link RedTapeView}.
 * <p>
 * @author pascal.perau
 */
public class RedTapeController implements IDossierSelectionHandler {

    @Getter
    private RedTapeModel model;

    @Getter
    private RedTapeView view;

    @Getter
    private final DossierTableController dossierTableController;

    private Set<Action> accessDependentActions;

    private NavigableSet<Long> viewOnlyCustomerIds;

    private SwingWorker<Void, Dossier> closedLoader;

    private Boolean shippingCostUiHelpEnabled = null;

    private boolean isShippingCostUiHelpEnabled() {
        if ( shippingCostUiHelpEnabled == null ) shippingCostUiHelpEnabled = Client.hasFound(ShippingCostService.class);
        return shippingCostUiHelpEnabled;
    }

    private NavigableSet<Long> getViewOnlyCustomerIds() {
        if ( viewOnlyCustomerIds == null ) viewOnlyCustomerIds = Client.lookup(MandatorSupporter.class).loadSalesdata().getViewOnlyCustomerIds();
        return viewOnlyCustomerIds;
    }

    private final PropertyChangeListener redTapeViewListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            switch (evt.getPropertyName()) {
                case RedTapeModel.PROP_SELECTED_DOSSIER:

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
                        if ( model.getPurchaseCustomer() == null ) JOptionPane.showMessageDialog(null, "Kein Kunde gewählt");
                        else {
                            CustomerDocument cdoc = new CustomerDocument(
                                    model.getPurchaseCustomer().getFlags(),
                                    model.getSelectedDocument(),
                                    model.getPurchaseCustomer().getShippingCondition(),
                                    model.getPurchaseCustomer().getPaymentMethod());
                            List<StateTransition<CustomerDocument>> transitions = lookup(RedTapeWorker.class).getPossibleTransitions(cdoc);
                            // Remove old Actions from the receiving of right changes.
                            for (Action accessDependent : accessDependentActions) {
                                if ( accessDependent instanceof AccessableAction )
                                    lookup(Guardian.class).remove((AccessableAction)accessDependent);
                                else lookup(Guardian.class).remove(accessDependent);
                            }
                            accessDependentActions.clear();
                            List<Action> stateActions = new ArrayList<>();
                            //check if selected document is order and invoice exist to restrict transitions
                            if ( model.getSelectedDocument().getDossier().getId() == 0 ) {
                                // Now this implies a legacy wrapped dossier, so no actions are possible.
                            } else if ( (model.getSelectedDocument().getType() == DocumentType.ORDER
                                         && !model.getSelectedDossier().getActiveDocuments(DocumentType.INVOICE).isEmpty())
                                    || getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().getId()) ) {
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
                                        lookup(Guardian.class).add(creditMemoAction);
                                        accessDependentActions.add(creditMemoAction);
                                        stateActions.add(creditMemoAction);
                                    } else if ( stateTransition.getHints().contains(Hint.CREATES_COMPLAINT) ) {
                                        ComplaintAction action = new ComplaintAction(parent(), RedTapeController.this, model.getSelectedDocument(), stateTransition);
                                        stateActions.add(action);
                                    } else if ( stateTransition.getHints().contains(Hint.CREATES_ANNULATION_INVOICE) ) {
                                        AnnulationInvoiceAction action = new AnnulationInvoiceAction(parent(), RedTapeController.this, model.getSelectedDocument(), stateTransition);
                                        lookup(Guardian.class).add(action);
                                        accessDependentActions.add(action);
                                        stateActions.add(action);
                                    } else if ( stateTransition.getEnablingRight() != null && stateTransition.getEnablingRight().equals(AtomicRight.CREATE_ANNULATION_INVOICE) ) {
                                        DefaultStateTransitionAction action = new DefaultStateTransitionAction(parent(), RedTapeController.this, cdoc, stateTransition);
                                        lookup(Guardian.class).add(action, AtomicRight.CREATE_ANNULATION_INVOICE);
                                        accessDependentActions.add(action);
                                        stateActions.add(action);
                                    } else {
                                        stateActions.add(new DefaultStateTransitionAction(parent(), RedTapeController.this, cdoc, stateTransition));
                                    }
                                }
                                if ( model.getSelectedDocument().getType() == DocumentType.BLOCK ) {
                                    DossierDeleteAction action = new DossierDeleteAction(parent(), RedTapeController.this, model.getSelectedDossier());
                                    lookup(Guardian.class).add(action);
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
                    model.setSearchResult(lookup(UniversalSearcher.class).searchCustomers(model.getSearch()));
                    break;
            }
        }
    };

    public static RedTapeController build() {
        RedTapeView view = new RedTapeView();
        RedTapeModel model = new RedTapeModel();
        RedTapeController controller = new RedTapeController();
        view.setController(controller);
        view.setModel(model);
        controller.setModel(model);
        controller.setView(view);
        return controller;
    }

    public RedTapeController() {
        this.accessDependentActions = new HashSet<>();
        this.dossierTableController = new DossierTableController();
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
        dossierTableController.setView(view.dossierTableView);
        this.view.addPropertyChangeListener(redTapeViewListener);
    }

    /**
     * Reload customer and all Dossiers.
     */
    private void reloadSelectionOnCustomerChange() {
        model.setSelectedDocument(null);
        model.setSelectedDossier(null);

        //ensure that even without selection a customer is found
        if ( model.getSelectedSearchResult() == 0 ) model.setPurchaseCustomer(model.getPurchaseCustomer().getId());
        else model.setPurchaseCustomer(model.getSelectedSearchResult());
        view.dossierButtonPanel.removeAll();
        view.dossierButtonPanel.repaint();
        view.dossierCommentArea.setText("");
        if ( closedLoader != null && !closedLoader.isDone() ) {
            if ( !closedLoader.cancel(false) )
                JOptionPane.showMessageDialog(view, "Canceling of running loader not possible, call Olli!");
        }
        view.dossierTableView.resetTableData((int)model.getPurchaseCustomer().getId());
    }

    /**
     * Updates the selection in case of data change.
     */
    public void reloadSelectionOnStateChange(Dossier dos) {
        Dossier oldDos = model.getSelectedDossier();
        dossierTableController.getModel().update(oldDos, dos);
        model.setSelectedDossier(null);
        model.setSelectedDossier(dos);
    }

    /**
     * Updates the selection in case of dossier deletion.
     * <p/>
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
        long customerId = lookup(CustomerCos.class).createCustomer();
        if ( customerId == 0 ) {
            UiAlert.message("Customer with Id 0 createt. Not possible. Either create error or we are running on a stub.");
            return;
        }
        model.setPurchaseCustomer(customerId);
        //reset search to avoid wrong customer selections
        model.setSearch(String.valueOf(customerId));
        view.searchResultList.setSelectedIndex(0);
    }

    /**
     * Opens a dialog to edit a Customer.
     * <p/>
     * @param recentCustomerId The customer that shall be edited
     */
    public void openUpdateCustomer(long recentCustomerId) {
        if ( !lookup(CustomerCos.class).updateCustomer(recentCustomerId) ) return;
        //reset search to avoid wrong customer selections
        model.setSearch(String.valueOf(recentCustomerId));
        view.searchResultList.setSelectedIndex(0);
        reloadSelectionOnCustomerChange();
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
                Dossier dos = lookup(RedTapeWorker.class).updateComment(model.getSelectedDossier(), sav.getText());
                reloadSelectionOnStateChange(dos);
            } catch (UserInfoException ex) {
                Ui.handle(ex);
            }
        }
    }

    /**
     * This method is called if a chosen Document will be printed and/or sent per E-Mail.
     * This Method become a Document and will open a JasperViewer that contains also a Send Button for sending E-Mail
     * <p/>
     * @param document
     */
    public void openDocument(Document document, boolean printAsReservation) {
        JasperPrint print = lookup(DocumentSupporter.class).render(document, (printAsReservation ? DocumentViewType.RESERVATION : DocumentViewType.DEFAULT));
        JDialog d = new JDialog(parent(), "Dokument drucken/versenden");
        d.setSize(800, 1000);
        d.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        d.setLocationRelativeTo(view);
        d.getContentPane().setLayout(new BorderLayout());
        boolean canEmaild = model.getPurchaseCustomer().getEmail() != null && model.getPurchaseCustomer().getEmail().trim().isEmpty();
        JRViewerCask jrViewerCask = new JRViewerCask(print, document, (printAsReservation ? DocumentViewType.RESERVATION : DocumentViewType.DEFAULT), canEmaild);
        d.getContentPane().add(jrViewerCask, BorderLayout.CENTER);
        d.setVisible(true);
        if ( jrViewerCask.isCorrectlyBriefed() ) {
            reloadSelectionOnStateChange(lookup(DocumentSupporter.class).briefed(document, lookup(Guardian.class).getUsername()));
        }
    }

    /**
     * Opens a dialog with detailed information of a {@link Dossier}.
     * <p/>
     * @param dos the {@link Dossier} entity.
     */
    public void openDossierDetailViewer(Dossier dos) {
        new HtmlDialog(parent(), Dialog.ModalityType.MODELESS).setText(LegacyBridgeUtil.toHtmlDetailed(dos)).setVisible(true);
    }

    /**
     * Opens a dialog with detailed information of a {@link Document}.
     * <p/>
     * @param doc the {@link Document} entity.
     */
    public void openDocumentViewer(Document doc) {
        HtmlDialog dialog = new HtmlDialog(parent(), Dialog.ModalityType.MODELESS);
        dialog.setText("<html>" + DocumentFormater.toHtmlDetailedWithPositions(doc) + "<br />"
                + lookup(CustomerService.class).asHtmlHighDetailed(model.getPurchaseCustomer().getId()) + "</html>");
        dialog.setVisible(true);
    }

    @Override
    public void selected(Dossier dossier) {
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
                openUpdateCustomer(model.getPurchaseCustomer().getId());
                view.customerDetailArea.setText(lookup(CustomerService.class).asHtmlHighDetailed(model.getPurchaseCustomer().getId()));
            }
        }));

        //build customer dependant actions.
        if ( getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().getId()) ) {
            // Don't allow anything here.
        } else if ( model.getPurchaseCustomer().getFlags().contains(CustomerFlag.SYSTEM_CUSTOMER) ) {
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), false, RedTapeController.this, model.getPurchaseCustomer().getId())));
        } else {
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), false, RedTapeController.this, model.getPurchaseCustomer().getId())));
            view.actionBar.add(new JButton(new DossierCreateAction(parent(), true, RedTapeController.this, model.getPurchaseCustomer().getId())));
        }

        JToolBar.Separator sep = new JToolBar.Separator();
        sep.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        view.actionBar.add(sep);

        if ( model.getSelectedDocument() != null && !getViewOnlyCustomerIds().contains(model.getPurchaseCustomer().getId()) ) {
            Document selDocument = model.getSelectedDocument();
            DossierUpdateAction action = new DossierUpdateAction(parent(), this, model.getPurchaseCustomer().getId(), model.getSelectedDocument());
            view.actionBar.add(new JButton(action));

            //Deactivate Button if a Update isn't possible or allowed.
            if ( !isSelectedDocumentEditable() ) {
                action.setEnabled(false);
            }

            if ( selDocument.getType().equals(DocumentType.CREDIT_MEMO) ) {
                lookup(Guardian.class).add(action, CREATE_ANNULATION_INVOICE);
                accessDependentActions.add(action);
            }
            view.setDocumentPopupActions(action, new AbstractAction("Details") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openDocumentViewer(model.getSelectedDocument());
                }
            });

            view.actionBar.add(new JButton(new DocumentPrintAction(selDocument, DocumentViewType.DEFAULT, this, model.getPurchaseCustomer().getId())));
            if ( selDocument.getType() == DocumentType.ORDER )
                view.actionBar.add(new JButton(new DocumentPrintAction(selDocument, DocumentViewType.RESERVATION, this, model.getPurchaseCustomer().getId())));
            if ( !EnumSet.of(DocumentType.ANNULATION_INVOICE, DocumentType.COMPLAINT, DocumentType.CREDIT_MEMO).contains(selDocument.getType()) ) {
                view.actionBar.add(new DocumentPrintAction(selDocument, DocumentViewType.SHIPPING, this, model.getPurchaseCustomer().getId()));
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
        return SwingCore.windowAncestor(view).orElse(SwingCore.mainFrame());
    }

}
