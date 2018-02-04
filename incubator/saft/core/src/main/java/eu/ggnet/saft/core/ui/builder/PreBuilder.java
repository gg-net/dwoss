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
package eu.ggnet.saft.core.ui.builder;

import java.awt.Component;
import java.awt.Window;

import javafx.scene.Parent;
import javafx.stage.Modality;

import eu.ggnet.saft.api.ui.UiParent;
import eu.ggnet.saft.core.ui.SwingCore;

/**
 *
 * @author oliver.guenther
 */
public class PreBuilder {

    /**
     * Represents the parent of the ui element, optional.
     * The default is in the swingmode SwingCore.mainFrame();
     */
    Window swingParent = SwingCore.mainFrame();

    /**
     * Sets the once mode.
     * If set to true, an once mode is enable. This ensures that one one window of the same type is created and show.
     * If minimised it becomes reopend, if in the back it becomes moved to the front.
     * Default = false.
     */
    boolean once = false;

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     * Default = null.
     */
    String id = null;

    /**
     * An optional title. If no title is given, the classname is used.
     * Default = null
     */
    String title = null;

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     * Default = false
     */
    boolean frame = false;

    /**
     * Optional value for the modality.
     * Default = null
     */
    Modality modality = null;

    /**
     * Sets the once mode.
     * If set to true, an once mode is enable. This ensures that one one window of the same type is created and show.
     * If minimised it becomes reopend, if in the back it becomes moved to the front.
     *
     * @param once the once mode
     * @return this as fluent usage
     */
    public PreBuilder once(boolean once) {
        this.once = once;
        return this;
    }

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     *
     * @param id the optional id.
     * @return this as fluent usage
     */
    public PreBuilder id(String id) {
        this.id = id;
        return this;
    }

    /**
     * An optional title. If no title is given, the classname is used.
     *
     * @param title the title;
     * @return this as fluent usage
     */
    public PreBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     *
     * @param frame if true frame is assumed.
     * @return this as fluent usage
     */
    public PreBuilder frame(boolean frame) {
        this.frame = frame;
        return this;
    }

    /**
     * Optional value for the modality.
     *
     * @param modality the modality to use
     * @return this as fluent usage
     */
    public PreBuilder modality(Modality modality) {
        this.modality = modality;
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param swingParent the parent
     * @return this as fluent usage
     */
    public PreBuilder parent(Component swingParent) {
        this.swingParent = SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param javaFxParent the parent
     * @return this as fluent usage
     */
    public PreBuilder parent(Parent javaFxParent) {
        this.swingParent = SwingCore.windowAncestor(javaFxParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param uiParent the parent
     * @return this as fluent usage
     */
    public PreBuilder parent(UiParent uiParent) {
        if ( uiParent == null ) return this;
        if ( uiParent.getSwingParent() != null ) return parent(uiParent.getSwingParent());
        return parent(uiParent.getJavafxParent());
    }

    /**
     * Initializes a new swing component handling.
     * The mode: swing is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new swing builder
     */
    public SwingBuilder swing() {
        return new SwingBuilder(this);
    }

    /**
     * Initializes a new fx dialog component handling.
     * The mode: the fx dialog is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new dialog builder
     */
    public DialogBuilder dialog() {
        return new DialogBuilder(this);
    }

    /**
     * Initializes a new fx component handling.
     * The mode: the fx pane is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new fxbuilder
     */
    public FxBuilder fx() {
        return new FxBuilder(this);
    }

    /**
     * Initializes a new fx component handling.
     * The mode: the fx pane is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new fxbuilder
     */
    public FxmlBuilder fxml() {
        return new FxmlBuilder(this);
    }

    /**
     * Initializes a alert, like the swing JOptionPane or the javafx 8u60 Alert.
     *
     * @return a new Ui.build().alert()builder.
     */
    public AlertBuilder alert() {
        return new AlertBuilder(this);
    }

    /**
     * Shortcut for alert().message(xxxx).show().
     *
     * @param message the message to be shown
     */
    public void alert(String message) {
        new AlertBuilder(this).message(message).show();
    }
}
