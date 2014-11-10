package eu.ggnet.saft.core.authorisation;

import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.saft.api.Accessable;
import eu.ggnet.saft.api.AuthenticationException;

import java.util.Set;

import javax.swing.Action;


/**
 * Simple Authentication Interface.
 */
public interface Guardian {

    /**
     * Logout a possible authenticated user.
     */
    void logout();

    /**
     * Returns the usernames of all once logged in users.
     *
     * @return the usernames of all once logged in users.
     */
    Set<String> getOnceLoggedInUsernames();

    /**
     * Used to authenticate. Throws an exception if authentication is not successful.
     *
     * @param user the username
     * @param pass the password
     * @throws AuthenticationException is thrown if authentication is not successful
     */
    void login(String user, char[] pass) throws AuthenticationException;

    /**
     * Returns the active Rights or an empty Set.
     *
     * @return the active Rights or an empty Set.
     */
    Set<Authorisation> getRights();

    /**
     * Returns the Username or an empty String if no one is authenticated yet.
     *
     * @return the Username or an empty String if no one is authenticated yet.
     */
    String getUsername();

    /**
     * The quick authentication mode, allows the change of the user be simply suppling his id.
     *
     * @param userId the userid.
     * @return true if userId was found and change was successful, otherwise false.
     */
    boolean quickAuthenticate(int userId);

    /**
     * Remove an accessDependet.
     *
     * @param instance the access dependent to remove.
     */
    void remove(Object instance);

    /**
     * Adds a UserChangeListener to the access.
     *
     * @param listener the listener
     */
    void addUserChangeListener(UserChangeListener listener);

    /**
     * Removes a UserChangeListener to the access.
     *
     * @param listener the listener
     */
    void removeUserChangeListener(UserChangeListener listener);

    /**
     * Add A {@link Accessable}.
     * The {@link Accessable} get called the method {@link Accessable#setEnable(boolean)} with true
     * when the Rights are setted and the method {@link Accessable#getNeededRights()} return a {@link Set<AtomicRight>} wich has the {@link AtomicRight}
     * containing.
     * <p>
     * @param accessable wich should be added in a intern List/Set.
     */
    public void add(Accessable accessable);

    /**
     * * Add a object wiche has a setEnabled Method, like {@link Action#setEnabled(boolean)}.
     * It will wrap this in a AccessEnabler which then controll it.
     * <p>
     * @param enableAble
     * @param atomicRight
     */
    public void add(Object enableAble, Authorisation atomicRight);

    /**
     * This method remove a {@link Accessable} from an internal list.
     * <p>
     * @param accessable the {@link Accessable} which should removed.
     */
    public void remove(Accessable accessable);

    /**
     * Tis method returns {@link Boolean#TRUE} if the current user have the given {@link AtomicRight}.
     * <p>
     * @param atomicRight the given {@link AtomicRight} wich will be checked.
     * @return {@link Boolean#TRUE} if the current user have the given {@link AtomicRight}.
     */
    public boolean hasRight(Authorisation atomicRight);
}
