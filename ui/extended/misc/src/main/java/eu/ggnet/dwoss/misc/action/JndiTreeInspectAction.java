/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.misc.action;

import eu.ggnet.saft.core.Client;

import java.awt.event.ActionEvent;
import java.util.*;

import javax.naming.Context;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.*;

import static eu.ggnet.saft.core.Client.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author oliver.guenther
 */
public class JndiTreeInspectAction extends AbstractAction {

    private final static Logger L = LoggerFactory.getLogger(JndiTreeInspectAction.class);

    public JndiTreeInspectAction() {
        super("Reinspect JndiTree");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Context context = Client.context(JndiTreeInspectAction.class.getName());
        NavigableMap<String, NavigableSet<String>> cache = new TreeMap<>();

        L.info("Running Jndi Tree inspection on Suffix: ''");
        inspectJndiTree(context, "", cache); // Not existing in Local Environment
        L.info("Running Jndi Tree inspection on Suffix: 'java:global'");
        inspectJndiTree(context, "java:global", cache);
        L.info("Running Jndi Tree inspection on Suffix: 'java:module'");
        inspectJndiTree(context, "java:module", cache); // Olli added, Not existing in Local Environment
        L.info("Running Jndi Tree inspection on Suffix: 'java:app'");
        inspectJndiTree(context, "java:app", cache); // Olli added, Not existing in Local Environment
        L.info("Running Jndi Tree Module Name inspection");

        String collect = cache.entrySet().stream().map(e -> " - " + e.getKey() + " : " + e.getValue()).collect(joining("\n"));
        NavigableSet<String> modules = inspectJndiTreeForModuleNames(context);
        String m = modules.stream().collect(joining("\n"));
        JOptionPane.showMessageDialog(null, "Tree:\n" + collect + "\nModules:\n" + m);
    }
}
