package eu.ggnet.dwoss.receipt.unit.chain.partno;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.ProductSpec;

/**
 * Validates the PartNo by Bean Validations of the Property.
 * <p/>
 * @author oliver.guenther
 */
public class ProductSpecMatches implements ChainLink<String> {

    private final SpecAgent specAgent;

    private final TradeName mustBrand;

    private final ProductGroup mustGroup;

    public ProductSpecMatches(SpecAgent specAgent, TradeName brand, ProductGroup group) {
        this.specAgent = Objects.requireNonNull(specAgent, "SpecAgent must not be null");
        this.mustBrand = brand;
        this.mustGroup = group;
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        ProductSpec spec = specAgent.findProductSpecByPartNoEager(value);
        if ( spec == null ) return new ChainLink.Result<>(value, ValidationStatus.ERROR, "ProductSpec existiert noch nicht, bitte anlegen");
        TradeName isBrand = spec.getModel().getFamily().getSeries().getBrand();
        if ( isBrand != mustBrand ) return new ChainLink.Result<>(value, ValidationStatus.ERROR,
                    "ProductSpec ist von der Marke " + isBrand.getName() + ", muss aber von der Marke " + mustBrand.getName() + " sein");
        ProductGroup isGroup = spec.getModel().getFamily().getSeries().getGroup();
        if ( isGroup != mustGroup ) return new ChainLink.Result<>(value, ValidationStatus.ERROR,
                    "ProductSpec ist aus der Warengruppe " + isGroup.getName() + ", muss aber aus der Warengruppe " + mustGroup.getName() + " sein");
        return new ChainLink.Result<>(value);
    }
}
