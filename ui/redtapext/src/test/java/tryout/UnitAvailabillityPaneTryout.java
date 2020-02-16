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

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.redtapext.ui.cap.UnitAvailabilityPane;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.RemoteLookup;

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
        });

        Application.launch(UnitAvailabilityTryoutApplication.class);
    }
}
