package eu.ggnet.dwoss.receipt.unit.chain.serial;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class SerialWasOnceInStock implements ChainLink<String> {

    private final UnitSupporter unitSupporter;

    private final String editRefurbishId;
    // Add the RefurbihsId on edit, to not tell something known.

    public SerialWasOnceInStock(UnitSupporter unitSupporter, String editRefurbishId) {
        this.unitSupporter = Objects.requireNonNull(unitSupporter, UnitSupporter.class.getSimpleName() + " is requiered");
        this.editRefurbishId = editRefurbishId;
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        String oldRefurbishId = unitSupporter.findRefurbishIdBySerial(value);
        if ( oldRefurbishId == null || oldRefurbishId.equals(editRefurbishId) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.WARNING, "Seriennummer war schon mal da, letzte SopoNr: " + oldRefurbishId);
    }
}
