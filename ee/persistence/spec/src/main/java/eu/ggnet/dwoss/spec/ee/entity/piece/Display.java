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
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.common.api.INoteModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static eu.ggnet.dwoss.spec.ee.entity.piece.Display.Resolution.*;
import static eu.ggnet.dwoss.spec.ee.entity.piece.Display.Size.*;

@Entity
@NamedQuery(name = "Display.bySizeResolutionTypeRation", query = "select d from Display d where d.size = ?1 and d.resolution = ?2 and d.type = ?3 and d.ration = ?4")
public class Display implements Serializable {

    static {
        Map<ProductGroup, List<Size>> m = new EnumMap<>(ProductGroup.class);
        m.put(ProductGroup.MONITOR, Arrays.asList(_15, _17, _19, _20, _22));
        m.put(ProductGroup.NOTEBOOK, Arrays.asList(_15));
        m.put(ProductGroup.ALL_IN_ONE, Arrays.asList(_15));
        m.put(ProductGroup.TABLET_SMARTPHONE, Arrays.asList(_7_9, _9_7, _15));
        _4_3 = m;
        m = new EnumMap<>(ProductGroup.class);
        m.put(ProductGroup.MONITOR, Arrays.asList(_19, _22, _24));
        m.put(ProductGroup.NOTEBOOK, Arrays.asList(_12_1, _13_3, _14, _15_4, _17, _17_3));
        m.put(ProductGroup.ALL_IN_ONE, Arrays.asList(_17_3));
        m.put(ProductGroup.TABLET_SMARTPHONE, Arrays.asList(_3_5, _5, _10_1, _12, _12_1));
        _16_10 = m;
        m = new EnumMap<>(ProductGroup.class);
        m.put(ProductGroup.MONITOR, Arrays.asList(_17, _18_5, _20_1, _21_5, _23, _23_6, _24, _26, _27, _28, _32, _37, _42, _10_1, _4, _4_7, _12_9, _35, _4_5, _12_5, _23_8));
        m.put(ProductGroup.NOTEBOOK, Arrays.asList(_8_9, _10_1, _11_6, _13_3, _14, _14_1, _15_6, _16, _17, _17_3, _18_4, _12, _4, _4_7, _27, _12_9, _35, _4_5, _12_5));
        m.put(ProductGroup.ALL_IN_ONE, Arrays.asList(_18_5, _19_5, _20_1, _21_5, _23, _24, _27, _28, _10_1, _4_7, _12_9, _35, _4, _4_5, _12_5, _23_8, _17_3));
        m.put(ProductGroup.TABLET_SMARTPHONE, Arrays.asList(_4, _5, _5_5, _5_7, _6, _7, _7_9, _8, _10_1, _11_6, _12, _4_7, _27, _12_9, _35, _4_5, _12_5));
        _16_9 = m;
        m = new EnumMap<>(ProductGroup.class);
        m.put(ProductGroup.MONITOR, new ArrayList<>());
        m.put(ProductGroup.NOTEBOOK, new ArrayList<>());
        m.put(ProductGroup.ALL_IN_ONE, new ArrayList<>());
        m.put(ProductGroup.TABLET_SMARTPHONE, Arrays.asList(_3_5));
        _3_2 = m;
        m = new EnumMap<>(ProductGroup.class);
        m.put(ProductGroup.MONITOR, Arrays.asList(_25, _29, _34));
        m.put(ProductGroup.NOTEBOOK, Arrays.asList(_25, _29));
        m.put(ProductGroup.ALL_IN_ONE, Arrays.asList(_25, _29));
        m.put(ProductGroup.TABLET_SMARTPHONE, new ArrayList<>());
        _21_9 = m;
    }

    private final static Map<ProductGroup, List<Size>> _4_3;

    private final static Map<ProductGroup, List<Size>> _16_10;

    private final static Map<ProductGroup, List<Size>> _16_9;

    private final static Map<ProductGroup, List<Size>> _3_2;

