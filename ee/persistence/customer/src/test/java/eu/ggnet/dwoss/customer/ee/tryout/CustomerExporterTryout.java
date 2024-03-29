/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.tryout;

import java.awt.Desktop;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.CustomerExporterOperation;
import eu.ggnet.dwoss.core.common.FileJacket;

/**
 *
 * @author oliver.guenther
 */
public class CustomerExporterTryout {

    public static void main(String[] args) throws IOException, InterruptedException {
        CustomerGenerator gen = new CustomerGenerator();
        FileJacket fj = new CustomerExporterOperation().toXls(IntStream.range(0, 40).mapToObj(i -> gen.makeCustomer()).collect(Collectors.toList()));

        Desktop.getDesktop().open(fj.toTemporaryFile());
        Thread.sleep(6000);

    }

}
