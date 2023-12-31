package itest;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.search.ee.Searcher;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the Searcher, very bad solution.
 *
 * @author Oliver Günther
 */
@RunWith(Arquillian.class)
public class SearcherOperationIT extends ArquillianProjectArchive {

    @EJB
    private Searcher searcher;

    /**
     * Very bad solution for the searcher test.
     * The SearcherOperation only discovers all defined SearchProvider and uses them together. This test makes use of the knowledge of the implemented
     * SearchProviders for UniqueUnit and Customer and their internal work. If the providers ever change in the way they search, this test will also fail.
     * TODO: create an extra test, that uses multiple testing implementations of searchproviders. So we can see if a broken test results from the Searcher or
     * the SearchProviders.
     *
     * @throws Exception if transaction fails
     */
    @Test
    public void testGlobalSearch() throws Exception {

        /*
        Simulation: 2 Providerstubs.
        1-2 letters no result
        3-6 letters 10 results from first provider
        5-10 letters 10 results from second provider
         */
        // Testing Search via Provider
//        assertThat(searcher).as("Searchprovider").isNotNull();
//        SearchRequest req = new SearchRequest("schlag*");
//        searcher.initSearch(req);
//
//        while (searcher.hasNext()) {
//            System.out.println(searcher.next());
//        }
        searcher.initSearch(new SearchRequest("12345"));
        assertThat(searcher.estimateMaxResults()).as("estimatedMax").isEqualTo(20);
        List<ShortSearchResult> allResult = new ArrayList<>();
        while (searcher.hasNext()) {
            allResult.addAll(searcher.next());
        }

        assertThat(allResult).as("12345").extracting(ssr -> ssr.key).
                contains(
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, 1),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, 2),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, 3),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, 1),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, 2),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, 3)
                );

    }

}
