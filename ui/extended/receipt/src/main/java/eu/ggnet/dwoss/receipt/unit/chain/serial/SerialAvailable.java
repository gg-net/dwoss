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
public class SerialAvailable implements ChainLink<String> {

    // Add the RefurbishId to allow one allready take Serial.
    private final UnitSupporter unitSupporter;

    private final String editRefurbihsId;

    public SerialAvailable(UnitSupporter refurbishIdSupporter, String editRefurbishId) {
        this.unitSupporter = Objects.requireNonNull(refurbishIdSupporter, UnitSupporter.class.getSimpleName() + " is requiered");
        this.editRefurbihsId = editRefurbishId;
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        if ( !unitSupporter.isSerialAvailable(value) ) {
            if ( editRefurbihsId != null && editRefurbihsId.equals(unitSupporter.findRefurbishIdBySerial(value)) ) {
                // Edit Mode and this is the same unit.
                return new ChainLink.Result<>(value);
            } else { // Both modes, but in edit not the same Unit.
                String refrubhishId = unitSupporter.findRefurbishIdBySerial(value);
                return new ChainLink.Result<>(value, ValidationStatus.ERROR, "Seriennummer ist nicht verfügbar, noch im Lager mit SopoNr " + refrubhishId);
            }
        }
        return new ChainLink.Result<>(value);
    }
}
