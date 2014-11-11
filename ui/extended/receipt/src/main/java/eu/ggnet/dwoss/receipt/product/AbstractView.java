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
package eu.ggnet.dwoss.receipt.product;

import eu.ggnet.dwoss.spec.entity.Tablet;
import eu.ggnet.dwoss.spec.entity.Notebook;
import eu.ggnet.dwoss.spec.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.entity.AllInOne;
import eu.ggnet.dwoss.spec.entity.Monitor;
import eu.ggnet.dwoss.spec.entity.BasicSpec;
import eu.ggnet.dwoss.spec.entity.Desktop;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IView;
import eu.ggnet.dwoss.util.IPreClose;

import java.awt.Window;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

/**
 * Abstract View to help with pre cloes
 * <p>
 * @author oliver.guenther
 * @param <T> type of ProductSpec
 */
public abstract class AbstractView<T extends ProductSpec> extends javax.swing.JPanel implements IPreClose, IView {

    public static AbstractView newView(ProductSpec spec, TradeName brand) {
        AbstractView productView;
        if ( spec instanceof Notebook ) {
            productView = new DisplayAbleView(ProductGroup.NOTEBOOK);
            productView.setSpec(spec);
        } else if ( spec instanceof AllInOne ) {
            productView = new DisplayAbleView(ProductGroup.ALL_IN_ONE);
            productView.setSpec(spec);
        } else if ( spec instanceof Tablet ) {
            productView = new DisplayAbleView(ProductGroup.TABLET_SMARTPHONE);
            productView.setSpec(spec);
        } else if ( spec instanceof Desktop ) {
            productView = new DesktopView();
            productView.setSpec(spec);
        } else if ( spec instanceof Monitor ) {
            productView = new MonitorView();
            productView.setSpec(spec);
        } else if ( spec instanceof BasicSpec ) {
            productView = new BasicView();
            productView.setSpec(spec);
        } else if ( spec instanceof DesktopBundle ) {
            DesktopBundleView view = new DesktopBundleView(brand.getManufacturer(), brand, ProductGroup.DESKTOP, ProductGroup.MONITOR);
            view.setSpec((DesktopBundle)spec);
            productView = view;
        } else {
            throw new RuntimeException(spec.getClass().getSimpleName() + " not yet implemented");
        }
        return productView;
    }

    protected Window parent;

    @Override
    public void setParent(Window parent) {
        this.parent = parent;
    }

    public abstract void setSpec(T t);

    public abstract T getSpec();

    @Override
    public boolean pre(CloseType type) {
        if ( type != CloseType.OK ) return true;
        Set<? extends ConstraintViolation> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(getSpec());
        if ( violations.isEmpty() ) return true;
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            if ( violation.getPropertyPath().toString().equals("model") ) continue;
            if ( violation.getPropertyPath().toString().endsWith("validationViolations") ) {
                sb.append(violation.getInvalidValue().toString()).append("\n");
            } else {
                sb.append("Validation Violation: ").append(violation.getPropertyPath()).append("=");
                sb.append(violation.getInvalidValue()).append(",").append(violation.getMessage()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Errors", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
