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
    
    private final int batch = 5;
    
    private final CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);

    private ObservableList<Customer> partialResults = FXCollections.observableArrayList();

    public ObservableList<Customer> getPartialResults() {
        return partialResults;
    }


    @Override
    protected Task<ObservableList<Customer>> createTask() {
        long limit = agent.countSearch(searchsting, customerFields);
        
        return new Task<ObservableList<Customer>>() {
            @Override
            protected ObservableList<Customer> call() throws Exception {
                partialResults.clear();
                for (int start = 0; start <= limit && !isCancelled(); start+= batch) {
                    List<Customer> result = agent.search(searchsting, customerFields, start, batch );
                    Platform.runLater(() -> {
                        partialResults.addAll(result);
                    });
                    updateProgress(start, batch);
                }

                return partialResults;
            }
        };
    }

}
