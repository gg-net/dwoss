package eu.ggnet.saft.sample.support;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.fx.OkCancelStage;

import java.time.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Shows a selector pane for the Revenue Report.
 * <p>
 * @author oliver.guenther
 */
@Title("Umsatzreport Parameter")
public class RevenueReportSelectorPane extends GridPane {

    private final ObjectProperty<Step> step = new SimpleObjectProperty<>();

    private final ObjectProperty<Category> category = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDate> start;

    private final ObjectProperty<LocalDate> end;

    public RevenueReportSelectorPane() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        ChoiceBox<Step> stepChoice = new ChoiceBox<>();
        stepChoice.getItems().addAll(Step.values());
        step.bind(stepChoice.getSelectionModel().selectedItemProperty());
        stepChoice.getSelectionModel().select(Step.DAY);

        addRow(0, new Label("Step:"), stepChoice);

        ChoiceBox<Category> contractorChoice = new ChoiceBox<>();
        contractorChoice.getItems().addAll(Category.values());

        category.bind(contractorChoice.getSelectionModel().selectedItemProperty());
        contractorChoice.getSelectionModel().selectFirst();
        addRow(1, new Label("Contractor:"), contractorChoice);

        DatePicker startPicker = new DatePicker(LocalDate.of(2014, 01, 01));
        start = startPicker.valueProperty();

        DatePicker endPicker = new DatePicker(LocalDate.of(2014, 12, 31));
        end = endPicker.valueProperty();

        addRow(2, new Label("Start:"), startPicker);
        addRow(3, new Label("End:"), endPicker);

    }

    public Step getStep() {
        return step.get();
    }

    public Category getCategory() {
        return category.get();
    }

    public Date getStart() {
        return Date.from(start.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getEnd() {
        return Date.from(end.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "RevenueReportSelectorPane{" + "step=" + step.get() + ", category=" + category.get() + ", start=" + start.get() + ", end=" + end.get() + '}';
    }

    public static void main(String[] args) throws InterruptedException {
        final JFXPanel p = new JFXPanel();
        final CountDownLatch block = new CountDownLatch(1);

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                RevenueReportSelectorPane pane = new RevenueReportSelectorPane();
                OkCancelStage<RevenueReportSelectorPane> stage = new OkCancelStage<>("Toller Title", pane);
                stage.showAndWait();
                if (stage.isOk()) {
                    System.out.println("OK Pressed");
                    System.out.println(pane);
                } else {
                    System.out.println("Closed without OK");
                }
                block.countDown();
            }
        });

        block.await();
    }

}
