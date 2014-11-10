package eu.ggnet.dwoss.receipt.unit.chain;

import java.util.*;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.chain.date.DateNotNull;
import eu.ggnet.dwoss.receipt.unit.chain.date.InPast;
import eu.ggnet.dwoss.receipt.unit.chain.partno.MandatorAllowedPartNo;
import eu.ggnet.dwoss.receipt.unit.chain.partno.ProductSpecExists;
import eu.ggnet.dwoss.receipt.unit.chain.refurbishId.RefurbishIdMatchesContractor;
import eu.ggnet.dwoss.receipt.unit.chain.refurbishId.RefurbishIdNotExist;
import eu.ggnet.dwoss.receipt.unit.chain.serial.SerialAvailable;
import eu.ggnet.dwoss.receipt.unit.chain.string.NotEmpty;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;

/**
 * Minimal Chains for Auto selection mode.
 * <p/>
 * @author oliver.guenther
 */
public class AutoChains extends Chains {

    AutoChains() {
    }

    @Override
    public List<ChainLink<String>> newRefubishIdChain(TradeName contractor, UnitSupporter unitSupporter, boolean isEdit) {
        if ( isEdit ) {
            List<ChainLink<String>> result = new ArrayList<>();
            result.add(new NotEmpty());
            return result;
        }
        return Arrays.asList(
                new NotEmpty(),
                new RefurbishIdMatchesContractor(contractor), new RefurbishIdNotExist(unitSupporter));
    }

    @Override
    public List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbhisId) {
        return Arrays.asList(new NotEmpty(), new SerialAvailable(unitSupporter, editRefurbhisId));
    }

    @Override
    public List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        return Arrays.asList(new NotEmpty(), new ProductSpecExists(specAgent), new MandatorAllowedPartNo(specAgent, allowedBrands));
    }

    @Override
    public List<ChainLink<Date>> newMfgDateChain() {
        return Arrays.asList(new DateNotNull(), new InPast());
    }
}
