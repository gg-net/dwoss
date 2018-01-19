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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jens.papenhagen
 */
public class CompanyPopUpViewTryOut extends Application {

    //CustomerComapnyController
    public static void main(String[] args) {

        launch(args);

//        //stub for the new Costumer modell with generator needed
//        Client.addSampleStub(CustomerAgent.class, new CustomerAgentStub());
//
//        Company c = new Company();
//
//        JButton close = new JButton("Schliessen");
//        close.addActionListener(e -> Ui.closeWindowOf(close));
//
//        JButton run = new JButton("OpenUi");
//        run.addActionListener(ev -> {
//            Ui.fxml().show(() -> c, CompanyPopUpController.class);
////            Ui.fxml().eval(() -> c, CompanyPopUpController.class);
//
//        });
//a
//        JPanel p = new JPanel();
//        p.add(run);
//        p.add(close);
//
//        UiCore.startSwing(() -> p);
    }

    JButton run = new JButton("OpenUi");

    run.addActionListener (ev
        -> {
            Ui.fxml().show(CompanyPopUpController.class);
        @Override
        public void start
        (Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("src/main/resources/eu.ggnet.dwoss.customer.ui/neo/CompanyPopUpView.fxml"));
             << << << < HEAD

            Scene scene = new Scene(root);
             == == == =
        });
         >>> >>> > origin / master

        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }

}
