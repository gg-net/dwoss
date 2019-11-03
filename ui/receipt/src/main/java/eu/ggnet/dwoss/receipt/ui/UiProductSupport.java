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

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ui.product.AbstractView;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;
import eu.ggnet.saft.core.Dl;

/**
 * Support Class for creation or edit of Products.
 * Not perfect, but a simple step to cleanup the ReceiptController.
 * <p/>
 * @author oliver.guenther
 */
public class UiProductSupport {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    // UnitController und EditPRoductAction
    public static ProductSpec createOrEditPart(TradeName manufacturer, String partNo, Window parent) throws UserInfoException {
        return createOrEditPart(manufacturer, partNo, null, null, parent);
    }

    // DesktopBundleView and internal.
    /**
     * Creates or edits Product with partNo.
     * <p/>
     * @param manufacturer     the manufacturer.
     * @param partNo           the partNo
     * @param allowedEditGroup if not null and there exist a Product with the partNo, the ProductGroup of that product must be like the parameter.
     * @param selectedBrand    if not null the brand is assumed as selected and may not be changed.
     * @param parent           the parent window for localisation
     * @return the created or edited ProductSpec
     * @throws UserInfoException if not ok
     */
    public static ProductSpec createOrEditPart(TradeName manufacturer, String partNo, TradeName selectedBrand, ProductGroup allowedEditGroup, Window parent) throws UserInfoException {
        validatePartNo(manufacturer, partNo);
        ProductSpec productSpec = Dl.remote().lookup(SpecAgent.class).findProductSpecByPartNoEager(partNo);
        boolean edit = false;
        if ( productSpec != null ) {
            edit = true;
            if ( allowedEditGroup != null && selectedBrand != null ) {
                if ( productSpec.getModel().getFamily().getSeries().getGroup() != allowedEditGroup ) {
                    throw new UserInfoException("Erlaubte Warengruppe ist " + allowedEditGroup + ", Artikel ist aber " + SpecFormater.toDetailedName(productSpec));
                } else if ( productSpec.getModel().getFamily().getSeries().getBrand() != selectedBrand ) {
                    throw new UserInfoException("Ausgewählte Marke ist " + selectedBrand + ", Artikel ist aber " + SpecFormater.toDetailedName(productSpec));
                }
            }
        }
        SimpleView simpleView;
        if ( edit ) simpleView = new SimpleView(productSpec);
        else if ( allowedEditGroup != null && selectedBrand != null )
            simpleView = new SimpleView(partNo, selectedBrand, allowedEditGroup);
        else simpleView = new SimpleView(manufacturer, partNo);
        OkCancelDialog<SimpleView> simpleDialog = new OkCancelDialog<>(parent, "Artikelkonfiguration", simpleView);
        simpleDialog.setVisible(true);
        if ( simpleDialog.isCancel() ) return null;
        ProductSpec spec = simpleView.getProductSpec();
        if ( edit ) spec = Dl.remote().lookup(ProductProcessor.class).refresh(spec, simpleView.getSelectedModel().get());
        AbstractView productView = AbstractView.newView(spec, simpleView.getSelectedModel().get().getFamily().getSeries().getBrand());
        if ( edit ) productView.setGtin(Dl.remote().lookup(UniqueUnitAgent.class).findById(Product.class, spec.getProductId()).getGtin());
        OkCancelDialog productDialog = new OkCancelDialog(parent, "Artikeldetailkonfiguration", productView);
        productDialog.setVisible(true);
        if ( productDialog.isCancel() ) return null;
        validate(simpleView.getSelectedModel().get());
        validate(productView.getSpec());
        if ( edit ) return Dl.remote().lookup(ProductProcessor.class).update(productView.getSpec(), productView.getGtin());
        // TODO: In Case of a Bundle autoupdate the name of the model.
        else return Dl.remote().lookup(ProductProcessor.class).create(productView.getSpec(), simpleView.getSelectedModel().get(), productView.getGtin());
    }

    private static void validate(Object o) throws UserInfoException {
        Set<ConstraintViolation<Object>> validate = VALIDATOR.validate(o);
        if ( !validate.isEmpty() ) throw new UserInfoException(ValidationUtil.formatToMultiLine(new ArrayList<>(validate), true));
    }

    public static void validatePartNo(TradeName manufacturer, String partNo) throws UserInfoException {
        if ( manufacturer == null ) throw new UserInfoException("Kein Hersteller übergeben");
        if ( !manufacturer.isManufacturer() ) throw new UserInfoException(manufacturer + " ist kein Hersteller");
        if ( manufacturer.getPartNoSupport() == null ) return;
        if ( !manufacturer.getPartNoSupport().isValid(partNo) ) throw new UserInfoException(manufacturer.getPartNoSupport().violationMessages(partNo));
    }
}
