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
package eu.ggnet.dwoss.redtapext.ui.dbs;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.common.ui.HtmlDialog;
import eu.ggnet.dwoss.common.ui.table.PojoFilter;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class DossierFilterController {

    protected class DossierFilter implements PojoFilter<Dossier> {

        private boolean isDossierId;

        private boolean isDirective;

        private boolean isConditions;

        private String dossierId = "";

        private boolean shouldSysCustomer;

        private boolean shouldNormalCustomer;

        private boolean isBookingClosed;

        private boolean isExactlyBriefed;

        private boolean isNotBookingClosed;

        private boolean isNotExactlyBriefed;

        private boolean isType;

        private boolean isPaymentMethod;

        private PaymentMethod paymentMethod = null;

        private DocumentType type = null;

        private Directive directive = null;

        private boolean inverse;

        private boolean inverseType;

        private EnumSet<Condition> conditions = EnumSet.noneOf(Condition.class);

        private final List<Long> systemCustomerIds;

        public DossierFilter(List<Long> systemCustomerIds) {
            this.systemCustomerIds = systemCustomerIds;
        }

        @Override
        public boolean filter(Dossier dossier) {
            //Collect Data

            Set<Condition> docConditions = new HashSet<>();
            for (Document doc : dossier.getActiveDocuments()) {
                docConditions.addAll(doc.getConditions());
            }

            Document activeDocument = null;
            for (Document document : dossier.getActiveDocuments()) {
                if ( document.getDirective() != Directive.NONE ) activeDocument = dossier.getActiveDocuments(document.getType()).get(0);
            }
            if ( !dossier.getActiveDocuments(DocumentType.INVOICE).isEmpty() ) {
                activeDocument = dossier.getActiveDocuments(DocumentType.INVOICE).get(0);
            } else {
                activeDocument = dossier.getActiveDocuments().get(dossier.getActiveDocuments().size() - 1);
            }

            //Apply Filter
            boolean isApply;
            try {
                isApply = !isDossierId || dossier.getIdentifier().matches("(?i).*" + dossierId + ".*");
            } catch (PatternSyntaxException e) {
                isApply = !isDossierId;
            }
            isApply &= !isDirective || dossier.getCrucialDirective() == directive;

            isApply &= !isConditions || inverse ^ docConditions.containsAll(conditions);

            if ( shouldSysCustomer && !shouldNormalCustomer ) {
                isApply &= systemCustomerIds.contains(dossier.getCustomerId());
            } else if ( !shouldSysCustomer && shouldNormalCustomer ) {
                isApply &= !systemCustomerIds.contains(dossier.getCustomerId());
            }

            if ( isBookingClosed && !isNotBookingClosed ) {
                isApply &= dossier.isClosed();
            } else if ( !isBookingClosed && isNotBookingClosed ) {
                isApply &= !dossier.isClosed();
            }

            if ( isExactlyBriefed && !isNotExactlyBriefed ) {
                isApply &= activeDocument.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED);
            } else if ( !isExactlyBriefed && isNotExactlyBriefed ) {
                isApply &= !activeDocument.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED);
            }

            if ( type == DocumentType.ORDER ) isApply &= !isType || inverseType ^ dossier.getActiveDocuments(DocumentType.INVOICE).isEmpty();
            else isApply &= !isType || inverseType ^ !dossier.getActiveDocuments(type).isEmpty();

            isApply &= !isPaymentMethod || dossier.getPaymentMethod().equals(paymentMethod);

            return isApply;
        }
    }

    private class DossierLoader extends SwingWorker<Void, Dossier> {

        private final Logger L = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Void doInBackground() throws Exception {
            view.progressBar.setIndeterminate(true);
            List<Dossier> foundDossiers;
            int amount = 10;
            int last = nextToLoad;
            do {
                L.debug("loading dossiers from {} to {}", last, amount);
                foundDossiers = Dl.remote().lookup(RedTapeAgent.class).findAllEagerDescending(last, amount);
                last += amount;
                publish(foundDossiers.toArray(new Dossier[0]));
                L.debug("T({}) published: {}", Thread.currentThread().getName(), identifiers(foundDossiers));
            } while (last < (nextToLoad + 100) && !isCancelled());
            L.debug("T({}) is complete", Thread.currentThread().getName());
            return null;
        }

        @Override
        protected void process(List<Dossier> dossiers) {
            if ( isCancelled() ) return;
            for (Dossier dossier : dossiers) {
                model.add(dossier);
            }
            L.debug("processed: {}", identifiers(dossiers));
        }

        @Override
        protected void done() {
            try {
                get();
                view.progressBar.setIndeterminate(false);
            } catch (CancellationException ex) {
                // Do nothing, normal cancel.
            } catch (ExecutionException | InterruptedException ex) {
                Ui.handle(ex);
            }
        }

        private List<String> identifiers(List<Dossier> dossiers) {
            List<String> result = new ArrayList<>();
            for (Dossier dossier : dossiers) {
                result.add("(id=" + dossier.getId() + "," + dossier.getIdentifier() + ")");
            }
            return result;
        }
    };

    private final DossierFilter filter = new DossierFilter(Dl.remote().lookup(CustomerService.class).allSystemCustomerIds());

    private DossierFilterModel model;

    private DossierFilterView view;

    private final List<Dossier> dossiers = new ArrayList<>();

    private DossierLoader dossierLoader;

    private int nextToLoad = 0;

    public void setView(DossierFilterView view) {
        this.view = view;
        setAvaibleConditionsButtons();
    }

    public void setModel(final DossierFilterModel model) {
        this.model = model;
        this.model.setFilter(filter);
        for (Dossier dossier : dossiers) {
            this.model.add(dossier);
        }
        restartDossierLoader();
    }

    public void filterDossierId(String id, boolean enable) {
        filter.dossierId = id;
        filter.isDossierId = enable;
        model.fireTableDataChanged();
    }

    public void filterDirective(Directive d, boolean enable) {
        filter.directive = d;
        filter.isDirective = enable;
        model.fireTableDataChanged();
    }

    public void filterConditions(EnumSet<Condition> conditions, boolean enable) {
        filter.conditions = conditions;
        filter.isConditions = enable;
        model.fireTableDataChanged();
    }

    public void filterInvers(boolean enable) {
        filter.inverse = enable;
        model.fireTableDataChanged();
    }

    public void filterBookingClosed(boolean isBookingClosed, boolean isNotBookingClosed) {
        filter.isBookingClosed = isBookingClosed;
        filter.isNotBookingClosed = isNotBookingClosed;
        model.fireTableDataChanged();
    }

    public void filterExcatlyBriefed(boolean isExactlyBriefed, boolean isNotExactlyBriefed) {
        filter.isExactlyBriefed = isExactlyBriefed;
        filter.isNotExactlyBriefed = isNotExactlyBriefed;
        model.fireTableDataChanged();
    }

    public void filterType(DocumentType type, boolean enable) {
        filter.type = type;
        filter.isType = enable;
        model.fireTableDataChanged();
    }

    public void filterCustomer(boolean sysCustomer, boolean normalCustomer) {
        filter.shouldNormalCustomer = normalCustomer;
        filter.shouldSysCustomer = sysCustomer;
        model.fireTableDataChanged();
    }

    public void filterInverseType(boolean inversType) {
        filter.inverseType = inversType;
        model.fireTableDataChanged();
    }

    public void filterPaymentMethod(PaymentMethod paymentMethod, boolean isEnable) {
        filter.paymentMethod = paymentMethod;
        filter.isPaymentMethod = isEnable;
        model.fireTableDataChanged();
    }

    public void toogleActive(String conditionName) {
        Condition condition = null;

        for (Condition forCondition : Condition.values()) {
            if ( forCondition.getName().equals(conditionName) ) {
                condition = forCondition;
            }
        }
        if ( filter.conditions.contains(condition) ) {
            filter.conditions.remove(condition);
        } else {
            filter.conditions.add(condition);
        }
        filterConditions(filter.conditions, !filter.conditions.isEmpty());

    }

    public void showSelectedDossier() {
        new HtmlDialog(view, Dialog.ModalityType.MODELESS).setText(Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(model.getSelected().getId())).setVisible(true);
    }

    /**
     * This Method sets on the view all avaible conditions as a checkbox.
     * In detail, this method gets the Enum of Condition and create for every Condition a Action.
     * This action will be setted at a JCheckbox and this will be added ad the conditionPanel.
     */
    public void setAvaibleConditionsButtons() {
        for (Condition condition : Condition.values()) {
            AbstractAction abstractAction = new AbstractAction(condition.getName()) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    DossierFilterController.this.toogleActive(ae.getActionCommand());
                }
            };
            view.conditionPanel.add(new JCheckBox(abstractAction));
        }
        view.repaint();
        view.revalidate();
    }

    public void cancelLoader() {
        if ( !dossierLoader.isDone() ) dossierLoader.cancel(false);
    }

    private void restartDossierLoader() {
        nextToLoad = 0;
        dossierLoader = new DossierLoader();
        dossierLoader.execute();
    }

    public void loadNextHundred() {
        dossierLoader = new DossierLoader();
        dossierLoader.execute();
        nextToLoad += 100;
    }

    public void openDossierDetailViewer(Dossier dos) {
        new HtmlDialog(view, Dialog.ModalityType.MODELESS).setText(Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(dos.getId())).setVisible(true);
    }
}
