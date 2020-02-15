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
package eu.ggnet.dwoss.core.system.autolog;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces default Loggers of slf4j based on the injection point class.
 * 
 * @author oliver.guenther
 */
public class LoggerProducer {

    public final static Logger L = LoggerFactory.getLogger(LoggerProducer.class);

    /**
     * Producer Method, autowired via CDI
     * 
     * @param ip the injection point
     * @return a correct logger instance
     */
    @Produces
    public static Logger createLogger(InjectionPoint ip) {
        L.debug("createLogger(ip={})", ip);
        return LoggerFactory.getLogger(ip.getBean().getBeanClass());
    }

}
