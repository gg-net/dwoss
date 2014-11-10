package eu.ggnet.dwoss.rules.partno;

import java.util.regex.Pattern;

/**
 *
 * @author bastian.venz
 */
public class LenovoPartNoSupport implements PartNoSupport {

    private static final String SHORT_PATTERN = "[0-9]{7}";

    private static final String LONG_PATTERN = "[a-zA-Z]{2}[0-9]{8}";

    @Override
    public boolean isValid(String partNo) {
        return violationMessages(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        if ( !(Pattern.matches(LONG_PATTERN, partNo) || Pattern.matches(SHORT_PATTERN, partNo)) ) {
            return "Part No Don't match patterns " + SHORT_PATTERN + " or " + LONG_PATTERN;
        }
        return null;
    }

    @Override
    public String normalize(String partNo) {
        if ( Pattern.matches(SHORT_PATTERN, partNo) && !Pattern.matches(LONG_PATTERN, partNo) ) {
            partNo = "NB0" + partNo;
        }
        return partNo;
    }
}
