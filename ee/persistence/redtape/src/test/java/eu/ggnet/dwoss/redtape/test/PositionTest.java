package eu.ggnet.dwoss.redtape.test;

import java.util.Set;

import javax.validation.*;

import org.junit.Test;

import eu.ggnet.dwoss.common.ee.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.entity.Position.DefaultUi;
import eu.ggnet.dwoss.redtape.ee.entity.Position.Returns;
import eu.ggnet.dwoss.redtape.ee.entity.PositionModifier;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau, oliver.guenther
 */
public class PositionTest {

    private final static Validator V = Validation.buildDefaultValidatorFactory().getValidator();

    Ledger L1 = new Ledger(1111, "DemoLedger 1");

    Ledger L2 = new Ledger(2222, "DemoLedger 2");

    @Test
    public void testEqualsContentBug() {

        Position p1 = Position.builder().type(PositionType.UNIT).name("SopoNr.: 9 Seriennummer: CCCCCCCCCC12011778EEEE")
                .price(1200).amount(1).tax(0.19)
                .description("Display: 21.5\" (54,61 cm), Crystal Bright, Full HD (1920x1080), 16:9, Farbe: schwarz, Videokonnektor(en) : VGA, DVI")
                .bookingAccount(L1).uniqueUnitId(9).build();

        Position p2 = Position.builder().type(PositionType.UNIT).name("SopoNr.: 9 Seriennummer: CCCCCCCCCC12011778EEEE")
                .price(1200).amount(1).tax(0.19)
                .description("Display: 21.5\" (54,61 cm), Crystal Bright, Full HD (1920x1080), 16:9, Farbe: schwarz, Videokonnektor(en) : VGA, DVI")
                .bookingAccount(L1).uniqueUnitId(9).build();

        assertTrue(p1.equalsContent(p2));
    }

    @Test
    public void testEqualsContent() {

        Position posUnit = Position.builder().amount(1).type(PositionType.UNIT).bookingAccount(L1).name("TestUnit").tax(GlobalConfig.DEFAULT_TAX.getTax()).price(50.0).description("TestUnit Description").uniqueUnitId(1).build();

        //copy and equality test
        Position posCopy = posUnit.partialClone();
        assertTrue(posCopy.equalsContent(posUnit));

        //id equality test
        PositionModifier.setId(posCopy, 2);
        assertFalse(posCopy.equalsContent(posUnit));
        PositionModifier.setId(posCopy, 0);
        assertTrue(posCopy.equalsContent(posUnit));

        //type eqality test
        posCopy.setType(PositionType.COMMENT);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setType(posUnit.getType());
        assertTrue(posCopy.equalsContent(posUnit));

        //bookingAccount equality test
        posCopy.setBookingAccount(L2);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setBookingAccount(posUnit.getBookingAccount().get());
        assertTrue(posCopy.equalsContent(posUnit));

        //name equality test
        posCopy.setName("Anderer Name");
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setName(posUnit.getName());
        assertTrue(posCopy.equalsContent(posUnit));

        //tax equality test
        posCopy.setTax(1.5);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setTax(posUnit.getTax());
        assertTrue(posCopy.equalsContent(posUnit));

        //price equality test
        posCopy.setPrice(25.5);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setPrice(posUnit.getPrice());
        assertTrue(posCopy.equalsContent(posUnit));

        //description equality test
        posCopy.setDescription("Neue Despcription");
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setDescription(posUnit.getDescription());
        assertTrue(posCopy.equalsContent(posUnit));

        //unitId equality test
        posCopy.setUniqueUnitId(2);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setUniqueUnitId(posUnit.getUniqueUnitId());
        assertTrue(posCopy.equalsContent(posUnit));

        //document equality test
        PositionModifier.setDocument(posCopy, new Document());
        assertFalse(posCopy.equalsContent(posUnit));
        PositionModifier.setDocument(posCopy, null);
        assertTrue(posCopy.equalsContent(posUnit));

        //ammount equality test
        posCopy.setAmount(3.);
        assertFalse(posCopy.equalsContent(posUnit));
        posCopy.setAmount(posUnit.getAmount());
        assertTrue(posCopy.equalsContent(posUnit));
    }

