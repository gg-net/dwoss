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
package eu.ggnet.saft.runtime;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

import javafx.application.Application;
import javafx.application.Platform;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.Accessable;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ActionFactory.MetaAction;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.UserChangeListener;

import static eu.ggnet.saft.core.Client.lookup;

public class SwingClient {

    // Helper for Substance Lafs.
    private static class SlafName {

        public SlafName(String name, String className) {
            this.name = name;
            this.className = "org.pushingpixels.substance.api.skin." + className;
        }

        String name;

        String className;
    }

    private static class LafAction implements ActionListener {

        private final String className;

        public LafAction(String className) {
            this.className = className;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(className);
                SwingUtilities.updateComponentTreeUI(lookup(Workspace.class).getMainFrame());
                lookup(UserPreferences.class).storeLaf(className);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                L.error("Error on changing LAF", ex);
            }
        }
    }

    private static final SlafName[] substanceLaafs = {
        new SlafName("Autumn", "SubstanceAutumnLookAndFeel"),
        new SlafName("Business", "SubstanceBusinessLookAndFeel"),
        new SlafName("Business Blue", "SubstanceBusinessBlueSteelLookAndFeel"),
        new SlafName("Business Black", "SubstanceBusinessBlackSteelLookAndFeel"),
        new SlafName("Cerulean", "SubstanceCeruleanLookAndFeel"),
        new SlafName("Challenger Deep", "SubstanceChallengerDeepLookAndFeel"),
        new SlafName("Creme", "SubstanceCremeLookAndFeel"),
        new SlafName("Creme Coffee", "SubstanceCremeCoffeeLookAndFeel"),
        new SlafName("Dust", "SubstanceDustLookAndFeel"),
        new SlafName("Dust Coffee", "SubstanceDustCoffeeLookAndFeel"),
        new SlafName("Emerald Dusk", "SubstanceEmeraldDuskLookAndFeel"),
        new SlafName("Gemini", "SubstanceGeminiLookAndFeel"),
        new SlafName("Graphite", "SubstanceGraphiteLookAndFeel"),
        new SlafName("Graphite Aqua", "SubstanceGraphiteAquaLookAndFeel"),
        new SlafName("Graphite Glass", "SubstanceGraphiteGlassLookAndFeel"),
        new SlafName("Magellan", "SubstanceMagellanLookAndFeel"),
        new SlafName("Mariner", "SubstanceMarinerLookAndFeel"),
        new SlafName("Mist Aqua", "SubstanceMistAquaLookAndFeel"),
        new SlafName("Mist Silver", "SubstanceMistSilverLookAndFeel"),
        new SlafName("Moderate", "SubstanceModerateLookAndFeel"),
        new SlafName("Nebula Brick Wall", "SubstanceNebulaBrickWallLookAndFeel"),
        new SlafName("Nebula", "SubstanceNebulaLookAndFeel"),
        new SlafName("Office Black 2007", "SubstanceOfficeBlack2007LookAndFeel"),
        new SlafName("Office Blue 2007", "SubstanceOfficeBlue2007LookAndFeel"),
        new SlafName("Office Silver 2007", "SubstanceOfficeSilver2007LookAndFeel"),
        new SlafName("Raven", "SubstanceRavenLookAndFeel"),
        new SlafName("Sahara", "SubstanceSaharaLookAndFeel"),
        new SlafName("Twilight", "SubstanceTwilightLookAndFeel")
    };

    private static final Logger L = LoggerFactory.getLogger(SwingClient.class);

    private ClientView view;

    private final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor((r) -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private int autologout = 0;

    /**
     * Does nothing, can be overridden by clients.
     */
    protected void close() {
    }

    public void init() {
        try {
            UIManager.setLookAndFeel(lookup(UserPreferences.class).loadLaf());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            L.warn("Cound not set LAF:" + ex.getMessage(), ex);
        }

        // Create the View
        view = new ClientView();

        // Collecting all MetaActions
        Collection<? extends ActionFactory> actionFactories = Lookup.getDefault().lookupAll(ActionFactory.class);
        if ( actionFactories == null || actionFactories.isEmpty() ) throw new IllegalStateException("No ActionFactories found");

        List<MetaAction> metaActions = new ArrayList<>();
        for (ActionFactory actionFactory : actionFactories) {
            metaActions.addAll(actionFactory.createMetaActions());
        }

        // Filling the Menus
        SortedMap<Integer, JMenu> finalMenus = buildMenus(metaActions);
        for (Map.Entry<Integer, JMenu> entry : finalMenus.entrySet()) {
            JMenu m = entry.getValue();
            view.menuBar.add(m);
            if ( m.getText().equals("System") ) m.add(buildLafMenu());
        }

        // Filling the Toolbar
        for (MetaAction metaAction : metaActions) {
            if ( metaAction.isToolbar() ) view.toolBar.add(metaAction.getAction());
        }
        view.toolBar.addSeparator();
        Collection<? extends ToolbarComponent> tcs = Lookup.getDefault().lookupAll(ToolbarComponent.class);
        SortedMap<Integer, Component> tccs = new TreeMap<>();
        for (ToolbarComponent tc : tcs) {
            if ( tc instanceof Component ) tccs.put(tc.getOrder(), (Component)tc);
            if ( (tc instanceof UserChangeListener) && Lookup.getDefault().lookup(Guardian.class) != null )
                lookup(Guardian.class).addUserChangeListener((UserChangeListener)tc);
        }
        for (Component tc : tccs.values()) {
            view.toolBar.add(tc);
        }

        // Filling the Main Panel
        Collection<? extends MainComponent> mcs = Lookup.getDefault().lookupAll(MainComponent.class);
        for (MainComponent mc : mcs) {
            if ( mc instanceof Component ) view.mainPanel.add((Component)mc, mc.getLayoutHint());
            if ( (mc instanceof UserChangeListener) && Lookup.getDefault().lookup(Guardian.class) != null )
                lookup(Guardian.class).addUserChangeListener((UserChangeListener)mc);
        }

        enableAccessRestrictions(metaActions);

        lookup(Workspace.class).setMainFrame(view);

        es.scheduleAtFixedRate(new HiddenMonitorDisplayer(view), 2, 2, TimeUnit.SECONDS);

        view.setLocationByPlatform(true);
        lookup(UserPreferences.class).loadLocation(view);

        Workspace ws = lookup(Workspace.class);
        ws.setMainFrame(view);
        ws.addShutdownListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lookup(UserPreferences.class).storeLocation(view);
                es.shutdownNow();
                try {
                    boolean result = es.awaitTermination(10, TimeUnit.SECONDS);
                    L.info("Shutdown Client,ExecutorService.isTerminated={}", result);
                } catch (InterruptedException ex) {
                }
                close();
            }
        });

        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                lookup(Workspace.class).shutdown();
            }
        });

        if ( System.getProperty("persistence.host") != null ) view.setTitle(view.getTitle() + " Datenbank:" + System.getProperty("persistence.host"));

        // Autostart Saft. This will be different one day.
        UiCore.continueSwing(view);
        UiCore.backgroundActivityProperty().addListener((ov, o, n) -> {
            EventQueue.invokeLater(() -> view.extraProgressPanel.setVisible(n));
            Platform.runLater(() -> view.progressIndicator.setProgress(n ? -1 : 0));
        });
    }

    private void ready(String postTitle) {
        AutoLoginLogout all = Lookup.getDefault().lookup(AutoLoginLogout.class);
        if ( all == null ) return;
        all.setTimeout(autologout);
        all.showAuthenticator();
        if ( postTitle == null ) postTitle = "";
        else postTitle = " - " + postTitle;
        view.setTitle(view.getTitle() + postTitle);
    }

    private void run(String postTitle) {
        view.setVisible(true);
        ready(postTitle);
    }

    public void show(final String postTitle, final Application.Parameters parameters) {
        if ( parameters != null && parameters.getNamed().containsKey("autologout") ) {
            autologout = Integer.parseInt(parameters.getNamed().get("autologout"));
            System.out.println("Aktiviere Autologout mit " + autologout + "s Timeout.");
        }
        run(postTitle);
    }

    private SortedMap<Integer, JMenu> buildMenus(Collection<MetaAction> metaActions) {
        Map<String, JMenu> menus = new HashMap<>();
        SortedMap<Integer, JMenu> finalMenus = new TreeMap<>();
        for (MetaAction metaAction : metaActions) {
            JMenu parrentMenu = null;
            Action action = metaAction.getAction();
            for (int i = 0; i < metaAction.getMenuNames().size(); i++) {
                List<String> names = metaAction.getMenuNames().subList(0, i + 1);
                String indexName = names.toString();
                String leafName = names.get(i);

                if ( !menus.containsKey(indexName) ) { // Gibt es das Menu schon, wenn nicht, lege es an
                    JMenu menu = new JMenu(leafName);
                    menus.put(indexName, menu);
                    if ( parrentMenu == null ) finalMenus.put(index(leafName), menu);
                    else parrentMenu.add(menu);
                }

                JMenu menu = menus.get(indexName);
                parrentMenu = menu;

                // If we are at the final iteration, add the action or the separator
                if ( i == metaAction.getMenuNames().size() - 1 ) {
                    if ( action == null ) menu.addSeparator();
                    else menu.add(action);
                }
            }
        }
        return finalMenus;
    }

    private void enableAccessRestrictions(Collection<MetaAction> metaActions) {
        Guardian accessCos = Lookup.getDefault().lookup(Guardian.class);
        if ( accessCos != null ) {
            for (ActionFactory.MetaAction metaAction : metaActions) {
                if ( metaAction.getAction() instanceof Accessable ) {
                    Accessable accessable = (Accessable)metaAction.getAction();
                    accessCos.add(accessable);
                }
            }
        }
    }

    // Not very clever solution for sorting values.
    private int index(String name) {
        switch (name) {
            case "System":
                return 1;
            case "Kunden und Aufträge":
                return 2;
            case "Listings":
                return 3;
            case "Reparatur":
                return 4;
            case "Geschäftsführung":
                return 6;
            case "Lager/Logisitk":
                return 7;
            case "Artikelstamm":
                return 8;
            case "Hilfe":
                return 10000;
            default:
                return new Random().nextInt(9000) + 100;
        }
    }

    private JMenu buildLafMenu() {
        String active = UIManager.getLookAndFeel().getClass().getName();
        JMenu lafMenu = new JMenu("Look & Feel");
        ButtonGroup bg = new ButtonGroup();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            JRadioButtonMenuItem b = new JRadioButtonMenuItem(info.getName());
            b.addActionListener(new LafAction(info.getClassName()));
            bg.add(b);
            lafMenu.add(b);
            if ( info.getClassName().equals(active) ) {
                b.setSelected(true);
            }
        }
        try {
            Class.forName("org.pushingpixels.substance.api.skin.SkinInfo");
            for (SlafName slaf : substanceLaafs) {
                JRadioButtonMenuItem b = new JRadioButtonMenuItem(slaf.name);
                b.addActionListener(new LafAction(slaf.className));
                bg.add(b);
                lafMenu.add(b);
                if ( slaf.className.equals(active) ) {
                    b.setSelected(true);
                }
            }
        } catch (ClassNotFoundException ex) {
            L.info("Class SkinInfo not found, Substance Lafs are not installed.");
        }

        return lafMenu;
    }
}
