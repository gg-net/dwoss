package eu.ggnet.saft.core.ui;

import eu.ggnet.saft.api.ui.Title;

/**
 * Title util class.
 *
 * @author oliver.guenther
 */
public class TitleUtil {

    /**
     * Returns the Value of {@link Title} if set on the parameter, otherwise empty.
     *
     * @param clazz the clazz to parse
     * @return the Value of {@link Title} if set on the parameter, otherwise empty.
     */
    public static String title(Class<?> clazz) {
        return title(clazz, null);
    }

    /**
     * Returns the Value of {@link Title} if set on the parameter, otherwise empty.
     *
     * @param clazz      a class to be the alternative
     * @param optionalId an optional id.
     * @return the Value of {@link Title} if set on the parameter, otherwise empty.
     */
    public static String title(Class<?> clazz, String optionalId) {
        Title annotation = clazz.getAnnotation(Title.class);
        if ( annotation == null ) return clazz.getSimpleName() + (optionalId == null ? "" : " : " + optionalId);
        if ( optionalId == null ) return annotation.value();
        return annotation.value().replace("{id}", optionalId);
    }

}
