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
package eu.ggnet.saft.core.cap;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates an intermediate proxy object for a given interface. All calls are
 * cached.
 *
 * @author Martin Ankerl (martin.ankerl@gmail.at)
 * @version $Rev$
 */
public class CachedProxy {

    /**
     * Query object to find out if the exact same query was already made.
     *
     * @author Martin Ankerl (martin.ankerl@profactor.at)
     * @version $Rev$
     */
    private static final class Args {

        private final Method mMethod;

        private final Object[] mArgs;

        private final int mHash;

        public Args(final Method m, final Object[] args) {
            mMethod = m;
            mArgs = args;
            // precalculate hash
            mHash = calcHash();
        }

        /**
         * Method and all the arguments have to be equal. Assumes that obj is of
         * the same type.
         */
        @Override
        public boolean equals(final Object obj) {
            final Args other = (Args)obj;
            if ( !mMethod.equals(other.mMethod) ) {
                return false;
            }
            if ( mArgs != null ) {
                for (int i = 0; i < mArgs.length; ++i) {
                    Object o1 = mArgs[i];
                    Object o2 = other.mArgs[i];
                    if ( !(o1 == null ? o2 == null : o1.equals(o2)) ) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Use the precalculated hash.
         */
        @Override
        public int hashCode() {
            return mHash;
        }

        /**
         * Try to use a good & fast hash function here.
         */
        public int calcHash() {
            int h = mMethod.hashCode();
            if ( mArgs != null ) {
                for (final Object o : mArgs) {
                    h = h * 65599 + (o == null ? 0 : o.hashCode());
                }
            }
            return h;
        }
    }

    /**
     * Creates an intermediate proxy object that uses cached results if
     * available, otherwise calls the given code.
     *
     * @param <T>
     *              Type of the class.
     * @param clazz
     * @param code
     *              The actual calculation code that should be cached.
     * @return The proxy.
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, final T code) {
        // create the cache
        final Map<Args, Object> argsToOutput = new HashMap<>();
        // proxy for the interface T
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                final Args input = new Args(method, args);
                Object result = argsToOutput.get(input);
                // check containsKey to support null values
                if ( result == null && !argsToOutput.containsKey(input) ) {
                    // make sure exceptions are handled transparently
                    try {
                        result = method.invoke(code, args);
                        argsToOutput.put(input, result);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
                return result;
            }
        });
    }
}
