package eu.ggnet.dwoss.uniqueunit.entity;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals(1, uu.getHistory().size());
        assertEquals("12345", uu.getRefurbishId());
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        assertEquals(1, uu.getHistory().size());
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "54321");
        assertEquals(2, uu.getHistory().size());
        assertEquals("54321", uu.getRefurbishId());
    }

}
