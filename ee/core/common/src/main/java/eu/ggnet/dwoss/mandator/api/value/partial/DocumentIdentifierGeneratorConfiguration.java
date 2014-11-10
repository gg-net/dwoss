package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

/**
 * A DocumentIdentifierGeneratorConfiguration
 * <p/>
 * @author oliver.guenther
 */
@Data
public class DocumentIdentifierGeneratorConfiguration implements Serializable {

    public static String VAR_PREFIX = "{PREFIX}";

    public static String VAR_COUNTER = "{COUNTER}";

    /**
     * A Selective Enum for the Prefix Generator.
     */
    public enum PrefixType {

        /**
         * Generates a Prefix, that consists of the last two digits of the actual year.
         */
        YY {
                    @Override
                    public String generate() {
                        return new SimpleDateFormat("yy").format(new Date());
                    }
                },
        /**
         * Generates a Prefix, that consists of the full year. E.g. 2014.
         */
        YYYY {
                    @Override
                    public String generate() {
                        return new SimpleDateFormat("yyyy").format(new Date());
                    }
                },
        /**
         * Generates a prefix, that is allways one (1).
         */
        ONE {

                    @Override
                    public String generate() {
                        return "1";
                    }

                };

        /**
         * Returns the generated prefix.
         * <p/>
         * @return the generated prefix
         */
        public abstract String generate();
    }

    /**
     * The pattern of the identifier, that contains {PREFIX} and {COUNTER}.
     * <p/>
     * e.g.: RS{PREFIX}_{COUNTER}
     */
    private final String pattern;

    /**
     * The PrefixType.
     */
    private final PrefixType prefixType;

    /**
     * The Format for the counter.
     */
    private final DecimalFormat counterFormat;
}
