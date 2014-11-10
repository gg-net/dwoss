package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * Masterdata for Mandator.
 * <p/>
 * @author oliver.guenther
 */
@Wither
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Company implements Serializable {

    private String name;

    private String street;

    private String city;

    private String zip;

    private URL logo;

    private String email;

    private String emailName;

    public String toSingleLine() {
        return name + " - " + street + " - " + zip + " " + city;
    }
}
