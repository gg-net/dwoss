/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.customer.ui;

import java.util.*;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.saft.Dl;

import lombok.*;

/**
 * Task to obtain all Customers from the database with partial results.
 *
 * @author jens.papenhagen
 */
public class CustomerTask extends Task<ObservableList<Customer>> {

    @Getter
    @Setter
    private Set<SearchField> customerFields;

    @Getter
    @Setter
    private String searchsting;

    private final CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);

    private final ReadOnlyObjectWrapper<ObservableList<Customer>> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
                    FXCollections.observableArrayList(new ArrayList()));

    public final ObservableList<Customer> getPartialResults() {
        return partialResults.get();
    }

    public final ReadOnlyObjectProperty<ObservableList<Customer>> partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    public CustomerTask(String searchsting, Set<SearchField> sFields) {
        this.customerFields = sFields;
        this.searchsting = searchsting;
    }

    public int getEstimate() {
        return agent.countSearch(searchsting, customerFields);
    }

    @Override
    protected ObservableList<Customer> call() throws Exception {

        //set agent
        long count = 10L;
        int batch = 20;

        for (int start = 0; start <= count && !isCancelled(); start += batch) {
            List<Customer> partialResult = agent.search(searchsting, customerFields, start, batch);
            Platform.runLater(() -> {
                getPartialResults().addAll(partialResult);
            });
            updateProgress(start, count);
        }
        return partialResults.get();
    }

}
