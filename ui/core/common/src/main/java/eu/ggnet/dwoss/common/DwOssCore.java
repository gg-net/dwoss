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
package eu.ggnet.dwoss.common;

import java.awt.EventQueue;
import java.awt.Window;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import javax.validation.ConstraintViolationException;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.UiAlert.Type;
import eu.ggnet.saft.core.authorisation.Guardian;

import static eu.ggnet.saft.core.exception.ExceptionUtil.*;

/**
 *
 * @author oliver.guenther
 */
// TODO: In the new UI Framework we need to add the Guardion through a listener to the UiCore or SaftCore
public class DwOssCore {

    /**
     * Inspects the tree of Causes, if it contaions a LayerEightException.
     * If LayerEightExceptin is found, only the message is shown.
     * Else the hole stacktree.
     *
     * @param parent
     * @param e      the ChildExcpetion
     */
    // Hint: This is like deprecated, everything should go through Saft.
    public static void show(Window parent, Exception e) {
        if ( containsInStacktrace(UserInfoException.class, e) ) {
            UserInfoException ex = extractFromStraktrace(UserInfoException.class, e);
            Alert.title(ex.getHead()).message(ex.getMessage()).parent(parent).show(map(ex.getType()));
        } else if ( isConnectionClosed(e) ) {
            Alert.title("Netzwerkfehler")
                    .message("Netzwerkfehler, die Verbindung wurde getrennt.")
                    .nl("Bitte die Software neu starten.")
                    .parent(parent)
                    .show(Type.ERROR);
        } else if ( containsInStacktrace(ConstraintViolationException.class, e) ) {
            dispatch(() -> {
                DetailDialog.show(parent, "Validationsfehler", "Fehler bei der Validation", extractFormatedViolations(e), toStackStrace(e));
            });
        } else {
            dispatch(() -> {
                DetailDialog.show(parent, "Systemfehler", extractDeepestMessage(e),
                        getUserInfo() + '\n' + toMultilineStacktraceMessages(e), getUserInfo() + '\n' + toStackStrace(e));
            });
        }
    }

    private static void dispatch(Runnable r) {
        if ( EventQueue.isDispatchThread() ) r.run();
        else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException ex) {
                // This will never happen.
            }
        }
    }

    private static UiAlert.Type map(UserInfoException.Type t1) {
        switch (t1) {
            case ERROR:
                return UiAlert.Type.ERROR;
            case WARNING:
                return UiAlert.Type.WARNING;
            case INFO:
            default:
                return UiAlert.Type.INFO;
        }
    }

    /**
     * Filters a LayerEightException.getMessage out of an ExceptionTrace, or else return e.toString.
     *
     * @param e the Exception to filter
     * @return the String
     */
    public static String filterL8E(Exception e) {
        String msg = deepShow(e);
        if ( msg != null ) return "Nutzerfehler: " + msg;
        else return e.toString();
    }

    private static String deepShow(Throwable e) {
        if ( e == null ) return null;
        if ( e instanceof UserInfoException ) return e.getMessage();
        return deepShow(e.getCause());
    }

    public static String fromList(String head, Collection<String> messages) {
        StringBuilder sb = new StringBuilder(head);
        sb.append("\n");
        for (String msg : messages) {
            sb.append("- ").append(msg).append("\n");
        }
        return sb.toString();
    }

    private static boolean isConnectionClosed(Throwable ex) {
        if ( ex == null ) return false;
        if ( ex instanceof SQLException && "Already closed.".equals(ex.getMessage()) ) return true;
        return isConnectionClosed(ex.getCause());
    }

    private static String extractFormatedViolations(Throwable ex) {
        if ( ex == null ) return "No ConstraintViolationException found, wrong usage!";
        if ( ex instanceof ConstraintViolationException )
            return ConstraintViolationFormater.toMultiLine(((ConstraintViolationException)ex).getConstraintViolations(), true);
        return extractDeepestMessage(ex.getCause());
    }

    public static String getUserInfo() {
        String windowsUser = System.getProperty("user.name");
        String host = "Konnte Hostname nicht auslesen";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            host = localHost.getCanonicalHostName();
            host += "/" + localHost.getHostName();
        } catch (UnknownHostException ex) {
        }
        String workspaceUser = "Konnte User nicht auslesen.";
        try {
            workspaceUser = Lookup.getDefault().lookup(Guardian.class).getUsername();
        } catch (Exception exception) {
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Beim Nutzer \"").append(workspaceUser).append("\" ist ein Fehler Aufgetreten!\n")
                .append("Windows Daten: User=").append(windowsUser).append(" Hostname=").append(host);
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        DwOssCore.show(null, new RuntimeException("Message 1", new IOException("Message 2", new IllegalAccessError("Message 3"))));
    }
}
