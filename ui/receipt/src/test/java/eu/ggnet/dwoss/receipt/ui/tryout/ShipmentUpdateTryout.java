package eu.ggnet.dwoss.receipt.ui.tryout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentUpdateStage;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.MandatorsStub;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author pascal.perau
 */
public class ShipmentUpdateTryout extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new Label("Main Applikation")));
        primaryStage.show();
        Shipment s = new Shipment();
        ShipmentUpdateStage stage = new ShipmentUpdateStage(s);
        stage.showAndWait();
        System.out.println(stage.getShipment());
    }

    @Override
    public void init() throws Exception {
        Dl.remote().add(Mandators.class, new MandatorsStub());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
