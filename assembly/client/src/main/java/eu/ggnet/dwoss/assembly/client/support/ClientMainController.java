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
package eu.ggnet.dwoss.assembly.client.support;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

import eu.ggnet.dwoss.misc.ui.cap.*;
import eu.ggnet.dwoss.misc.ui.cap.MovmentMenuItemsProducer.MovementLists;
import eu.ggnet.dwoss.misc.ui.cap.SalesListingCreateMenuItemProducer.SalesListingCreateMenus;
import eu.ggnet.dwoss.misc.ui.mc.FileListPane;
import eu.ggnet.dwoss.redtapext.ui.cap.*;
import eu.ggnet.dwoss.search.ui.SearchCask;

/**
 * Main UI, consist of menubar, toolbar, statusline and main ui container.
 *
 * @author oliver.guenther
 */
public class ClientMainController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ToolBar toolBar;

    @FXML
    private SplitPane mainSplitPane;

    @Inject
    private Instance<Object> instance;

    @FXML
    void initialize() {
        populateMenu();
        populateToolbar();
        
        UnitAvailabilityPane unitAvailability = instance.select(UnitAvailabilityPane.class).get();
        SearchCask search = instance.select(SearchCask.class).get();
        FileListPane filelist = instance.select(FileListPane.class).get();
        MonitorPane monitorPane = instance.select(MonitorPane.class).get();
        
        SplitPane right = new SplitPane(filelist,monitorPane);
        right.setOrientation(Orientation.VERTICAL);
        right.setDividerPositions(0.7);
                
        mainSplitPane.getItems().addAll(unitAvailability,search,right);
        mainSplitPane.setDividerPositions(0.4,0.8,1);
        
        //Scheduled Executor.  new MonitorServerManager(monitorPane));
    }

    public void add(Menu menu) {
        menuBar.getMenus().add(menu);
        menuBar.autosize();
    }

    /**
     * Fills all the menus
     */
    private void populateMenu() {
        MenuBuilder m = instance.select(MenuBuilder.class).get();

        // -- System
        Menu system_datenbank = new Menu("Datenbank");
        system_datenbank.getItems().addAll(m.items(
                ProductSpecExportAction.class,
                DatabaseValidationAction.class));

        Menu system = new Menu("System");
        system.getItems().add(system_datenbank);

        // -- Kunden und Aufträge
        Menu cao = new Menu("Kunden und Aufträge");
        cao.getItems().addAll(m.items(RedTapeMenuItem.class, DossiersByStatusAction.class, ShowUnitViewAction.class));

        // -- Listings
        Menu listings = new Menu("Listings");
        SalesListingCreateMenus menus = instance.select(SalesListingCreateMenus.class).get();
        listings.getItems().addAll(menus.items);

        // -- Geschäftsführung
        Menu gl_allgemein = new Menu("Allgemeine Reporte");
        gl_allgemein.getItems().addAll(m.items(
                UnitQualityReportAction.class,
                ExportInputReportAction.class,
                ExportDossierToXlsAction.class,
                CreditMemoReportAction.class,
                OptimizedCreditMemoReportAction.class,
                DebitorsReportAction.class,
                DirectDebitReportAction.class
        ));

        Menu gl_close = new Menu("Abschluss Reporte");
        gl_close.getItems().addAll(m.items(
                ResolveRepaymentAction.class,
                LastWeekCloseAction.class
        ));

        Menu gl = new Menu("Geschäftsführung");
        gl.getItems().addAll(gl_allgemein, gl_close, m.item(OpenSalesChannelManagerAction.class),m.item(SageExportAction.class));

        // -- Lager/Logistik
        MovementLists ml = instance.select(MovementLists.class).get();

        Menu logistik = new Menu("Lager/Logistik");
        logistik.getItems().addAll(ml.shippingOrPickup, ml.inventur);

        // -- Artikelstamm
        Menu artikelstamm_imageIds = new Menu("Bilder Ids");
        artikelstamm_imageIds.getItems().addAll(m.items(ExportImageIdsForCustomerMenuItem.class, ExportImageIdsAction.class, ImportImageIdsAction.class));

        Menu artikelstamm = new Menu("Artikelstamm");
        artikelstamm.getItems().addAll(m.item(SalesProductAction.class), artikelstamm_imageIds);

        // -- Hilfe
        Menu help = new Menu("Hilfe");
        help.getItems().addAll(
                m.item(AboutAction.class),
                m.item(ShowMandatorAction.class)
        );

        menuBar.getMenus().addAll(system, cao, listings, gl, logistik, artikelstamm, help);
        menuBar.autosize();
    }
    
    private void populateToolbar() {
        toolBar.getItems().add(instance.select(RedTapeToolbarButton.class).get());
    }
    

}
