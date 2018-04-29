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

import eu.ggnet.saft.core.ui.Once;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.Frame;
import eu.ggnet.saft.core.ui.ClosedListener;
import eu.ggnet.saft.core.ui.StoreLocation;
import eu.ggnet.saft.api.IdSupplier;

import java.awt.Dialog;
import java.awt.Window;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JComponent;

import javafx.scene.layout.Pane;
import javafx.stage.Modality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.TitleUtil;
import eu.ggnet.saft.core.ui.UiParent;

import lombok.*;
import lombok.experimental.Wither;

/**
 * Internal Parameter class.
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
public class UiParameter {

    /**
     * Type of the build process.
     */
    public static enum Type {
        FX {

            @Override
            public Object selectRelevantInstance(UiParameter p) {
                return p.getPane();
            }

        },
        FXML {

            @Override
            public Object selectRelevantInstance(UiParameter p) {
                return p.getController();
            }
        },
        DIALOG {

            @Override
            public Object selectRelevantInstance(UiParameter p) {
                return p.getDialog();
            }
        }, SWING {

            @Override
            public Object selectRelevantInstance(UiParameter p) {
                return p.getJPanel();
            }
        };

        public abstract Object selectRelevantInstance(UiParameter p);

    }

    private final Logger L = LoggerFactory.getLogger(UiParameter.class);

    private boolean once;

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     * Default = null.
     */
    private String id;

    /**
     * An optional title. If no title is given, the classname is used.
     * Default = null
     */
    private String title;

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     * Default = false
     */
    private boolean frame;

    /**
     * Optional value for the modality.
     * Default = null
     */
    @Getter
    private Modality modality;

    /**
     * Type of the build process, never null.
     */
    @Getter
    private final Type type;

    // --- Elements, which are constructed on the run, some may be null.
    @Getter
    private UiParent uiParent;

    @Wither
    @Getter
    private Class<?> rootClass = null;

    @Getter
    private Object preResult;

    @Wither
    @Getter
    private Pane pane;

    @Wither
    @Getter
    private JComponent jPanel;

    @Wither
    @Getter
    private Window window;

    @Wither
    @Getter
    private FxController controller;

    @Wither
    @Getter
    private javafx.scene.control.Dialog dialog;

    @Builder
    UiParameter(Boolean once, Boolean frame, String id, String title, Modality modality, UiParent uiParent, Type type) {
        this.once = once == null ? false : once;
        this.frame = frame == null ? false : frame;
        this.id = id;
        this.title = title;
        this.modality = modality;
        if ( uiParent != null ) {
            this.uiParent = uiParent;
        } else if ( UiCore.getMainFrame() != null ) {
            this.uiParent = UiParent.of(UiCore.getMainFrame());
        } else if ( UiCore.getMainStage() != null ) {
            this.uiParent = null;
            // TODO: Look into this Later
            //  this.uiParent = UiParent.of(UiCore.getMainStage());
        } else {
            throw new IllegalStateException("No UiParent set and noe in core.");
        }
        this.type = Objects.requireNonNull(type, "Type not set, not allowed");
    }

    /**
     * Adds the pre result, and if type of id supplier, adds the id.
     *
     * @param preResult the pre result
     * @return a new UiParameter
     */
    public UiParameter withPreResult(Object preResult) {
        if ( preResult == null ) return this;
        this.preResult = preResult;
        if ( id == null && preResult instanceof IdSupplier ) id = ((IdSupplier)preResult).id();
        return this;
    }

    /**
     * Returns the once value, either set or inspected in the root class.
     *
     * @return the once value, either set or inspected in the root class.
     */
    public boolean isOnce() {
        if ( rootClass != null ) {
            Once onceAnnotation = rootClass.getAnnotation(Once.class);
            if ( onceAnnotation != null ) {
                L.debug("OnceAnnotation is set on {} with {}", rootClass, onceAnnotation.value());
                return onceAnnotation.value();
            }
        }
        return once;
    }

    /**
     * Returns the frame value, either set or inspect annotation on the root class.
     * The annotation has a higher priority over the set value.
     *
     * @return the frame value
     */
    public boolean isFramed() {
        if ( rootClass != null ) {
            Frame frameAnnotaion = rootClass.getAnnotation(Frame.class);
            if ( frameAnnotaion != null ) return true;
        }
        return frame;
    }

    /**
     * Returns the modality for swingOrMain.
     *
     * @return the modality for swingOrMain
     */
    public Dialog.ModalityType toSwingModality() {
        if ( modality == null ) return Dialog.ModalityType.MODELESS;
        switch (modality) {
            case APPLICATION_MODAL:
                return Dialog.ModalityType.APPLICATION_MODAL;
            case WINDOW_MODAL:
                return Dialog.ModalityType.DOCUMENT_MODAL;
            case NONE:
                return Dialog.ModalityType.MODELESS;
        }
        return Dialog.ModalityType.MODELESS;
    }

    /**
     * Returns a title, either set or via class annotation.
     *
     * @return the title
     */
    public String toTitle() {
        return title == null ? TitleUtil.title(rootClass, id) : title;
    }

    /**
     * Returns a key string based on the root class and the id.
     *
     * @return a key string based on the root class and the id
     */
    public String toKey() {
        return rootClass.getName() + (id == null ? "" : ":" + id);
    }

    /**
     * Returns the reference class for icons and else.
     *
     * @return
     */
    public Class<?> getRefernceClass() {
        return type.selectRelevantInstance(this).getClass();
    }

    /**
     * Returns the relevat implementation based on the type of a closedlistnener.
     *
     * @return the relevat implementation based on the type of a closedlistnener
     */
    public Optional<ClosedListener> getClosedListenerImplemetation() {
        if ( type.selectRelevantInstance(this) instanceof ClosedListener ) {
            return Optional.of((ClosedListener)type.selectRelevantInstance(this));
        }
        return Optional.empty();
    }

    public UiParameter optionalConsumePreResult() {
        if ( preResult == null ) return this;
        if ( !(type.selectRelevantInstance(this) instanceof Consumer) ) return this;
        ((Consumer)type.selectRelevantInstance(this)).accept(preResult);
        return this;
    }

    /**
     * Returns true if the StoreLocation annotation was set.
     *
     * @return true if the StoreLocation annotation was set.
     */
    public boolean isStoreLocation() {
        return (getRefernceClass().getAnnotation(StoreLocation.class) != null);
    }

}
