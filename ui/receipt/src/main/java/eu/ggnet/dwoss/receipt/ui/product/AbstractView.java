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
package eu.ggnet.dwoss.receipt.ui.product;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

import javax.validation.*;

import eu.ggnet.dwoss.core.widget.saft.VetoableOnOk;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.AlertType;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 * Abstract View to help with pre cloes
 * <p>
 * @author oliver.guenther
 */
public abstract class AbstractView extends javax.swing.JPanel implements VetoableOnOk, Consumer<SpecAndModel>, ResultProducer<SpecAndModel> {

    public static Class<? extends AbstractView> selectView(SpecAndModel sam) {
        ProductSpec spec = Objects.requireNonNull(sam, "sam must not be null").spec();
        if ( spec instanceof Notebook ) return NotebookView.class;
        if ( spec instanceof AllInOne ) return AllInOneView.class;
        if ( spec instanceof Tablet ) return TabletSmartPhoneView.class;
        if ( spec instanceof Desktop ) return DesktopView.class;
        if ( spec instanceof Monitor ) return MonitorView.class;
        if ( spec instanceof BasicSpec ) return BasicView.class;
        if ( spec instanceof DesktopBundle ) return DesktopBundleView.class;
        throw new IllegalArgumentException(spec.getClass().getSimpleName() + " not yet implemented");
    }

    public static AbstractView newView(SpecAndModel sam) {
        Class<? extends AbstractView> clazz = selectView(sam);
        try {
            return (AbstractView)clazz.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public abstract SpecAndModel getResult();

    @Override
    public boolean mayClose() {
        SpecAndModel sam = getResult();
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation> violations = new HashSet<>();
        violations.addAll(v.validate(sam.spec()));
        violations.addAll(v.validate(sam.model()));
        //TODO: If there is time, make this more beutiful.        //TODO: If there is time, make this more beutiful.

        if ( violations.isEmpty() ) return true;
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            if ( violation.getPropertyPath().toString().equals("model") ) continue; // TODO: Why is this excluded ?
            sb.append(violation.getMessage()).append("\n");
        }
        Ui.build(this).alert().title("Fehler").message(sb.toString()).show(AlertType.ERROR);
        return false;
    }
}
