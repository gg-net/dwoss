package eu.ggnet.dwoss.receipt.unit.chain;

import java.util.*;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.chain.partno.*;
import eu.ggnet.dwoss.receipt.unit.chain.serial.SerialAvailable;
import eu.ggnet.dwoss.receipt.unit.chain.serial.SerialWasOnceInStock;
import eu.ggnet.dwoss.receipt.unit.chain.string.*;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;

/**
 * The Chains for Lenovo.
 * <p/>
 * @author bastian.venz
 */
public class LenovoChains extends AcerChains {

    @Override
    public List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbhisId) {
        return Arrays.asList(
                new NotEmpty(), new Trim(), new ToUpperCase(),
                new SerialAvailable(unitSupporter, editRefurbhisId),
                new SerialWasOnceInStock(unitSupporter, editRefurbhisId));
    }

    @Override
    public List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        return Arrays.asList(new NotEmpty(), new Trim(), new ToUpperCase(),
                new ValidLenovoPartNo(), new ProductSpecExists(specAgent), new MandatorAllowedPartNo(specAgent, allowedBrands));
    }
}
