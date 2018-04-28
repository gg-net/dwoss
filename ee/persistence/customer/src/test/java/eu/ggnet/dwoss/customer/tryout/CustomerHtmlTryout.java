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
package eu.ggnet.dwoss.customer.tryout;

import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ee.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;

/**
 *
 * @author oliver.guenther
 */
public class CustomerHtmlTryout extends Application {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CustomerGenerator gen = new CustomerGenerator();
        Customer c1 = gen.makeOldCustomer();

        Customer c2 = gen.makeCustomer();

        DefaultCustomerSalesdata defaults = DefaultCustomerSalesdata.builder()
                .allowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER))
                .paymentCondition(PaymentCondition.CUSTOMER)
                .shippingCondition(ShippingCondition.DEALER_ONE)
                .paymentMethod(PaymentMethod.DIRECT_DEBIT).build();

        String MATCHCODE = c1.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");

        OldCustomer c0 = ConverterUtil.convert(c1, MATCHCODE, defaults);

        WebView view = new WebView();
        view.getEngine().loadContent(Css.toHtml5WithStyle(
                "<h1>OldCustomer.toHtml()</h1>"
                + c0.toHtmlHighDetailed()
                + "<hr /><h1>makeOldCustmer : Customer.toHtml(MATCHCODE,defaults)</h1>"
                + c1.toHtml(MATCHCODE, defaults)
                + "<hr /><h1>makeOldCustmer : Customer.toHtml()</h1>"
                + c1.toHtml()
                + "<hr /><h1>makeCustmer : Customer.toHtml()</h1>"
                + c2.toHtml()));
        primaryStage.setScene(new Scene(new BorderPane(view)));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
