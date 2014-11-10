package eu.ggnet.dwoss.receipt.unit.chain.partno;

import java.util.Objects;
import java.util.Set;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;
import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.ProductSpec;

/**
 * Validates the PartNo is of a Brand that the Mandator may sale.
 * <p/>
 * @author oliver.guenther
 */
public class MandatorAllowedPartNo implements ChainLink<String> {

    private final SpecAgent specAgent;

    private final Set<TradeName> allowedBrands;

    public MandatorAllowedPartNo(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        this.specAgent = Objects.requireNonNull(specAgent, "SpecAgent must not be null");
        this.allowedBrands = Objects.requireNonNull(allowedBrands, "Mandator must not be null");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        ProductSpec spec = specAgent.findProductSpecByPartNoEager(value);
        if ( spec == null || allowedBrands.contains(spec.getModel().getFamily().getSeries().getBrand()) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, "Mandant darf keine Geräte der Marke " + spec.getModel().getFamily().getSeries().getBrand().getName() + " verkaufen");
    }
}
