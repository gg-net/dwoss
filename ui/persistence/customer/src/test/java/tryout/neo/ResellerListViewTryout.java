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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ui.neo.ResellerListView;

import tryout.stub.CustomerAgentStub;

/**
 *
 * @author oliver.guenther
 */
public class ResellerListViewTryout {

    public static class InnerApplication extends Application {

        CustomerAgent agent = new CustomerAgentStub();

        @Override
        public void start(Stage s) throws Exception {
            ResellerListView view = new ResellerListView();
            s.setScene(new Scene(view));

            view.accept(agent.findAllResellerListCustomersEager());

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
