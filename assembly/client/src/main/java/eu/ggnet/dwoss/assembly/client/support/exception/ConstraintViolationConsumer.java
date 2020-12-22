/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support.exception;

import java.util.*;
import java.util.function.BiConsumer;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.util.ValidationUtil;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.UiParent;

/**
 *
 * @author oliver.guenther
 */
public class ConstraintViolationConsumer implements  BiConsumer<Optional<UiParent>, ConstraintViolationException>  {

    private final static Logger L = LoggerFactory.getLogger(ConstraintViolationConsumer.class);

    @Override
    public void accept(Optional<UiParent> optParent,ConstraintViolationException ex) {
        L.info("ConstraintViolationException {}", ValidationUtil.formatToSingleLine(new HashSet(ex.getConstraintViolations())));        
        Objects.requireNonNull(optParent, "optParent must not be null").map(p -> Ui.build().parent(p)).orElse(Ui.build()).title("Validationsfehler").swing()
                    .show(() -> new DetailView("Fehler bei der Validation", ValidationUtil.formatToMultiLine(ex.getConstraintViolations(), true), ExceptionUtils.toStackStrace(ex),
                    Dl.local().lookup(CachedMandators.class).loadMandator().bugMail()));
    }

}
