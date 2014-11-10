package eu.ggnet.dwoss.util;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import lombok.Getter;

import static eu.ggnet.dwoss.util.UserInfoException.Type.INFO;

/**
 * An Exception that is used to inform the User about something.
 * This Exception will not rollback a Transaction on the Server.
 *
 * @author oliver.guenther
 */
public class UserInfoException extends Exception {

    public enum Type {

        ERROR, WARNING, INFO
    }

    @Getter
    private final Type type;

    @Getter
    private final String head;

    public UserInfoException(String message) {
        super(message);
        type = INFO;
        head = "Nutzerfehler";
    }

    public UserInfoException(String title, String message) {
        super(message);
        this.type = INFO;
        this.head = title;
    }

    public UserInfoException(String title, String message, Type type) {
        super(message);
        this.type = type;
        head = title;
    }

    public UserInfoException(Set<?> violation) {
        super(fromViolations(violation));
        type = INFO;
        head = "Nutzerfehler";
    }

    public UserInfoException(List<String> messages) {
        super(fromList(messages));
        type = INFO;
        head = "Nutzerfehler";
    }

    public UserInfoException(String title, List<String> messages) {
        super(fromList(messages));
        type = INFO;
        head = (title == null ? "Nutzerfehler" : title);
    }

    private static String fromList(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        if ( messages.size() > 2 ) sb.append("Viele Fehler:\n");
        for (String msg : messages) {
            sb.append("- ").append(msg).append("\n");
        }
        return sb.toString();
    }

    private static String fromViolations(Set<?> violation) {
        StringBuilder sb = new StringBuilder();
        for (Object o : violation) {
            if ( o instanceof ConstraintViolation ) {
                ConstraintViolation v = (ConstraintViolation)o;
                sb.append("Eigenschaft: ").append(v.getPropertyPath());
                sb.append(", Fehler: ").append(v.getMessage());
            } else {
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }
}
