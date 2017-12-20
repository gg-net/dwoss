/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
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
 * Task to obtain all Products from the database with partial results.
 *
 * @author lucas.huelsen
 */
public class ProductTask extends Task<ObservableList<Product>> {

    private ReadOnlyObjectWrapper<ObservableList<Product>> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
                    FXCollections.observableArrayList(new ArrayList()));

    public final ObservableList<Product> getPartialResults() {
        return partialResults.get();
    }

    public final ReadOnlyObjectProperty<ObservableList<Product>> partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    @Override
    protected ObservableList<Product> call() throws Exception {

        UniqueUnitAgent agent = Client.lookup(UniqueUnitAgent.class);

        long count = agent.count(Product.class);
        int batch = 20;

        for (int start = 0; start <= count && !isCancelled(); start += batch) {
            List<Product> partialResult = agent.findAll(Product.class, start, batch);
            Platform.runLater(() -> {
                getPartialResults().addAll(partialResult);
            });
            updateProgress(start, count);
        }
        return partialResults.get();
    }

}