    private final static Map<ProductGroup, List<Size>> _21_9;

    /**
     * The Ration of a Display.
     */
    @AllArgsConstructor
    public static enum Ration implements INoteModel {

        FOUR_TO_THREE(
                "4:3",
                _4_3,
                EnumSet.of(VGA, SVGA, XGA, SXGA, SXGA_PLUS, UXGA, QXGA)),
        SIXTEEN_TO_TEN(
                "16:10",
                _16_10,
                EnumSet.of(RETINA_4, WSVGA, WXGA, WXGA_PLUS, WSXGA, WSXGA_PLUS, WUXGA, WQXGA, RETINA_PRO15, RETINA_DISPLAY, WVGA, QHD_1440P)),
        SIXTEEN_TO_NINE(
                "16:9",
                _16_9,
                EnumSet.of(WSVGA, RETINA_5SC, WXGA, HD, WSXGA, HD_720P, FULL_HD, QWXGA, WQHD, RETINA_DISPLAY, UHD, APPLE_5K, WUXGA, IPHONE_6, IPAD_PRO, WVGA, QHD_1440P, _4K)),
        THREE_TO_TWO(
                "3:2",
                _3_2,
                EnumSet.of(RETINA_4)),
        TWENTY_ONE_TO_NINE(
                "21:9",
                _21_9,
                EnumSet.of(UWHD, UWQHD));

        @Getter
        private final String note;

        private final Map<ProductGroup, List<Size>> sizes;

        private final EnumSet<Resolution> resolutions;

        public static Set<Ration> getRelevantRations(ProductGroup pg) {
            Set<Ration> relevantRations = EnumSet.noneOf(Ration.class);
            for (Ration ration : values()) {
                if ( ration.sizes.get(pg) == null || ration.sizes.get(pg).isEmpty() ) continue;
                relevantRations.add(ration);
            }
            return relevantRations;
        }

        /**
         * Returns all possible sizes.
         * <p>
         * @return all possible sizes.
         */
        public Set<Size> getSizes() {
            Set<Size> result = EnumSet.noneOf(Size.class);
            for (List<Size> s : sizes.values()) {
                result.addAll(s);
            }
            return result;
        }

        /**
         * Returns possible sizes for a specific product group.
         * <p>
         * @param group the group as filter
         * @return possible sizes for a specific product group.
         */
        public List<Size> getSizes(ProductGroup group) {
            return sizes.get(group);
        }

        /**
         * Returns all possilbe resolutions.
         * <p>
         * @return all possilbe resolutions.
         */
        public EnumSet<Resolution> getResolutions() {
            return EnumSet.copyOf(resolutions);
        }

        /**
         * Returns a set of resolutions up to the supplied maximum resolution.
         * <p>
         * @param maxResolution the maximum resolution
         * @return a set of resolutions up to the supplied maximum resolution
         */
        public EnumSet<Resolution> getResolutions(Resolution maxResolution) {
            EnumSet<Resolution> result = getResolutions();
            result.retainAll(EnumSet.range(Resolution.values()[0], maxResolution));
            return result;
        }
    }

    @AllArgsConstructor
    @Getter
    public static enum Type implements INoteModel {

        CRYSTAL_BRIGHT("Crystal Bright"),
        MATT("Matt");

        private final String note;

    }

    @AllArgsConstructor
    @Getter
    public static enum Resolution implements INoteModel {

