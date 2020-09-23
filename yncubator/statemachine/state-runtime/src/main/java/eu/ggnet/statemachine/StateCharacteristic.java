/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.statemachine;

import java.io.Serializable;

/**
 * A tagging interface for characteristics of {@link State}<code>s</code> for the {@link StateMachine} that uses equals and hashCode.
 * The implementation of this interface is used to represent {@link State}<code>s</code>. A good practice is a delegate.
 * <p/>
 * Let's assume a class Box:
 * <pre>
 * <code>
 * public class Box {
 *
 *   private boolean closed;
 *
 *   public boolean isClosed() { return closed; }
 *
 *   public void setClosed(boolean closed) { this.closed = closed; }
 *
 * }
 * </code>
 * </pre>
 * And let's assume, the chracteristics is the property closed and the class uses some other kind of equals and hashCode
 * (or the Developer was to lazy to implement).
 * <p/>
 * A Delegate Example:
 * <pre>
 * <code>
 * public class BoxStateCharacteristics implements StateCharacteristic&lt;Box&gt; {
 *
 *   private Box box;
 *
 *   public BoxStateCharacteristics(Box box) { this.box = box; }
 *
 *   public int hashCode() { return 89 * 3 + (box.isClosed() ? 1 : 0); }
 *
 *   public boolean equals(Object obj) {
 *     if (obj == null) { return false; }
 *     if (getClass() != obj.getClass()) { return false; }
 *     final BoxStateCharacteristics other = (BoxStateCharacteristics) obj;
 *     if (this.box.isClosed() != other.box.isClosed()) { return false; }
 *     return true;
 *   }
 *
 * }
 * </code>
 * </pre>
 *
 * @author oliver.guenther
 */
public interface StateCharacteristic<T> extends Serializable {

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
