/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.EventQueue;
import java.io.File;
import java.util.*;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import javafx.scene.control.*;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import org.slf4j.Logger;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.widget.TikaUtil;
import eu.ggnet.dwoss.misc.ee.StockTaking;
import eu.ggnet.dwoss.misc.ee.movement.MovementListingProducer;
import eu.ggnet.dwoss.misc.ee.movement.MovementListingProducer.ListType;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 * Produces Menus Invetur and Versund & Abholung.
 *
 * @author oliver.guenther
 */
public class MovmentMenuItemsProducer {

    public static class MovementXlsMenuItem extends MenuItem {

        public void setStockAndType(MovementListingProducer.ListType listType, PicoStock stock) {
            Objects.requireNonNull(stock, "stock must not be null");
            Objects.requireNonNull(listType, "listType must not be null");
            setText(listType.description + " - " + stock.shortDescription + " - XLS");
            setOnAction((e) -> {
                Ui.exec(() -> {
                    Ui.osOpen(Ui.progress().call(() -> Dl.remote().lookup(MovementListingProducer.class).generateXls(listType, stock).toTemporaryFile()));
                });
            });
        }
    }

    public static class MovementPdfMenuItem extends MenuItem {

        public void setStockAndType(MovementListingProducer.ListType listType, PicoStock stock) {
            Objects.requireNonNull(stock, "stock must not be null");
            Objects.requireNonNull(listType, "listType must not be null");
            setText(listType.description + " - " + stock.shortDescription + " - PDF");
            setOnAction((e) -> {
                Ui.exec(() -> {
                    // TODO: Switch to JavaFX Implementation of RedTape.
                    JasperPrint jasper = Ui.progress().call(() -> Dl.remote().lookup(MovementListingProducer.class).generateList(listType, stock));
                    JasperViewer viewer = new JasperViewer(jasper, false);
                    EventQueue.invokeLater(() -> viewer.setVisible(true));
                });
            });
        }
    }

    public static class StockTakingMenuItem extends CustomMenuItem {

        public StockTakingMenuItem nullableStock(PicoStock stock) {

            Label l = new Label("Inventur" + (stock == null ? "" : " für " + stock.shortDescription) + " vervollständigen");
            Tooltip t = new Tooltip("Vervollständigt eine Inventur mit den Informationen aus der Datenbank\n"
                    + "Benötigt eine XLS Datei die in der ersten Tabelle in der ersten Spalte die Sonderposten Nummern hat\n"
                    + "Die oberste Zeile wird als Überschrift ignoriert.");
            Tooltip.install(l, t);
            setContent(l);
            setOnAction((e) -> {
                Ui.exec(() -> {
                    Optional<File> inFile = Ui.fileChooser().open().opt();
                    if ( !inFile.isPresent() ) return;
                    Ui.build().dialog().eval(
                            () -> new Alert(CONFIRMATION, (stock == null ? "" : " für " + stock.shortDescription) + " aus der Datei:" + inFile.get().getPath() + " vervollständigen ?"))
                            .opt()
                            .filter(b -> b == OK)
                            .map(b -> TikaUtil.isExcel(inFile.get()))
                            .filter(Ui.failure()::handle)
                            .map(Reply::getPayload)
                            .map(f -> Ui.progress().call(() -> Dl.remote().lookup(StockTaking.class).fullfillDetails(new FileJacket("in", ".xls", f), (stock == null ? null : stock.id))))
                            .ifPresent(f -> Ui.osOpen(f.toTemporaryFile()));

                });
            });
            return this;
        }
    }

    public static class MovementLists {

        public final Menu shippingOrPickup;

        public final Menu inventur;

        public MovementLists(Menu shippingOrPickup, Menu inventur) {
            this.shippingOrPickup = Objects.requireNonNull(shippingOrPickup);
            this.inventur = Objects.requireNonNull(inventur);
        }

    }

    @Inject
    private Instance<Object> instance;

    @Inject
    private Logger log;

    @Produces
    public MovementLists createLists() {
        List<PicoStock> allStocks = Dl.remote().lookup(StockApi.class).findAllStocks();
        Menu shippingOrPickup = new Menu("Versand & Abholung");
        Menu inventur = new Menu("Inventur");

        for (PicoStock ps : allStocks) {
            for (ListType listType : MovementListingProducer.ListType.values()) {
                MovementPdfMenuItem item = instance.select(MovementPdfMenuItem.class).get();
                item.setStockAndType(listType, ps);
                shippingOrPickup.getItems().add(item);
            }
            for (ListType listType : MovementListingProducer.ListType.values()) {
                MovementXlsMenuItem item = instance.select(MovementXlsMenuItem.class).get();
                item.setStockAndType(listType, ps);
                shippingOrPickup.getItems().add(item);
            }
            StockTakingMenuItem item = instance.select(StockTakingMenuItem.class).get().nullableStock(ps);
            inventur.getItems().add(item);
        }

        StockTakingMenuItem item = instance.select(StockTakingMenuItem.class).get().nullableStock(null);
        inventur.getItems().add(item);

        log.debug("createLists() created: shippingOrPickup={}, inventur={}", shippingOrPickup.getItems(), inventur.getItems());
        return new MovementLists(shippingOrPickup, inventur);
    }
}
