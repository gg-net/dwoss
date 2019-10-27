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
package eu.ggnet.dwoss.customer.ee.tryout;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
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

        Customer c1 = CustomerGenerator.makeCustomer();
        Customer c2 = CustomerGenerator.makeSimpleConsumerCustomer();
        Customer c3 = CustomerGenerator.makeSimpleBussinesCustomer();

        //make wthout invoice adress
        Customer c4 = CustomerGenerator.makeSimpleConsumerCustomer();
        c4.getAddressLabels().clear();

        DefaultCustomerSalesdata defaults = new DefaultCustomerSalesdata.Builder()
                .addAllAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER))
                .paymentCondition(PaymentCondition.CUSTOMER)
                .shippingCondition(ShippingCondition.FIVE)
                .paymentMethod(PaymentMethod.DIRECT_DEBIT).build();

        String c1mcode = c1.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");
        String c2mcode = c2.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");
        String c3mcode = c3.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");
        String c4mcode = c4.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");

        WebView view = new WebView();
        view.getEngine().loadContent(Css.toHtml5WithStyle(
                "<hr /><h1>makeCustomer : Customer.toHtml(MATCHCODE,defaults)</h1>"
                + c1.toHtml(c1mcode, defaults)
                + "<hr /><h1>makeCustomer : Customer.toHtml()</h1>"
                + c1.toHtml()
                + "<hr /><h1>makeSimpleConsumerCustomer : Customer.toHtml(MATCHCODE,defaults)</h1>"
                + c2.toHtml(c2mcode, defaults)
                + "<hr /><h1>makeSimpleConsumerCustomer : Customer.toHtml()</h1>"
                + c2.toHtml()
                + "<hr /><h1>makeSimpleBussinesCustomer : Customer.toHtml(MATCHCODE,defaults)</h1>"
                + c3.toHtml(c3mcode, defaults)
                + "<hr /><h1>makeSimpleBussinesCustomer : Customer.toHtml()</h1>"
                + c3.toHtml()
                + "<hr /><h1>makeSimpleBussinesCustomer : Customer.toHtml(MATCHCODE,defaults)</h1>"
                + c4.toHtml(c4mcode, defaults)
                + "<hr /><h1>makeSimpleBussinesCustomer : Customer.toHtml()</h1>"
                + c4.toHtml()
        ));
        primaryStage.setScene(new Scene(new BorderPane(view)));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
