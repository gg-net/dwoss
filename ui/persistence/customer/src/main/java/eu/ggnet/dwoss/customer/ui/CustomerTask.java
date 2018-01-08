/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.customer.ui;

import eu.ggnet.dwoss.customer.CustomerAgent;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.saft.Client;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Task to obtain all Customers from the database with partial results.
 *
 * @author jens.papenhagen
 */
public class CustomerTask extends Task<ObservableList<Customer>> {

    private ReadOnlyObjectWrapper<ObservableList<Customer>> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
                    FXCollections.observableArrayList(new ArrayList()));

    public final ObservableList<Customer> getPartialResults() {
        return partialResults.get();
    }

    public final ReadOnlyObjectProperty<ObservableList<Customer>> partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    @Override
    protected ObservableList<Customer> call() throws Exception {

        CustomerAgent agent = Client.lookup(CustomerAgent.class);

        long count = agent.count(Customer.class);
        int batch = 20;

        for (int start = 0; start <= count && !isCancelled(); start += batch) {
            List<Customer> partialResult = agent.findAll(Customer.class, start, batch);
            Platform.runLater(() -> {
                getPartialResults().addAll(partialResult);
            });
            updateProgress(start, count);
        }
        return partialResults.get();
    }

}
