/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.spec.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.spec.ee.entity.QAllInOne.allInOne;
import static eu.ggnet.dwoss.spec.ee.entity.QBasicSpec.basicSpec;
import static eu.ggnet.dwoss.spec.ee.entity.QDesktop.desktop;
//import static eu.ggnet.dwoss.spec.ee.entity.QDesktopBundle.desktopBundle;
import static eu.ggnet.dwoss.spec.ee.entity.QMonitor.monitor;
import static eu.ggnet.dwoss.spec.ee.entity.QNotebook.notebook;
import static eu.ggnet.dwoss.spec.ee.entity.QProductFamily.productFamily;
import static eu.ggnet.dwoss.spec.ee.entity.QProductModel.productModel;
import static eu.ggnet.dwoss.spec.ee.entity.QProductSeries.productSeries;
import static eu.ggnet.dwoss.spec.ee.entity.QProductSpec.productSpec;
import static eu.ggnet.dwoss.spec.ee.entity.QTablet.tablet;
import static eu.ggnet.dwoss.spec.ee.entity.piece.QCpu.cpu;
import static eu.ggnet.dwoss.spec.ee.entity.piece.QDisplay.display;
import static eu.ggnet.dwoss.spec.ee.entity.piece.QGpu.gpu;

/**
 * Utility class to clear the customer database.
 *
 * @author oliver.guenther
 */
public class SpecDeleteUtils {

    private SpecDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        queryFactory.delete(desktopBundle).execute();
        queryFactory.delete(allInOne).execute();
        queryFactory.delete(desktop).execute();
        queryFactory.delete(tablet).execute();
        queryFactory.delete(monitor).execute();
        queryFactory.delete(notebook).execute();
        queryFactory.delete(basicSpec).execute();

        em.flush();

        queryFactory.delete(productModel).execute();
        queryFactory.delete(productFamily).execute();
        queryFactory.delete(productSeries).execute();

        queryFactory.delete(cpu).execute();
        queryFactory.delete(display).execute();
        queryFactory.delete(gpu).execute();

        em.flush();
    }

    /**
     * If the database is empty, the result is null, otherwise a message with more details.
     *
     * @param em the custmoer emtitymanager
     * @return null if empty, otherwise a message with details.
     */
    public static String validateEmpty(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        if ( !queryFactory.from(allInOne).fetch().isEmpty() ) return "spec.AllInOne is not empty";
        if ( !queryFactory.from(basicSpec).fetch().isEmpty() ) return "spec.BasicSpec is not empty";
        if ( !queryFactory.from(desktop).fetch().isEmpty() ) return "spec.Desktop is not empty";
//        if ( !queryFactory.from(desktopBundle).fetch().isEmpty() ) return "spec.DesktopBundle is not empty";
        if ( !queryFactory.from(monitor).fetch().isEmpty() ) return "spec.Monitor is not empty";
        if ( !queryFactory.from(notebook).fetch().isEmpty() ) return "spec.Notebook is not empty";
        if ( !queryFactory.from(productSpec).fetch().isEmpty() ) return "spec.ProductSpec is not empty";
        if ( !queryFactory.from(productModel).fetch().isEmpty() ) return "spec.ProductModel is not empty";
        if ( !queryFactory.from(productSeries).fetch().isEmpty() ) return "spec.ProductSeries is not empty";
        if ( !queryFactory.from(productFamily).fetch().isEmpty() ) return "spec.ProductFamily is not empty";
        if ( !queryFactory.from(tablet).fetch().isEmpty() ) return "spec.Tablet is not empty";
        if ( !queryFactory.from(cpu).fetch().isEmpty() ) return "spec.Cpu is not empty";
        if ( !queryFactory.from(display).fetch().isEmpty() ) return "spec.Display is not empty";
        if ( !queryFactory.from(gpu).fetch().isEmpty() ) return "spec.Gpu is not empty";
        return null;
    }

}
