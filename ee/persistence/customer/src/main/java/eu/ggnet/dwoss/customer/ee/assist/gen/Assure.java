/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.assist.gen;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Flags for the generator instead of a million extra methods.
 */
@FreeBuilder
public interface Assure {

    class Builder extends Assure_Builder {

        @SuppressWarnings("OverridableMethodCallInConstructor")
        public Builder() {
            simple(false);
            business(false);
            consumer(false);
            useResellerListEmailCommunication(false);
            emailDomain("example.local");
        }

    };

    /**
     * Shortcut for new Builder().build();
     *
     * @return defaults.
     */
    static Assure defaults() {
        return new Builder().build();
    }

    /**
     * Indicates, that only simple customers must be generated, defaults to false.
     *
     * @return simple
     */
    boolean simple();

    /**
     * Indicates, that only business customers must be generated, defaults to false.
     *
     * @return business
     */
    boolean business();

    /**
     * Indicates, that only consumer customers must be generated, defaults to false.
     *
     * @return consumer
     */
    boolean consumer();

    /**
     * Indicates, that all customers generated have a reseller list email communication set, default to false.
     * False does not mean, there will never be a reseller list email communication set, but only, that there might be.
     *
     * @return useResellerListEmailCommunication
     */
    boolean useResellerListEmailCommunication();

    /**
     * All generated emails will be of the supplied string as domain, defaults to example.local.
     * This is useful if you can configure a domain with a catch all to test all mailing live.
     *
     * @return the emailDoamin
     */
    String emailDomain();

    /**
     * For all supplied strings, metadata will be generated. Null or empty indicates no metadata generation.
     *
     * @return mandator metadata matchcodes
     */
    List<String> mandatorMetadataMatchCodes();

}
