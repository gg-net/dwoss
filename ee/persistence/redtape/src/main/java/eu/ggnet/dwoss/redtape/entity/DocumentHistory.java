package eu.ggnet.dwoss.redtape.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Represents the History of a Document.
 * It is embedded to make it easier to build. It is assumed, that history information are create in an operation, while the Document itself
 * may be changed at ui level.
 *
 * @author oliver.guenther
 */
@Embeddable
public class DocumentHistory implements Serializable {

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "historyRelease", columnDefinition = "DATETIME")
    private Date release;

    @NotNull
    @Column(name = "historyArranger")
    private String arranger;

    @Lob
    @NotNull
    @Column(length = 65536, name = "historyComment")
    private String comment;

    public DocumentHistory() {
        this(new Date(), "", "");
    }

    public DocumentHistory(String arranger, String comment) {
        this(new Date(), arranger, comment);
    }

    public DocumentHistory(Date release, String arranger, String comment) {
        this.release = release;
        this.arranger = arranger;
        this.comment = comment;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public String getArranger() {
        return arranger;
    }

    public void setArranger(String arranger) {
        this.arranger = arranger;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "DocumentHistory{" + "release=" + release + ", arranger=" + arranger + ", comment=" + comment + '}';
    }

}
