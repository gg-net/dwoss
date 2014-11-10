package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

/**
 * DocumentParameter.
 *
 * @author oliver.guenther
 */
@Data
public class MailDocumentParameter implements Serializable {

    private final String name;

    private final String documentType;

    /**
     * Evaluates this parameter object by replacing the paramters in the template.
     * <p>
     * @param template the template, must not be null
     * @return the final replacement object.
     */
    //TODO: Another worst case solution, but we can life with it.
    public String eval(String template) {
        return Objects.requireNonNull(template, "eval was called with null template")
                .replaceAll("\\$parameter.name", getName())
                .replaceAll("\\$parameter.documentType", getDocumentType());

    }
}
