package eu.ggnet.dwoss.util.interactiveresult;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
public interface InteractionListener {

    public enum TrueFalseCancel {

        TRUE, FALSE, CANCEL
    }

    /**
     * Notify the listener.
     * <p>
     * @param type    the type of message
     * @param head    the headline
     * @param message the message.
     */
    void notify(String head, String message, UserInfoException.Type type);

    /**
     * Called for a Yes/No action.
     * <p>
     * @param type    the type of message
     * @param head    the head of the message
     * @param message the message body.
     * @return true for yes, false for no.
     */
    boolean interactYesNo(String head, String message, UserInfoException.Type type);

    boolean interactOkCancel(String head, String message, UserInfoException.Type type);

    TrueFalseCancel interactYesNoCancel(String head, String message, UserInfoException.Type type);

}
