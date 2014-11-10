package eu.ggnet.dwoss.receipt.unit.chain.partno;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.ProductSpec;

/**
 * Validates the PartNo by Bean Validations of the Property.
 * <p/>
 * @author oliver.guenther
 */
public class ProductSpecExists implements ChainLink<String> {

    private final SpecAgent specAgent;

    public ProductSpecExists(SpecAgent specAgent) {
        this.specAgent = Objects.requireNonNull(specAgent, "SpecAgent must not be null");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        ProductSpec spec = specAgent.findProductSpecByPartNoEager(value);
        if ( spec == null ) return new ChainLink.Result<>(value, ValidationStatus.ERROR, "ProductSpec existiert noch nicht, bitte anlegen");
        return new ChainLink.Result<>(value);
    }
}
