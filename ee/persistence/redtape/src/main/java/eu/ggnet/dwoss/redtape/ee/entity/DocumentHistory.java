/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.redtape.ee.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
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
    //</editor-fold>
    
    @Override
    public String toString() {
        return "DocumentHistory{" + "release=" + release + ", arranger=" + arranger + ", comment=" + comment + '}';
    }

}
