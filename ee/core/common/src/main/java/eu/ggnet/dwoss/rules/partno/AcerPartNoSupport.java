package eu.ggnet.dwoss.rules.partno;

import eu.ggnet.dwoss.rules.AcerRules;

/**
 * Support for Acer PartNos.
 * <p>
 * @author oliver.guenther
 */
public class AcerPartNoSupport implements PartNoSupport {

    @Override
    public boolean isValid(String partNo) {
        return AcerRules.validatePartNo(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        return AcerRules.validatePartNo(partNo);
    }

    @Override
    public String normalize(String partNo) {
        return partNo;
    }

}
