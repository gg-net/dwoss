/*
 * Copyright (C) 2018 GG-Net GmbH
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
import java.util.List;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ui.neo.SelectDefaultEmailCommunicationView;
import eu.ggnet.dwoss.customer.ui.neo.SelectDefaultEmailCommunicationView.Selection;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;

/**
 *
 * @author oliver.guenther
 */
public class SelectDefaultEmailCommunicationTryout extends Application {

    private static long lastid = 1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<Communication> comms = Arrays.asList(c("demo@demo.com"), c("info@demo.com"));

        UiCore.startJavaFx(primaryStage, () -> {
            Button b = new Button("Open Select Email");
            b.setOnAction(e -> Ui.build().fx().eval(() -> new Selection(comms, null), () -> new SelectDefaultEmailCommunicationView())
                    .cf().thenAccept(System.out::println).handle(Ui.handler()));
            return new FlowPane(b);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Communication c(String s) {
        Communication c = new Communication(lastid++);
        c.setType(EMAIL);
        c.setIdentifier(s);
        return c;
    }

}
