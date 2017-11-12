package eu.ggnet.saft;

import java.awt.Desktop;
import java.awt.Dialog;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

import javafx.stage.Modality;

import eu.ggnet.saft.api.CallableA1;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.all.OkCancelResult;
import eu.ggnet.saft.core.all.OnceCaller;

/**
 * Util is
 *
 * @author oliver.guenther
 */
public class UiUtil {

    @Deprecated
    public static <V, R> Callable<R> onOk(CallableA1<V, R> function, OnceCaller<OkCancelResult<V>> before) {
        return () -> {
            if ( before.ifPresentIsNull() ) return null; // Chainbreaker
            OkCancelResult<V> result = before.get();
            if ( !result.ok ) return null;  // Break Chain on demand
            UiCore.backgroundActivityProperty().set(true);
            R r = function.call(result.value);
            UiCore.backgroundActivityProperty().set(false);
            return r;
        };
    }

    @Deprecated
    public static <T> Callable<Void> osOpen(OnceCaller<T> before) {
        return () -> {
            if ( before.ifPresentIsNull() ) return null; // Chainbreaker
            T beforeResult = before.get();
            if ( beforeResult == null ) return null; // Null result is also useless here.
            if ( beforeResult instanceof File ) {
                Desktop.getDesktop().open((File)beforeResult);
            } else {
                throw new IllegalArgumentException("No Os support for Object Type: " + beforeResult.getClass());
            }
            return null;
        };
    }

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

    public static Optional<Dialog.ModalityType> toSwing(Modality m) {
        if ( m == null ) return Optional.empty();
        switch (m) {
            case APPLICATION_MODAL:
                return Optional.of(Dialog.ModalityType.APPLICATION_MODAL);
            case WINDOW_MODAL:
                return Optional.of(Dialog.ModalityType.DOCUMENT_MODAL);
            case NONE:
                return Optional.of(Dialog.ModalityType.MODELESS);
        }
        return Optional.empty();
    }

    @Deprecated
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if ( cs == null || (strLen = cs.length()) == 0 ) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ( Character.isWhitespace(cs.charAt(i)) == false ) {
                return false;
            }
        }
        return true;
    }

}
