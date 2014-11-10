package eu.ggnet.dwoss.util.gen;

/**
 * A Address
 *
 * @author oliver.guenther
 */
public class GeneratedAddress {

    private final String street;

    private final int number;

    private final String postalCode;

    private final String town;

    public GeneratedAddress(String street, int number, String postalCode, String town) {
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public int getNumber() {
        return number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTown() {
        return town;
    }

    @Override
    public String toString() {
        return "Address{" + "street=" + street + ", number=" + number + ", postalCode=" + postalCode + ", town=" + town + '}';
    }

}
