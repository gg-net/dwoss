/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao.jasper;

import java.util.Optional;

import net.sf.jasperreports.engine.JasperPrint;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.saft.core.ui.ExceptionWrapper.RunableWithException;

/**
 * Container for the all inputs of the JasperFxView. 
 * 
 * Most of the values are neede for the mail feature.
 * 
 * @author oliver.guenther
 */
@FreeBuilder
public interface JasperFxViewData {
    
    /**
     * The rendered print.
     * 
     * @return the rendered print
     */
    JasperPrint jasperPrint();
    
    /**
     * The relevant document.
     * 
     * @return relevant document
     */
    Document document();
    
    
    /**
     * Callback, if the document can be mailed.
     * 
     * @return the callback.
     */
    Optional<RunableWithException> mailCallback();
   
    class Builder extends JasperFxViewData_Builder {};
}
