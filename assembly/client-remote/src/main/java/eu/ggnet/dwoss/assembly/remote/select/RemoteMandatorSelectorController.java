package eu.ggnet.dwoss.assembly.remote.select;

import java.io.IOException;
import java.net.URL;

import eu.ggnet.dwoss.util.dialog.Alert;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class RemoteMandatorSelectorController {

    /**
     * Creates the approriated Loader and calls load().
     * <p>
     * @return the approriated Loader and calls load().
     */
    public static FXMLLoader newAutoLoader() {
        try {
            FXMLLoader loader = new FXMLLoader(getFxmlUrl());
            loader.load();
            return loader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getFxmlUrl() {
        return RemoteMandatorSelectorController.class.getResource("RemoteMandatorSelectorView.fxml");
    }

    private static class ModeListCell extends ListCell<RemoteMode> {

        @Override
        protected void updateItem(RemoteMode item, boolean empty) {
            super.updateItem(item, empty);
            if ( item != null ) {
                setText(item.getDescription());
            }
        }
    }

    // Getter
    private final BooleanProperty ok = new SimpleBooleanProperty(false);

    private Stage stage;

    @FXML
    private ComboBox<RemoteMode> mandatorComboBox;

    @FXML
    private TextField urlField;

    @FXML
    void cancelPressed(ActionEvent event) {
        ok.set(false);
        if ( stage != null ) {
            stage.close();
        }
    }

    @FXML
    void showUsage(ActionEvent event) {
        Alert.builder()
                .title("Usage:")
                .body("APPLICATION --mandator=MANDATOR --mode=MODE\n"
                        + "APPLICATION --url=URL\n"
                        + "APPLICATION --select\n\n"
                        + "MANDATOR values:\n"
                        + "  ggnet = GG-Net GmbH\n"
                        + "  elus = Elbe Logistik & Service GmbH\n"
                        + "MODE values:\n"
                        + "  testing = Testing Server on obsidian\n"
                        + "  productive = Prdoctive Server on retrax\n"
                        + "URL = Url to ejb server (e.g. " + RemoteMode.GG_NET_PRODUCTIVE.getUrl() + ")\n"
                        + "--select (Displays Selector Dialog)")
                .build()
                .show();
    }

    @FXML
    void okPressed(ActionEvent event) {
        ok.set(true);
        if ( stage != null ) {
            stage.close();
        }
    }

    public boolean isOk() {
        return ok.get();
    }

    public boolean isCancel() {
        return !ok.get();
    }

    public BooleanProperty okProperty() {
        return ok;
    }

    public String getUrl() {
        return urlField.getText();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void initialize() {
        mandatorComboBox.setItems(FXCollections.observableArrayList(RemoteMode.values()));
        mandatorComboBox.setButtonCell(new ModeListCell());
        mandatorComboBox.setCellFactory(new Callback<ListView<RemoteMode>, ListCell<RemoteMode>>() {

            @Override
            public ListCell<RemoteMode> call(ListView<RemoteMode> p) {
                return new ModeListCell();
            }
        });
        mandatorComboBox.valueProperty().addListener(new ChangeListener<RemoteMode>() {

            @Override
            public void changed(ObservableValue<? extends RemoteMode> ov, RemoteMode oldValue, RemoteMode newValue) {
                if ( newValue == null ) {
                    urlField.setText("");
                } else {
                    urlField.setText(newValue.getUrl());
                }
            }
        });
        urlField.editableProperty().bind(mandatorComboBox.valueProperty().isEqualTo(RemoteMode.FREE));
        mandatorComboBox.setValue(RemoteMode.GG_NET_PRODUCTIVE);
    }

}
