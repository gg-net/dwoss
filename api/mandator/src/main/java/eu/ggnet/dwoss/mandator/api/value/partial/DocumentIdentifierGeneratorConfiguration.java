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

import org.inferred.freebuilder.FreeBuilder;

/**
 * Configuation for the generator of document identifieres.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface DocumentIdentifierGeneratorConfiguration extends Serializable {

    class Builder extends DocumentIdentifierGeneratorConfiguration_Builder {
    };

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
         * 
         * @return the generated prefix
         */
        public abstract String generate();
    }

    /**
     * The pattern of the identifier, that contains {PREFIX} and {COUNTER}.
     * </>
     * e.g.: RS{PREFIX}_{COUNTER}
     *
     * @return the pattern
     */
    String pattern();

    /**
     * The PrefixType.
     *
     * @return the prefice type.
     */
    PrefixType prefixType();

    /**
     * The Format for the counter.
     *
     * @return the counter.
     */
    DecimalFormat counterFormat();

    /**
     * The initial value for the counter on reset, normaly the start of the year.
     *
     * @return the initial value for the counter on reset.
     */
    long initialValue();

    public static DocumentIdentifierGeneratorConfiguration create(String pattern, PrefixType type, DecimalFormat counterFormat, long initialValue) {
        return new DocumentIdentifierGeneratorConfiguration.Builder().pattern(pattern).prefixType(type).counterFormat(counterFormat).initialValue(initialValue).build();
    }

}
