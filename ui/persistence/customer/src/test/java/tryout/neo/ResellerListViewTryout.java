/*
 * Copyright (C) 2019 GG-Net GmbH
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
package tryout.neo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.customer.api.ResellerListCustomer;
import eu.ggnet.dwoss.customer.ui.neo.ResellerListView;

/**
 *
 * @author oliver.guenther
 */
public class ResellerListViewTryout {

    public static class InnerApplication extends Application {

        @Override
        public void start(Stage s) throws Exception {
            ResellerListView view = new ResellerListView();
            s.setScene(new Scene(view));

            view.accept(Arrays.asList(
                    new ResellerListCustomer.Builder().id(0).name("Max Musterman").email("max@gmail.com").build(),
                    new ResellerListCustomer.Builder().id(0).name("Firma Musterman").email("musterman@gmail.com").build(),
                    new ResellerListCustomer.Builder().id(0).name("Hans Wurst").email("wurst@hans.com").build()
            ));

            s.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

    }

    public static void main(String[] args) {
        InnerApplication.main(args);
    }

}
