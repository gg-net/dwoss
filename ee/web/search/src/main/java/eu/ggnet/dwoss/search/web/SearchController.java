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
package eu.ggnet.dwoss.search.web;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.GlobalKey;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.ee.SearcherOperation;

/**
 * This class provides methods for RevenueReport generation in DW-Web.
 * <p>
 * @author pascal.perau
 */
@Named
@ManagedBean
@ViewScoped
public class SearchController implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private SearchLazyModel lazyModel;

    @Inject
    private SearcherOperation searcher;

    private ShortSearchResult selectedShortSearchResult;

    public ShortSearchResult getSelectedShortSearchResult() {
        return selectedShortSearchResult;
    }

    public void setSelectedShortSearchResult(ShortSearchResult selectedShortSearchResult) {
        this.selectedShortSearchResult = selectedShortSearchResult;
    }

    @PostConstruct
    public void init() {
        lazyModel = new SearchLazyModel(searcher);
    }

    public SearchLazyModel getLazyModel() {
        return lazyModel;
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("SearchResult Selected", ((ShortSearchResult)event.getObject()).toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public String getDetails(GlobalKey key) {
        LOG.info("Key: " + key);
        try {
            LOG.info("Details: " + searcher.details(key));

        } catch (Exception e) {
            LOG.info("Details Exception " + e.getMessage());
        }
        return searcher.details(key);
    }
}
