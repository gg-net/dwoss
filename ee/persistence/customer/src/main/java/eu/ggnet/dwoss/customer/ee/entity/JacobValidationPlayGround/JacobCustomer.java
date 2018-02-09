package eu.ggnet.dwoss.customer.ee.entity.JacobValidationPlayGround;

import eu.ggnet.dwoss.customer.ee.entity.Company;
import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.*;

import lombok.*;

@ToString(exclude = {"companies", "contacts", "mandatorMetadata"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@SuppressWarnings({"FieldMayBeFinal"})
public class JacobCustomer implements Serializable {

    /**
     * Fields for detailed Search.
     */
    public static enum SearchField {
        ID,
        FIRSTNAME,
        LASTNAME,
        COMPANY,
        ADDRESS
    }

    @AllArgsConstructor
    @Getter
    public static enum Source {
        EXISTING("Bestandskunde"),
        JH_CAM_TOOL("CAM Tool T&S"),
        SOPO_STORE("Sonderposten Store"),
        SOPO_ONLINE("Sonderposten Online"),
        ONEADO("Oneado Online Shop");

        private final String name;
    }

    public static enum ExternalSystem {
        SAGE, LEXWARE
    }

    @Id
    @Getter
    @GeneratedValue
    private long id;

    @Getter
    @Version
    private short optLock;

    /**
     * A list of {@link Company}<code>s</code> represented by the customer.
     */
    @Getter
    @NotNull
    private List<Company> companies = new ArrayList<>();

    /**
     * All contacts association with the customer.
     */
    @Getter
    @NotNull
    private List<Contact> contacts = new ArrayList<>();

    /**
     * Optional Mandator Metadate.
     */
    @Getter
    @NotNull
    private List<MandatorMetadata> mandatorMetadata = new ArrayList<>();

    @Getter
    @NotNull
    private Set<CustomerFlag> flags = new HashSet<>();

    @Getter
    @Setter
    private Source source;

    @Getter
    @NotNull
    private Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    @Getter
    @Setter
    private String keyAccounter;  // Null is ok.

    @Getter
    @Setter
    private String comment;

    /**
     * maximum of size2, consisting of
     */
    @Getter
    @Size(min = 1, max = 2)
    private List<AddressLabel> addressLabels = new ArrayList<>();
}
