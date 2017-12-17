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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Set;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.redtapext.ui.cao.dossierTable.DossierTableModel;

import eu.ggnet.dwoss.util.Tuple2;

import lombok.Getter;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * The RedTape main component model containing all selection and list information needed.
 * <p>
 * @author pascal.perau
 */
public class RedTapeModel {

    private List<Document> documents;

    private Set<Position> positions;

    private Dossier selectedDossier;

    private Document selectedDocument;

    private String search;

    private List<Tuple2<Long, String>> searchResult;

    @Getter
    private CustomerMetaData purchaseCustomer;

    private long selectedSearchResult;

    @Getter
    private DossierTableModel dossierTableModel;

    public static final String PROP_DIRECTIVE_BUTTON_ACTIONS = "directiveButtonActions";

    public static final String PROP_DOSSIER_BUTTON_ACTIONS = "dossierButtonActions";

    public static final String PROP_SELECTED_SEARCH_RESULT = "selectedSearchResult";

    public static final String PROP_CUSTOMER = "customer";

    public static final String PROP_SEARCH_RESULT = "searchResult";

    public static final String PROP_SEARCH = "search";

    public static final String PROP_SELECTED_DOSSIER = "selectedDossier";

    public static final String PROP_SELECTED_DOCUMENT = "selectedDocument";

    public static final String PROP_POSITIONS = "positions";

    public static final String PROP_DOCUMENTS = "documents";

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public RedTapeModel() {
        this.dossierTableModel = new DossierTableModel();
    }

    /**
     * Get the value of searchResult
     *
     * @return the value of searchResult
     */
    public List<Tuple2<Long, String>> getSearchResult() {
        return searchResult;
    }

    /**
     * Set the value of searchResult
     *
     * @param searchResult new value of searchResult
     */
    public void setSearchResult(List<Tuple2<Long, String>> searchResult) {
        List<Tuple2<Long, String>> oldSearchResult = this.searchResult;
        this.searchResult = searchResult;
        propertyChangeSupport.firePropertyChange(PROP_SEARCH_RESULT, oldSearchResult, searchResult);
    }

    /**
     * Get the value of selectedSearchResult
     *
     * @return the value of selectedSearchResult
     */
    public long getSelectedSearchResult() {
        return selectedSearchResult;
    }

    /**
     * Set the value of selectedSearchResult
     *
     * @param selectedSearchResult new value of selectedSearchResult
     */
    public void setSelectedSearchResult(long selectedSearchResult) {
        long oldSelectedSearchResult = this.selectedSearchResult;
        this.selectedSearchResult = selectedSearchResult;
        propertyChangeSupport.firePropertyChange(PROP_SELECTED_SEARCH_RESULT, oldSelectedSearchResult, selectedSearchResult);
    }

    /**
     * Set the value of customer
     *
     * @param id id of the customer
     */
    public void setPurchaseCustomer(long id) {
        CustomerMetaData old = this.purchaseCustomer;
        this.purchaseCustomer = lookup(CustomerService.class).asCustomerMetaData(id);
        propertyChangeSupport.firePropertyChange(PROP_CUSTOMER, old, purchaseCustomer);
    }

    /**
     * Get the value of search
     *
     * @return the value of search
     */
    public String getSearch() {
        return search;
    }

    /**
     * Set the value of search
     *
     * @param search new value of search
     */
    public void setSearch(String search) {
        String oldSearch = this.search;
        this.search = search;
        propertyChangeSupport.firePropertyChange(PROP_SEARCH, oldSearch, search);
    }

    /**
     * Get the value of selectedDossier
     *
     * @return the value of selectedDossier
     */
    public Dossier getSelectedDossier() {
        return selectedDossier;
    }

    /**
     * Set the value of selectedDossier
     *
     * @param selectedDossier new value of selectedDossier
     */
    public void setSelectedDossier(Dossier selectedDossier) {
        Dossier oldSelectedDossier = this.selectedDossier;
        this.selectedDossier = selectedDossier;
        propertyChangeSupport.firePropertyChange(PROP_SELECTED_DOSSIER, oldSelectedDossier, selectedDossier);
    }

    /**
     * Get the value of selectedDocument
     *
     * @return the value of selectedDocument
     */
    public Document getSelectedDocument() {
        return selectedDocument;
    }

    /**
     * Set the value of selectedDocument
     *
     * @param selectedDocument new value of selectedDocument
     */
    public void setSelectedDocument(Document selectedDocument) {
        Document oldSelectedDocument = this.selectedDocument;
        this.selectedDocument = selectedDocument;
        propertyChangeSupport.firePropertyChange(PROP_SELECTED_DOCUMENT, oldSelectedDocument, selectedDocument);
    }

    /**
     * Get the value of positions
     *
     * @return the value of positions
     */
    public Set<Position> getPositions() {
        return positions;
    }

    /**
     * Set the value of positions
     *
     * @param positions new value of positions
     */
    public void setPositions(Set<Position> positions) {
        Set<Position> oldPositions = this.positions;
        this.positions = positions;
        propertyChangeSupport.firePropertyChange(PROP_POSITIONS, oldPositions, positions);
    }

    /**
     * Get the value of documents
     *
     * @return the value of documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Set the value of documents
     *
     * @param documents new value of documents
     */
    public void setDocuments(List<Document> documents) {
        List<Document> oldDocuments = this.documents;
        this.documents = documents;
        propertyChangeSupport.firePropertyChange(PROP_DOCUMENTS, oldDocuments, documents);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
