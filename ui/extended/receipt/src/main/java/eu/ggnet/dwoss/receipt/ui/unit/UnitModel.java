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
package eu.ggnet.dwoss.receipt.ui.unit;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.Warranty;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

import java.util.*;

import javax.swing.Action;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 * The Unit Model.
 * Warning: This is not the complete model, because it was design afterwards.
 * Most of the Model Information is in the view.
 * <p/>
 * @author oliver.guenther
 */
public class UnitModel {

    /**
     * A survey merging a message with a status.
     */
    public static class Survey {

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

    public static class MetaUnit {

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
            return ((warrentyTillSetted) ? warrentyTill != null : true)
                    && refurbishId.getSurvey().isOkOrWarn()
                    && serial.getSurvey().isOkOrWarn()
                    && partNo.getSurvey().isOkOrWarn()
                    && mfgDate.getSurvey().isOkOrWarn();
        }
    }

    public static class MetaValue<T> {

        private T value;

        private List<ChainLink<T>> chain;

        private final Survey survey = new Survey();

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public List<ChainLink<T>> getChain() {
            return chain;
        }

        public void setChain(List<ChainLink<T>> chain) {
            this.chain = chain;
        }

        public Survey getSurvey() {
            return survey;
        }

        public boolean isSet() {
            if ( value == null ) return false;
            if ( value instanceof String ) return !StringUtils.isBlank(((String)value));
            return true;
        }
    }

    private final Set<Action> actions = new HashSet<>();

    @NotNull(message = "contractor not set")
    private TradeName contractor;

    private ReceiptOperation operation;

    private String operationComment;

    private Product product;

    private String productSpecDescription;

    private final MetaUnit metaUnit = new MetaUnit();

    /**
     * Represents the mode of support (validation, auto update of values) in the ui.
     * <p/>
     */
    private TradeName mode;

    private boolean editMode;

    public ReceiptOperation getOperation() {
        return operation;
    }

    public void setOperation(ReceiptOperation operation) {
        this.operation = operation;
    }

    public MetaUnit getMetaUnit() {
        return metaUnit;
    }

    public TradeName getContractor() {
        return contractor;
    }

    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
    }

    public String getOperationComment() {
        return operationComment;
    }

    public void setOperationComment(String operationComment) {
        this.operationComment = operationComment;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductSpecDescription() {
        return productSpecDescription;
    }

    public void setProductSpecDescription(String productSpecDescription) {
        this.productSpecDescription = productSpecDescription;
    }

    public TradeName getMode() {
        return mode;
    }

    public void setMode(TradeName mode) {
        this.mode = mode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public Set<Action> getActions() {
        return Collections.unmodifiableSet(actions);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
