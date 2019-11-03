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
package eu.ggnet.dwoss.core.common;

/**
 * A simple Interface to return a note from any object.
 * This interface should no longer be used, as there are better ways to perform the intended task.
 * But it doesn't hurt anyone, and it might be remove if we ever redesign the receipt ui.
 *
 * @author pascal.perau
 */
public interface INoteModel {

    /**
     * Returns an info note of some relevance.
     *
     * @return an info note
     */
    public String getNote();
}
