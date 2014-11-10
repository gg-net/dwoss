package eu.ggnet.dwoss.receipt.unit.chain;

import java.util.*;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.chain.partno.*;
import eu.ggnet.dwoss.receipt.unit.chain.serial.*;
import eu.ggnet.dwoss.receipt.unit.chain.string.*;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;

/**
 * The Chains for Apple.
 * <p/>
 * @author oliver.guenther
 */
public class AppleChains extends AcerChains {

    @Override
    public List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbhisId) {
        return Arrays.asList(
                new NotEmpty(), new Trim(), new ToUpperCase(), new RemoveIfStartsWith("S"),
                new ValidAppleSerial(), new SerialAvailable(unitSupporter, editRefurbhisId), new AppleSerialToPartNoAndMfgDate(),
                new SerialWasOnceInStock(unitSupporter, editRefurbhisId));
    }

    @Override
    public List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        return Arrays.asList(new NotEmpty(), new Trim(), new ToUpperCase(),
                new RemoveIfStartsWith("1P"), new ValidApplePartNo(),
                new ProductSpecExists(specAgent), new MandatorAllowedPartNo(specAgent, allowedBrands));
    }

}
