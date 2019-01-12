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
package eu.ggnet.dwoss.price.ee.engine.support;

import java.util.*;

import eu.ggnet.dwoss.price.ee.EngineTracer;
import eu.ggnet.dwoss.price.ee.engine.Tuple2;

/**
 * This makes the world simple.
 * TODO: Think about direct filters. (Like Validaion Estimate Blocking .... makes multiple Columns in PriceManagement
 *
 * @author oliver.guenther
 */
public class TraceCollector {

    private final List<EngineTracer> tracers = new ArrayList<>();

    public <T> T capture(Tuple2<EngineTracer, T> tuple) {
        tracers.add(tuple._1());
        return tuple._2();
    }

    public void add(EngineTracer t) {
        tracers.add(t);
    }

    public EngineTracer.Status getStatus() {
        EngineTracer.Status status = EngineTracer.Status.INFO;
        for (EngineTracer tracer : tracers) {
            if ( tracer.getStatus().ordinal() > status.ordinal() ) status = tracer.getStatus();
        }
        return status;
    }

    public void error(String clazz, String msg) {
        EngineTracer t = new EngineTracer(clazz, null);
        t.error(msg);
        add(t);
    }

    public String getMessages() {
        StringBuilder messages = new StringBuilder();
        for (Iterator<EngineTracer> it = tracers.iterator(); it.hasNext();) {
            EngineTracer et = it.next();
            messages.append(et.collectMessages());
            if ( it.hasNext() ) messages.append(", ");
        }
        return messages.toString();
    }

}