    @Test
    public void testDefaultValidation() {
        Position p = new Position();
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        Document doc = new Document();
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("Hans", "Info"));
        doc.append(p);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setType(PositionType.UNIT);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setPrice(1);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setAmount(1);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setTax(1);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setUniqueUnitId(1);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setUniqueUnitProductId(1);
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setName("Name");
        assertFalse("Position must be invalid.", V.validate(p).isEmpty());
        p.setDescription("Blub");
        assertTrue("Position must be valid! " + ConstraintViolationFormater.toSingleLine(V.validate(p)), V.validate(p).isEmpty());
    }

    //<editor-fold defaultstate="collapsed" desc="Test Positions with Validation Group DefaultUi">
    @Test
    public void testDefaultUiUnitValid() {
        Position p = makeValidDefaultUiUnit();
        Set<ConstraintViolation<Position>> violations = V.validate(p, DefaultUi.class);
        assertTrue("Position must be valid for group " + DefaultUi.class + ", but is not. "
                + ConstraintViolationFormater.toSingleLine(violations), violations.isEmpty());
        violations = V.validate(p);
        assertFalse("Position must be invalid for group default, but is valid.", violations.isEmpty());
    }

    @Test
    public void testDefaultUiServiceValid() {
        Position p = makeValidDefaultUiService();
        Set<ConstraintViolation<Position>> violations = V.validate(p, DefaultUi.class);
        assertTrue("Position must be valid for group " + DefaultUi.class + ", but is not. "
                + ConstraintViolationFormater.toSingleLine(violations), violations.isEmpty());
        violations = V.validate(p);
        assertFalse("Position must be invalid for group default, but is valid.", violations.isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidPriceZero() {
        Position p = makeValidDefaultUiUnit();
        p.setPrice(0);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidAmount() {
        Position p = makeValidDefaultUiUnit();
        p.setAmount(2);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidUniqueUnitId() {
        Position p = makeValidDefaultUiUnit();
        p.setUniqueUnitId(0);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidProductId() {
        Position p = makeValidDefaultUiUnit();
        p.setUniqueUnitProductId(0);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiServiceInvalidPriceZero() {
        Position p = makeValidDefaultUiService();
        p.setPrice(0);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiServiceInvalidAmount() {
        Position p = makeValidDefaultUiService();
        p.setAmount(0);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidName() {
        Position p = makeValidDefaultUiUnit();
        p.setName(null);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    @Test
    public void testDefaultUiUnitInvalidDescription() {
        Position p = makeValidDefaultUiUnit();
        p.setDescription(null);
        assertFalse("Position must be invalid for group " + DefaultUi.class + ", but is valid.", V.validate(p, DefaultUi.class).isEmpty());
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Test Positions with Validation Group Returns">
    @Test
    public void testReturnsUnitValid() {
        Position p = makeValidReturnsUnit();
        Set<ConstraintViolation<Position>> violations = V.validate(p, Returns.class);
        assertTrue("Position must be valid for group " + Returns.class + ", but is not. "
                + ConstraintViolationFormater.toSingleLine(violations), violations.isEmpty());
        violations = V.validate(p);
        assertFalse("Position must be invalid for group default, but is valid.", violations.isEmpty());
    }

    @Test
    public void testReturnsServiceValid() {
        Position p = makeValidReturnsService();
        Set<ConstraintViolation<Position>> violations = V.validate(p, Returns.class);
        assertTrue("Position must be valid for group " + Returns.class + ", but is not. "
                + ConstraintViolationFormater.toSingleLine(violations), violations.isEmpty());
        violations = V.validate(p);
        assertFalse("Position must be invalid for group default, but is valid.", violations.isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidPriceNotZero() {
        Position p = makeValidReturnsUnit();
        p.setPrice(1);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidAmount() {
        Position p = makeValidReturnsUnit();
        p.setAmount(2);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidUniqueUnitId() {
        Position p = makeValidReturnsUnit();
        p.setUniqueUnitId(0);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidProductId() {
        Position p = makeValidReturnsUnit();
        p.setUniqueUnitProductId(0);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsServiceInvalidPriceZero() {
        Position p = makeValidReturnsService();
        p.setPrice(0);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsServiceInvalidTax() {
        Position p = makeValidReturnsService();
        p.setTax(0);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsServiceInvalidAmount() {
        Position p = makeValidReturnsService();
        p.setAmount(0);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidName() {
        Position p = makeValidReturnsUnit();
        p.setName(null);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }

    @Test
    public void testReturnsUnitInvalidDescription() {
        Position p = makeValidReturnsUnit();
        p.setDescription(null);
        assertFalse("Position must be invalid for group " + Returns.class + ", but is valid.", V.validate(p, Returns.class).isEmpty());
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Make Positions">

    private Position makeValidDefaultUiUnit() {
        Position p = new Position();
        p.setType(PositionType.UNIT);
        p.setPrice(1);
        p.setAmount(1);
        p.setTax(1);
        p.setUniqueUnitId(1);
        p.setUniqueUnitProductId(1);
        p.setName("Name");
        p.setDescription("Blub");
        return p;
    }

    private Position makeValidDefaultUiService() {
        Position p = new Position();
        p.setType(PositionType.SERVICE);
        p.setPrice(1);
        p.setAmount(1);
        p.setTax(1);
        p.setName("Name");
        p.setDescription("Blub");
        return p;
    }

    private Position makeValidReturnsUnit() {
        Position p = new Position();
        p.setType(PositionType.UNIT);
        p.setPrice(0);
        p.setAmount(1);
        p.setTax(0);
        p.setUniqueUnitId(1);
        p.setUniqueUnitProductId(1);
        p.setName("Name");
        p.setDescription("Blub");
        return p;
    }

    private Position makeValidReturnsService() {
        Position p = new Position();
        p.setType(PositionType.SERVICE);
        p.setPrice(1);
        p.setAmount(1);
        p.setTax(1);
        p.setName("Name");
        p.setDescription("Blub");
        return p;
    }
    //</editor-fold>
}
