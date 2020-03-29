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
package eu.ggnet.dwoss.assembly.remote.exception;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.swing.DetailDialog;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.SwingSaft;
import eu.ggnet.saft.core.ui.builder.UiWorkflowBreak;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static eu.ggnet.saft.core.ui.AlertType.WARNING;
import static eu.ggnet.saft.core.ui.exception.ExceptionUtil.*;

/**
 *
 * @author oliver.guenther
 */
public class DwFinalExceptionConsumer implements Consumer<Throwable> {

    private final static Logger L = LoggerFactory.getLogger(DwFinalExceptionConsumer.class);

    private final Optional<Supplier<String>> bugMail;

    public DwFinalExceptionConsumer(Supplier<String> bugMail) {
        this.bugMail = Optional.ofNullable(bugMail);
    }

    @Override
    public void accept(Throwable throwable) {
        Throwable b = Objects.requireNonNull(throwable, "Throwable must not be null");
        if ( b instanceof UiWorkflowBreak || b.getCause() instanceof UiWorkflowBreak ) {
            L.debug("FinalExceptionConsumer catches UiWorkflowBreak, which is ignored by default");
            return;
        }
        L.error("Systemfehler: {} , {}", b.getClass().getSimpleName(), b.getMessage());
        if ( b.getMessage() != null && b.getMessage().contains("pushingpixels") ) return; // Ignore alle plaf problems
        String deepestMessage = extractDeepestMessage(b);
        if ( deepestMessage.contains("EJBCLIENT000025") ) {
            Ui.exec(() -> {
                Ui.build().title("Netzwerkfehler").alert()
                        .message("Es ist eine Netzwerkproblem aufgetreten")
                        .nl("Bitte das aktuelle Fenster einmal schliessen und noch einmal versuchen")
                        .show(WARNING);
            });
        } else {
            SwingSaft.run(() -> {
                DetailDialog.show(SwingCore.mainFrame(), "Systemfehler", deepestMessage,
                        getUserInfo() + '\n' + toMultilineStacktraceMessages(b), getUserInfo() + '\n' + toStackStrace(b),
                        bugMail.orElse(() -> null).get());
            });
        }
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
        String workspaceUser = Dl.local().optional(Guardian.class).map(Guardian::getUsername).orElse("Konnte User nicht auslesen");
        StringBuilder sb = new StringBuilder();
        sb.append("Beim Nutzer \"").append(workspaceUser).append("\" ist ein Fehler Aufgetreten!\n")
                .append("Windows Daten: User=").append(windowsUser).append(" Hostname=").append(host);
        return sb.toString();
    }

}
