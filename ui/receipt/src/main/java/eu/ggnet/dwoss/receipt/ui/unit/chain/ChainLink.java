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
package eu.ggnet.dwoss.receipt.ui.unit.chain;

import java.util.Date;

import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;

/**
 *
 * @author oliver.guenther
 */
public interface ChainLink<T> {

    public static class Optional {

        public final String partNo;

        public final Date mfgDate;

        public Optional(String partNo, Date mfgDate) {
            this.partNo = partNo;
            this.mfgDate = mfgDate;
        }
        
        /**
         * Merges this and other into a new instance using all values of this and only non null values of other.
         * <p/>
         * @param other the other one
         * @return a new instance of optional.
         */
        public Optional merge(Optional other) {
            if ( other == null ) return this;
            return new Optional(
                    other.partNo == null ? this.partNo : other.partNo,
                    other.mfgDate == null ? this.mfgDate : other.mfgDate);
        }
    }

    /*
     * Die Chain muss können:
     * - validität : (Soll weiter gemacht werden ?)
     * - unterschied warnung/error
     * - nachricht über details
     * - veränderung des eigenen Wertes
     * - veränderung/setzen von anderen Werten
     * - auslösen von neuen chains für die anderen werte. -> Das impliziert sich durch anderer Wert gesetzt
     * ------
     * - veränderung des Chainmodes. ? ( Das könnte ich extra machen )
     *
     */
    public static class Result<T> {

        /**
         * The value under observation.
         */
        public final T value;

        /**
         * Is the result of the execution valid meaning the chain contiues.
         */
        public final ValidationStatus valid;

        /**
         * A message, describing what happend.
         */
        public final String message;

        /**
         * Optional values, which may be created as hint.
         */
        public final Optional optional;

        public Result(T value, ValidationStatus valid, String message, Optional optional) {
            this.value = value;
            this.valid = valid;
            this.message = message;
            this.optional = optional;
        }

        public Result(ValidationStatus valid, String message) {
            this(null, valid, message, null);
        }

        public Result(T value, ValidationStatus valid, String message) {
            this(value, valid, message, null);
        }

        public Result(T value) {
            this(value, ValidationStatus.OK, "Eingabe ist zulässig", null);
        }

        public Result(T value, Optional optional) {
            this(value, ValidationStatus.OK, "Eingabe ist zulässig", optional);
        }
        
        public Result withOptional(Optional o) {
            return new Result(value, valid, message, o);
        }

        public boolean isValid() {
            if ( valid != null && valid == ValidationStatus.OK ) return true;
            return false;
        }

        public boolean hasOptionals() {
            if ( optional == null ) return false;
            if ( optional.mfgDate == null && optional.partNo == null ) return false;
            return true;
        }
    }

    Result<T> execute(T t);
}
