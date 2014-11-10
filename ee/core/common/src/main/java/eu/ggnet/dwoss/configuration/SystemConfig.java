package eu.ggnet.dwoss.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains configuration options to tweak the system configuration.
 * <p/>
 * @author oliver.guenther
 */
public class SystemConfig {

    static {

        Map<String, String> o = new HashMap<>();
        o.put("log4j.category.OpenEJB.options", "warn");
        o.put("log4j.category.OpenEJB.server", "warn");
        o.put("log4j.category.OpenEJB.cdi", "warn");
        o.put("log4j.category.OpenEJB.startup", "warn");
        o.put("log4j.category.OpenEJB.startup.service", "warn");
        o.put("log4j.category.OpenEJB.startup.config", "warn");

        OPENEJB_LOG_WARN = o;

        o = new HashMap<>();

        o.put("openejb.deployments.classpath.require.descriptor", "true");

        OPENEJB_EJB_XML_DISCOVER = o;

        o = new HashMap<>();

        o.put("log4j.rootLogger", "TRACE,socket,console");
        o.put("log4j.category.OpenEJB", "info");
        o.put("log4j.category.OpenEJB.options", "info");
        o.put("log4j.category.OpenEJB.server", "info");
        o.put("log4j.category.OpenEJB.startup", "info");
        o.put("log4j.category.OpenEJB.startup.service", "warn");
        o.put("log4j.category.OpenEJB.startup.config", "info");
        o.put("log4j.category.OpenEJB.hsql", "info");
        o.put("log4j.category.CORBA-Adapter", "info");
        o.put("log4j.category.Transaction", "warn");
        o.put("log4j.category.org.apache.activemq", "error");
        o.put("log4j.category.org.apache.geronimo", "error");
        o.put("log4j.category.openjpa", "warn");
//        o.put("log4j.category.org.hibernate", "debug");
//        o.put("log4j.category.org.hibernate.internal.SessionFactoryImpl", "error");
        o.put("log4j.category.org.hibernate.tool.hbm2ddl", "fatal");
        o.put("log4j.category.de", "trace");

        o.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
        o.put("log4j.appender.console.layout", "org.apache.log4j.SimpleLayout");
        o.put("log4j.appender.console.threshold", "ERROR");
        o.put("log4j.appender.socket", "org.apache.log4j.net.SocketAppender");
        o.put("log4j.appender.socket.Port", "4445");
        o.put("log4j.appender.socket.threshold", "TRACE");
        o.put("log4j.appender.socket.RemoteHost", "localhost");

        OPENEJB_LOG_TESTING = o;

        o = new HashMap<>();

        o.put("log4j.rootLogger", "DEBUG , warnFile, infoFile, socket");
        o.put("log4j.category.org.hibernate.tool.hbm2ddl", "fatal");

//        o.put("log4j.appender.splash", "de.dw.SplashAppender");
//        o.put("log4j.appender.splash.threshold", "INFO");
        o.put("log4j.appender.socket", "org.apache.log4j.net.SocketAppender");
        o.put("log4j.appender.socket.Port", "4445");
        o.put("log4j.appender.socket.threshold", "DEBUG");
        o.put("log4j.appender.socket.RemoteHost", "localhost");

//        o.put("log4j.appender.syslog", "org.apache.log4j.net.SyslogAppender");
//        o.put("log4j.appender.syslog.threshold", "DEBUG");
//        o.put("log4j.appender.syslog.layout", "org.apache.log4j.PatternLayout");
//        o.put("log4j.appender.syslog.layout.ConversionPattern", "%-5p [%t] %c.%M: %m %x");
//        o.put("log4j.appender.syslog.header", "TRUE");
//        o.put("log4j.appender.syslog.syslogHost", "starscream.gg-net.de");
//        o.put("log4j.appender.syslog.facility", "LOCAL0");
//        o.put("log4j.appender.infoFile", "org.apache.log4j.FileAppender");
//        o.put("log4j.appender.infoFile.file", GlobalConfig.USER_CONFIG_DIRECTORY.getPath() + "/logs/info.log");
//        o.put("log4j.appender.infoFile.append", "false");
//        o.put("log4j.appender.infoFile.threshold", "INFO");
//        o.put("log4j.appender.infoFile.layout", "org.apache.log4j.PatternLayout");
//        o.put("log4j.appender.infoFile.layout.ConversionPattern", "%d{ISO8601} %-5p [%t] %c: %m%n");
//
//        o.put("log4j.appender.warnFile", "org.apache.log4j.RollingFileAppender");
//        o.put("log4j.appender.warnFile.file", GlobalConfig.USER_CONFIG_DIRECTORY.getPath() + "/logs/problem.log");
//        o.put("log4j.appender.warnFile.maxFileSize", "2MB");
//        o.put("log4j.appender.warnFile.threshold", "WARN");
//        o.put("log4j.appender.warnFile.layout", "org.apache.log4j.PatternLayout");
//        o.put("log4j.appender.warnFile.layout.ConversionPattern", "%d{ISO8601} %-5p [%t] %c: %m%n");
        OPENEJB_LOG_PRODUCTIVE = o;
    }

    /**
     * Sets all logging outputs of an OpenEJB instance to warn.
     */
    public final static Map<String, String> OPENEJB_LOG_WARN;

    /**
     * Disables classpath discovery, only takes jars with ejb-jar.xml
     */
    public final static Map<String, String> OPENEJB_EJB_XML_DISCOVER;

    /**
     * Supplys different logging schema including a socket connection.
     */
    public final static Map<String, String> OPENEJB_LOG_TESTING;

    /**
     * Supplys different logging schema including a socket connection, syslog and local files.
     */
    public final static Map<String, String> OPENEJB_LOG_PRODUCTIVE;
}
