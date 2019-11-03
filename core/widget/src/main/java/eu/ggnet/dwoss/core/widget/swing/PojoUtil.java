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
package eu.ggnet.dwoss.core.widget.swing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * Utility class that allows a string property reflectiv access to pojos.
 * <p>
 * @author oliver.guenther
 * @deprecated either use a real expression engine or java8 lambdas.
 */
@Deprecated
public class PojoUtil {

    /**
     * Tries to invoke the property get method on the supplied pojo, allows chaining with a point.
     * Like "name.first" on o executes: o.getName().getFirst();
     *
     * @param propertyName the property value
     * @param pojo         the pojo
     * @return the result of the pojo call.
     * @deprecated use lambdas.
     */
    @Deprecated
    public static Object getValue(String propertyName, Object pojo) {
        if ( pojo == null ) return "pojo == null";
        return chainedValue(propertyName, pojo);
    }

    /**
     * Iterates over the collection and trys to usea method getName, otherwise toString.
     * <p>
     * @param elems the elements.
     * @return a sting with the values concated by ","
     */
    public static String toNames(Collection<?> elems) {
        String re = "";
        for (Iterator it = elems.iterator(); it.hasNext();) {
            Object object = it.next();
            try {
                Method m = object.getClass().getMethod("getName", new Class[]{});
                String name = (String)m.invoke(object, new Object[]{});
                re += !name.equals("null") ? name : object.toString();
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                re += object.toString();
            }
            if ( it.hasNext() ) re += ", ";
        }
        return re;
    }

    private static Object chainedValue(String propertyChain, Object main) {
        if ( !propertyChain.contains(".") ) {
            return invoke(propertyChain, main);
        }
        int dot = propertyChain.indexOf(".");
        String property = propertyChain.substring(0, dot);
        if ( invoke(property, main) == null ) {
            return null;
        }
        return chainedValue(propertyChain.substring(dot + 1, propertyChain.length()), invoke(property, main));
    }

    private static Object invoke(String property, Object main) {
        Method m;
        try {
            try {
                m = main.getClass().getMethod(
                        "get"
                        + property.substring(0, 1).toUpperCase()
                        + property.substring(1));
            } catch (NoSuchMethodException ex) {
                try {
                    // Trying "is" in the case of booleans
                    m = main.getClass().getMethod(
                            "is"
                            + property.substring(0, 1).toUpperCase()
                            + property.substring(1));
                } catch (NoSuchMethodException | SecurityException ex1) {
                    throw new RuntimeException("Exeption during invoke().getMethod()", ex1);
                }
            }
            m.setAccessible(true);
            return m.invoke(main);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            throw new RuntimeException("Exeption during invoke()", ex);
        }
    }
}
