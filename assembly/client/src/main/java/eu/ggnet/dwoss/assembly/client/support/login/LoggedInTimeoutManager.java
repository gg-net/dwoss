/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support.login;

import eu.ggnet.dwoss.core.widget.Dl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.util.Duration;

import org.slf4j.Logger;

import eu.ggnet.dwoss.core.widget.event.UserChange;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.core.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

/**
 * CDI Bean to enable disable the timeout and set it.
 *
 * @author oliver.guenther
 */
@Singleton
public class LoggedInTimeoutManager {

    /**
     * Default Timeout of 3 minutes.
     */
    private LocalTime timeOut = LocalTime.of(0, 3, 0);

    @Inject
    private Logger log;

    @Inject
    private LoggedInTimeoutStorage storage;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("mm:ss");

    private final AtomicBoolean timerOn = new AtomicBoolean(false);

    private final AtomicBoolean loggedOut = new AtomicBoolean(false);

    private Timeline timeline;

    private final RadioButton on = new RadioButton("Ein");

    private final RadioButton off = new RadioButton("Aus");

    private final TextField countDown = new TextField("");

    private final IntegerProperty timeSeconds = new SimpleIntegerProperty();

    private boolean once = false;

    public Pane createToolbarElementOnce() {
        if ( once ) throw new RuntimeException("createPane() called a second time. Not allowed");
        once = true;
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(on, off);

        countDown.setText(timeFormatter.format(timeOut));
        countDown.setPrefWidth(60);
        Tooltip tooltip = new Tooltip("Eingabe ist Ok.");
        Tooltip.install(countDown, tooltip);

        countDown.setOnKeyReleased(e -> {
            try {
                TemporalAccessor result = timeFormatter.parse(countDown.getText());
                timeOut = LocalTime.of(0, result.get(ChronoField.MINUTE_OF_HOUR), result.get(ChronoField.SECOND_OF_MINUTE));
                tooltip.setText("Eingabe ist Ok.");
                on.setDisable(false);
                storage.storeTimeOut(timeOut);
                log.debug("countDown.setOnAction() setting new timeOut {}", timeOut);
            } catch (DateTimeParseException ex) {
                tooltip.setText("Eingabe " + countDown.getText() + " nicht ok. "
                        + "Eingabe muss den Format mm:ss entsprechen. Führende 0 ist notwendig. (z.b. 5:00 -> 05:00)");
                on.setDisable(true); // Disable on es indicator.
            }
        });

        // Load status
        if ( storage.loadActive() ) {
            countDown.setDisable(true);
            on.setSelected(true);
        } else {
            countDown.setDisable(false);
            off.setSelected(true);
        }

        on.setDisable(true);
        off.setDisable(true);

        off.setOnAction(e -> {
            log.debug("off.setOnAction() called: stopping timer and enabling input");
            timerOn.set(false);
            if ( timeline != null ) {
                timeline.stop();
            }
            countDown.setText(timeFormatter.format(timeOut));
            countDown.setDisable(false);
            storage.storeActive(false);
        });

        on.setOnAction(e -> {
            log.debug("on.setOnAction() called: starting timer");
            countDown.setDisable(true);
            startTime();
            storage.storeActive(true);
        });

        timeSeconds.addListener((ov, o, n) -> {
            countDown.setText(timeFormatter.format(LocalTime.ofSecondOfDay(n.intValue())));
        });

        HBox low = new HBox(on, off, new Label("Countdown: "), countDown);
        low.setAlignment(Pos.CENTER);
        low.setSpacing(5);
        VBox main = new VBox(new Label("Timeout für angemeldete Benutzer"), low);
        main.setAlignment(Pos.CENTER);
        return main;
    }

    /**
     * Resets the timer.
     * If the timer is running it will restart it.
     */
    public void resetTime() {
        if ( timerOn.get() ) {
            timeline.playFrom("start"); // Info: jumpTo does not work.
        }
    }

    /**
     * Call to enforce a manual timeout.
     * Will stop all internal timers and execute the runOnTimeout action.
     */
    public void manualTimeout() {
        log.debug("manualTimeout() stopping timer and calling runOnTimeout.");
        if ( timerOn.compareAndSet(true, false) ) {
            timeline.stop();
        }
        runOnTimeout();
    }

    /**
     * Start the timer.
     */
    public void startTime() {
        if ( on == null || !on.isSelected() ) {
            log.debug("startTime() ignoring, LoggedInTimeout is set to off");
            return;
        }
        log.debug("startTime() starting a new timeline");
        timerOn.set(true);

        // create then new timeline
        if ( timeline != null ) {
            timeline.stop();
        }
        timeSeconds.set(timeOut.toSecondOfDay());
        timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(timeOut.toSecondOfDay() + 1),
                        new KeyValue(timeSeconds, 0)));
        timeline.setOnFinished(e -> runOnTimeout());
        timeline.playFromStart();
    }

    /**
     * Obeserves userchanges
     *
     * @param userChange the userchange
     */
    public void changeUser(@Observes UserChange userChange) {
        if ( userChange.allowedRights().contains(AtomicRight.MODIFY_LOGGED_IN_TIMEOUT) ) {
            on.setDisable(false);
            off.setDisable(false);
            countDown.setDisable(on.isSelected());
        } else {
            on.setDisable(true);
            off.setDisable(true);
            countDown.setDisable(true);
        }
    }

    private void runOnTimeout() {
        if ( !loggedOut.compareAndSet(false, true) ) return; // disables Bounces
        Dl.local().lookup(Guardian.class).logout();
        Ui.build().title("Login").modality(Modality.APPLICATION_MODAL).fxml().show(() -> new LoginScreenConfiguration.Builder()
                .onSuccess(p -> {
                    Ui.closeWindowOf(p);
                    loggedOut.set(false);
                    startTime();
                })
                .onCancel(() -> UiCore.shutdown())
                .guardian(Dl.local().lookup(Guardian.class))
                .build(), LoginScreenController.class);
    }

    @PostConstruct
    private void postInit() {
        timeOut = storage.loadTimeout();
    }
}
