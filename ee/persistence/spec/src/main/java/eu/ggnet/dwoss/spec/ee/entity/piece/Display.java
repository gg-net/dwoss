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
package eu.ggnet.dwoss.spec.ee.entity.piece;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.INoteModel;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

import static eu.ggnet.dwoss.spec.ee.entity.piece.Display.Resolution.values;
import static eu.ggnet.dwoss.spec.ee.entity.piece.Display.Size.values;

@Entity
@NamedQuery(name = "Display.bySizeResolutionTypeRation", query = "select d from Display d where d.size = ?1 and d.resolution = ?2 and d.type = ?3 and d.ration = ?4")
@SuppressWarnings("PersistenceUnitPresent")
public class Display extends BaseEntity implements Serializable {

    /**
     * The Ration of a Display.
     */
    public static enum Ration implements INoteModel {

        FOUR_TO_THREE(4, 3),
        SIXTEEN_TO_TEN(16, 10),
        SIXTEEN_TO_NINE(16, 9),
        THREE_TO_TWO(3, 2),
        TWENTY_ONE_TO_NINE(21, 9),
        THIRTEEN_TO_SIX(13, 6, "iPhone X");

        private final int horizontal;

        private final int vertical;

        private final String description;

        private Ration(int horizontal, int vertical, String description) {
            this.horizontal = horizontal;
            this.vertical = vertical;
            this.description = description;
        }

        private Ration(int horizontal, int vertical) {
            this(horizontal, vertical, null);
        }

        @Override
        public String getNote() {
            return horizontal + ":" + vertical + (description != null ? " " + description : "");
        }

        /**
         * Returns Ration ordered by horizontal() + vertical().
         *
         * @return Ration ordered by horizontal() + vertical().
         */
        public static List<Ration> orderedValues() {
            var result = new ArrayList<Ration>(Arrays.asList(values()));
            result.sort(Comparator.comparingInt(r -> r.horizontal + r.vertical));
            return result;
        }

    }

    public static enum Type implements INoteModel {

        CRYSTAL_BRIGHT("Crystal Bright"),
        MATT("Matt");

        private final String note;

        private Type(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

    }

    // TODO: Noch nach werten umbauen.
    public static enum Resolution implements INoteModel {

        VGA("VGA", 640, 480),
        SVGA("SVGA", 800, 600),
        WSVGA("WSVGA", 1024, 600),
        XGA("XGA", 1024, 768),
        WXGA("WXGA", 1280, 800),
        WXGA_PLUS("WXGA+", 1440, 900),
        SXGA("SXGA", 1280, 1024),
        HD("HD", 1366, 768),
        WSXGA("WSXGA/HD+", 1600, 900),
        SXGA_PLUS("SXGA+", 1400, 1050),
        WSXGA_PLUS("WSXGA+", 1680, 1050),
        FULL_HD("Full HD", 1920, 1080),
        WUXGA("WUXGA", 1920, 1200),
        UXGA("UXGA", 1600, 1200),
        QWXGA("QWXGA", 2048, 1152),
        QXGA("QXGA", 2048, 1536),
        WQXGA("WQXGA", 2560, 1600),
        RETINA_PRO15("Retina Pro15", 2880, 1800),
        WQHD("WQHD", 2560, 1440),
        RETINA_4("Retina 4", 960, 640),
        RETINA_5SC("Retina 55C", 1136, 640),
        HVGA("HVGA", 480, 320),
        IPHONE_6(1334, 750),
        HD_720P("HD 720p", 1280, 720),
        UHD("UHD", 3840, 2160),
        RETINA_DISPLAY("Retina Display", 2304, 1440),
        UWHD("UWHD", 2560, 1080),
        UWQHD("UWQHD", 3440, 1440),
        APPLE_5K(5120, 2880),
        IPAD_PRO(2732, 2048),
        WVGA("WVGA", 854, 480),
        QHD_1440P("QHD", 2160, 1440),
        FOUR_K("Real 4K", 4096, 2306),
        UNKNOWN_1(2224, 1668),
        IPHONE_X("iPhone X", 2436, 1125),
        UNKNOWN_3(1792, 828),
        UNKNOWN_4(2688, 1242),
        FIVE_K("5K", 5120, 2880),
        UNKNOWN_6(1280, 960),
        UNKNOWN_7(4096, 2304);

