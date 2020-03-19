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
package tryout;

import java.util.*;

import eu.ggnet.dwoss.assembly.client.DwOssMain;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.sample.impl.Sample;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

/**
 *
 * @author oliver.guenther
 */
public class ClientTryout {

    public static void main(String[] args) {
        // TODO: To be build.

        // TODO: remove later,
        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });

        Dl.local().add(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                if ( "test".equalsIgnoreCase(user) && "test".equals(String.valueOf(pass)) ) {
                    setRights(new Operator(user, 123, Collections.emptyList()));
                    return;
                } // success
                if ( "admin".equalsIgnoreCase(user) && "admin".equals(String.valueOf(pass)) ) {
                    setRights(new Operator(user, 666, Arrays.asList(AtomicRight.values())));
                    return;
                } // success
                throw new AuthenticationException("User or Pass wrong");
            }
        });

        Dl.remote().add(StockApi.class, new StockApi() {
            @Override
            public List<PicoStock> findAllStocks() {
                return Arrays.asList(new PicoStock(1, "Hamburg"), new PicoStock(2, "Bremen"));
            }
        });

        Dl.remote().add(Mandators.class, new Mandators() {
            @Override
            public Mandator loadMandator() {
                return Sample.MANDATOR;
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                return Sample.CONTRACTORS;
            }

            @Override
            public PostLedger loadPostLedger() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        DwOssMain.main(new String[]{"--host=localhost", "--app=noapp", "--user=admin", "--pass=admin", "--disableRemote"});
    }

}
