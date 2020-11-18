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
package eu.ggnet.dwoss.mail.ee;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.customer.api.ResellerListCustomer;
import eu.ggnet.dwoss.customer.api.ResellerListService;
import eu.ggnet.dwoss.mail.demand.ResellerListSendSubscriptionConfiguration;
import eu.ggnet.dwoss.mail.demand.SmtpConfiguration;
import eu.ggnet.dwoss.misc.api.SalesListingService;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class MailSalesListingServiceBean implements MailSalesListingService {

    private final static Logger L = LoggerFactory.getLogger(MailSalesListingServiceBean.class);

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private SmtpConfiguration smtpConfiguration;

    @Inject
    private ResellerListSendSubscriptionConfiguration sendConfiguration;

    @Inject
    private SalesListingService sls;

    @Inject
    private ResellerListService rls;

    @Override
    public void generateResellerXlsAndSendToSubscribedCustomers() {
        SubMonitor m = monitorFactory.newSubMonitor("Transfer");
        m.message("sending Mail");
        m.start();
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", smtpConfiguration);
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", sendConfiguration);

        List<ResellerListCustomer> customers = rls.allResellerListCustomers();
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", customers);

        List<FileJacket> lists = sls.generateXlses(SalesChannel.RETAILER);
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", lists);

        try {
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(smtpConfiguration.hostname);
            email.setFrom(sendConfiguration.fromAddress, sendConfiguration.fromName);
            email.setAuthentication(smtpConfiguration.smtpAuthenticationUser, smtpConfiguration.smtpAuthenticationPass);
            email.setStartTLSEnabled(smtpConfiguration.useStartTls);
            email.setSSLCheckServerIdentity(false);
            email.setSSLOnConnect(smtpConfiguration.useSsl);
            email.setCharset(smtpConfiguration.charset);

            email.addTo(sendConfiguration.toAddress);
            email.setSubject(sendConfiguration.subject);
            email.setMsg(sendConfiguration.message);

            for (ResellerListCustomer customer : customers) {
                email.addBcc(customer.email());
            }

            for (FileJacket fj : lists) {
                email.attach(
                        new javax.mail.util.ByteArrayDataSource(fj.getContent(), "application/xls"),
                        fj.getHead() + fj.getSuffix(), "Die Händlerliste für die Marke ");
            }

            email.send();
            m.finish();
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        L.info("generateResellerXlsAndSendToSubscribedCustomers() reseller lists {} send to {} customers",
                lists.stream().map(FileJacket::getHead).collect(Collectors.toList()), customers.size());
    }

}
