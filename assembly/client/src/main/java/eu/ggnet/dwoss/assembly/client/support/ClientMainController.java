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

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Location;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Type;
import eu.ggnet.dwoss.misc.ui.cap.*;

import static eu.ggnet.dwoss.core.common.values.SalesChannel.CUSTOMER;
import static eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Location.FTP;
import static eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Type.PDF;

/**
 * Main UI, consist of menubar, toolbar, statusline and main ui container.
 *
 * @author oliver.guenther
 */
public class ClientMainController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private FlowPane toolBar;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @Inject
    private Instance<Object> instance;

    @FXML
    void initialize() {
        populateMenu();
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

        // -- Geschäftsführung
        Menu gl_allgemein = new Menu("Allgemeine Reporte");
        gl_allgemein.getItems().addAll(m.items(
                UnitQualityReportAction.class,
                ExportInputReportAction.class));

        Menu gl_close = new Menu("Abschluss Reporte");
        gl_close.getItems().addAll(m.items(ResolveRepaymentAction.class));

        Menu gl = new Menu("Geschäftsführung");
        gl.getItems().addAll(gl_allgemein, gl_close, m.item(OpenSalesChannelManagerAction.class));

        // -- Lager/Logistik
        Menu logistik_inventur = new Menu("Inventur");
        logistik_inventur.getItems().add(m.item(StockTakingAction.class));

        Menu logistik = new Menu("Lager/Logistik");
        logistik.getItems().add(logistik_inventur);

        // -- Artikelstamm
        Menu artikelstamm_imageIds = new Menu("Bilder Ids");
        artikelstamm_imageIds.getItems().addAll(m.items(ExportImageIdsAction.class, ImportImageIdsAction.class));
        // Todo: Solotion for: actions.add(new MetaAction("Artikelstamm", "Bilder Ids", new ExportImageIdsAction(SalesChannel.CUSTOMER)));

        Menu artikelstamm = new Menu("Artikelstamm");
        artikelstamm.getItems().addAll(artikelstamm_imageIds);

        // -- Hilfe
        Menu help = new Menu("Hilfe");
        help.getItems().addAll(
                m.item(AnAction.class, new ListingSupport() {
                    @Override
                    public Type type() {
                        return PDF;
                    }

                    @Override
                    public Location location() {
                        return FTP;
                    }

                    @Override
                    public SalesChannel channel() {
                        return CUSTOMER;
                    }

                    @Override
                    public String name() {
                        return "Wichtige Liste";
                    }

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ListingSupport.class;
                    }

                }),
                m.item(AboutAction.class),
                m.item(ShowMandatorAction.class)
        );

        menuBar.getMenus().addAll(system, gl, logistik, artikelstamm, help);
        menuBar.autosize();
    }

}
