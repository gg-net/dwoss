package eu.ggnet.dwoss.rules.partno;

import eu.ggnet.dwoss.rules.AppleRules;

/**
 * Apple PartNos Support.
 * <p>
 * @author oliver.guenther
 */
public class ApplePartNoSupport implements PartNoSupport {

    @Override
    public boolean isValid(String partNo) {
        return AppleRules.validatePartNo(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        return AppleRules.validatePartNo(partNo);
    }

    @Override
    public String normalize(String partNo) {
        return partNo;
    }

}
