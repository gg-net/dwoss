package eu.ggnet.saft.core.authorisation;

/**
 * Can be Informed about UserChanges.
 * 
 * @author oliver.guenther
 */
public interface UserChangeListener {
    
    /**
     * Called if a User is logged in.
     * 
     * @param name 
     */
    public void loggedIn(String name);

    /**
     * Called if a User is logged out.
     */
    public void loggedOut();
    
}
