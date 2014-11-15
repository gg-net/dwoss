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
