/* 
 * Copyright (C) 2014 pascal.perau
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


import java.awt.Window;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JOptionPane;
import javax.validation.ConstraintViolationException;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;
import eu.ggnet.saft.core.authorisation.Guardian;

/**
 *
 * @author oliver.guenther
 */
// TODO: In the new UI Framework we need to add the Guardion through a listener to the UiCore or SaftCore
public class ExceptionUtil {

    // mapping
    /**
     * Inspects the tree of Causes, if it contaions a LayerEightException.
     * If LayerEightExceptin is found, only the message is shown.
     * Else the hole stacktree.
     *
     * @param parent
     * @param e      the ChildExcpetion
     */
    public static void show(Window parent, Exception e) {
        if ( contains(UserInfoException.class, e) ) {
            UserInfoException ex = extract(UserInfoException.class, e);
            JOptionPane.showMessageDialog(parent, ex.getMessage(), ex.getHead(), DesktopUtil.toOptionPane(ex.getType()));
        } else if ( isConnectionClosed(e) ) {
            JOptionPane.showMessageDialog(parent, "Netzwerkfehler, die Verbindung wurde getrennt.\nBitte die Software neu starten.", "Netzwerkfehler", JOptionPane.ERROR_MESSAGE);
        } else if ( contains(ConstraintViolationException.class, e) ) {
            DetailDialog.show(parent, "Validationsfehler", "Fehler bei der Validation", extractFormatedViolations(e), stackStraceAsString(e));
        } else {
            DetailDialog.show(parent, "Systemfehler", extractDeepestMessage(e),
                    getUserInfo() + '\n' + cascadeStraceAsString(e), getUserInfo() + '\n' + stackStraceAsString(e));
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

    private static String extractDeepestMessage(Throwable ex) {
        if ( ex.getCause() == null ) return ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage();
        return extractDeepestMessage(ex.getCause());
    }

    private static String cascadeStraceAsString(Throwable ex) {
        if ( ex == null ) return "";
        if ( ex.getCause() == null ) return ex.getClass().getSimpleName() + ":" + ex.getLocalizedMessage();
        return ex.getClass().getSimpleName() + ":" + ex.getLocalizedMessage() + System.getProperty("line.separator") + cascadeStraceAsString(ex.getCause());
    }

    private static boolean contains(Class<?> clazz, Throwable ex) {
        if ( ex == null ) return false;
        if ( ex.getClass().equals(clazz) ) return true;
        return contains(clazz, ex.getCause());
    }

    private static <T> T extract(Class<T> clazz, Throwable ex) {
        if ( ex == null ) throw new NullPointerException("No Class in Stacktrace : " + clazz);
        if ( ex.getClass().equals(clazz) ) return (T)ex;
        return extract(clazz, ex.getCause());
    }

    /**
     * Converts exception stack trace as string
     *
     * @param ex
     * @return
     */
    private static String stackStraceAsString(Throwable ex) {
        try (StringWriter sw = new StringWriter()) {
            ex.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private static String getUserInfo() {
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
        ExceptionUtil.show(null, new RuntimeException("Message 1", new IOException("Message 2", new IllegalAccessError("Message 3"))));
    }
}
