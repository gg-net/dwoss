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
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.SearchPane;
import eu.ggnet.dwoss.search.api.SearchResult;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.saft.core.Client;

/**
 *
 * @author bastian.venz
 */
public class SearchTryout {

    private final static class Result implements SearchResult {

        private final String shortDescription;

        public Result(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        @Override
        public String shortDescription() {
            return shortDescription;
        }

        @Override
        public String toString() {
            return "Result{" + "shortDescription=" + shortDescription + '}';
        }

    }

    @Test
    public void runTryout() throws InterruptedException {
        Client.addSampleStub(Searcher.class, new Searcher() {

            private final Logger LOG = LoggerFactory.getLogger(Searcher.class);

            private String activeSearch;

            private int count = 0;

            @Override
            public void initSearch(String search) {
                LOG.info("Search inited with " + search + ", resetting count");
                activeSearch = search;
                count = 0;
            }

            @Override
            public List<SearchResult> next() {
                LOG.info("Next called (search={}, count={})", activeSearch, count);
                // return with a window of 3 untill count >= 0;
                if ( count >= 10 ) return Collections.EMPTY_LIST;
                try {
                    Thread.sleep(2000); // Intensive search
                } catch (InterruptedException ex) {
                    // Ignore
                }
                return Arrays.asList(
                        new Result(activeSearch + " result " + (count++)),
                        new Result(activeSearch + " result " + (count++)),
                        new Result(activeSearch + " result " + (count++))
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
        });

        new JFXPanel();    // Implicit start the platform

        CountDownLatch count = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("TestSearch");
            SearchPane search = new SearchPane();
            Scene scene = new Scene(search, Color.ALICEBLUE);
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                count.countDown();
            });
            stage.show();
        });

        count.await();

    }
}
