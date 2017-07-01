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
package eu.ggnet.dwoss.server;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.util.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author oliver.guenther
 */
@Named
@ApplicationScoped
public class Server {

    private final Logger L = LoggerFactory.getLogger(Server.class);

    @Getter
    @Setter
    private String prefix = "";

    @EJB
    private MandatorSupporter mandatorSupport;

    public List<String> inspectJndi() {
        try {
            return Utils.inspect(new InitialContext(), prefix).stream().map(np -> "(name=" + np.getName() + ")").collect(Collectors.toList());
        } catch (NamingException ex) {
            L.warn("Jndi Tree Module Name inspection on Suffix {} failed: {}", prefix, ex.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    public String getCompanyName() {
        return mandatorSupport.loadMandator().getCompany().getName();
    }

    public Mandator getMandator() {
        return mandatorSupport.loadMandator();
    }
}
