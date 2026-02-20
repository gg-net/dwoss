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
package eu.ggnet.dwoss.redtapext.ee;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.customer.api.ResellerListCustomer;
import eu.ggnet.dwoss.customer.api.ResellerListService;
import eu.ggnet.dwoss.core.common.values.ResellerListSendSubscriptionConfiguration;
import eu.ggnet.dwoss.misc.api.SalesListingService;
import eu.ggnet.dwoss.redtapext.ee.mail.*;

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
    private GraphEmailService mailService;

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

        List<ResellerListCustomer> customers = rls.allResellerListCustomers();
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", customers);

        List<FileJacket> lists = sls.generateXlses(SalesChannel.RETAILER);
        L.debug("generateResellerXlsAndSendToSubscribedCustomers() preparing mail with {}", lists);

        EmailMessage.Builder emailBuilder = EmailMessage.builder();
        emailBuilder.from(sendConfiguration.fromAddress);

        emailBuilder.to(sendConfiguration.toAddress);
        emailBuilder.subject(sendConfiguration.subject);
        emailBuilder.body(sendConfiguration.message);

        emailBuilder.bcc(customers.stream().map(ResellerListCustomer::email).toList());

        for (FileJacket fj : lists) {
            emailBuilder.attachment(fj.getHead() + fj.getSuffix(), "application/xls", fj.getContent());
        }

        try {
            mailService.sendEmail(emailBuilder.build());
        } catch (EmailException ex) {
            L.error("Mailsendig failed",ex);
        }

        m.finish();
        L.info("generateResellerXlsAndSendToSubscribedCustomers() reseller lists {} send to {} customers",
                lists.stream().map(FileJacket::getHead).collect(Collectors.toList()), customers.size());
    }

}
