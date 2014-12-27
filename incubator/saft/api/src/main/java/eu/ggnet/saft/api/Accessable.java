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
package eu.ggnet.saft.api;

/**
 * This interface can be implement to make a Class Accessable with a Specific {@link AtomicRight}.
 * <p>
 * @author Bastian Venz
 */
public interface Accessable {

    /**
     * This method implements the Logic which represent that the Object should be enabled.
     * <p>
     * @param enable should the implementing class enabled?
     */
    void setEnabled(boolean enable);

    /**
     * This is method returns the {@link AtomicRight} which is needed to enable the implemented Class.
     * <p>
     * @return the {@link AtomicRight} which is needed to enable the implemented Class.
     */
    Authorisation getNeededRight();

}
