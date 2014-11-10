package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.metawidget.inspector.annotation.UiLarge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 *
 * @author oliver.guenther
 */
@Wither
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ListingMailConfiguration implements Serializable {

    @NotNull
    private String fromAddress; // Reconsider if we need that. Wie have a default.

    @NotNull
    private String toAddress;

    @NotNull
    private String subject;

    @UiLarge
    @NotNull
    private String message;

    @NotNull
    private String charset;

    private String signature;

    public String toMessage() {
        return message + (signature == null ? "" : signature);
    }
}
