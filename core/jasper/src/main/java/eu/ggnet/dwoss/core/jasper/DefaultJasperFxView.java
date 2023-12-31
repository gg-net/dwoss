/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.core.jasper;

import java.util.function.Consumer;

import net.sf.jasperreports.engine.JasperPrint;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class DefaultJasperFxView extends AbstractJasperFxView implements Consumer<JasperPrint> {

    @Override
    public void accept(JasperPrint print) {
        setJasperPrint(print);
    }

}
