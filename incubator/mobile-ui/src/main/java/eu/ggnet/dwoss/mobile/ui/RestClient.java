/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.mobile.ui;

import org.codegist.crest.annotate.*;

import eu.ggnet.dwoss.uniqueunit.api.UnitShard;


/**
 *
 * @author Administrator
 */
@EndPoint(MobileMainApp.URL)
@Path("unitOverseer")
@Consumes("application/json")
public interface RestClient {
 
    @Path("unit/{search}")
    public UnitShard getUnit(@PathParam("search") String unit);
            
    
}
