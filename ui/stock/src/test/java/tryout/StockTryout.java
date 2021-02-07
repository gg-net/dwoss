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
package tryout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.*;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.LocalDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.rights.api.User;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ui.ScrapResultController;
import eu.ggnet.dwoss.stock.ui.StockUpiImpl;
import eu.ggnet.dwoss.stock.ui.cap.*;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Swing;

import tryout.support.Stubs;

/**
 *
 * @author oliver.guenther
 */
public class StockTryout {

    public static void main(String[] args) {
        cdi();
    }

    public static void cdi() {
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(StockTryout.class);
        ci.addPackages(WidgetProducers.class);
        ci.addPackages(true, StockUpiImpl.class);
        ci.disableDiscovery();
        SeContainer container = ci.initialize();
        Instance<Object> instance = container.getBeanManager().createInstance();

        Saft saft = instance.select(Saft.class).get();
        saft.addOnShutdown(() -> container.close());
        UiCore.initGlobal(saft);

        RemoteDl remote = instance.select(RemoteDl.class).get();
        LocalDl local = instance.select(LocalDl.class).get();

        var stubs = new Stubs();
        remote.add(StockAgent.class, stubs.stockAgent());
        remote.add(StockApi.class, stubs.stockApi());
        remote.add(StockTransactionProcessor.class, stubs.stockTransactionProcessor());

        local.add(Guardian.class, new AbstractGuardian() {

            {
                setUserAndQuickLogin(new User.Builder().setUsername("Testuser").build(), 123);
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });

        JFrame f = UiUtil.startup(() -> {

            // Top
            JMenuBar menubar = new JMenuBar();
            JMenu stock = new JMenu("Lager/Logistik");
            menubar.add(stock);

            stock.add(instance.select(CreateSimpleAction.class).get());
            stock.add(instance.select(ScrapUnitsAction.class).get());
            stock.add(instance.select(DeleteUnitsAction.class).get());

            // Center
            DefaultListModel<String> model = new DefaultListModel<>();
            model.addAll(prepareHelper(stubs));
            JList<String> list = new JList<>(model);

            // Bottom
            JButton showScrapResult = new JButton("ScrapResult anzeigen");
            showScrapResult.addActionListener(e -> saft.build().fxml().show(() -> {
                return Arrays.asList(
                        new StockApi.Scraped.Builder().description("SopoNr 1234").successful(true).comment("Kommentar 1").build(),
                        new StockApi.Scraped.Builder().description("SopoNr 3333").successful(false).comment("Kommentar 2").build()
                );
            }, ScrapResultController.class));
            JButton close = new JButton("Applikation beenden");
            close.addActionListener(e -> saft.closeWindowOf(close));

            JPanel buttons = new JPanel(new FlowLayout());
            buttons.add(showScrapResult);
            buttons.add(close);

            JPanel p = new JPanel(new BorderLayout());
            p.add(menubar, BorderLayout.NORTH);
            p.add(new JScrollPane(list), BorderLayout.CENTER);
            p.add(buttons, BorderLayout.SOUTH);

            return p;

        });
        saft.core(Swing.class).initMain(f);
    }

    public static List<String> prepareHelper(Stubs stubs) {
        return stubs.stockAgent().findAll(StockUnit.class).stream()
                .map(su -> su.getRefurbishId() + " | " + su.getStock().getName() + " | " + (su.getLogicTransaction() == null ? "" : "auf LT"))
                .collect(Collectors.toList());
    }

}
