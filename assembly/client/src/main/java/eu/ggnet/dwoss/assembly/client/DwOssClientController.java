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
package eu.ggnet.dwoss.assembly.client;

import eu.ggnet.dwoss.assembly.client.support.ActiveStockSelectorToolbarPane;
import eu.ggnet.dwoss.assembly.client.support.LafMenuManager;
import eu.ggnet.dwoss.assembly.client.support.LocalProgressSimulatorMenuItem;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;

import org.slf4j.Logger;

import eu.ggnet.dwoss.assembly.client.support.MenuBuilder;
import eu.ggnet.dwoss.assembly.client.support.RightsToolbarManager;
import eu.ggnet.dwoss.assembly.client.support.login.LoggedInTimeoutManager;
import eu.ggnet.dwoss.assembly.client.support.monitor.*;
import eu.ggnet.dwoss.core.widget.event.UserChange;
import eu.ggnet.dwoss.customer.ui.cap.*;
import eu.ggnet.dwoss.mail.ui.cap.SendResellerListToSubscribedCustomersMenuItem;
import eu.ggnet.dwoss.misc.ui.cap.MovmentMenuItemsProducer.MovementLists;
import eu.ggnet.dwoss.misc.ui.cap.SalesListingCreateMenuItemProducer.SalesListingCreateMenus;
import eu.ggnet.dwoss.misc.ui.cap.*;
import eu.ggnet.dwoss.misc.ui.mc.FileListPane;
import eu.ggnet.dwoss.misc.ui.toolbar.OpenDirectoryToolbarButton;
import eu.ggnet.dwoss.price.ui.cap.*;
import eu.ggnet.dwoss.price.ui.cap.build.PriceSubMenuBuilder.PriceSubMenu;
import eu.ggnet.dwoss.receipt.ui.cap.*;
import eu.ggnet.dwoss.receipt.ui.product.SpecListAction;
import eu.ggnet.dwoss.redtapext.ui.cap.*;
import eu.ggnet.dwoss.report.ui.cap.*;
import eu.ggnet.dwoss.rights.ui.cap.RightsManagmentAction;
import eu.ggnet.dwoss.search.ui.SearchCask;
import eu.ggnet.dwoss.search.ui.cap.OpenSearchAction;
import eu.ggnet.dwoss.stock.ui.cap.*;
import eu.ggnet.dwoss.uniqueunit.ui.cap.ProductListAction;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.auth.UserChangeListener;

/**
 * Main UI, consist of menubar, toolbar, statusline and main ui container.
 *
 * @author oliver.guenther
 */
