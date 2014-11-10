package eu.ggnet.dwoss.common.log;

import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Interceptor
@AutoLogger
public class AutoLoggerInterceptor {

    @AroundInvoke
    @AroundTimeout
    public Object manageTransaction(InvocationContext ctx) throws Exception {
        Class<?> clazz = ctx.getMethod().getDeclaringClass();
        String method = ctx.getMethod().getName();
        Logger log = LoggerFactory.getLogger(clazz);
        if ( log.isDebugEnabled() ) log.debug(method + " executed with parameters = " + ctx.getParameters());
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