        private final String description;

        private final int height;

        private final int width;

        private Resolution(int width, int height) {
            this(null, width, height);
        }

        private Resolution(String description, int width, int height) {
            this.description = description;
            this.height = height;
            this.width = width;
        }

        @Override
        public String getNote() {
            return "(" + width + "x" + height + ")" + (description != null ? " " + description : "");
        }

        public String description() {
            return description;
        }

        public int height() {
            return height;
        }

        public int width() {
            return width;
        }

        /**
         * Returns Resolution ordered by height() + width().
         *
         * @return Resolution ordered by height() + width().
         */
        public static List<Resolution> orderedValues() {
            var result = new ArrayList<Resolution>(Arrays.asList(values()));
            result.sort(Comparator.comparingInt(r -> r.height + r.width));
            return result;
        }

    }

    /**
     * The sizes of Displays.
     */
    public static enum Size implements INoteModel {

        _10_1(10.1),
        _11_6(11.6),
        _13_3(13.3),
        _14(14),
        _15(15),
        _15_4(15.4),
        _15_6(15.6),
        _16(16),
        _17(17),
        _17_3(17.3),
        _18_4(18.4),
        _18_5(18.5),
        _19(19),
        _20_1(20.1),
        _21_5(21.5),
        _22(22),
        _23(23),
        _24(24),
        _26(26),
        _27(27),
        _32(32),
        _14_1(14.1),
        _8_9(8.9),
        _12_1(12.1),
        _37(37),
        _42(42),
        _7(7),
        _23_6(23.6),
        _20(20),
        _8(8),
        _7_9(7.9),
        _9_7(9.7),
        _19_5(19.5),
        _3_5(3.5),
        _4(4),
        _5_7(5.7),
        _4_7(4.7),
        _5_5(5.5),
        _5(5),
        _28(28),
        _12(12),
        _25(25),
        _29(29),
        _34(34),
        _12_9(12.9),
        _35(35),
        _4_5(4.5),
        _12_5(12.5),
        _23_8(23.8),
        _6(6),
        _55(55),
        _10_5(10.5),
        _5_8(5.8),
        _6_1(6.1),
        _6_5(6.5);

        private final double size;

        private final static DecimalFormat DF = new DecimalFormat("0.00");

        private Size(double size) {
            this.size = size;
        }

        public double getValue() {
            return size;
        }

        @Override
        public String getNote() {
            return size + "\" (" + DF.format(size * 2.54) + " cm)";
        }

        /**
         * Returns sizes ordered by getValue().
         *
         * @return sizes ordered by getValue()
         */
        public static List<Size> orderedValues() {
            var result = new ArrayList<Size>(Arrays.asList(values()));
            result.sort(Comparator.comparingDouble(Size::getValue));
            return result;
        }
    }

    @XmlTransient
    @Id
    @GeneratedValue
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @XmlAttribute
    @Column(columnDefinition = "bit(1)")
    private boolean led;

    @XmlAttribute
    @Column(name = "panelSize")
    @NotNull
    private Size size;

    @XmlAttribute
    @NotNull
    private Resolution resolution;

    @XmlAttribute
    @NotNull
    private Type type;

    @XmlAttribute
    @NotNull
    private Ration ration;

    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public Display() {
    }

    public Display(Size displaySize, Resolution resolutionType, Type displayType, Ration displayRation) {
        this.size = displaySize;
        this.resolution = resolutionType;
        this.type = displayType;
        this.ration = displayRation;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }

    public Double getEconomicValue() {
        return economicValue;
    }

    public void setEconomicValue(Double economicValue) {
        this.economicValue = economicValue;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size displaySize) {
        this.size = displaySize;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolutionType) {
        this.resolution = resolutionType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type displayType) {
        this.type = displayType;
    }

    public Ration getRation() {
        return ration;
    }

    public void setRation(Ration displayRation) {
        this.ration = displayRation;
    }

    public boolean isLed() {
        return led;
    }

    public void setLed(boolean led) {
        this.led = led;
    }
    //</editor-fold>

    public boolean equalsContent(Display other) {
        if ( other == null ) return false;
        if ( this.size != other.size ) return false;
        if ( this.resolution != other.resolution ) return false;
        if ( this.type != other.type ) return false;
        if ( this.ration != other.ration ) return false;
        if ( this.led != other.led ) return false;
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
