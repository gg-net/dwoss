/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.saft.core.authorisation;

import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.saft.api.Accessable;

import java.util.*;

import javax.swing.JComponent;

import lombok.*;

/**
 * A Helper wrapper, that checks if a supplied object has a method setEnabled(boolean).
 *
 * @author oliver.guenther
 */
@EqualsAndHashCode
@ToString
public class JComponentEnabler implements Accessable {

    private final Set<JComponent> components;

    private final Authorisation authorisation;

    /**
     * Constructor, verifies if the supplied object has a method setEnabled(boolean)
     *
     * @param components  the supplied instance, must not be null.
     * @param neededRight
     * @throws NullPointerException     if the supplied instance is null.
     * @throws IllegalArgumentException if no setEnabled method exists.
     */
    public JComponentEnabler(Authorisation neededRight, JComponent... components) throws NullPointerException, IllegalArgumentException {
        this.authorisation = neededRight;
        this.components = new HashSet<>(Arrays.asList(components));
    }

    /**
     * Actually calling setEnabled on the supplied instance.
     *
     * @param enable the value of enabled.
     */
    @Override
    public void setEnabled(boolean enable) {
        for (JComponent component : components) {
            component.setEnabled(enable);
        }
    }

    @Override
    public Authorisation getNeededRight() {
        return authorisation;
    }

}
