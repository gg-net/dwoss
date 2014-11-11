/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.dossiertable;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.api.LegacyBridge;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.redtape.IDossierSelectionHandler;

import eu.ggnet.dwoss.common.ExceptionUtil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static eu.ggnet.dwoss.redtape.dossiertable.DossierTableController.IMAGE_NAME.*;


import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DossierTableController {
    
    @Getter
    @RequiredArgsConstructor
    static enum IMAGE_NAME {

        CLOSED_ICON("closed_icon.png"),
        COMPLAINT_ICON("complaint_icon.png"),
        CANCELED_ICON("canceled_icon.png"),
        COMPLAINT_REJECTED_ICON("complaint_rejected_icon.png"),
        COMPLAINT_WITHDRAWN_ICON("complaint_withdrawn_icon.png"),
        COMPLAINT_ACCEPTED_ICON("complaint_accepted_icon.png"),
        ANNULATION_INVOICE_ICON("annulation_invoice_icon.png"),
        CREDIT_MEMO_ICON("credit_memo_icon.png"),
        HELP_BASIC_OPEN("basic_open_sample_loaded.png"),
        HELP_FILTER_ALL("filter_all.png"),
        HELP_FILTER_SALE_OPEN("filter_sale_open.png"),
        HELP_FILTER_SALE_CLOSED("filter_sale_closed.png"),
        HELP_FILTER_ACC_OPEN("filter_acc_open.png"),
        HELP_FILTER_ACC_CLOSED("filter_acc_closed.png"),
        HELP_CHANGE_COLUMNS("change_columns.png");

        private final String fileName;
    }

    private final static Dossier[] T = new Dossier[0];

    private abstract class DossierLoader extends SwingWorker<Void, Dossier> {

        protected final Logger L = LoggerFactory.getLogger(this.getClass());

        protected final long customerId;

        private final String loader;

        public DossierLoader(long customerId, String loader) {
            this.customerId = customerId;
            this.loader = loader;
            L.debug("new Loader({},hashcode={})", loader, hashCode());
        }

        protected abstract List<Dossier> find(int last, int amount);

        @Override
        protected Void doInBackground() throws Exception {
            List<Dossier> foundDossiers;
            int amount = 3;
            int last = 0;
            view.progressBar.setIndeterminate(true);
            view.progressBar.setString("Lade " + loader + " Vorgänge");
            do {
                foundDossiers = find(last, amount);
                last += amount;
                publish(foundDossiers.toArray(T));
                L.debug("T({}) published: {}", Thread.currentThread().getName(), toIdIdentifieres(foundDossiers));
            } while (!foundDossiers.isEmpty() && !isCancelled());
            L.debug("T({}) is complete", Thread.currentThread().getName());
            return null;
        }

        @Override
        protected void process(List<Dossier> dossiers) {
            if ( !isCancelled() ) {
                for (Dossier dossier : dossiers) {
                    model.add(dossier);
                }
                L.debug("T({}) processed: {} ", Thread.currentThread().getName(), toIdIdentifieres(dossiers));
            }
        }

        @Override
        protected void done() {
            try {
                get();
                view.progressBar.setIndeterminate(false);
                view.progressBar.setString(loader + " Vorgänge geladen");
            } catch (CancellationException ex) {
                L.debug("Worker {} canceled", this);
            } catch (InterruptedException | ExecutionException ex) {
                ExceptionUtil.show(null, ex);
            }
        }
    }

    private class ClosedDossierLoader extends DossierLoader {

        public ClosedDossierLoader(long customerId) {
            super(customerId, "Geschlossene");
        }

        @Override
        protected List<Dossier> find(int last, int amount) {
            return lookup(RedTapeAgent.class).findDossiersClosedByCustomerIdEager(customerId, last, amount);
        }

    }

    private class LegacyDossierLoader extends DossierLoader {

        public LegacyDossierLoader(long customerId) {
            super(customerId, lookup(LegacyBridge.class).name());
        }

        @Override
        protected List<Dossier> find(int last, int amount) {
            return lookup(LegacyBridge.class).findByCustomerId(customerId, last, amount);
        }

    }

    private class OpenDossierLoader extends DossierLoader {

        public OpenDossierLoader(long customerId) {
            super(customerId, "Offene");
        }

        @Override
        protected List<Dossier> find(int last, int amount) {
            if ( last == 0 ) return lookup(RedTapeAgent.class).findDossiersOpenByCustomerIdEager(customerId);
            return new ArrayList<>();
        }

    }

    private DossierTableView view;

    private DossierTableModel model;

    private DossierLoader closedLoader;

    private DossierLoader openLoader;

    private DossierLoader legacyLoader;

    private IDossierSelectionHandler selectionHandler;

    private boolean openLoaded = false;

    private boolean closedLoaded = false;

    private boolean legacyLoaded = false;

    public DossierTableModel getModel() {
        return model;
    }

    public void setModel(DossierTableModel model) {
        this.model = model;
    }

    public DossierTableView getView() {
        return view;
    }

    public void setView(DossierTableView view) {
        this.view = view;
    }

    public IDossierSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    public void setSelectionHandler(IDossierSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    public void loadOpenDossiers(long customerId) {
        if ( model == null || openLoaded ) return;
        openLoader = new OpenDossierLoader(customerId);
        openLoaded = true;
        openLoader.execute();

    }

    public void loadClosedDossiers(long customerId) {
        if ( model == null || customerId <= 0 || closedLoaded ) return;
        closedLoader = new ClosedDossierLoader(customerId);
        closedLoaded = true;
        closedLoader.execute();
    }

    public void loadLegacyDossiers(long customerId) {
        if ( model == null || customerId <= 0 || legacyLoaded || !Client.hasFound(LegacyBridge.class) ) return;
        legacyLoader = new LegacyDossierLoader(customerId);
        legacyLoaded = true;
        legacyLoader.execute();
    }

    public void resetLoader() {
        if ( openLoader != null ) openLoader.cancel(true);
        if ( closedLoader != null ) closedLoader.cancel(true);
        if ( legacyLoader != null ) legacyLoader.cancel(true);
        openLoaded = false;
        closedLoaded = false;
        legacyLoaded = false;
    }

    public void selectionChanged(Dossier dos) {
        selectionHandler.selected(dos);
    }

    /**
     * Generates a html formated String that represents a description of usage for the DossierTableView.
     * <p/>
     * @return a html formated String that represents a description of usage for the DossierTableView.
     */
    public String generateHelp() {
        String res = "<h1>Nutzung der Vorgangstabelle</h1><ol type=\"disc\">";
        res += "<li>Wurde ein Kunde ausgewählt sind standardmäßig alle verkaufstechnisch offenen Vorgänge geladen."
                + "<br />Bei einer Auswahl verhält sich der Rest der Anwendung wie gehabt (Dokumente u. Positionen werden geladen).<br />"
                + "<img src=\"" + load(HELP_BASIC_OPEN) + "\"><br /></li>";
        res += "<li>Zusätzlich kann jetzt zwischen fünf verschiedenen Filtern gewechselt werden der Vorgänge aus/einblendet:<br />"
                + "- <img src=\"" + load(HELP_FILTER_ALL) + "\"> liefert alle Vorgänge.<br />"
                + "- <img src=\"" + load(HELP_FILTER_SALE_OPEN) + "\"> liefert alle verkaufstechnisch offenen Vorgänge.<br />"
                + "- <img src=\"" + load(HELP_FILTER_SALE_CLOSED) + "\"> liefert alle verkaufstechnisch geschlossenen Vorgänge.<br />"
                + "- <img src=\"" + load(HELP_FILTER_ACC_OPEN) + "\"> liefert alle buchhalterisch offenen Vorgänge.<br />"
                + "- <img src=\"" + load(HELP_FILTER_ACC_CLOSED) + "\"> liefert alle buchhalterisch abgeschlossenen Vorgänge.<br /></li>";
        res += "<li>Zusätzlich zu einer Drag&Drop funktion für Spalten kann jede Spalte ein/ausgeblendet werden:<br />"
                + "<img src=\"" + load(HELP_CHANGE_COLUMNS) + "\"></li>";
        res += "</ol>";
        return res;
    }

    private static List<String> toIdIdentifieres(Collection<Dossier> dossiers) {
        List<String> result = new ArrayList<>();
        for (Dossier dossier : dossiers) {
            result.add("(id=" + dossier.getId() + "," + dossier.getIdentifier() + ")");
        }
        return result;
    }
    
    static URL load(IMAGE_NAME image) {
        return DossierIconPanelRenderer.class.getResource(image.getFileName());
    }

}
