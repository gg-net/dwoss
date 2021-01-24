/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.core.widget;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.core.widget.auth.Accessable;
import eu.ggnet.dwoss.rights.api.AtomicRight;

/**
 * This class Implements {@link Accessable} and the {@link AtomicRight} which is returned {@link Accessable#getNeededRight() } will be setted in the
 * Constructor.
 * <p>
 * @author Bastian Venz
 */
public abstract class AccessableAction extends AbstractAction implements Accessable {

    private final AtomicRight authorisation;

    public AccessableAction(AtomicRight atomicRight) {
        super(atomicRight.toName());
        this.authorisation = atomicRight;
    }

    @Override
    public AtomicRight getNeededRight() {
        return authorisation;
    }

    @Override
    public String toString() {
        return "AccessableAction{" + "atomicRight=" + authorisation + " ,action=" + super.toString() + " ,actionName=" + super.getValue(NAME) + '}';
    }

}
