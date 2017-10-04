package eu.ggnet.dwoss.uniqueunit.test;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import static eu.ggnet.dwoss.uniqueunit.entity.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitTest {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void history() {
        UniqueUnit uu = new UniqueUnit();
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        assertThat(uu)
                .hasRefurbishId("12345")
                .returns(1, u -> u.getHistory().size());

        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        assertThat(uu)
                .hasRefurbishId("12345")
                .returns(1, u -> u.getHistory().size());
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "54321");
        assertThat(uu)
                .hasRefurbishId("54321")
                .returns(2, u -> u.getHistory().size());
    }

}
