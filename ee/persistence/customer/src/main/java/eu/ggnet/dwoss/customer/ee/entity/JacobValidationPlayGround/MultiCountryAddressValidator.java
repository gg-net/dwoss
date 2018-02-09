/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.entity.JacobValidationPlayGround;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jacob.weinhold
 */
public class MultiCountryAddressValidator implements ConstraintValidator<ValidAddress, Address> {

    public void initialize(ValidAddress constraintAnnotation) {

    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext constraintValidatorContext) {

        Country country = address.getCountry();
        if ( country == null || country.getIso2() == null || address.getZipCode() == null ) {
            return true;
        }

        switch (country.getIso2()) {
            case "FR":
                if ( address.getZipCode().length() == 5 && StringUtils.isNumeric(address.getZipCode()) )
                    return true;// Check if address.getZipCode() is valid for France

            case "GB":
                if ( StringUtils.containsOnly(address.getZipCode(), " abcdefghijklmnopqrstuvwxyz0123456789")
                        && (address.getZipCode().length() > 5 && address.getZipCode().length() < 9) )
                    return true;// Check if address.getZipCode() is valid for Great Britain

        }
        return false;
    }
}
