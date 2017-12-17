/*
 * Copyright (C) 2014 bastian.venz
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

import java.util.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.ui.SearchCask;
import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.fx.FxSaft;

/**
 *
 * @author bastian.venz
 */
public class SearchTryout {

    @Test
    public void runTryout() throws InterruptedException {
        Client.addSampleStub(Searcher.class, new Searcher() {

            private final Logger LOG = LoggerFactory.getLogger(Searcher.class);

            private SearchRequest activeSearch;

            private int count = 0;

            @Override
            public void initSearch(SearchRequest search) {
                LOG.info("Search inited with " + search + ", resetting count");
                activeSearch = search;
                count = 0;
            }

            @Override
            public List<ShortSearchResult> next() {
                LOG.info("Next called (search={}, count={})", activeSearch, count);
                // return with a window of 3 untill count >= 0;
                if ( count >= 10 ) return Collections.EMPTY_LIST;
                if ( count > 0 ) {
                    try {
                        Thread.sleep(1000); // Intensive search
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }
                return Arrays.asList(
                        new ShortSearchResult(new GlobalKey(Component.CUSTOMER, count++), "A interesting result based on " + activeSearch),
                        new ShortSearchResult(new GlobalKey(Component.CUSTOMER, count++), "A interesting result based on " + activeSearch),
                        new ShortSearchResult(new GlobalKey(Component.CUSTOMER, count++), "A interesting result based on " + activeSearch)
                );
            }

            @Override
            public boolean hasNext() {
                return count < 10;
            }

            @Override
            public int estimateMaxResults() {
                return 10;
            }

            @Override
            public String details(GlobalKey key) {
                return "<b> Details of " + key + "</b>";
            }
        });
        JFXPanel panel = new JFXPanel();
        panel.setScene(new Scene(new Label("UiCore Main Panel")));
        panel.setSize(100, 100);

        UiCore.startSwing(() -> panel);

        FxSaft.dispatch(() -> {
            Stage stage = new Stage();
            stage.setTitle("TestSearch");
            SearchCask search = new SearchCask();
            Scene scene = new Scene(search, Color.ALICEBLUE);
            stage.setScene(scene);
            stage.showAndWait();
            return null;
        });

    }
}
