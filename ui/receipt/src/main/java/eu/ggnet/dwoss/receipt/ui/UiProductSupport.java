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
package eu.ggnet.dwoss.receipt.ui;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Set;

import javax.validation.*;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ui.product.AbstractView;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.impl.Swing;
import eu.ggnet.saft.core.ui.UiParent;

/**
 * Support Class for creation or edit of Products.
 * Not perfect, but a simple step to cleanup the ReceiptController.
 * <p/>
 * @author oliver.guenther
 */
public class UiProductSupport {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static ProductSpec createOrEditPart(SimpleView.CreateOrEdit in, UiParent parent) throws UserInfoException {
        Window w = UiCore.global().core(Swing.class).unwrap(parent).orElse(null);
        return createOrEditPart(in, w);
    }

    public static ProductSpec createOrEditPart(SimpleView.CreateOrEdit in, Window parent) throws UserInfoException {
        SimpleView simpleView = new SimpleView();
        simpleView.accept(in);
        OkCancelDialog<SimpleView> simpleDialog = new OkCancelDialog<>(parent, "Artikelkonfiguration", simpleView);
        simpleDialog.setVisible(true);
        if ( simpleDialog.isCancel() ) return null;
        ProductSpec spec = simpleView.getProductSpec();
        if ( simpleView.isEdit() ) spec = Dl.remote().lookup(ProductProcessor.class).refresh(spec, simpleView.getSelectedModel().get()); // Sollte in Simpleview passieren
        AbstractView productView = AbstractView.newView(spec, simpleView.getSelectedModel().get().getFamily().getSeries().getBrand());
        if ( simpleView.isEdit() ) productView.setGtin(Dl.remote().lookup(UniqueUnitAgent.class).findById(Product.class, spec.getProductId()).getGtin()); // Sollte im accept passieren.
        OkCancelDialog productDialog = new OkCancelDialog(parent, "Artikeldetailkonfiguration", productView);
        productDialog.setVisible(true);
        if ( productDialog.isCancel() ) return null;
        validate(simpleView.getSelectedModel().get());  // preclose
        validate(productView.getSpec());    // preclose
        if ( simpleView.isEdit() ) return Dl.remote().lookup(ProductProcessor.class).update(productView.getSpec(), productView.getGtin());
        // TODO: In Case of a Bundle autoupdate the name of the model.
        else return Dl.remote().lookup(ProductProcessor.class).create(productView.getSpec(), simpleView.getSelectedModel().get(), productView.getGtin());

    }

    private static void validate(Object o) throws UserInfoException {
        Set<ConstraintViolation<Object>> validate = VALIDATOR.validate(o);
        if ( !validate.isEmpty() ) throw new UserInfoException(ValidationUtil.formatToMultiLine(new ArrayList<>(validate), true));
    }

}
