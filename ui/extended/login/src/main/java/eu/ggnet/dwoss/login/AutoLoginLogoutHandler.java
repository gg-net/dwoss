package eu.ggnet.dwoss.login;

import java.awt.AWTEvent;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.core.AutoLoginLogout;

import static eu.ggnet.saft.core.Client.lookup;
import static java.awt.AWTEvent.KEY_EVENT_MASK;
import static java.awt.AWTEvent.MOUSE_EVENT_MASK;
import static java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK;

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
        lookup(Workspace.class).addShutdownListener(event -> stsx.shutdown());

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
        Guardian accessCos = lookup(Guardian.class);
        accessCos.logout();
        dialog = new AutoLogoutDialog(lookup(Workspace.class).getMainFrame(), accessCos.getOnceLoggedInUsernames());
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
