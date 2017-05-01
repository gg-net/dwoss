/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.assembly.web.report;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for RevenueReport generation in DW-Web.
 * <p>
 * @author pascal.perau
 */
@Named
@SessionScoped
public class SearchBean implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SearchBean.class);

//    @Inject
//    UniqueUnitEao uniqueUnitEao;
//
//    public List<UniqueUnit> getUnits() {
//        return uniqueUnitEao.findAll();
//    }
//
//    public LazyDataModel<UniqueUnit> getLazyUnitModel() {
//        return new LazyDataModel<UniqueUnit>() {
//
//            {
//                setRowCount(uniqueUnitEao.count());
//            }
//
//            @Override
//            public List<UniqueUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//                LOG.debug("LazyDataModel: first={1}, pageSize={2}, sortField={3}, sortOrder={4}", first, pageSize, sortField, sortOrder);
//                return uniqueUnitEao.findAll(first, pageSize);
//            }
//
//        };
//
//    }
}
