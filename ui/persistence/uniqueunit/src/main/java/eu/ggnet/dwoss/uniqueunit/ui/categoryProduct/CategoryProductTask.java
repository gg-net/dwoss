/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.CategoryProduct;
import eu.ggnet.saft.Dl;

/**
 * Task to obtain all Products from the database with partial results.
 *
 * @author lucas.huelsen
 */
public class CategoryProductTask extends Task<ObservableList<CategoryProduct>> {

    private final ObservableList<CategoryProduct> partialResults = FXCollections.observableArrayList();

    public final ObservableList<CategoryProduct> getPartialResults() {
        return partialResults;
    }

    @Override
    protected ObservableList<CategoryProduct> call() throws Exception {

        UniqueUnitAgent agent = Dl.remote().lookup(UniqueUnitAgent.class);

        long count = agent.count(CategoryProduct.class);
        int batch = 20;

        for (int start = 0; start <= count && !isCancelled(); start += batch) {
            List<CategoryProduct> partialResult = agent.findAll(CategoryProduct.class, start, batch);
            Platform.runLater(() -> {
                getPartialResults().addAll(partialResult);
            });
            updateProgress(start, count);

        }
        return partialResults;
    }

}
