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
package eu.ggnet.dwoss.spec.ee.entity;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import eu.ggnet.dwoss.core.common.INoteModel;

/**
 * The extension of information for {@link ProductSpec}s.
 * <p>
 * @author oliver.guenther
 * @has n - m BasicSpec.VideoPort
 * @has 1 - n BasicSpec.Color
 * @has 0..n - 0..n ProductSpec.Extra
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class BasicSpec extends ProductSpec {

    /**
     * Enum class with the colors they possible for a part.
     */
    public static enum Color implements INoteModel {

        RED("rot"),
        GREEN("grün"),
        ORANGE("orange"),
        BLACK("schwarz"),
        SILVER("silber"),
        BLUE("blau"),
        WHITE("weiß"),
        PINK("pink"),
        GREY("grau"),
        WHITE_SILVER("weiss-silber"),
        BLACK_BROWN("schwarz-braun"),
        BLACK_SILVER("schwarz-silber"),
        BLACK_ORANGE("schwarz-orange"),
        BLACK_WHITE("schwarz-weiss"),
        BLACK_RED("schwarz-rot"),
        BLACK_BLUE("schwarz-blau"),
        BROWN("braun"),
        BROWN_SILVER("braun-silber"),
        COPPER("bronze"),
        COPPER_SILVER("bronze-silber"),
        PURPPLE_WHITE("lila-weiß"),
        BLUE_WHITE("blau-weiß"),
        RED_SILVER("rot-silber"),
        RED_WHITE("rot-weiß"),
        PURPLE("lila"),
        PURPLE_BLACK("lila-schwarz"),
        ROSEGOLD("rosegold"),
        GOLD("gold"),
        APRICOT("apricot"),
        BLACK_COPPER("schwarz-kupfer"),
        BLACK_GREY("schwarz-grau"),
        BLACK_GOLD("schwarz-gold");

        private String note;

        private Color(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    /**
     * Enum class that contains all available display connections.
     */
    public enum VideoPort implements INoteModel {

        VGA("VGA"),
        HDMI("HDMI", 5),
        DVI("DVI"),
        MINI_HDMI("mini HDMI"),
        DISPLAY_PORT("Display Port"),
        MINI_DISPLAY_PORT("mini Display Port");

        private final String note;

        private final double economicValue;

        private VideoPort(String note) {
            this(note, 0);
        }

        private VideoPort(String note, double economicValue) {
            this.note = note;
            this.economicValue = economicValue;
        }

        @Override
        public String getNote() {
            return note;
        }

        public double getEconomicValue() {
            return economicValue;
        }
    }

    @XmlElement(name = "port")
    @XmlElementWrapper
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<VideoPort> videoPorts = EnumSet.noneOf(VideoPort.class);

    @XmlElement(name = "extra")
    @XmlElementWrapper
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Extra> extras = EnumSet.noneOf(Extra.class);

    private Color color;

    @Lob
    @Column(length = 65536)
    private String comment;

    public BasicSpec(String partNo, Long productId) {
        super(partNo, productId);
    }

    public BasicSpec() {
    }

    /**
     * Non Productive Constructor
     *
     * @param id the database id, normally auto generated
     */
    BasicSpec(long id) {
        super(id);
    }

    public Set<Extra> getExtras() {
        return extras;
    }

    public void setExtras(Set<Extra> extras) {
        this.extras = extras;
    }

    public void setExtras(Extra... extraa) {
        this.extras = EnumSet.copyOf(Arrays.asList(extraa));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<VideoPort> getVideoPorts() {
        return videoPorts;
    }

    public void setVideoPorts(Set<VideoPort> availableConnections) {
        this.videoPorts = availableConnections;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "BasicDetails{" + super.toString() + "videoPorts=" + videoPorts + ", extras=" + extras
                + ", color=" + color + ", comment=" + comment + '}';
    }
}
