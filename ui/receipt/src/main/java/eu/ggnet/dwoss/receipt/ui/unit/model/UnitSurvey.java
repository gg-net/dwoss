/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.unit.model;

import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;

/**
 * A survey merging a message with a status.
 */
public class UnitSurvey {

    private String message = "";

    private ValidationStatus status = ValidationStatus.OK;

    public String getMessage() {
        return message;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public void validating(String msg) {
        setStatus(ValidationStatus.VALIDATING, msg);
    }

    void ok(String msg) {
        setStatus(ValidationStatus.OK, msg);
    }

    void warn(String msg) {
        setStatus(ValidationStatus.WARNING, msg);
    }

    void error(String msg) {
        setStatus(ValidationStatus.ERROR, msg);
    }

    public boolean isOk() {
        return status == ValidationStatus.OK;
    }

    public boolean isOkOrWarn() {
        return status == ValidationStatus.OK || status == ValidationStatus.WARNING;
    }

    public boolean isValidating() {
        return status == ValidationStatus.VALIDATING;
    }

    public boolean isError() {
        return status == ValidationStatus.ERROR;
    }

    public boolean isWarning() {
        return status == ValidationStatus.WARNING;
    }

}
