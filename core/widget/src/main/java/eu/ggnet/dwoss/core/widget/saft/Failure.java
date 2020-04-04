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
package eu.ggnet.dwoss.core.widget.saft;

import java.awt.Component;

import javafx.scene.Parent;

import eu.ggnet.saft.core.Ui;

import static eu.ggnet.saft.core.ui.AlertType.ERROR;

/**
 * Failure Handler, usefull in an Optional {@link Reply} chain.
 *
 * @author oliver.guenther
 */
public class Failure {

    public static <T> boolean handle(Reply<T> reply) {
        if ( reply == null ) throw new NullPointerException("Reply is null, not allowed");
        if ( reply.hasSucceded() ) return true;
        Ui.build().title("Fehler")
                .alert()
                .message(reply.getSummary())
                .show(ERROR);
        return false;
    }

    public static <T> boolean handle(Component swingParent, Reply<T> reply) {
        if ( reply == null ) throw new NullPointerException("Reply is null, not allowed");
        if ( reply.hasSucceded() ) return true;
        Ui.build(swingParent).title("Fehler")
                .alert()
                .message(reply.getSummary())
                .show(ERROR);
        return false;

    }

    public static <T> boolean handle(Parent fxParent, Reply<T> reply) {
        if ( reply == null ) throw new NullPointerException("Reply is null, not allowed");
        if ( reply.hasSucceded() ) return true;
        Ui.build(fxParent).title("Fehler")
                .alert()
                .message(reply.getSummary())
                .show(ERROR);
        return false;
    }

}
