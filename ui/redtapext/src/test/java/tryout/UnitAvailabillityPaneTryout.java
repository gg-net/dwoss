/*
 * Copyright (C) 2017 GG-Net GmbH
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
package tryout;

import java.util.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.SanityResult;
import eu.ggnet.dwoss.redtapext.ui.cap.UnitAvailabilityPane;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

/**
 *
 * @author oliver.guenther
 */
public class UnitAvailabillityPaneTryout {

    public static class UnitAvailabilityTryoutApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setScene(new Scene(new UnitAvailabilityPane()));
            primaryStage.show();
        }

    }

    public static void main(String[] args) {
        final Map<String, SimpleUniqueUnit> suuMap = new HashMap<>();
        suuMap.put("1", new SimpleUniqueUnit.Builder().id(1).refurbishedId("1").shortDescription("Gerät mit Id 1").build());
        suuMap.put("2", new SimpleUniqueUnit.Builder().id(2).refurbishedId("10").lastRefurbishId("2").shortDescription("Aspire(2) Predator").build());
        suuMap.put("3", new SimpleUniqueUnit.Builder().id(3).refurbishedId("3").shortDescription("Lenovo (3) Legion").build());
        suuMap.put("4", new SimpleUniqueUnit.Builder().id(4).refurbishedId("4").shortDescription("Samsung (4) Gross und Klapbar").build());
        suuMap.put("5", new SimpleUniqueUnit.Builder().id(5).refurbishedId("5").shortDescription("AEG Waschmaschine (5)").build());
        suuMap.put("6", new SimpleUniqueUnit.Builder().id(6).refurbishedId("6").shortDescription("Handschrubber (6)").build());
        suuMap.put("7", new SimpleUniqueUnit.Builder().id(7).refurbishedId("7").shortDescription("Staubsauger (7)").build());

        final PicoStock stock1 = new PicoStock(1, "Hamburg");
        final PicoStock stock2 = new PicoStock(2, "Berlin");

        final Map<Long, SimpleStockUnit> stuMap = new HashMap<>();
        stuMap.put(1l, new SimpleStockUnit.Builder().id(3).uniqueUnitId(1).onLogicTransaction(false).shortDescription("StockUnit(1)").stock(stock1).build());
        stuMap.put(2l, new SimpleStockUnit.Builder().id(7).uniqueUnitId(2).onLogicTransaction(false).shortDescription("StockUnit(2)").stock(stock1).build());
        stuMap.put(3l, new SimpleStockUnit.Builder().id(5).uniqueUnitId(3).onLogicTransaction(false).shortDescription("StockUnit(3)").stock(stock2).build());
        stuMap.put(4l, new SimpleStockUnit.Builder().id(9).uniqueUnitId(4).onLogicTransaction(false).shortDescription("StockUnit(4)").stock(stock2).build());
        stuMap.put(5l, new SimpleStockUnit.Builder().id(5).uniqueUnitId(5).onLogicTransaction(true).shortDescription("StockUnit(5)")
                .stockTransaction(new SimpleStockTransaction.Builder().id(1).source(stock1).destination(stock2).shortDescription("Umfuhr von Hamburg nach Berlin").build())
                .build());

        final Map<Long, SanityResult> srMap = new HashMap<>();
        srMap.put(2l, new SanityResult.Builder().blocked(true).details("RedTape hat ein offenes Dokument").build());
        srMap.put(3l, new SanityResult.Builder().blocked(false).details("").build());
        srMap.put(5l, new SanityResult.Builder().blocked(true).details("RedTape hat ein offenes Dokument").build());

        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });

        Dl.remote().add(UniqueUnitApi.class, new UniqueUnitApi() {
            @Override
            public SimpleUniqueUnit findByRefurbishedId(String refurbishId) {
                return suuMap.get(refurbishId);
            }

            @Override
            public String findAsHtml(long id, String username) {
                return "<html>Unitid " + id + "</html>";
            }

        });

        Dl.remote().add(StockApi.class, new StockApi() {
            @Override
            public SimpleStockUnit find(long id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
                return stuMap.get(uniqueUnitId);
            }
        });

        Dl.remote().add(RedTapeApi.class, new RedTapeApi() {
            @Override
            public SanityResult sanityCheck(long uniqueUnitId) {
                return srMap.get(uniqueUnitId);
            }
        });

        Dl.local().add(ActiveStock.class, new ActiveStock() {

            private PicoStock activeStock = stock2;

            @Override
            public PicoStock getActiveStock() {
                return activeStock;
            }

            @Override
            public void setActiveStock(PicoStock activeStock) {
                this.activeStock = activeStock;
            }

        });

        Dl.local().add(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String string, char[] chars) throws AuthenticationException {
                setRights(new Operator(string, 0, new ArrayList<>()));
            }
        });

        Application.launch(UnitAvailabilityTryoutApplication.class);
    }
}
