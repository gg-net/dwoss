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
package eu.ggnet.saft;

import java.util.*;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.cap.CachedProxy;
import eu.ggnet.saft.core.cap.RemoteLookup;
import eu.ggnet.saft.core.ui.Workspace;
import eu.ggnet.saft.core.ui.WorkspaceService;

import lombok.Getter;

/**
 * This is the global entry point for fat clients, see in Code for future.
 * The usage for now is only the lookup.
 * <p/>
 * @author oliver.guenther
 * @deprecated Use {@link Dl}
 */
// FIXME: Split this class in WorkspaceServiceDiscovery, LocalLookup and Remotelookup.
// TODO: Find the local lookuped Classes. Should be that mutch.
/*
Folgendes Gedankenexperiment. Wie würde die Leistung dieser Class mit CDI funktionieren.
1. CDI würde nur das lokale Lookup machen -> LocalLookup potentialer Wrapper für CDI
2. Mit CDI würde man das RemoteLookup discovern, wäre aber eh ein Singleton -> Direktimplementation von Remotelookup. Könnte sich ja selbst über CDI finden.
   Methode RemoteLookup.getInstance() {CDI.current().select(RemoteLookup).get() } Alle statischen Methoden wrappen einfach das getInstance().
   Cache ist halt ein Feature von RemoteLookup. Und addSample ist das selbe wie mit CDI den container zu manipulieren.
3. Workspace ist auch ein SingleTon. -> Kann also auch als selbstdiscovernde CDI Singleton betrachtet werden. Sieh oben.
 */
@Deprecated
public class Client {

    // Don't use info Logglevel here until the Progress is lookuped in a different way. e.g. keep the instance until the connection fails.
    private final static Logger L = LoggerFactory.getLogger(Client.class);

    private final static WorkspaceService WORKSPACE = new WorkspaceService();

    private final static Map<String, Object> SAMPLE_STUB = new HashMap<>();

    private final static Map<Class<?>, ? super Object> CACHE = new HashMap<>();

    @Getter
    private static RemoteLookup remoteLookup;
    @Deprecated
    public static void setRemoteLookup(RemoteLookup rl) {
        Client.remoteLookup = rl;
    }

    /**
     * Returns the sample stub for inspection.
     *
     * @return the sample stub for inspection.
     */
    @Deprecated
    public static Map<String, Object> getSampleStub() {
        return Collections.unmodifiableMap(SAMPLE_STUB);
    }

    /**
     * Tries to lookup an implementation of the supplied class/interface.
     * The discovery goes as follows:
     * <ul>
     * <li>clazz is {@link Workspace} return the static handled instance</li>
     * <li>internal cache has a match</li>
     * <li>sample stub has a match</li>
     * <li>a optional set {@link RemoteLookup} has a match</li>
     * <li>a local {@link Lookup#getDefault() } has a match</li>
     * <li>fail with {@link NullPointerException}</li>
     * </ul>
     * <p/>
     * @param <T>   the type of the resulting instance
     * @param clazz the clazz to use as identifier, must not be null.
     * @return the fist found implementation.
     * @throws NullPointerException if clazz is null.
     */
    @Deprecated
    public static <T> T lookup(Class<T> clazz) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(clazz, "clazz is null");
        L.debug("Looking Up {}", clazz.getName());
        //HINT: The Workspace is a special case, we just handle it here. This could be optimized.
        if ( clazz.equals(Workspace.class) ) return (T)WORKSPACE;
        // Loading Cached Values
        if ( CACHE.containsKey(clazz) && CACHE.get(clazz) != null ) {
            L.debug("Using Cache for {}", clazz.getSimpleName());
            return (T)CACHE.get(clazz);
        }
        // The Sample Stub allows the "injection" of different implementations before any real lookup. This is normaly used ony for tryout and samples.
        // This could be done better with a injection framework, but through this implementation, we don't need any server at all.
        if ( SAMPLE_STUB.containsKey(clazz.getName()) ) return (T)SAMPLE_STUB.get(clazz.getName());

        T result = remoteLookupAndCache(clazz);
        if ( result != null ) return result;

        // Final Local lookup an posible exception.
        return Objects.requireNonNull(Lookup.getDefault().lookup(clazz), "No Instance of " + clazz + " via Lookup found");
    }

    /**
     * Allows to ask the Client, if it can find an implementation of the supplied anywhere.
     * Looks into samplestub, remotelookup and local lookup.
     * <p>
     * @param <T>
     * @param clazz the class to look for an implementation
     * @return ture if existent.
     */
    @Deprecated
    public static <T> boolean hasFound(Class<T> clazz) {
        // Allows the hasFound in a sample environment. A key without a value just means a optional service missing.
        if ( SAMPLE_STUB.containsKey(clazz.getName()) ) return SAMPLE_STUB.get(clazz.getName()) != null;
        if ( remoteLookup != null ) return remoteLookup.contains(clazz);
        return Lookup.getDefault().lookup(clazz) != null;
    }

    /**
     * Allows the client to cash lookups of specific classes.
     * The user should be sure, that these never change.
     * <p>
     * @param clazz the class to cache.
     */
    @Deprecated
    public static <T> void enableCache(Class<T> clazz) {
        CACHE.put(clazz, null);
    }

    /**
     * WARNING: Adds a instance to the sample subs.
     * Do not use in productive environment.
     * <p/>
     * @param <T>   type parameter
     * @param clazz the clazz as index
     * @param t     the instance.
     */
    @Deprecated
    public static <T> void addSampleStub(Class<T> clazz, T t) {
        SAMPLE_STUB.put(clazz.getName(), t);
        L.warn("Client lookup Sample Stub filled with {}. If this is happening in the productive system, this is definitivly wrong", clazz.getName());
    }

    /**
     * Returns null or the Reference to the endpoint.
     * If cache is enabled a successfull discovery will be tried to be stored in the cache.
     *
     * @param <T>   The type parameter of the instance.
     * @param clazz the class identifying the instance.
     * @return the instance itself or null.
     */
    @Deprecated
    private static <T> T remoteLookupAndCache(Class<T> clazz) {
        if ( remoteLookup == null ) return null;
        L.debug("Trying RemoteLookup {}", remoteLookup);
        T result = remoteLookup.lookup(clazz);
        if ( CACHE.containsKey(clazz) ) CACHE.put(clazz, CachedProxy.create(clazz, result));
        return result;
    }

}
