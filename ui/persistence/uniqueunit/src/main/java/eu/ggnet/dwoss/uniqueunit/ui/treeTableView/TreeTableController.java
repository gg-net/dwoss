/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.treeTableView;

import java.net.URL;
import java.util.*;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.ui.treeTableView.data.*;
import eu.ggnet.dwoss.uniqueunit.ui.treeTableView.data.gen.CategoryProductGenerator;
import eu.ggnet.dwoss.uniqueunit.ui.treeTableView.data.gen.ProductGenerator;
import eu.ggnet.saft.api.ui.FxController;

/**
 * FXML Controller class
 *
 * @author lucas.huelsen
 */
public class TreeTableController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(TreeTableController.class);

    @FXML
    private TreeTableView<DataWrapper> view;

    @FXML
    private TreeTableColumn<DataWrapper, String> overview;

    @FXML
    private TreeTableColumn<DataWrapper, Integer> amount;

    private TreeItem<DataWrapper> loading = new TreeItem<>(new DataWrapper() {
        @Override
        public String getName() {
            return "loading...";
        }
    });

    private TreeItem<DataWrapper> withoutCp = new TreeItem<>(new DataWrapper() {
        @Override
        public String getName() {
            return "Ohne KategorieProdukt";
        }
    });

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        TreeItem<DataWrapper> root = new TreeItem<>(new DataWrapper() {
            @Override
            public String getName() {
                return "Root";
            }
        });

        view.setRoot(root);
        root.setExpanded(true);
        view.setShowRoot(false);
        root.getChildren().addAll(getTradeNames());

        overview.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<DataWrapper, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<DataWrapper, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getValue().getName());
            }
        });

        amount.setCellValueFactory(new Callback<CellDataFeatures<DataWrapper, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(CellDataFeatures<DataWrapper, Integer> param) {
                DataWrapper dw = param.getValue().getValue();

                SimpleObjectProperty<Integer> result;

                if ( dw instanceof ProductGroupWrapper ) {
                    result = new SimpleObjectProperty<>(amountOfCategoryProducts(((ProductGroupWrapper)dw).getTradeName(), ((ProductGroupWrapper)dw).getProductGroup()));
                    return result;
                }

                if ( dw instanceof CategoryProductWrapper ) {
                    result = new SimpleObjectProperty<>(amountOfProducts(((CategoryProductWrapper)dw).getCategoryProductId()));
                    return result;
                }

                if ( dw instanceof ProductWrapper ) {
                    result = new SimpleObjectProperty<>(amountOfUnitCollections(((ProductWrapper)dw).getProductId()));
                    return result;
                }

                if ( dw instanceof UnitCollectionWrapper ) {
                    result = new SimpleObjectProperty<>(amountOfUnits(((UnitCollectionWrapper)dw).getUnitCollectionId()));
                    return result;
                }

                result = new SimpleObjectProperty<>(0);
                return result;

            }
        });

    }

    private ObservableList<TreeItem<DataWrapper>> getTradeNames() {
        ObservableList<TreeItem<DataWrapper>> result = FXCollections.observableArrayList();
        for (TradeName tradeName : TradeName.values()) {
            TreeItem<DataWrapper> item = new TreeItem<>(new TradeNameWrapper(tradeName));
            item.getChildren().addAll(getProductGroups(tradeName));
            result.add(item);
        }
        return result;
    }

    private ObservableList<TreeItem<DataWrapper>> getProductGroups(TradeName tradeName) {
        ObservableList<TreeItem<DataWrapper>> result = FXCollections.observableArrayList();
        for (ProductGroup group : ProductGroup.values()) {
            TreeItem<DataWrapper> item = new TreeItem<>();
            ProductGroupWrapper productGroupWrapper = new ProductGroupWrapper(group, tradeName, item);
            item.setValue(productGroupWrapper);

            item.getChildren().add(loading);

            item.expandedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if ( productGroupWrapper.isLoading() ) {
                        return;
                    }
                    productGroupWrapper.setLoading(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            item.getChildren().addAll(getCategoryProducts(productGroupWrapper.getTradeName(), productGroupWrapper.getProductGroup()));
                            item.getChildren().remove(loading);
                        }
                    }).start();
                    L.info("Products loaded");
                }
            });
            result.add(item);
        }
        return result;
    }

    private ObservableList<TreeItem<DataWrapper>> getCategoryProducts(TradeName tradeName, ProductGroup group) {
        ObservableList<TreeItem<DataWrapper>> result = FXCollections.observableArrayList();
        for (CategoryProduct cp : loadCp(group, tradeName)) {
            TreeItem<DataWrapper> item = new TreeItem<>();
            CategoryProductWrapper categoryProductWrapper = new CategoryProductWrapper(item, cp);
            item.setValue(categoryProductWrapper);

            item.getChildren().add(loading);

            item.expandedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if ( categoryProductWrapper.isLoading() ) {
                        return;
                    }
                    categoryProductWrapper.setLoading(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            item.getChildren().addAll(getProducts(cp));
                            item.getChildren().remove(loading);

                        }
                    }).start();
                }
            });

            result.add(item);
        }
        return result;
    }

    private ObservableList<TreeItem<DataWrapper>> getProducts(CategoryProduct cp) {
        ObservableList<TreeItem<DataWrapper>> result = FXCollections.observableArrayList();
        for (Product product : loadProducts(cp)) {
            TreeItem<DataWrapper> item = new TreeItem<>();
            ProductWrapper productWrapper = new ProductWrapper(item, product);
            item.setValue(productWrapper);

            item.getChildren().add(loading);

            item.expandedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if ( productWrapper.isLoading() ) {
                        return;
                    }
                    productWrapper.setLoading(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            item.getChildren().addAll(getUnitCollections(product));
                            item.getChildren().remove(loading);
                        }
                    }).start();
                }
            });

            result.add(item);
        }

        return result;
    }

    private ObservableList<TreeItem<DataWrapper>> getUnitCollections(Product product) {
        ObservableList<TreeItem<DataWrapper>> result = FXCollections.observableArrayList();
        for (UnitCollection uc : loadUnitCollections(product)) {
            TreeItem<DataWrapper> item = new TreeItem<>();
            UnitCollectionWrapper unitCollectionWrapper = new UnitCollectionWrapper(item, uc);
            item.setValue(unitCollectionWrapper);

            result.add(item);
        }

        return result;
    }

    // ----------- DB ----------- //
    public List<CategoryProduct> loadCp(ProductGroup group, TradeName tradeName) {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException ex) {
        }
        return new ArrayList<>(new CategoryProductGenerator().generateCategoryProduct(4));
    }

    public List<Product> loadProducts(CategoryProduct cp) {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException ex) {
        }
        return new ArrayList<>(new ProductGenerator().generateProduct(8));
    }

    public List<UnitCollection> loadUnitCollections(Product product) {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException ex) {
        }
        return new ArrayList<>(product.getUnitCollections());
    }

    public List<UniqueUnit> loadUniqueUnits(long unitCollectionId) {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException ex) {
        }
        return new ArrayList<>(new ProductGenerator().generateProduct(1).get(0).getUniqueUnits());
    }

    public List<UniqueUnit> loadUnsetUniqueUnits(long productId) {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException ex) {
        }
        return new ArrayList<>(new ProductGenerator().generateProduct(1).get(0).getUniqueUnits());
    }

    public int amountOfCategoryProducts(TradeName tradeName, ProductGroup group) {
        return 5;
    }

    public int amountOfProducts(long categoryProductId) {
        return 10;
    }

    public int amountOfUnitCollections(long productId) {
        return 6;
    }

    public int amountOfUnits(long unitCollectionId) {
        return 20;
    }

}
