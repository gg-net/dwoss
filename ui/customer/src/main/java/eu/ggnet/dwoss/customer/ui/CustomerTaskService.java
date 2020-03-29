/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.customer.ui;

import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 * Task to obtain all Customers from the database with partial results.
 *
 * @author jens.papenhagen
 */
public class CustomerTaskService extends Service<ObservableList<PicoCustomer>> {

    private Set<SearchField> customerFields;

    private String searchsting;

    private final int batch = 5;

    private final CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);

    private final ObservableList<PicoCustomer> partialResults = FXCollections.observableArrayList();

    public ObservableList<PicoCustomer> getPartialResults() {
        return partialResults;
    }

    public void setCustomerFields(Set<SearchField> customerFields) {
        this.customerFields = customerFields;
    }

    public void setSearchsting(String searchsting) {
        this.searchsting = searchsting;
    }

    @Override
    protected Task<ObservableList<PicoCustomer>> createTask() {
        long limit = agent.countSearch(searchsting, customerFields);

        return new Task<ObservableList<PicoCustomer>>() {
            @Override
            protected ObservableList<PicoCustomer> call() throws Exception {
                Platform.runLater(() -> partialResults.clear());
                for (int start = 0; start <= limit && !isCancelled(); start += batch) {
                    List<PicoCustomer> result = agent.search(searchsting, customerFields, start, batch);
                    Platform.runLater(()
                            -> partialResults.addAll(result));
                    updateProgress(start, limit);
                }
                if ( partialResults.isEmpty() ) {
                    Platform.runLater(()
                            -> partialResults.add(new PicoCustomer(0, "Kein Kunden gefunden")));
                }

                return partialResults;
            }
        };
    }

}
