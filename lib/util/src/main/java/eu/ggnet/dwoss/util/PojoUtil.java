package eu.ggnet.dwoss.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * Utilclass for Pojos.
 * <p>
 * @author oliver.guenther
 */
public class PojoUtil {

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
                re += !name.equals("null") ? name.toString() : object.toString();
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
