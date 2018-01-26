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
package tryout;

import java.util.Arrays;

import javax.swing.JLabel;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.ServiceViewCask;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancel;

import static eu.ggnet.dwoss.rules.TaxType.GENERAL_SALES_TAX_DE_SINCE_2007;

/**
 *
 * @author oliver.guenther
 */
public class ServiceViewCaskTryout {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new JLabel("Main Applikation"));
        Client.addSampleStub(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                setRights(new Operator(user, 1, Arrays.asList(AtomicRight.values())));
            }
        });

        Client.addSampleStub(MandatorSupporter.class, new MandatorSupporter() {

            @Override
            public Mandator loadMandator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                return new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT,
                        Arrays.asList(SalesChannel.CUSTOMER, SalesChannel.RETAILER), null);
            }

            @Override
            public ReceiptCustomers loadReceiptCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SpecialSystemCustomers loadSystemCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Contractors loadContractors() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public PostLedger loadPostLedger() {
                return new PostLedger();
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String loadMandatorAsHtml() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
// () -> Position.builder().type(PositionType.SERVICE).price(30.).build()
        Ui.build().swing().eval(() -> OkCancel.wrap(new ServiceViewCask(GENERAL_SALES_TAX_DE_SINCE_2007)))
                .ifPresent(System.out::println);
    }

}
