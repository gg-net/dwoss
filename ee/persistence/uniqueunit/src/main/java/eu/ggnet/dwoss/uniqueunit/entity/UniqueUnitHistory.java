package eu.ggnet.dwoss.uniqueunit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

import lombok.*;

/**
 * Represents a history element for a {@link UniqueUnit}.
 * <p/>
 * @has 1 - 1 Type
 * @author pascal.perau
 */
@Entity
@NoArgsConstructor
@ToString(exclude = "uniqueUnit")
public class UniqueUnitHistory extends IdentifiableEntity implements Serializable, Comparable<UniqueUnitHistory> {

    @Deprecated // TODO: Not in use, remove some day
    public enum Type {

        UNDEFINED, UNIQUE_UNIT, STOCK, SOPO
    }

    @Id
    @GeneratedValue
    @Getter
    private long id;

    @Basic
    @Lob
    @Getter
    @Setter
    @Column(length = 65536)
    private String comment;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    @Getter
    @Setter
    private Date occurence;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @Getter
    UniqueUnit uniqueUnit;

    // TODO: Change type to some optional String based tag, or remove
    @Getter
    @Setter
    private Type type;

    /**
     * Id bases Constructor, do not use in production environment
     *
     * @param id
     * @param type
     * @param occurence
     * @param comment
     */
    public UniqueUnitHistory(int id, Type type, Date occurence, String comment) {
        this.id = id;
        this.comment = comment;
        this.occurence = occurence;
        this.type = type;
    }

    public UniqueUnitHistory(Type type, String comment) {
        this.comment = comment;
        this.type = type;
        this.occurence = new Date();
    }

    public UniqueUnitHistory(Type type, Date occurence, String comment) {
        this.comment = comment;
        this.occurence = occurence;
        this.type = type;
    }

    @Override
    public int compareTo(UniqueUnitHistory o) {
        if ( this.equals(o) ) return 0;
        if ( this.occurence.equals(o.occurence) ) return (this.hashCode() - o.hashCode()) > 0 ? -1 : 1;
        return this.occurence.compareTo(o.occurence);
    }
}
