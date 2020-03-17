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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.event.UserChange;
import eu.ggnet.dwoss.rights.api.AtomicRight;

/**
 * CDI Bean to enable disable the timeout and set it.
 *
 * @author oliver.guenther
 */
@Singleton
public class LoggedInTimeout {

    private LocalTime timeOut = LocalTime.of(0, 3, 0);

    private Logger log = LoggerFactory.getLogger(LoggedInTimeout.class);

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("mm:ss");

    private final AtomicBoolean timerOn = new AtomicBoolean(true);

    private Timeline timeline;

    private final RadioButton on = new RadioButton("Ein");

    private final RadioButton off = new RadioButton("Aus");

    private Optional<Runnable> runOnTimeout = Optional.empty();

    private Optional<Consumer<LocalTime>> timeOutStore = Optional.empty();

    private final IntegerProperty timeSeconds = new SimpleIntegerProperty();

    private boolean once = false;

    public Pane createPane() {
        if ( once ) throw new RuntimeException("createPane() called a second time. Not allowed");
        once = true;
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(on, off);

        TextField countDown = new TextField(timeFormatter.format(timeOut));
        countDown.setPrefWidth(60);
        Tooltip tooltip = new Tooltip("Eingabe ist Ok.");
        Tooltip.install(countDown, tooltip);

        countDown.setOnKeyReleased(e -> {
            try {
                TemporalAccessor result = timeFormatter.parse(countDown.getText());
                timeOut = LocalTime.of(0, result.get(ChronoField.MINUTE_OF_HOUR), result.get(ChronoField.SECOND_OF_MINUTE));
                timeOutStore.ifPresent(s -> s.accept(timeOut));
                tooltip.setText("Eingabe ist Ok.");
                on.setDisable(false);
                log.debug("countDown.setOnAction() setting new timeOut {}", timeOut);
            } catch (DateTimeParseException ex) {
                tooltip.setText("Eingabe " + countDown.getText() + " nicht ok. "
                        + "Eingabe muss den Format mm:ss entsprechen. Führende 0 ist notwendig. (z.b. 5:00 -> 05:00)");
                on.setDisable(true); // Disable on es indicator.
            }
        });

        // Default is on and selected.
        // timeout and timeron are also defaults ...
        countDown.setDisable(true);
        on.setSelected(true);
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
        });

        on.setOnAction(e -> {
            log.debug("on.setOnAction() called: starting timer");
            countDown.setDisable(true);
            startTime();
        });

        //----
        timeSeconds.addListener((ov, o, n) -> {
            countDown.setText(timeFormatter.format(LocalTime.ofSecondOfDay(n.intValue())));
        });

        //----
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
        runOnTimeout.ifPresent(r -> Platform.runLater(r));
    }

    /**
     * An optional action to be run on the end of the timer.
     *
     * @param runnable timeout action
     */
    public void setTimeoutAction(Runnable runnable) {
        runOnTimeout = Optional.ofNullable(runnable);
    }

    /**
     * Change the timeOut.
     *
     * @param localTime
     */
    public void setTimeoutAndStartTime(LocalTime localTime) {
        if ( localTime == null ) return;
        this.timeOut = localTime;
        startTime();
    }

    /**
     * Set a optional consumer for storing any new timeout.
     *
     * @param consumer the timeout consumer
     */
    public void setTimeoutStore(Consumer<LocalTime> consumer) {
        timeOutStore = Optional.ofNullable(consumer);
    }

    /**
     * Start the timer.
     */
    public void startTime() {
        log.debug("startTime() starting a new timeline");
        if ( on == null || !on.isSelected() ) return;
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
        timeline.setOnFinished(e -> runOnTimeout.ifPresent(r -> Platform.runLater(r)));
        timeline.playFromStart();
    }

    /**
     * Obeserves userchanges
     *
     * @param userChange the userchange
     */
    public void changeUser(@Observes UserChange userChange) {
        on.setDisable(!userChange.allowedRights().contains(AtomicRight.MODIFY_LOGGED_IN_TIMEOUT));
        off.setDisable(!userChange.allowedRights().contains(AtomicRight.MODIFY_LOGGED_IN_TIMEOUT));
    }
}
