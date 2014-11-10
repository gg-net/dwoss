package eu.ggnet.dwoss.customer.api;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A concise representation of a customer.
 * <p>
 * @author pascal.perau
 */
@Data
@AllArgsConstructor
public class UiCustomer implements Serializable {

    private long id;

    private String title;

    private String firstName;

    private String lastName;

    private String company;

    private String simpleHtml;

    private String email;

    /**
     * Ledger of the FiBu, if 0 not used.
     */
    private int ledger;

    public String toNameCompanyLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(company != null ? company + " " : "");
        sb.append(title != null ? title + " " : "");
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

    public String toTitleNameLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(title != null ? title + " " : "");
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

    public String toNameLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

}
