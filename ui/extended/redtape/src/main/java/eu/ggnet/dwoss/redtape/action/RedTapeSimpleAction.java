package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import eu.ggnet.dwoss.redtape.RedTapeView;

import static javax.swing.Action.SMALL_ICON;

/**
 *
 * @author pascal.perau
 */
public class RedTapeSimpleAction extends AbstractAction {

    public RedTapeSimpleAction() {
        super("Kunden und Aufträge verwalten");
        putValue(SMALL_ICON, new ImageIcon(loadSmallIcon()));
        putValue(LARGE_ICON_KEY, new ImageIcon(loadLargeIcon()));
        putValue(SHORT_DESCRIPTION, "Öffnet Kunden und Aufträge");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RedTapeView.showSingleInstance();
    }

    static URL loadSmallIcon() {
        return RedTapeSimpleAction.class.getResource("kua_small.png");
    }

    static URL loadLargeIcon() {
        return RedTapeSimpleAction.class.getResource("kua_large.png");
    }
}
