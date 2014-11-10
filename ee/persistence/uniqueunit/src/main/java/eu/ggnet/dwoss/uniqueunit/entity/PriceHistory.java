package eu.ggnet.dwoss.uniqueunit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A history to keep track of price changes in {@link UniqueUnit}s and {@link Product}s
 * <p/>
 * @author pascal.perau
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class PriceHistory implements Serializable {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @Getter
    private PriceType type;

    @Getter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fromDate")
    private Date date;

    @Getter
    private double price;

    @Lob
    @Getter
    @Setter
    @Column(length = 65536)
    private String comment;

    public PriceHistory(PriceType type, double price, Date date, String comment) {
        this.type = type;
        this.date = date;
        this.price = price;
        this.comment = comment;
    }
}
