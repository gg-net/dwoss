package eu.ggnet.dwoss.redtape.renderer;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.redtape.entity.Position;

import static javafx.scene.text.TextAlignment.*;

/**
 *
 * @author oliver.guenther
 */
public class PositionListCell extends ListCell<Position> {

    static URL loadLeftArrow() {
        return PositionListCell.class.getResource("left_arrow.png");
    }

    static URL loadDownArrow() {
        return PositionListCell.class.getResource("down_arrow.png");
    }

    private static final DecimalFormat CUR = new DecimalFormat("#,##0.00 €");

    private static final DecimalFormat A = new DecimalFormat("0.##");

    public static class Factory implements Callback<ListView<Position>, ListCell<Position>> {

        @Override
        public ListCell<Position> call(ListView<Position> listView) {
            return new PositionListCell(listView);
        }
    }

    private final static int BORDER = 25;

    private final static int SHOW_HIDE_IMAGE = 30;

    /**
     * The space the refurbishId takes.
     */
    // TODO: Find some automatism
    private final static int OFFSET = 10;

    private final static ObjectProperty<FontSmoothingType> FONT_SMOOTHING_TYPE_PROPERTY = new SimpleObjectProperty<>(FontSmoothingType.LCD);

    private final IntegerProperty refurbishIdLengthProperty = new SimpleIntegerProperty(0);

    private final Text refurbishIdText;

    private final Text head;

    private final Text body;

    private final Text foot;

    private final FlowPane pane;

    private final Image show;

    private final Image hide;

    private final ImageView iv;

    public PositionListCell(ListView<Position> listView) {
        pane = new FlowPane(2, 2);

        refurbishIdText = new Text();
        refurbishIdText.fontSmoothingTypeProperty().bind(FONT_SMOOTHING_TYPE_PROPERTY);
        refurbishIdText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 18));
        refurbishIdText.setFill(Color.DARKBLUE);

        head = new Text();
        head.fontSmoothingTypeProperty().bind(FONT_SMOOTHING_TYPE_PROPERTY);
        head.wrappingWidthProperty().bind(listView.widthProperty().subtract(BORDER + SHOW_HIDE_IMAGE).subtract(refurbishIdLengthProperty));
        try {
            show = new Image(loadLeftArrow().openStream(), 20, 20, true, true);
            hide = new Image(loadDownArrow().openStream(), 20, 20, true, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        body = new Text();
        body.fontSmoothingTypeProperty().bind(FONT_SMOOTHING_TYPE_PROPERTY);
        body.wrappingWidthProperty().bind(listView.widthProperty().subtract(BORDER));
        body.setTextAlignment(JUSTIFY);
        body.setFont(Font.font("Arial", 10));

        iv = new ImageView(show);
        iv.setOnMouseClicked((MouseEvent event) -> {
            if ( event.getButton().equals(MouseButton.PRIMARY) ) {
                ObservableList<Node> children1 = pane.getChildren();
                if ( children1.contains(body) ) {
                    children1.remove(body);
                    iv.setImage(show);
                } else {
                    children1.add(1, body);
                    iv.setImage(hide);
                }
            }
        });

        foot = new Text();
        foot.fontSmoothingTypeProperty().bind(FONT_SMOOTHING_TYPE_PROPERTY);
        foot.setTextAlignment(RIGHT);
        foot.wrappingWidthProperty().bind(listView.widthProperty().subtract(BORDER));

        StackPane refurbishIdPane = new StackPane();
        refurbishIdPane.getChildren().add(refurbishIdText);

        HBox hb = new HBox(5);
        hb.getChildren().addAll(refurbishIdPane, head, iv);
        pane.getChildren().addAll(hb, foot);

//        text.textProperty().bind(itemProperty().asString());
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(Position item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }
        /*
         TODO: Cause the ListCell is reused, normaly we would need to clean up the case, that a cell is showing the body from before.
         But in JDK 8 a click on the iv allways calls updateItem on all Items. So in JDK 7 this works in 8 not.
         If we ever find out, how to change this behavior, uncoment the following code.
         */

        /*
         ObservableList<Node> children = pane.getChildren();
         if ( children.contains(body) ) {
         children.remove(body);
         iv.setImage(show);
         }
         */
        setGraphic(pane);
        // TODO: If we ever store the RefrubishId as Metadata on the position, change this.
        String header = item.getName();
        int s = header.indexOf("SopoNr:");
        if ( s != -1 ) {
            int e = header.indexOf(" ", s);
            String refurbishId = header.substring(s + 7, e);
            refurbishIdText.setText(refurbishId);
            refurbishIdLengthProperty.set(OFFSET * refurbishId.length());
        } else {
            refurbishIdText.setText("");
            refurbishIdLengthProperty.set(0);
        }
        head.setText(item.getName());
        body.setText(item.getDescription());

        TreeItem<String> root = new TreeItem<>(item.getName());
        TreeItem<String> leaf = new TreeItem<>(item.getDescription());

        root.getChildren().add(leaf);

        switch (item.getType()) {
            case COMMENT:
                foot.setText("");
                break;
            case UNIT:
                foot.setText("netto: " + CUR.format(item.getPrice()) + " | brutto: " + CUR.format(item.getAfterTaxPrice()));
                break;
            default:
                foot.setText("Menge: " + A.format(item.getAmount())
                        + " | netto: " + CUR.format(item.getPrice() * item.getAmount())
                        + " | brutto: " + CUR.format(item.getAfterTaxPrice() * item.getAmount()));
        }
    }

}
