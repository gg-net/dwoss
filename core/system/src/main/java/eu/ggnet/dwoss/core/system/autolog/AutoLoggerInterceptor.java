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
package eu.ggnet.dwoss.core.system.autolog;

import java.util.Arrays;

import javax.interceptor.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Interceptor
@AutoLogger
//@Priority()
public class AutoLoggerInterceptor {

    @AroundInvoke
    @AroundTimeout
    public Object manageTransaction(InvocationContext ctx) throws Exception {

        Class<?> clazz = ctx.getMethod().getDeclaringClass();
        String method = ctx.getMethod().getName();
        Logger log = LoggerFactory.getLogger(clazz);
        if ( log.isDebugEnabled() ) log.debug(method + " executed with parameters = " + Arrays.toString(ctx.getParameters()));
        else log.info(method + " excuted");

        try {
            Object result = ctx.proceed();
            log.info(method + " completed sucessful");
            if ( log.isDebugEnabled() ) log.debug(method + " returns = " + result);
            return result;
        } catch (Exception e) {
            log.info(method + " completed with " + e.getClass().getSimpleName() + ":" + e.getMessage());
            throw e;
        }
    }
}
