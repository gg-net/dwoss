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
package eu.ggnet.saft.api;

import java.lang.annotation.*;

/**
 * ViewClassImplementation for JNDI Name resoulution.
 * The Wildfly needs in for the remotelookup ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName the viewClass.
 * The JavaSpec defines it optional, but wildfly thinks otherwise. This Annotation added to the remote interface must contation the classname of the
 * implemetation, so the client can build the full jndi name.
 *
 * @author oliver.guenther
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RemoteViewClass {

    String value();

}
