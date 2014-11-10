package eu.ggnet.saft.api;

/**
 * This interface can be implement to make a Class Accessable with a Specific {@link AtomicRight}.
 * <p>
 * @author Bastian Venz
 */
public interface Accessable {

    /**
     * This method implements the Logic wiche represent that the Object should be enabled.d
     * <p>
     * @param enable should the implementing class enabled?
     */
    void setEnabled(boolean enable);

    /**
     * This is method returns the {@link AtomicRight} wich is needed to enable the implmented Class.
     * <p>
     * @return the {@link AtomicRight} wich is needed to enable the implmented Class.
     */
    Authorisation getNeededRight();

}
