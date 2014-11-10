package eu.ggnet.saft.core.authorisation;

import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.saft.api.Accessable;

import javax.swing.AbstractAction;


/**
 * This class Implements {@link Accessable} and the {@link AtomicRight} wich is returned {@link Accessable#getNeededRight() } will be setted in the Constructor.
 * <p>
 * @author Bastian Venz
 */
public abstract class AccessableAction extends AbstractAction implements Accessable {

    private final Authorisation authorisation;

    public AccessableAction(Authorisation atomicRight) {
        super(atomicRight.toName());
        this.authorisation = atomicRight;
    }

    @Override
    public Authorisation getNeededRight() {
        return authorisation;
    }

    @Override
    public String toString() {
        return "AccessableAction{" + "atomicRight=" + authorisation + " ,action=" + super.toString() + " ,actionName=" + super.getValue(NAME) + '}';
    }

}
