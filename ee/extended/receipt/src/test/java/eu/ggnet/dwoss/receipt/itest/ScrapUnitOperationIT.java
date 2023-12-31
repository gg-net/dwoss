package eu.ggnet.dwoss.receipt.itest;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.api.StockApi.Scraped;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnitHistory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of Scrap over stock, uniqueunit and redtape.
 *
 * @author oliver.guenther
 */
// TODO: This is a test of multiple persitence layers. move later to the supertestproject.
@RunWith(Arquillian.class)
public class ScrapUnitOperationIT extends ArquillianProjectArchive {

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @EJB
    private StockApi api;

    @EJB
    private UniqueUnitAgent uuAgent;

    @EJB
    private RedTapeAgent rtAgent;

    @Test
    public void scrap() throws UserInfoException {
        var uus = receiptGenerator.makeUniqueUnits(5, true, true);
        // HINT: If this test fails, that probally the units are not scrapable.
        // TODO: Enhance test, so that fails would be much more vissible.

        List<Long> suids = uus.stream().map(uu -> api.findByUniqueUnitId(uu.getId())).map(ssu -> ssu.id()).collect(Collectors.toList());
        List<Scraped> scraps = api.scrap(suids, "Integration test scrap", "Testuser");

        // Verify, that the ScrapEvent creates historys on the unique unit. See UniqueUnitEventObserver
        for (Scraped scrap : scraps) {
            UniqueUnit uu = uuAgent.findByIdEager(UniqueUnit.class, (int)scrap.uniqueUnitId());
            assertThat(uu.getHistory().stream().map(UniqueUnitHistory::getComment).anyMatch(s -> s.contains("Verschrottung")))
                    .as("UniqueUnit(" + uu.getId() + ").history contains Verschrottung").isTrue();
        }

        // Verify, that the ScrapEvent create dossiers on the scrapcustomers. See RedTapeEventObserver
        List<Dossier> dossiers = rtAgent.findAllEager(Dossier.class);

        assertThat(dossiers.stream()
                .flatMap(d -> d.getActiveDocuments(DocumentType.BLOCK).stream())
                .flatMap(d -> d.getPositions(PositionType.UNIT).values().stream())
                .map(Position::getUniqueUnitId).collect(Collectors.toList())
        ).as("All UniqueUnit.ids on the Documents")
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(scraps.stream().mapToInt(s -> (int)s.uniqueUnitId()).boxed().collect(Collectors.toList()));

        assertThat(dossiers.stream()
                .flatMap(d -> d.getActiveDocuments(DocumentType.BLOCK).stream())
                .flatMap(d -> d.getPositions(PositionType.COMMENT).values().stream())
                .allMatch(p -> p.getName().equals("Verschrottung"))).as("All Dossiers should contain the comment Verschrottung").isTrue();

    }

}
