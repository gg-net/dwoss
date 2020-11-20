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
package eu.ggnet.dwoss.mail.demand;

import java.util.Objects;

/**
 * Configuration for the reseller list, and how it is send per mail
 * 
 * @author oliver.guenther
 */
public class ResellerListSendSubscriptionConfiguration {
    
    public final String fromAddress;
    
    public final String fromName;
    
    public final String toAddress;
    
    public final String subject;
    
    public final String message;

    public ResellerListSendSubscriptionConfiguration(String fromAddress, String fromName, String toAddress, String subject, String message) {
        this.fromAddress = Objects.requireNonNull(fromAddress);
        this.fromName = Objects.requireNonNull(fromName);
        this.toAddress = Objects.requireNonNull(toAddress);
        this.subject = Objects.requireNonNull(subject);
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public String toString() {
        return "ResellerListSendSubscriptionConfiguration{" + "fromAddress=" + fromAddress + ", fromName=" + fromName + ", toAddress=" + toAddress + ", subject=" + subject + ", message=" + message + '}';
    }

}
