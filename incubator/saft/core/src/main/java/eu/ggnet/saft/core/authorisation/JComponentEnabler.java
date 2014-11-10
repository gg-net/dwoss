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
