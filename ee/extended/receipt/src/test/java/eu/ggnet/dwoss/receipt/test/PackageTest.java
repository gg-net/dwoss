/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.test;

import org.junit.Test;

import eu.ggnet.dwoss.receipt.itest.ReceiptUnitOperationIT;

import static java.lang.Package.getPackage;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class PackageTest {

    public static Package itest() {
        System.out.println("1:" + getPackage("eu.ggnet.dwoss.receipt.itest"));
        ReceiptUnitOperationIT t = new ReceiptUnitOperationIT();
        System.out.println("2:" + getPackage("eu.ggnet.dwoss.receipt.itest"));
        return getPackage("eu.ggnet.dwoss.receipt.itest");
    }

    public static Package test() {
        return getPackage("eu.ggnet.dwoss.receipt.test");
    }

    public static Package receipt() {

        return getPackage("eu.ggnet.dwoss.receipt");
    }

    @Test
    public void testItest() {

//        assertThat(receipt()).describedAs("receipt package").isNotNull();
        assertThat(test()).describedAs("test package").isNotNull();
        assertThat(itest()).describedAs("itest package").isNotNull();
    }

}
