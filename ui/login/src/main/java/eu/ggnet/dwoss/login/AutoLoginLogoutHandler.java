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
package eu.ggnet.dwoss.login;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static java.awt.AWTEvent.*;

@ServiceProvider(service = AutoLoginLogout.class)
public class AutoLoginLogoutHandler implements AWTEventListener, KeyEventDispatcher, ActionListener, AutoLoginLogout {

    private final ScheduledExecutorService stsx = Executors.newSingleThreadScheduledExecutor();

    private int timeInSeconds;

    private AutoLogoutDialog dialog;

    private ScheduledFuture<?> schedule;

    @SuppressWarnings("LeakingThisInConstructor")
    public AutoLoginLogoutHandler() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, MOUSE_MOTION_EVENT_MASK | MOUSE_EVENT_MASK | KEY_EVENT_MASK);
        UiCore.addOnShutdown(() -> stsx.shutdown());
    }

    /**
     * Sets the timeout and enables the auto logout.
     * <p/>
     * @param tis the timeout in seconds, 0 or smaller disables auto logout.
     */
    @Override
    public void setTimeout(int tis) {
        timeInSeconds = tis;
        if ( timeInSeconds <= 0 ) {
            timeInSeconds = 0;
            if ( schedule != null ) schedule.cancel(true);
            schedule = null;
        } else {
            schedule = stsx.schedule(() -> showAuthenticator(), timeInSeconds, TimeUnit.SECONDS);;
        }
    }

    @Override
    public void showAuthenticator() {
        if ( dialog != null ) return; // When the Dialog is already open, return.
        Guardian accessCos = Dl.local().lookup(Guardian.class);
        accessCos.logout();
        dialog = new AutoLogoutDialog(UiCore.getMainFrame(), accessCos.getOnceLoggedInUsernames());
        dialog.setVisible(true);
        dialog = null;
    }

    /**
     * Captures all AWTEvents to set and reset the timer to automatically logout the user.
     * <p/>
     * @param event The captured event group
     */
    @Override
    public void eventDispatched(AWTEvent event) {
        if ( schedule != null && dialog == null ) {
            schedule.cancel(true);
            schedule = stsx.schedule(() -> {
                showAuthenticator();
            }, timeInSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * Captures the combination Shift + Strg + L KeyEvents to quick logout the user.
     * <p/>
     * @param e the captured KeyEvent
     * @return weither the dispatcher should pass the events through or not.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if ( e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_L ) {
            if ( dialog == null ) {
                showAuthenticator();
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAuthenticator();
    }
}
