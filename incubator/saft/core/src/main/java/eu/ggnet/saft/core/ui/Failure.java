/*
 * Copyright (C) 2017 GG-Net GmbH
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

import javafx.scene.Parent;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;

import static eu.ggnet.saft.core.ui.AlertType.ERROR;

/**
 *
 * @author oliver.guenther
 */
public class Failure {

    // maybe a panel could also happen
    /**
     * Represents the parent of the ui element, optional.
     */
    protected Window swingParent = null;

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param swingParent the parent
     * @return this as fluent usage
     */
    public Failure parent(Component swingParent) {
        this.swingParent = SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param javaFxParent the parent
     * @return this as fluent usage
     */
    public Failure parent(Parent javaFxParent) {
        this.swingParent = SwingCore.windowAncestor(javaFxParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Handles failed replys by showing them in the registered way.
     * For now this method shows an alert if the reply.hasSucceded is false.
     * This method is best used in the filterd method of Optional as it will return true if reply.hasSucceded is ture, otherwise false.
     *
     *
     * @param <T>   the type of reply payload, must not be null
     * @param reply the reply
     * @return ture if reply.hasSucceded is ture, else false.
     */
    public <T> boolean handle(Reply<T> reply) {
        if ( reply == null ) throw new NullPointerException("Reply is null, not allowed");
        if ( reply.hasSucceded() ) return true;
        Ui.build(swingParent).title("Fehler")
                .alert()
                .message(reply.getSummary())
                .show(ERROR);
        return false;
    }

}
