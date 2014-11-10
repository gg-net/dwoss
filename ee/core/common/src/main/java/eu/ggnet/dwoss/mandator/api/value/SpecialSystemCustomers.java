package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import eu.ggnet.dwoss.rules.DocumentType;

import lombok.Value;

/**
 * Contains SystemCustomers, which do not use the {@link DocumentType#BLOCK}, but a special one.
 * <p>
 * @author oliver.guenther
 */
@Value
public class SpecialSystemCustomers implements Serializable {

    private final Map<Long, DocumentType> specialCustomers;

    public Optional<DocumentType> get(Long customerId) {
        return Optional.ofNullable(specialCustomers.get(customerId));
    }

}