        VGA("VGA (640x480)"),
        SVGA("SVGA (800x600)"),
        WSVGA("WSVGA (1024x600)"),
        XGA("XGA (1024x768)"),
        WXGA("WXGA (1280x800)"),
        WXGA_PLUS("WXGA+ (1440x900)"),
        SXGA("SXGA (1280x1024)"),
        HD("HD (1366x768)"),
        WSXGA("WSXGA/HD+ (1600x900)"),
        SXGA_PLUS("SXGA+ (1400x1050)"),
        WSXGA_PLUS("WSXGA+ (1680x1050)"),
        FULL_HD("Full HD (1920x1080)"),
        WUXGA("WUXGA (1920x1200)"),
        UXGA("UXGA (1600x1200)"),
        QWXGA("QWXGA (2048×1152)"),
        QXGA("QXGA (2048x1536)"),
        WQXGA("WQXGA (2560×1600)"),
        RETINA_PRO15("Retina Pro15 (2880x1800)"),
        WQHD("WQHD (2560x1440)"),
        RETINA_4("(960x640)"),
        RETINA_5SC("(1136x640)"),
        HVGA("HVGA (480x320)"),
        IPHONE_6("1334x750"),
        HD_720P("HD 720p (1280x720)"),
        UHD("UHD (3840x2160)"),
        RETINA_DISPLAY("Retina Display (2304x1440)"),
        UWHD("2560 x 1080"),
        UWQHD("3440 x 1440"),
        APPLE_5K("5120 x 2880"),
        IPAD_PRO("2732 x 2048"),
        WVGA("854 x 480"),
        QHD_1440P("QHD (2160 x 1440)"),
        _4K("Real 4K (4096 x 2306)");

        private final String note;

    }

    /**
     * The sizes of Displays.
     */
    @AllArgsConstructor
    public static enum Size implements INoteModel {

        _10_1(10.1, WUXGA),
        _11_6(11.6, FULL_HD),
        _13_3(13.3, WQHD),
        _14(14, FULL_HD),
        _15(15, UHD),
        _15_4(15.4, RETINA_PRO15),
        _15_6(15.6, FULL_HD),
        _16(16, HD),
        _17(17, UHD),
        _17_3(17.3, UHD),
        _18_4(18.4, FULL_HD),
        _18_5(18.5, FULL_HD),
        _19(19, FULL_HD),
        _20_1(20.1, FULL_HD),
        _21_5(21.5, FULL_HD),
        _22(22, WSXGA_PLUS),
        _23(23, FULL_HD),
        _24(24, FULL_HD),
        _26(26, WUXGA),
        _27(27, APPLE_5K),
        _32(32, WUXGA),
        _14_1(14.1, HD),
        _8_9(8.9, WSVGA),
        _12_1(12.1, WSXGA),
        _37(37, WUXGA),
        _42(42, WUXGA),
        _7(7, WXGA),
        _23_6(23.6, FULL_HD),
        _20(20, UXGA),
        _8(8, FULL_HD),
        _7_9(7.9, QXGA),
        _9_7(9.7, QXGA),
        _19_5(19.5, FULL_HD),
        _3_5(3.5, RETINA_4),
        _4(4, RETINA_5SC),
        _5_7(5.7, HD_720P),
        _4_7(4.7, IPHONE_6),
        _5_5(5.5, FULL_HD),
        _5(5, FULL_HD),
        _28(28, UHD),
        _12(12, RETINA_DISPLAY),
        _25(25, UWHD),
        _29(29, UWHD),
        _34(34, UWQHD),
        _12_9(12.9, IPAD_PRO),
        _35(35, UHD),
        _4_5(4.5, WVGA),
        _12_5(12.5, FULL_HD),
        _23_8(23.8, FULL_HD),
        _6(6, FULL_HD);

        private final double size;

        @Getter
        private final Resolution maxResolution;

        private final static DecimalFormat DF = new DecimalFormat("0.00");

        public double getValue() {
            return size;
        }

        @Override
        public String getNote() {
            return size + "\" (" + DF.format(size * 2.54) + " cm)";
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

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Display other = (Display)obj;
        if ( this.id != other.id ) return false;
        return true;
    }

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
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Display{" + "id=" + id + ", led=" + led + ", size=" + size + ", resolution=" + resolution + ", type=" + type + ", ration=" + ration + ", economicValue=" + economicValue + '}';
    }

    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getValidationViolations() {
        if ( resolution.ordinal() > size.getMaxResolution().ordinal() ) return "resolution > size.maxResolution";
        return null;
    }
}
