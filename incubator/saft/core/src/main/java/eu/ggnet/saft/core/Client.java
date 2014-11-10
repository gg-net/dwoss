package eu.ggnet.saft.core;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.*;
import javax.naming.*;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;
import org.openide.util.Lookup;
import org.slf4j.*;

import static java.awt.TrayIcon.MessageType.WARNING;
import static java.util.stream.Collectors.joining;

/**
 * This is the global entry point for fat clients.
 * The usage for now is only the lookup.
 * <p/>
 * @author oliver.guenther
 */
//HINT: Name is not perfekt, but we stick with it for now. Alternatives are Lookup, Service, ServiceBus , Valet ...
public class Client {

    static {
        try (InputStream is = Objects.requireNonNull(loadProperties()).openStream()) {
            Properties p = new Properties();
            p.load(is);
            VERSION = p.getProperty("version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final static Logger L = LoggerFactory.getLogger(Client.class);

    private final static String VERSION;

    public final static String[] FIXED_JAVA_EE_MODULE_NAMES = {
        "dwoss-ee-core-common-" + VERSION,
        "dwoss-ee-persistence-customer-" + VERSION,
        "dwoss-ee-extended-misc-" + VERSION,
        "dwoss-ee-extended-receipt-" + VERSION,
        "dwoss-ee-extended-redtape-" + VERSION,
        "dwoss-ee-persistence-redtape-" + VERSION,
        "dwoss-ee-persistence-report-" + VERSION,
        "dwoss-ee-persistence-rights-" + VERSION,
        "dwoss-ee-extended-price-" + VERSION,
        "dwoss-ee-core-progress-" + VERSION,
        "dwoss-ee-persistence-uniqueunit-" + VERSION,
        "dwoss-ee-persistence-spec-" + VERSION,
        "dwoss-ee-persistence-stock-" + VERSION,
        "dwoss-mandator-sample-service -" + VERSION,
        "dw-ee-pro-sopo-" + VERSION,
        "dw-ee-pro-priceestimator-" + VERSION,
        "dw-core-mandator-ggnet-" + VERSION,
        "dw-core-mandator-elus-" + VERSION,
        "dw-ee-persistence-repair-" + VERSION,
        "dw-pro-components-mandator-service-ggnet-" + VERSION,
        "dw-pro-components-mandator-service-elus-" + VERSION
    };

    private final static NavigableSet<String> DYNAMIC_JAVA_EE_MODULE_NAMES = new TreeSet<>();

    private final static String[] EJB_SUFFIXES = {"Operation", "Wrapper", "Bean"};

    private final static WorkspaceService workspace = new WorkspaceService();

    private final static Map<String, Object> sampleStubs = new HashMap<>();

    private final static Map<Class<?>, ? super Object> CACHE = new HashMap<>();

    private static final NavigableMap<String, NavigableSet<String>> CLIENT_JNDI_NAME_CACHE = new TreeMap<>();

    private static TrayIcon sampleStubTrayIcon;

    private static boolean developerAnnoyed = false;

    /**
     * Request a context.
     * <p>
     * @param cause supplying a optional cause, to get, why this context was requested.
     * @return a context
     */
    public static Context context(String cause) {
        // As you can not ask a context if it is close, we have to pick it up always new.
        return Objects.requireNonNull(Lookup.getDefault().lookup(Server.class), "Server not found via LookUp, Nature: " + cause).getContext();
    }

    /**
     * Tries to lookup an implementation of the supplied class/interface.
     * The discovery goes as follows:
     * <ul>
     * <li>If the clazz is {@link Workspace} or {@link WorspaceModifier} return the static handled instance</li>
     * <li>If the clazz doesn't have any of the anotations {@link Remote}, {@link Stateful}, {@link Stateless}, {@link Stateful} then
     * use
     * <code>{@link Lookup#getDefault() }.lookup(clazz)</code> and ensure the result is not null.
     * <ul><li>If the result is null throw a {@link NullPointerException}</li></ul></li>
     * <li>Else aquire a {@link Context}, either using an internal cached one or initiating a new one, query the jndi namespace using each element of "Name
     * Variations" and java:global/"Project Namespace"/"name variations", returning the frist found implementation
     * <ul>
     * <li>Name Variations (name is based on clazz.simpleName)
     * <ul><li>If clazz doesn't have the annotation {@link Remote} use <i>name</i></li>
     * <li>Else:
     * <ol>
     * <li><i>name</i>, if name ends with <i>OperationRemote</i> or <i>WrapperRemote</i></li>
     * <li><i>nameRemote</i>, if name ends with <i>Operation</i> or <i>Wrapper</i></li>
     * <li><i>name</i><code>.substring(0, length - 6)</code>(<i>OperationRemote</i> & <i>WrapperRemote</i> & <i>Operation</i> & <i>Wrapper</i>),
     * if name ends with <i>Remote</i> and 1 or 2 did not match</li>
     * <li><i>name</i>(<i>OperationRemote</i> & <i>WrapperRemote</i> & <i>Operation</i> & <i>Wrapper</i>), if name doesn't end with <i>Remote</i> and 1 or 2 did
     * not match</li>
     * </ol>
     * </li>
     * </ul>
     * </ul>
     * </li>
     * </ul>
     * <p/>
     * @param <T>   the type of the resulting instance
     * @param clazz the clazz to use as identifier, must not be null.
     * @return the fist found implementation.
     * @throws NullPointerException if clazz is null.
     */
    public static <T> T lookup(Class<T> clazz) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(clazz, "clazz is null");
        L.info("Looking Up {}", clazz.getName());
        //HINT: The Workspace is a special case, we just handle it here. This could be optimized.
        if ( clazz.equals(Workspace.class) ) return (T)workspace;
        // Loading Cached Values
        if ( CACHE.containsKey(clazz) && CACHE.get(clazz) != null ) {
            L.debug("Using Cache for {}", clazz.getSimpleName());
            return (T)CACHE.get(clazz);
        }
        // The Sample Stub allows the "injection" of different implementations before any real lookup. This is normaly used ony for tryout and samples.
        // This could be done better with a injection framework, but through this implementation, we don't need any server at all.
        if ( sampleStubs.containsKey(clazz.getName()) ) return (T)sampleStubs.get(clazz.getName());
        // If it doesn't have bean annotations, it must be a local look up.
        if ( !hasBeanAnnotation(clazz) ) return Objects.requireNonNull(Lookup.getDefault().lookup(clazz), "No Instance of " + clazz + " via Lookup found");
        T result = remoteLookup(clazz);
        // fill the cache if enabled (key is set).
        if ( CACHE.containsKey(clazz) ) CACHE.put(clazz, CachedProxy.create(clazz, result));
        return result;
    }

    /**
     * Allows to ask the Client, if it can find an implementation of the supplied somethere.
     * If the Class is not found in the first place but the Client is a Locale Client, it will try the old way.
     * <p>
     * @param <T>
     * @param clazz the class to look for an implementation
     * @return ture if existent.
     */
    public static <T> boolean hasFound(Class<T> clazz) {
        // Allows the hasFound in a sample environment. A key without a value just means a optional service missing.
        if ( sampleStubs.containsKey(clazz.getName()) ) return sampleStubs.get(clazz.getName()) != null;
        boolean containsKey = CLIENT_JNDI_NAME_CACHE.containsKey(clazz.getName());
        if ( containsKey == true ) return true; // For now, only return if positive.
        // In the negative case, start a fallback search.
        return (remoteLookupFallback(clazz, context(clazz.getName()), new ArrayList<>()) != null);
    }

    /**
     * Allows the client to cash lookups of specific classes.
     * The user should be sure, that these never change.
     * <p>
     * @param clazz the class to cache.
     */
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
    public static <T> void addSampleStub(Class<T> clazz, T t) {
        sampleStubs.put(clazz.getName(), t);
        L.warn("Client lookup Sample Stub filled with {}. If this is happening in the productive system, this is definitivly wrong", clazz.getName());
        if ( !SystemTray.isSupported() ) return;
        if ( sampleStubTrayIcon == null ) {
            Image img = Toolkit.getDefaultToolkit().getImage(loadWarningIcon());
            sampleStubTrayIcon = new TrayIcon(img);
            sampleStubTrayIcon.addActionListener(e -> JOptionPane.showMessageDialog(null,
                    "Elements in the Sample Stub\n - " + sampleStubs.keySet().stream().collect(Collectors.joining("\n - ")),
                    "Elements in the Sample Stub",
                    JOptionPane.WARNING_MESSAGE));
            try {
                SystemTray.getSystemTray().add(sampleStubTrayIcon);
                sampleStubTrayIcon.displayMessage("Deutsche Warenwirtschaft Sample Stub Active",
                        "SampleStubs were added to the Client, use only in non productive mode.", WARNING);
            } catch (AWTException ex) {
                L.error("Could not add to SystemTray", ex);
            }
        }
    }

    public static void inspectJndiTree(Context context, String suffix) {
        inspectJndiTree(context, suffix, CLIENT_JNDI_NAME_CACHE);
    }

    /**
     * Inspects the JNDI Name Tree and Fills all founded Classes with interfaces into the foundJndiNames Map.
     * <p>
     * @param context the context
     * @param suffix  the suffix
     * @param cache   a mutable list to fill and for recursion
     * @return the inspected result
     */
    // TODO: It would be great to make this completely functional, but for now it's ok.
    public static NavigableMap<String, NavigableSet<String>> inspectJndiTree(Context context, String suffix, NavigableMap<String, NavigableSet<String>> cache) {
        try {
            NamingEnumeration<NameClassPair> list = context.list(suffix);
            while (list != null && list.hasMore()) {
                try {
                    String name = list.next().getName();
                    if ( name.contains("EjbModule") || name.contains("com.sun.javafx") ) continue; // Ignoring some defaults in the jndi tree
                    String[] split = name.split("!");
                    if ( split.length > 1 ) { // Only want implementains of Beans, everything else is ignored.
                        String key = split[1];
                        String[] values = {suffix + "/" + split[0], split[0] + "Remote"}; // Second element adding without suffix. @OG whats to know why.
                        if ( cache.get(key) == null ) cache.put(key, new TreeSet<>());
                        for (String value : values) {
                            if ( cache.get(key).add(value) ) L.debug("Storing in cache: key={}, value={}", key, value);
                        }
                    }
                    inspectJndiTree(context, suffix + "/" + name, cache);
                } catch (NamingException ex) {
                    L.warn("Jndi Tree inspection on SubSuffix {} failed: {}", suffix, ex.getMessage());
                }
            }
        } catch (NamingException ex) {
            L.warn("Jndi Tree inspection on Suffix {} failed: {}", suffix, ex.getMessage());
        }
        return cache;
    }

    // Extracted, Either the inspectJndiTree becomes successful or we must keep the fallback. Nethertheless this is just for fun here.
    public static NavigableSet<String> inspectJndiTreeForModuleNames(Context context) {
        NavigableSet<String> result = new TreeSet<>();
        String suffix = "java:global";
        try {
            NamingEnumeration<NameClassPair> list = context.list(suffix);
            while (list != null && list.hasMore()) {
                try {
                    String name = list.next().getName();
                    if ( name.contains("EjbModule") || name.contains("com.sun.javafx") ) continue; // Ignoring some values
                    String[] split = name.split("!");
                    if ( split.length == 1 ) {
                        L.debug("Storing in projects {}", suffix + "/" + name);
                        result.add(suffix + "/" + name);
                    }
                } catch (NamingException ex) {
                    L.warn("Jndi Tree Module Name inspection on suffix {} failed: {}", suffix, ex.getMessage());
                }
            }
        } catch (NamingException ex) {
            L.warn("Jndi Tree Module Name inspection on Suffix {} failed: {}", suffix, ex.getMessage());
        }
        return result;
    }

    /**
     * Returns null or the Reference to the running (possibly created instance) of the supplied class.
     *
     * @param <T>   The type parameter of the instance.
     * @param clazz the class identifying the instance.
     * @return the instance itself or null.
     * @throws IllegalArgumentException If some error in the process happens, like nothing is found.
     */
    private static <T> T remoteLookup(Class<T> clazz) throws IllegalArgumentException {
        Context context = context(clazz.getName());
        if ( CLIENT_JNDI_NAME_CACHE.isEmpty() ) {
            L.info("Running Jndi Tree inspection on Suffix: ''");
            inspectJndiTree(context, ""); // Not existing in Local Environment
            L.info("Running Jndi Tree inspection on Suffix: 'java:global'");
            inspectJndiTree(context, "java:global");
            L.info("Running Jndi Tree inspection on Suffix: 'java:module'");
            inspectJndiTree(context, "java:module"); // Olli added, Not existing in Local Environment
            L.info("Running Jndi Tree inspection on Suffix: 'java:app'");
            inspectJndiTree(context, "java:app"); // Olli added, Not existing in Local Environment
            L.info("Running Jndi Tree Module Name inspection");
            DYNAMIC_JAVA_EE_MODULE_NAMES.addAll(inspectJndiTreeForModuleNames(context));
            L.info("Jndi Tree inspection complete, the clientJndiNameCache has now a size of {}", CLIENT_JNDI_NAME_CACHE.size());
            if ( L.isDebugEnabled() ) {
                L.debug("Final CLIENT_JNDI_NAME_CACHE {}", CLIENT_JNDI_NAME_CACHE);
                System.out.println("Final CLIENT_JNDI_NAME_CACHE");
                CLIENT_JNDI_NAME_CACHE.entrySet().forEach(t -> System.out.println(t.getKey() + " - " + t.getValue()));
                L.debug("Final Projects {}", DYNAMIC_JAVA_EE_MODULE_NAMES);
                System.out.println("Final DYNAMIC_JAVA_EE_MODULE_NAMES");
                DYNAMIC_JAVA_EE_MODULE_NAMES.forEach(x -> System.out.println(x));
            }
        }
        List<String> errors = new ArrayList<>();
        String clazzName = clazz.getName();
        if ( CLIENT_JNDI_NAME_CACHE.containsKey(clazzName) ) {
            for (String name : CLIENT_JNDI_NAME_CACHE.get(clazzName)) {
                try {
                    T result = (T)context.lookup(name);
                    L.debug("Succesful look up via Cache(key={},value={}) class {}", clazzName, name, result.getClass().getName());
                    context.close();
                    return result;
                } catch (NamingException ne) {
                    errors.add("NamingException(jndiName=" + name + ", message=" + ne.getMessage() + ")");
                }
            }
        }
        T result = remoteLookupFallback(clazz, context, errors);
        if ( result != null ) return result;
        throw new IllegalArgumentException("No Candidate for " + clazz.getSimpleName() + " was found, tried:\n"
                + errors.stream().collect(joining("\n "))
                + "\nUsing Cache:\n"
                + CLIENT_JNDI_NAME_CACHE.entrySet().stream().map(e -> " - " + e.getKey() + " : " + e.getValue()).collect(joining("\n")));
    }

    static URL loadProperties() {
        return Client.class.getResource("project.properties");
    }

    static URL loadWarningIcon() {
        return Client.class.getResource("warning-icon.png");
    }

    /**
     * This Method is a Fallback on the Local client because it is possible, for unknown reason, that the Progress and Common Project is not in the JNDI Name
     * tree.
     * <p>
     * @param <T>
     * @param clazz
     * @param context
     * @param errors
     * @return
     */
    private static <T> T remoteLookupFallback(Class<T> clazz, Context context, List<String> errors) {
        String name = clazz.getSimpleName();
        List<String> nameVariations = new ArrayList<>();
        if ( clazz.getAnnotation(Remote.class) == null ) {
            nameVariations.add(name);
        } else { // The Remote Name Variations
            for (String suffix : EJB_SUFFIXES) {
                if ( name.endsWith(suffix + "Remote") ) nameVariations.add(name);
            }
            if ( nameVariations.isEmpty() ) { // If the first does not match
                for (String opname : EJB_SUFFIXES) {
                    nameVariations.add((name.endsWith("Remote") ? name.substring(0, name.length() - 6) : name) + opname + "Remote");
                    nameVariations.add((name.endsWith("Remote") ? name.substring(0, name.length() - 6) : name) + opname);
                }
            }
        }
        List<String> jndiNames = new ArrayList<>(nameVariations);
        for (String projectNamespace : FIXED_JAVA_EE_MODULE_NAMES) {
            for (String nameVariation : nameVariations) {
                jndiNames.add("java:global/" + projectNamespace + "/" + nameVariation);
            }
        }
        for (String jndiName : jndiNames) {
            try {
                // TODO: If this exception never happens, get ride of the requireNonNull(),
                Object result = Objects.requireNonNull(context.lookup(jndiName), "Lookup of " + jndiName + ", returned null, renable null handling.");
//                if ( result == null ) {
//                    errors.add(jndiName + "=no result");
//                    continue;
//                }
                L.warn("Succesful Fallback Lookup is used for {} ", clazz.getName());
                // Special Developer handling
                annoyDeveloper(clazz.getName());
                // Closing the Context afterwards.
                context.close();
                return (T)result;
            } catch (NamingException ex) {
                errors.add("NamingException(jndiName=" + jndiName + ", message=" + ex.getMessage() + ")");
            } catch (ClassCastException ex) {
                errors.add("ClassCastException:" + ex.getMessage());
            }
        }
        return null;
    }

    private static void annoyDeveloper(String name) {
        if ( developerAnnoyed ) return;
        developerAnnoyed = true;
        List<String> devs = Arrays.asList("oliver.guenther", "bastian.venz", "pascal.perau");
        if ( !devs.contains(SystemUtils.USER_NAME) ) return;
        final String msg = "Found via Fallback, but not via Cache, Lookup:" + name + "\n"
                + "Cache:\n"
                + CLIENT_JNDI_NAME_CACHE.entrySet().stream().map(e -> " - " + e.getKey() + " : " + e.getValue()).collect(joining("\n"));
        EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(null, msg, "Fallback Lookup in use", JOptionPane.ERROR_MESSAGE));
    }

    private static boolean hasBeanAnnotation(Class<?> clazz) {
        if ( clazz.getAnnotation(Remote.class) != null ) return true;
        if ( clazz.getAnnotation(Stateless.class) != null ) return true;
        if ( clazz.getAnnotation(Stateful.class) != null ) return true;
        return clazz.getAnnotation(Singleton.class) != null;
    }
}
