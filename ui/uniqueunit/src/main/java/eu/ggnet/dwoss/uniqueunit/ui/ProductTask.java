/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui;

import java.util.List;

import jakarta.inject.Inject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

import jakarta.enterprise.context.Dependent;

/**
 * Task to obtain all Products from the database with partial results.
 *
 * @author lucas.huelsen
 */
@Dependent
public class ProductTask extends Task<ObservableList<Product>> {

    @Inject
    private RemoteDl remote;

    private final ObservableList<Product> partialResults = FXCollections.observableArrayList();

    public final ObservableList<Product> getPartialResults() {
        return partialResults;
    }

    @Override
    protected ObservableList<Product> call() throws Exception {

        UniqueUnitAgent agent = remote.lookup(UniqueUnitAgent.class);

        long count = agent.count(Product.class);
        int batch = 20;

        for (int start = 0; start <= count && !isCancelled(); start += batch) {
            List<Product> partialResult = agent.findAll(Product.class, start, batch);
            Platform.runLater(() -> {
                getPartialResults().addAll(partialResult);
            });
            updateProgress(start, count);
        }
        return partialResults;
    }

}
