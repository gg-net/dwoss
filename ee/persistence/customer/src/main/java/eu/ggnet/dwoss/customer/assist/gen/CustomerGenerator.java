package eu.ggnet.dwoss.customer.assist.gen;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.util.gen.Name;
import eu.ggnet.dwoss.util.gen.NameGenerator;
import eu.ggnet.dwoss.util.gen.GeneratedAddress;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.customer.entity.*;

import static eu.ggnet.dwoss.customer.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.entity.Contact.Sex.MALE;

/**
 *
 * @author oliver.guenther
 */
public class CustomerGenerator {

    private final Random R = new Random();

    private class RandomEnum<T extends Enum> {

        private final T[] values;

        public RandomEnum(Class<T> clazz) {
            values = clazz.getEnumConstants();
        }

        public T random() {
            return values[R.nextInt(values.length)];
        }
    }

    private final NameGenerator GEN = new NameGenerator();

    /**
     * Generates a {@link Customer}.
     * This customer will contain randomly generated collections for:<ul>
     * <li>{@link Customer#companies}</li>
     * <li>{@link Customer#contacts}</li>
     * <li>{@link Customer#mandatorMetadata}</li>
     * </ul>
     * with a maximum of 15 each.
     * <p>
     * @return a generated {@link Customer}.
     */
    public Customer makeCustomer() {
        Customer c = new Customer();
        int r = R.nextInt(14) + 1;
        for (int i = 0; i < r; i++) {
            c.add(makeCompany());
            c.add(makeContact());
        }
        c.add(makeMandatorMetadata());
        return c;
    }

    /**
     * Generates a {@link Company}.
     * {@link Company#prefered} is never set.
     * <p>
     * @return a generated {@link Company}.
     */
    public Company makeCompany() {
        Company c = new Company();
        c.setLedger(R.nextInt(1000) + 1);
        c.setName(GEN.makeCompanyName());
        c.add(makeCommunication());
        return c;
    }

    /**
     * Generates an amount of {@link Company}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Company> makeCompanies(int amount) {
        List<Company> contacts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            contacts.add(makeCompany());
        }
        return contacts;
    }

    /**
     * Generates a {@link Contact}.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Contact makeContact() {
        Contact c = new Contact();
        Name n = GEN.makeName();
        c.setFirstName(n.getFirst());
        c.setLastName(n.getLast());
        c.setSex(n.getGender().ordinal() == 1 ? MALE : FEMALE);
        c.setTitle(R.nextInt(1000) % 3 == 0 ? "Dr." : null);
        c.add(makeCommunication());
        c.add(makeAddress());
        return c;
    }

    /**
     * Generates an amount of {@link Contact}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Contact> makeContacts(int amount) {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            contacts.add(makeContact());
        }
        return contacts;
    }

    /**
     * Generates a {@link Address}.
     * {@link Address#preferedType} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Address makeAddress() {
        GeneratedAddress a = GEN.makeAddress();
        Address customerAddress = new Address();
        customerAddress.setCity(a.getTown());
        customerAddress.setStreet(a.getStreet());
        customerAddress.setZipCode(a.getPostalCode());
        return customerAddress;
    }

    /**
     * Generates an amount of persisted {@link Address}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Address> makeAddresses(int amount) {
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            addresses.add(makeAddress());
        }
        return addresses;
    }

    /**
     * Generates a non persisted {@link Communication}.
     * {@link Communication#prefered} is never set.
     * <p>
     * @return a generated {@link Communication}.
     */
    private Communication makeCommunication() {
        Communication c = new Communication();
        c.setType(new RandomEnum<>(Communication.Type.class).random());
        c.setIdentifier(RandomStringUtils.randomAlphanumeric(5));
        return c;
    }

    private MandatorMetadata makeMandatorMetadata() {
        MandatorMetadata m = new MandatorMetadata();
        m.setMandatorMatchcode(RandomStringUtils.randomAlphanumeric(4));
        m.setPaymentCondition(new RandomEnum<>(PaymentCondition.class).random());
        m.setPaymentMethod(new RandomEnum<>(PaymentMethod.class).random());
        m.setShippingCondition(new RandomEnum<>(ShippingCondition.class).random());
        EnumSet.allOf(SalesChannel.class).stream().filter(t -> R.nextInt(10) < 3).forEach(t -> m.add(t));
        return m;
    }

}
