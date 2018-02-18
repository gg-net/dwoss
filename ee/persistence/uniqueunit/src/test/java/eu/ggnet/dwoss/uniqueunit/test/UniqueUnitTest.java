package eu.ggnet.dwoss.uniqueunit.test;

import org.junit.Test;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitTest {

    @Test
    public void history() {
        UniqueUnit uu = new UniqueUnit();
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        assertThat(uu)
                .returns("12345", UniqueUnit::getRefurbishId)
                .returns(1, u -> u.getHistory().size());

        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        assertThat(uu)
                .returns("12345", UniqueUnit::getRefurbishId)
                .returns(1, u -> u.getHistory().size());
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "54321");
        assertThat(uu)
                .returns("54321", UniqueUnit::getRefurbishId)
                .returns(2, u -> u.getHistory().size());
    }

}
