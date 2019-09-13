/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.util;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

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

    private final Type type;

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

    public Type getType() {
        return type;
    }

    public String getHead() {
        return head;
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
