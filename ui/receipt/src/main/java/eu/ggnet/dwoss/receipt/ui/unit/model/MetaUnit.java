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

import java.util.Date;

import eu.ggnet.dwoss.core.common.values.Warranty;
import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 *
 * @author oliver.guenther
 */
public class MetaUnit {

    private final MetaValue<String> refurbishId = new MetaValue<>();

    private final MetaValue<String> serial = new MetaValue<>();

    private final MetaValue<String> partNo = new MetaValue<>();

    private final MetaValue<Date> mfgDate = new MetaValue<>();

    private Date warrentyTill;

    private boolean warrentyTillSetted = false;

    public MetaValue<String> getRefurbishId() {
        return refurbishId;
    }

    public MetaValue<String> getSerial() {
        return serial;
    }

    public MetaValue<String> getPartNo() {
        return partNo;
    }

    public MetaValue<Date> getMfgDate() {
        return mfgDate;
    }

    public Date getWarrentyTill() {
        return warrentyTill;
    }

    public boolean isWarrentyTillSetted() {
        return warrentyTillSetted;
    }

    public void setWarrentyTill(Date warrentyTill) {
        this.warrentyTill = warrentyTill;
    }

    public void setWarrentyTillSetted(boolean warrentyTillSetted) {
        this.warrentyTillSetted = warrentyTillSetted;
    }

    /**
     * Load Values refurbishId, serial, partNo and mfgDate from a UniqueUnit.
     * <p/>
     * @param uu the uniqueUnit as source.
     */
    public void loadFrom(UniqueUnit uu) {
        if ( uu == null ) return;
        refurbishId.setValue(uu.getRefurbishId());
        refurbishId.getSurvey().setStatus(ValidationStatus.OK, "");
        serial.setValue(uu.getSerial());
        serial.getSurvey().setStatus(ValidationStatus.OK, "");
        if ( uu.getProduct() != null ) {
            partNo.setValue(uu.getProduct().getPartNo());
        }
        mfgDate.setValue(uu.getMfgDate());
        if ( uu.getWarranty() == Warranty.WARRANTY_TILL_DATE ) warrentyTillSetted = true;
        warrentyTill = uu.getWarrentyValid();
    }

    /**
     * Updating the values refurbishId, serial and mfgDate of the supplied UniqueUnit.
     * <p/>
     * @param uu the uniqueUnit as target.
     */
    public void loadTo(UniqueUnit uu) {
        if ( uu == null ) return;
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId.getValue());
        uu.setIdentifier(UniqueUnit.Identifier.SERIAL, serial.getValue());
        uu.setMfgDate(mfgDate.getValue());
        uu.setWarrentyValid(warrentyTill);
    }

    /**
     * Returns true if all of the meta values of the meta unit are ok or warn, else false.
     * <p/>
     * @return true if all of the meta values of the meta unit are ok or warn, else false.
     */
    public boolean isOkOrWarn() {
        return ((warrentyTillSetted) ? warrentyTill != null : true) && refurbishId.getSurvey().isOkOrWarn() && serial.getSurvey().isOkOrWarn() && partNo.getSurvey().isOkOrWarn() && mfgDate.getSurvey().isOkOrWarn();
    }

}
