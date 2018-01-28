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
package eu.ggnet.dwoss.customer.ui.old;

import java.beans.*;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomerAgent;

import static eu.ggnet.saft.Client.lookup;

/**
 * Controller for the create of a Customer.
 *
 * @author oliver.guenther
 */
public class CustomerCreateWithSearchController {

    private final static Logger L = LoggerFactory.getLogger(CustomerCreateWithSearchController.class);

    private OldCustomer customer;

    private CustomerCreateWithSearchView view;

    private Timer timer = new Timer();

    private ExecutorService es = Executors.newSingleThreadExecutor();

    private PropertyChangeListener customerListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case OldCustomer.PROP_VORNAME:
                case OldCustomer.PROP_NACHNAME:
                case OldCustomer.PROP_FIRMA:
                case OldCustomer.PROP_EMAIL:
                    timer.cancel();
                    // special case, we don't wont intelligent search if everything becomes empty
                    if ( isEmpty(customer.getFirma()) && isEmpty(customer.getVorname()) && isEmpty(customer.getNachname()) && isEmpty(customer.getEmail()) )
                        break;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // This ensures correct output of search results.
                            es.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        view.searchResultList.setListData(lookup(CustomerService.class).asUiCustomers(customer.getFirma(), customer.getVorname(), customer.getNachname(), customer.getEmail(), true).toArray());
                                    } catch (Exception e) {
                                        L.error("Exception in Backgroundsearch.", e);
                                    }
                                }
                            });
                        }
                    }, 1000);
                    break;
                default:
            }
        }
    };

    public OldCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(OldCustomer customer) {
        if ( this.customer != null ) this.customer.removePropertyChangeListener(customerListener);
        this.customer = customer;
        if ( customer != null ) customer.addPropertyChangeListener(customerListener);
    }

    public CustomerCreateWithSearchView getView() {
        return view;
    }

    public void setView(CustomerCreateWithSearchView view) {
        this.view = view;
    }

    /**
     * Handles a new selection in the SearchResult of the View.
     *
     * @param customerId the id of the customer that is selected
     */
    public void selectCustomer(long customerId) {
        OldCustomer existingCustomer = lookup(OldCustomerAgent.class).findById(customerId);
        view.setCustomer(existingCustomer);
        setCustomer(existingCustomer);
    }

    /**
     * Handles the exectution of a search.
     *
     * @param search a string, that will be use to compare multiple fields of a customer.
     */
    public void findCustomers(String search) {
        view.searchResultList.setListData(lookup(CustomerService.class).asUiCustomers(search).toArray());
    }

    private boolean isEmpty(String in) {
        return (in == null || in.trim().equals(""));
    }
}
