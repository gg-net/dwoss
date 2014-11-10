package eu.ggnet.dwoss.receipt.unit.chain.refurbishId;

import java.util.Objects;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.mandator.api.service.MandatorService;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.TradeName;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class RefurbishIdMatchesContractor implements ChainLink<String> {

    private final TradeName contractor;

    public RefurbishIdMatchesContractor(TradeName contractor) {
        this.contractor = Objects.requireNonNull(contractor, "Contractor must not be null");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        if ( !Client.hasFound(MandatorService.class) )
            return new ChainLink.Result<>(value, ValidationStatus.WARNING, "Kein MandatorService");
        if ( Client.lookup(MandatorService.class).isAllowedRefurbishId(contractor, value) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, "SopoNr ist nicht für Lieferant " + contractor.getName() + " zulässig");
    }
}
