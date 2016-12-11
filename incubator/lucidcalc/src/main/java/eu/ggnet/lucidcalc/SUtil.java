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
package eu.ggnet.lucidcalc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Util class that contains some useful Action implementations.
 */
public class SUtil {

    private static class BeanProperty<T> implements SAction<T> {

        private String propertyName;

        public BeanProperty(String property) {
            this.propertyName = property;
        }

        @Override
        public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, T lineModel) {
            return chainedValue(propertyName, lineModel);
        }

        private Object chainedValue(String propertyChain, Object main) {
            if ( !propertyChain.contains(".") ) {
                return invoke(propertyChain, main);
            }
            int dot = propertyChain.indexOf(".");
            String property = propertyChain.substring(0, dot);
            return chainedValue(propertyChain.substring(dot + 1, propertyChain.length()), invoke(property, main));
        }

        private Object invoke(String property, Object main) {
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
                    } catch (Exception ex1) {
                        throw new RuntimeException("Exeption during invoke().getMethod()", ex1);
                    }
                }
                m.setAccessible(true);
                return m.invoke(main);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Exeption during invoke()", ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Exeption during invoke()", ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException("Exeption during invoke()", ex);
            } catch (SecurityException ex) {
                throw new RuntimeException("Exeption during invoke()", ex);
            }
        }

        @Override
        public CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, T lineModel) {
            return null;
        }
    }

    private final static SAction SELF_ROW = new SAction() {

        @Override
        public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
            if ( !(lineModel instanceof Object[]) ) {
                return "LineModel not of type Object[]";
            }
            Object o = ((Object[])lineModel)[relativeColumnIndex];
            if ( !(o instanceof SFormula) ) {
                return "o.getClass != SFormula, but " + o;
            }
            for (Object elem : ((SFormula)o).getElements()) {
                if ( elem instanceof SSelfRowReference ) {
                    ((SSelfRowReference)elem).setRowIndex(absoluteRowIndex);
                }
            }
            return o;
        }

        @Override
        public CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
            return null;
        }
    };

    private final static SAction NULL = new SAction() {

        @Override
        public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
            return null;
        }

        @Override
        public CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
            return null;
        }
    };

    public static SAction getBeanProperty(String name) {
        return new BeanProperty(name);
    }

    public static SAction getSelfRow() {
        return SELF_ROW;
    }

    /**
     * Returns an SAction that always returns null
     *
     * @return an SAction that always returns null
     */
    public static SAction getNull() {
        return NULL;
    }

    public static SAction getConstant(final Object constant) {
        return new SAction() {

            @Override
            public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
                return constant;
            }

            @Override
            public CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
                return null;
            }
        };
    }

    /**
     * Creates new {@link SSelfRowReference}
     *
     * @param column
     * @return new {@link SSelfRowReference}
     */
    public static SSelfRowReference SR(int column) {
        return new SSelfRowReference(column);
    }
}
