/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.common.exception;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.common.DetailDialog;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.SwingSaft;

import static eu.ggnet.saft.core.exception.ExceptionUtil.*;

/**
 *
 * @author oliver.guenther
 */
public class DwFinalExceptionConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable b) {
        SwingSaft.execute(() -> {
            DetailDialog.show(SwingCore.mainFrame(), "Systemfehler", extractDeepestMessage(b),
                    getUserInfo() + '\n' + toMultilineStacktraceMessages(b), getUserInfo() + '\n' + toStackStrace(b));
        });
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

}
