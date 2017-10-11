/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    /**
     * ToString HTML representation.
     *
     * @return HTML view of the DocumentIdentifierGeneratorConfiguration.
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder("DocumentIdentifierGeneratorConfiguration: ");
        sb.append("<ul>");
        sb.append("<li>");
        sb.append("<b>VAR_PREFIX: </b>");
        sb.append(VAR_PREFIX);
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>VAR_COUNTER: </b>");
        sb.append(VAR_COUNTER);
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>Pattern: </b>");
        sb.append(pattern);
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>This PrefixTypes are available: </b>");
        sb.append("<ul>");
        for (PrefixType value : PrefixType.values()) {
            sb.append("<li>");
            sb.append(value.generate());
            sb.append("</li>");
        }
        sb.append("</ul>");
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>Set PrefixType: </b>");
        sb.append(prefixType.generate());
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>counterFormat: </b>");
        sb.append(counterFormat);
        sb.append("</li>");
        sb.append("</ul>");

        return sb.toString();
    }

    /**
     * toHtmlSingleLine HTML representation.
     *
     * @return HTML on a Singleline view of the DocumentIdentifierGeneratorConfiguration.
     */
    public String toHtmlSingleLine() {
        StringBuilder sb = new StringBuilder("DocumentIdentifierGeneratorConfiguration: ");
        sb.append("<ul>");
        
        sb.append("<li>");
        sb.append("<b>VAR_PREFIX: </b>");
        sb.append(VAR_PREFIX);
        sb.append("</li>");

        sb.append("<li>");
        sb.append("<b>VAR_COUNTER: </b>");
        sb.append(VAR_COUNTER);
        sb.append("</li>");

        sb.append("</ul>");

        return sb.toString();
    }
}
