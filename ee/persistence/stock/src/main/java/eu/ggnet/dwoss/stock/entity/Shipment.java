package eu.ggnet.dwoss.stock.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.persistence.entity.Identifiable;

import lombok.*;

@Entity
@Getter
@ToString
public class Shipment implements Serializable, Identifiable {

    public static enum Status {

        ANNOUNCED, DELIVERED, OPENED, CLOSED, RECEIPT_PLANED
    }

    @Id
    @GeneratedValue
    private long id;

    @Setter
    private String shipmentId;

    @Lob
    @Column(length = 65536)
    @Setter
    private String comment;

    @Temporal(TemporalType.DATE)
    @Setter
    private Date date;

    @NotNull
    @Setter
    private TradeName contractor;

    @NotNull
    @Setter
    private TradeName defaultManufacturer;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    private static final long serialVersionUID = 1L;

    /**
     * DefaultConsturctor with status = ANNOUNCED and date = new Date().
     */
    public Shipment() {
        status = Status.ANNOUNCED;
        date = new Date();
    }

    public Shipment(String shipmentId, TradeName contractor, TradeName defaultManufacturer, Status status) {
        this();
        this.shipmentId = shipmentId;
        this.contractor = contractor;
        this.defaultManufacturer = defaultManufacturer;
        this.status = status;
    }

    /**
     * Sideeffect, updates Date.
     *
     * @param status
     */
    public void setStatus(Status status) {
        this.date = new Date();
        this.status = status;
    }

}
