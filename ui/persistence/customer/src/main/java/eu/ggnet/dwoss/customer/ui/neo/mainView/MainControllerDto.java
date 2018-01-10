/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.neo.mainView;

import java.util.*;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.rules.CustomerFlag;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jacob.weinhold
 */
public class MainControllerDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private short optLock;

    @Getter
    @Setter
    private List<Company> companies;

    @Getter
    @Setter
    private List<Contact> contacts;

    @Getter
    @Setter
    private List<MandatorMetadata> mandatorMetadata;

    @Getter
    @Setter
    private Set<CustomerFlag> flags = new HashSet<>();

    @Getter
    @Setter
    private Source source;

    @Getter
    @Setter
    private Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    @Getter
    @Setter
    private String keyAccounter;

    @Getter
    @Setter
    private String comment;

    public static void setControllerState(CustomerExpandedController controller, MainControllerDto dto) {
        controller.setId(dto.getId());
        controller.setComment(dto.getComment());
        controller.setCompanies(dto.getCompanies());
        controller.setContacts(dto.getContacts());
        controller.setFlags(dto.getFlags());
        controller.setId(dto.getId());
        controller.setKeyAccounter(dto.getKeyAccounter());
        controller.setMandatorMetadata(dto.getMandatorMetadata());
        controller.setOptLock(dto.getOptLock());
        controller.setSource(dto.getSource());

    }
}
