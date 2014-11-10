package eu.ggnet.dwoss.receipt.unit.chain.refurbishId;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class RefurbishIdNotExist implements ChainLink<String> {

    private final UnitSupporter unitSupporter;

    public RefurbishIdNotExist(UnitSupporter refurbishIdSupporter) {
        this.unitSupporter = Objects.requireNonNull(refurbishIdSupporter, UnitSupporter.class.getSimpleName() + " is requiered");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        if ( unitSupporter.isRefurbishIdAvailable(value) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, "SopoNr ist nicht verfügbar");
    }
}
