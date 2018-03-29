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
package eu.ggnet.saft.core.ui;

import java.awt.Component;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.saft.UiCore;

import lombok.NonNull;

/**
 * Parent, that can hold either a swingOrMain or a javafx parent.
 *
 * @author oliver.guenther
 */
public abstract class UiParent {

    private static abstract class UiParentSwingMode extends UiParent {

        @Override
        public Window swingOrMain() throws IllegalStateException {
            Window result = swing();
            if ( result == null ) result = UiCore.getMainFrame();
            return result;
        }

        @Override
        public final Stage fxOrMain() throws IllegalStateException {
            throw new IllegalStateException("Calling javafx parent in swing mode.");
        }

        @Override
        public final Stage fx() throws IllegalStateException {
            throw new IllegalStateException("Calling javafx parent in swing mode.");
        }
    }

    private static abstract class UiParentJavaFxMode extends UiParent {

        @Override
        public final Stage fxOrMain() throws IllegalStateException {
            Stage fx = fx();
            if ( fx == null ) fx = UiCore.getMainStage();
            return fx;
        }

        @Override
        public final Window swingOrMain() throws IllegalStateException {
            throw new IllegalStateException("Calling swing parent in javafx mode.");
        }

        @Override
        public final Window swing() throws IllegalStateException {
            throw new IllegalStateException("Calling swing parent in javafx mode.");
        }
    }

    private static class UiParentSwingModeJavaFxElement extends UiParentSwingMode {

        private final Node javafxElement;

        public UiParentSwingModeJavaFxElement(@NonNull Node javafxElement) {
            this.javafxElement = javafxElement;
        }

        @Override
        public Window swing() throws IllegalStateException {
            return SwingCore.windowAncestor(javafxElement).orElse(null);
        }

    }

    private static class UiParentSwingModeSwingElement extends UiParentSwingMode {

        private final Component swingElement;

        public UiParentSwingModeSwingElement(@NonNull Component swingElement) {
            this.swingElement = swingElement;
        }

        @Override
        public Window swing() throws IllegalStateException {
            if ( swingElement instanceof Window ) return (Window)this.swingElement;
            return SwingUtilities.getWindowAncestor(swingElement);
        }
    }

    private static class UiParentJavaFxModeJavaFxElement extends UiParentJavaFxMode {

        private final Node javafxElement;

        public UiParentJavaFxModeJavaFxElement(@NonNull Node javafxElement) {
            this.javafxElement = javafxElement;
        }

        @Override
        public final Stage fx() throws IllegalStateException {
            Scene scene = javafxElement.getScene();
            if ( scene == null ) return null;
            javafx.stage.Window window = scene.getWindow();
            if ( !(window instanceof Stage) ) return null; // Consider the Window as reuslt for the future.
            return (Stage)window;
        }

    }

    private static class UiParentJavaFxModeDefault extends UiParentJavaFxMode {

        @Override
        public final Stage fx() throws IllegalStateException {
            return UiCore.getMainStage();
        }

    }

    private static class UiParentSwingModeDefault extends UiParentSwingMode {

        @Override
        public final Window swing() throws IllegalStateException {
            return UiCore.getMainFrame();
        }

    }

    /**
     * Returns a new wrapped parrent of a swingOrMain component.
     *
     * @param swingParent the swingparent to be wrapped.
     * @return a new wrapped parrent of a swingOrMain component.
     */
    public static UiParent of(Component swingParent) {
        if ( UiCore.isSwing() ) return new UiParentSwingModeSwingElement(swingParent);
        if ( UiCore.isFx() ) throw new IllegalArgumentException("Not yet implemented");
        throw new IllegalArgumentException("UiCore is neither in FX nore in Swing mode. Is the Core running ?");
    }

    /**
     * Returns a new wrapped parrent of a javafx parent.
     *
     * @param javafxElement a javafxparent
     * @return a new wrapped parrent of a javafx parent
     */
    public static UiParent of(Node javafxElement) {
        if ( UiCore.isSwing() ) return new UiParentSwingModeJavaFxElement(javafxElement);
        if ( UiCore.isFx() ) return new UiParentJavaFxModeJavaFxElement(javafxElement);
        throw new IllegalArgumentException("UiCore is neither in FX nore in Swing mode. Is the Core running ?");
    }

    public static UiParent defaults() {
        if ( UiCore.isSwing() ) return new UiParentSwingModeDefault();
        if ( UiCore.isFx() ) return new UiParentJavaFxModeDefault();
        throw new IllegalArgumentException("UiCore is neither in FX nore in Swing mode. Is the Core running ?");
    }

    /**
     * Returns the Swing window wrapped arround the supplied element, or the main window if none found.
     * Swing mode expected.
     *
     * @return the wrapping window or the main if none found..
     * @throws IllegalStateException if called in fx mode.
     */
    public abstract Window swingOrMain() throws IllegalStateException;

    /**
     * Returns the Swing window wrapped arround the supplied element, or null if none found.
     * Swing mode expected.
     *
     * @return the wrapping window or null.
     * @throws IllegalStateException if called in fx mode.
     */
    public abstract Window swing() throws IllegalStateException;

    /**
     * Returns the JavaFx stage wrapped arround the supplied element, or the main window if none found.
     * JavaFx mode expected.
     *
     * @return the wrapping javafx stage or the main stage.
     * @throws IllegalStateException if called in swing mode.
     */
    public abstract Stage fxOrMain() throws IllegalStateException;

    /**
     * Returns the JavaFx stage wrapped arround the supplied element, or null if none found.
     * JavaFx mode expected.
     *
     * @return the wrapping javafx stage or null.
     * @throws IllegalStateException if called in swing mode.
     */
    public abstract Stage fx() throws IllegalStateException;

    /**
     * Multipatform consumer.
     *
     * @param swingConsumer  consumer, called only in swing mode and if a swing parent is not null.
     * @param javaFxConsumer consuer, called only in fx mode and if a fx parent is not null.
     */
    public void ifPresent(Consumer<Window> swingConsumer, Consumer<Stage> javaFxConsumer) {
        if ( UiCore.isSwing() && swing() != null ) swingConsumer.accept(swing());
        if ( UiCore.isFx() && fx() != null ) javaFxConsumer.accept(fx());
    }

}