public class DwOssClientController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ToolBar toolBar;

    @FXML
    private SplitPane mainSplitPane;

    @Inject
    protected Instance<Object> instance;

    @Inject
    private ServerAllProgressPoller msm;

    @Inject
    private MonitorManager monitorManager;

    @Inject
    private Event<UserChange> event;

    @Inject
    private Logger log;

    @FXML
    void initialize() {
        menuBar.getMenus().addAll(populateMenu());
        toolBar.getItems().addAll(populateToolbar());
        populateMain();
        menuBar.autosize();
    }

    private void populateMain() {
        // -- Init Ui
        UnitAvailabilityPane unitAvailability = instance.select(UnitAvailabilityPane.class).get();
        SearchCask search = instance.select(SearchCask.class).get();
        search.disableProgressBar();
        UiCore.addOnShutdown(() -> search.closed());

        FileListPane filelist = instance.select(FileListPane.class).get();
        MonitorPane monitorPane = monitorManager.createPane();

        SplitPane right = new SplitPane(filelist, monitorPane);
        right.setOrientation(Orientation.VERTICAL);
        right.setDividerPositions(0.7);

        mainSplitPane.getItems().addAll(unitAvailability, search, right);
        mainSplitPane.setDividerPositions(0.4, 0.8, 1);

        Guardian guard = Dl.local().lookup(Guardian.class);

        guard.addUserChangeListener(new UserChangeListener() {
            @Override
            public void loggedIn(String username) {
                log.info("loggedIn(username={}): fireing UserChange event");
                event.fire(new UserChange(username, guard.getRights()));
            }

            @Override
            public void loggedOut() {
                // Ignore
            }
        });
    }

    /**
     * Fills all the menus.
     *
     * @return list of menus
     */
    protected List<Menu> populateMenu() {
        MenuBuilder m = instance.select(MenuBuilder.class).get();

        // -- System
        Menu system_datenbank = new Menu("Datenbank");
        system_datenbank.getItems().addAll(m.items(
                ProductSpecExportAction.class,
                DatabaseValidationAction.class,
                RecreateSearchIndex.class));

        Menu system = new Menu("System");
        system.getItems().addAll(system_datenbank, LafMenuManager.createLafMenu());

        // -- Kunden und Aufträge
        Menu cao = new Menu("Kunden und Aufträge");
        cao.getItems().addAll(m.items(
                RedTapeMenuItem.class,
                DossiersByStatusAction.class,
                ShowUnitViewAction.class,
                OpenSearchAction.class,
                CustomerSearchAction.class,
                AddCustomerAction.class,
                ShowResellerMailCustomers.class
        ));

        // -- Listings
        Menu listings = new Menu("Listings");
        listings.getItems().add(m.item(AllSalesListingAction.class));
        listings.getItems().addAll(instance.select(SalesListingCreateMenus.class).get().items);
        listings.getItems().add(m.item(SendResellerListToSubscribedCustomersMenuItem.class));

        // -- Rechte
        Menu rights = new Menu("Rechte");
        rights.getItems().add(m.item(RightsManagmentAction.class));

        // -- Geschäftsführung
        Menu gl_allgemein = new Menu("Allgemeine Reporte");
        gl_allgemein.getItems().addAll(m.items(
                UnitQualityReportAction.class,
                ExportInputReportAction.class,
                ExportDossierToXlsAction.class,
                CreditMemoReportAction.class,
                OptimizedCreditMemoReportAction.class,
                DebitorsReportAction.class,
                DirectDebitReportAction.class,
                ReportRefurbishmentAction.class
        ));

        Menu gl_close = new Menu("Abschluss Reporte");
        gl_close.getItems().addAll(m.items(
                ResolveRepaymentAction.class,
                LastWeekCloseAction.class,
                OpenRawReportLinesAction.class,
                CreateNewReportAction.class,
                SelectExistingReportAction.class,
                CreateReturnsReportAction.class,
                ExportRevenueReportAction.class
        ));

        Menu gl_price = new Menu("Preise");
        gl_price.getItems().addAll(m.items(
                PriceExportAction.class,
                PriceImportAction.class,
                PriceBlockerAction.class,
                GenerateOnePriceAction.class,
                PriceExportImportAction.class,
                PriceByInputFileAction.class
        ));

        Menu gl_imexport = instance.select(PriceSubMenu.class).get().menu;

        Menu gl = new Menu("Geschäftsführung");
        gl.getItems().addAll(gl_imexport, gl_price, gl_allgemein, gl_close,
                m.item(OpenSalesChannelManagerAction.class),
                m.item(SageExportAction.class),
                m.item(ExportAllCustomers.class)
        );

        // -- Lager/Logistik
        MovementLists ml = instance.select(MovementLists.class).get();

        Menu logistik = new Menu("Lager/Logistik");
        logistik.getItems().addAll(m.items(
                OpenShipmentAction.class,
                EditUnitAction.class,
                ScrapUnitAction.class,
                DeleteUnitAction.class
        ));

        logistik.getItems().add(new SeparatorMenuItem());
        logistik.getItems().addAll(m.items(
                CreateSimpleAction.class,
                RemoveUnitFromTransactionAction.class,
                OpenStockTransactionManager.class,
                OpenCommissioningManager.class
        ));

        logistik.getItems().add(new SeparatorMenuItem());

        logistik.getItems().addAll(m.items(
                RollInPreparedTransactionsAction.class,
                AuditReportByRangeAction.class,
                AuditReportOnRollInAction.class
        ));

        logistik.getItems().addAll(ml.shippingOrPickup, ml.inventur);

        // -- Artikelstamm
        Menu artikelstamm_imageIds = new Menu("Bilder Ids");
        artikelstamm_imageIds.getItems().addAll(m.items(NextImageIdAction.class, ExportImageIdsForCustomerMenuItem.class, ExportImageIdsAction.class, ImportImageIdsAction.class));

        Menu artikelstamm = new Menu("Artikelstamm");
        artikelstamm.getItems().addAll(m.items(
                ProductListAction.class,
                UpdateProductAction.class,
                CpuManagementAction.class,
                GpuManagementAction.class,
                SpecListAction.class,
                AddCommentAction.class
        ));

        artikelstamm.getItems().addAll(m.item(SalesProductAction.class), artikelstamm_imageIds);

        // -- Hilfe
        Menu help = new Menu("Hilfe");
        help.getItems().addAll(
                m.item(AboutAction.class),
                m.item(ShowMandatorAction.class),
                m.item(LocalProgressSimulatorMenuItem.class)
        );

        return Arrays.asList(system, cao, listings, gl, artikelstamm, rights, logistik, help);
    }

    protected List<Node> populateToolbar() {
        return Arrays.asList(instance.select(RedTapeToolbarButton.class).get(),
                instance.select(RightsToolbarManager.class).get().createNode(),
                instance.select(ActiveStockSelectorToolbarPane.class).get(),
                instance.select(LoggedInTimeoutManager.class).get().createToolbarElementOnce(),
                instance.select(OpenDirectoryToolbarButton.class).get());
    }

    public static URL loadIcon() {
        return DwOssClientController.class.getResource("app-icon3.png"); // NOI18N
    }

}
