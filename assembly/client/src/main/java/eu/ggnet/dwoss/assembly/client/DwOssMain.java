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

import java.util.Arrays;

import javafx.application.Application;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.client.support.ContainerConfiguration;
import eu.ggnet.dwoss.assembly.remote.cdi.MainCdi;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;
import eu.ggnet.dwoss.customer.ui.CustomerTaskService;
import eu.ggnet.dwoss.mail.ui.cap.SendResellerListToSubscribedCustomersMenuItem;
import eu.ggnet.dwoss.misc.ui.AboutController;
import eu.ggnet.dwoss.price.ui.PriceBlockerViewCask;
import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.redtapext.ui.ReactivePicoUnitDetailViewCask;
import eu.ggnet.dwoss.report.ui.RawReportView;
import eu.ggnet.dwoss.rights.ui.UiPersona;
import eu.ggnet.dwoss.search.ui.SearchCask;
import eu.ggnet.dwoss.stock.ui.StockUpiImpl;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;

/**
 * Main Startup Class for the Client, starting the Application.
 * Setting the container configuration, cause auto discovery is disabled in dw and then starting the Application.
 *
 * @see DwOssApplication.
 * @author oliver.guenther
 */
public class DwOssMain {

    public static void main(String[] args) {
        LoggerFactory.getLogger(DwOssMain.class).info("main({}) starting", Arrays.asList(args));

        ContainerConfiguration cc = ContainerConfiguration.instance();
        cc.addPackages(true, MainCdi.class);
        cc.addPackages(true, DwOssMain.class);
        cc.addPackages(true, CustomerTaskService.class); // customer.ui
        cc.addPackages(true, SendResellerListToSubscribedCustomersMenuItem.class); // mail.ui
        cc.addPackages(true, PriceBlockerViewCask.class); // price.ui
        cc.addPackages(true, UiUnitSupport.class); // receipt.ui
        cc.addPackages(true, RawReportView.class); // report.ui
        cc.addPackages(true, UiPersona.class); // rights.ui
        cc.addPackages(true, StockUpiImpl.class); // stock.ui
        cc.addPackages(true, ProductTask.class); // uniqueunit.ui
        cc.addPackages(true, ReactivePicoUnitDetailViewCask.class); // redtapext.ui
        cc.addPackages(true, AboutController.class); // misc.ui
        cc.addPackages(true, SearchCask.class); // search.ui
        cc.addPackages(LoggerProducer.class); // core.system. autolog
        cc.addPackages(GlobalConfig.class); // Global Config produces.

        Application.launch(DwOssApplication.class, args);
    }
}
