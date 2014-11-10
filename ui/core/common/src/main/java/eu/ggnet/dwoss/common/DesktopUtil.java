package eu.ggnet.dwoss.common;

import eu.ggnet.dwoss.common.ExceptionUtil;

import java.awt.Desktop;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.util.UserInfoException.Type.*;

/**
 * Util class for Desktop
 */
public class DesktopUtil {

    private final static Map<UserInfoException.Type, Integer> mapping;

    static {
        Map<UserInfoException.Type, Integer> t = new HashMap<>();
        t.put(INFO, JOptionPane.INFORMATION_MESSAGE);
        t.put(WARNING, JOptionPane.WARNING_MESSAGE);
        t.put(ERROR, JOptionPane.ERROR_MESSAGE);
        mapping = t;
    }

    /**
     * Retuns the JOptionPane value for the UserInfoException Type.
     * <p>
     * @param type the type
     * @return
     */
    public static int toOptionPane(UserInfoException.Type type) {
        return mapping.get(type);
    }

    /**
     * A shorter way to use Desktop.open()
     *
     * @param file the file to be opened
     */
    public static void open(final File file) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Desktop.getDesktop().open(file);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (ExecutionException | InterruptedException e) {
                    ExceptionUtil.show(null, e);
                }
            }
        }.execute();
    }
}
