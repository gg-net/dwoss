package eu.ggnet.saft.core.exception;

import java.io.*;

/**
 * Util class to get or format Exceptions to strings.
 *
 * @author oliver.guenther
 */
public class ExceptionUtil {

    /**
     * Extract the deepest Throwable and return its message.
     *
     * @param ex the exception to parse the stack trace.
     * @return the simple class name and the message of the deepest throwable.
     */
    public static String extractDeepestMessage(Throwable ex) {
        if (ex == null) return "";
        if (ex.getCause() == null) return ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage();
        return extractDeepestMessage(ex.getCause());
    }

    /**
     * Returns all stack trace class simple names and messages as a multiline string.
     *
     * @param ex the exception to start with.
     * @return all messages and class names.
     */
    public static String toMultilineStacktraceMessages(Throwable ex) {
        if (ex == null) return "";
        if (ex.getCause() == null) return ex.getClass().getSimpleName() + ":" + ex.getLocalizedMessage();
        return ex.getClass().getSimpleName() + ":" + ex.getLocalizedMessage() + "\n" + toMultilineStacktraceMessages(ex.getCause());
    }

    /**
     * Converts exception stack trace as string
     *
     * @param ex
     * @return
     */
    public static String toStackStrace(Throwable ex) {
        try (StringWriter sw = new StringWriter()) {
            ex.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static boolean containsInStacktrace(Class<?> clazz, Throwable ex) {
        if (ex == null) return false;
        if (ex.getClass().equals(clazz)) return true;
        return containsInStacktrace(clazz, ex.getCause());
    }

    public static <T> T extractFromStraktrace(Class<T> clazz, Throwable ex) {
        if (ex == null) throw new NullPointerException("No Class in Stacktrace : " + clazz);
        if (ex.getClass().equals(clazz)) return (T) ex;
        return extractFromStraktrace(clazz, ex.getCause());
    }
}
