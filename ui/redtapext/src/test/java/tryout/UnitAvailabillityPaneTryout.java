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
import eu.ggnet.dwoss.redtape.api.UnitAvailability;
import eu.ggnet.dwoss.redtapext.ui.cap.UnitAvailabilityPane;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

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

        final PicoStock stock1 = new PicoStock(1, "Hamburg");
        final PicoStock stock2 = new PicoStock(2, "Berlin");

        final Map<String, UnitAvailability> r = new HashMap<>();

        r.put("1", new UnitAvailability.Builder().refurbishId("1").available(false).exists(true).uniqueUnitId(1).build());
        r.put("2", new UnitAvailability.Builder().refurbishId("2").available(false).exists(true).uniqueUnitId(2)
                .stockInformation("Lager Hamburg").stockId(1).build());
        r.put("3", new UnitAvailability.Builder().refurbishId("3").available(false).exists(true).uniqueUnitId(3)
                .stockInformation("Umfuhr von Hamburg nach Berlin").build());
        r.put("4", new UnitAvailability.Builder().refurbishId("4").available(false).exists(true).uniqueUnitId(4)
                .stockInformation("Lager Hamburg").stockId(1).conflictDescription("RedTape hat ein offenes Dokument !").build());
        r.put("5", new UnitAvailability.Builder().refurbishId("5").available(true).exists(true).uniqueUnitId(5)
                .stockInformation("Lager Berlin").stockId(2).build());
        r.put("6", new UnitAvailability.Builder().refurbishId("15").available(true).exists(true).uniqueUnitId(6)
                .stockInformation("Lager Hamburg").stockId(1).lastRefurbishId("6").build());

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

        Dl.remote().add(RedTapeApi.class, new RedTapeApi() {

            @Override
            public UnitAvailability findUnitByRefurbishIdAndVerifyAviability(String refurbishId) {
                return Optional.ofNullable(r.get(refurbishId)).orElse(new UnitAvailability.Builder().refurbishId(refurbishId).available(false).exists(false).build());
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
