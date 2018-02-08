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
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.saft.Dl;

import lombok.*;

/**
 * Task to obtain all Customers from the database with partial results.
 *
 * @author jens.papenhagen
 */
public class CustomerTaskService extends Service<ObservableList<Customer>> {

    @Getter
    @Setter
    private Set<SearchField> customerFields;

    @Getter
    @Setter
    private String searchsting;

    private final CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);

    private final ObservableList<Customer> partialResults = FXCollections.observableArrayList(new ArrayList());

    public final ObservableList<Customer> getPartialResults() {
        return partialResults;
    }


    @Override
    protected Task<ObservableList<Customer>> createTask() {
        return new Task<ObservableList<Customer>>() {
            @Override
            protected ObservableList<Customer> call() throws Exception {
                partialResults.clear();

                long count = agent.countSearch(searchsting, customerFields);
                int batch = 5;

                for (int start = 0; start <= count && !isCancelled(); start += batch) {
                    List<Customer> result = agent.search(searchsting, customerFields, start, batch);
                    Platform.runLater(() -> {
                        partialResults.addAll(result);
                    });
                    updateProgress(start, count);
                }

                return partialResults;
            }
        };
    }

}
