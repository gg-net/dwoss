package eu.ggnet.saft.core.authorisation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import eu.ggnet.saft.api.Accessable;
import eu.ggnet.saft.api.Authorisation;

/**
 * A Helper wrapper, that checks if a supplied object has a method setEnabled(boolean).
 *
 * @author oliver.guenther
 */
public class AccessEnabler implements Accessable {

    private Object object;

    private Method setEnabled;

    private Authorisation ar;

    /**
     * Constructor, verifies if the supplied object has a method setEnabled(boolean)
     *
     * @param object the supplied instance, must not be null.
     * @throws NullPointerException     if the supplied instance is null.
     * @throws IllegalArgumentException if no setEnabled method exists.
     */
    public AccessEnabler(Object object, Authorisation ar) throws NullPointerException, IllegalArgumentException {
        if ( object == null ) throw new NullPointerException("Supplied Instance is null");
        try {
            setEnabled = object.getClass().getMethod("setEnabled", Boolean.TYPE);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalArgumentException("The supplied instance has no setEnabled(boolean) Method, " + object, ex);
        }
        this.object = object;
        this.ar = ar;
    }

    public AccessEnabler(Object object) {
        this(object, null);
    }

    /**
     * Actually calling setEnabled on the supplied instance.
     *
     * @param enable the value of enabled.
     * @throws IllegalArgumentException if something went wrong while calling setEnabled.
     * @throws IllegalStateException    if something went wrong while calling setEnabled.
     */
    @Override
    public void setEnabled(boolean enable) throws IllegalArgumentException, IllegalStateException {
        try {
            setEnabled.invoke(object, enable);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException("The supplied instance cannot be called with setEnabled, " + object, ex);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.object);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final AccessEnabler other = (AccessEnabler)obj;
        return Objects.equals(this.object, other.object);
    }

    @Override
    public Authorisation getNeededRight() {
        return ar;
    }

    @Override
    public String toString() {
        return "AccessEnabler{" + "object=" + object + ", setEnabled=" + setEnabled + ", ar=" + ar + '}';
    }

}
