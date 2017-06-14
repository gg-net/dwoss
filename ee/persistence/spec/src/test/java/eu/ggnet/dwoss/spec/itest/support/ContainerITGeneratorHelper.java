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
package eu.ggnet.dwoss.spec.itest.support;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.eao.CpuEao;
import eu.ggnet.dwoss.spec.eao.GpuEao;
import eu.ggnet.dwoss.spec.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

/**
 *
 * @author olive
 */
@Stateless
public class ContainerITGeneratorHelper {

    @Inject
    @Specs
    private EntityManager specEm;

    public ProductSpec makeOne() {
        SpecGenerator g = new SpecGenerator();
        ProductSpec spec = g.makeSpec();
        ProductModel model = spec.getModel();
        ProductModelEmo productModelEmo = new ProductModelEmo(specEm);
        model = productModelEmo.request(model.getFamily().getSeries().getBrand(), model.getFamily().getSeries().getGroup(), model.getFamily().getSeries().getName(), model.getFamily().getName(), model.getName());
        spec.setModel(model);
        if ( spec instanceof DisplayAble ) {
            DisplayAble da = (DisplayAble)spec;
            da.setDisplay(new DisplayEmo(specEm).weakRequest(da.getDisplay().getSize(), da.getDisplay().getResolution(), da.getDisplay().getType(), da.getDisplay().getRation()));
        }
        if ( spec instanceof Desktop ) {
            Desktop desktop = (Desktop)spec;
            if ( desktop.getCpu() == null || desktop.getGpu() == null ) throw new IllegalArgumentException("Cpu or Gpu of a Desktop are null. " + desktop);
            Cpu cpu = new CpuEao(specEm).findById(desktop.getCpu().getId());
            Gpu gpu = new GpuEao(specEm).findById(desktop.getGpu().getId());
            if ( cpu != null ) desktop.setCpu(cpu);
            if ( gpu != null ) desktop.setGpu(gpu);
        }
        specEm.persist(spec);
        return spec;
    }

}
