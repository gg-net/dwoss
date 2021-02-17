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
package eu.ggnet.dwoss.receipt.ui.unit.model;

import java.util.*;

import javax.swing.Action;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

/**
 * The Unit Model.
 * Warning: This is not the complete model, because it was design afterwards.
 * Most of the Model Information is in the view.
 * <p/>
 * @author oliver.guenther
 */
public class UnitModel {

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
