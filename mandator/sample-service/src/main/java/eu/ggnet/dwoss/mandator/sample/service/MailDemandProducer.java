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
package eu.ggnet.dwoss.mandator.sample.service;

import jakarta.enterprise.inject.Produces;

import eu.ggnet.dwoss.mail.demand.ResellerListSendSubscriptionConfiguration;
import eu.ggnet.dwoss.mail.demand.SmtpConfiguration;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author oliver.guenther
 */
@ApplicationScoped
public class MailDemandProducer {

    @Produces
    private final static ResellerListSendSubscriptionConfiguration CONFIGURATION
            = new ResellerListSendSubscriptionConfiguration("company@example.local", "Company", "company@example.local", "Händlerliste", "Hier ist die Händlerliste");

    @Produces
    private final static SmtpConfiguration SMTP_CONFIGURATION
            = new SmtpConfiguration("localhost", "user", "user", "UTF-8", false,false);

}
