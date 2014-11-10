package eu.ggnet.dwoss.rules.partno;

import java.util.regex.Pattern;

/**
 * OttoPartNoSupport.
 * <p>
 * @author oliver.guenther
 */
public class OttoPartNoSupport implements PartNoSupport {

    @Override
    public boolean isValid(String partNo) {
        return violationMessages(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        if ( partNo == null ) return "PartNo is null";
        if ( !Pattern.matches("[0-9]{3}.[0-9]{3}", partNo) ) return "PartNo " + partNo + " does not match the Pattern [0-9]{3}.[0-9]{3}";
        return null;
    }

    @Override
    public String normalize(String partNo) {
        if ( partNo != null && Pattern.matches("[0-9]{6}", partNo) ) { // Rebuild
            return partNo.substring(0, 3) + "." + partNo.substring(3);
        }
        return partNo;
    }

}
