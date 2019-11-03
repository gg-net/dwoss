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
package eu.ggnet.dwoss.spec.ee.format;

import java.util.*;

import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.Color;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.VideoPort;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Hdd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Odd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Os;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.core.common.INoteModel;

/**
 *
 * @author oliver.guenther, pascal.perau
 */
public abstract class SpecFormater {

    private static String randomVar() {
        return "" + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)));
    }

    public static String toSource(ProductSpec spec) {
        String var = randomVar();
        String re = spec.getClass().getSimpleName() + " " + var + " = new " + spec.getClass().getName() + "();\n";

        if ( spec == null ) return "ProductSpec is null";
        if ( spec instanceof DesktopBundle ) re += toDesktopBundleSource(var, (DesktopBundle)spec);
        if ( spec instanceof Desktop ) re += toDesktopSpecSource(var, (Desktop)spec);
        if ( spec instanceof DisplayAble ) re += toDisplaySource(var, ((DisplayAble)spec).getDisplay());
        if ( spec instanceof BasicSpec ) re += toBasicSpecSource(var, (BasicSpec)spec);
        return re;
    }

    private static String toBasicSpecSource(String var, BasicSpec spec) {
        String r = "";
        if ( spec.getExtras() != null && !spec.getExtras().isEmpty() ) {
            r += var + ".setExtras(";
            for (Iterator it = spec.getExtras().iterator(); it.hasNext();) {
                Extra extra = (Extra)it.next();
                r += "ProductSpec.Extra." + extra;
                if ( it.hasNext() ) {
                    r += ",";
                }
            }
            r += ");\n";
        }
        if ( spec.getVideoPorts() != null && !spec.getVideoPorts().isEmpty() ) {
            r += var + ".setVideoPorts(EnumSet.of(";
            for (Iterator it = spec.getVideoPorts().iterator(); it.hasNext();) {
                VideoPort port = (VideoPort)it.next();
                r += "BasicSpec.VideoPort." + port;
                if ( it.hasNext() ) {
                    r += ",";
                }
            }
            r += "));\n";
        }
        if ( spec.getColor() != null ) r += var + ".setColor(Color." + spec.getColor() + ");\n";
        if ( spec.getComment() != null && !spec.getComment().isEmpty() ) r += var + ".setComment(\"" + spec.getComment().replaceAll("\\\\", "\\\\\\\\").
                    replaceAll("\\\"", "\\\\\"") + "\");\n";
        return r;
    }

    private static String toDesktopSpecSource(String var, Desktop desktop) {
        String r = "";
        if ( desktop.getOs() != null ) r += var + ".setOs(Desktop.Os." + desktop.getOs() + ");\n";
        if ( desktop.getCpu() != null ) r += var + ".setCpu(" + CpuFormater.toSource(desktop.getCpu()) + ");\n";
        if ( desktop.getGpu() != null ) r += var + ".setGpu(" + GpuFormater.toSource(desktop.getGpu()) + ");\n";
        if ( desktop.getHdds() != null && desktop.getHdds().isEmpty() ) {
            r += var + ".setHdds(EnumSet.of(";
            for (Iterator it = desktop.getHdds().iterator(); it.hasNext();) {
                Hdd hdd = (Hdd)it.next();
                r += "Desktop.Hdd." + hdd;
                if ( it.hasNext() ) {
                    r += ",";
                }
            }
            r += "));\n";
        }
        if ( desktop.getOdds() != null && desktop.getHdds().isEmpty() ) {
            r += var + ".setOdds(EnumSet.of(";
            for (Iterator it = desktop.getOdds().iterator(); it.hasNext();) {
                Odd odd = (Odd)it.next();
                r += "Desktop.Odd." + odd;
                if ( it.hasNext() ) {
                    r += ",";
                }
            }
            r += "));\n";
        }
        r += var + ".setMemory(" + desktop.getMemory() + ");\n";
        return r;
    }

    private static String toDisplaySource(String var, Display display) {
        String r = "";
        if ( display.getSize() != null ) r += var + ".getDisplay().setSize(Display.Size." + display.getSize() + ");\n";
        if ( display.getResolution() != null ) r += var + ".getDisplay().setResolution(Display.Resolution." + display.getResolution() + ");\n";
        if ( display.getType() != null ) r += var + ".getDisplay().setType(Display.Type." + display.getType() + ");\n";
        if ( display.getRation() != null ) r += var + ".getDisplay().setRation(Display.Ration." + display.getRation() + ");\n";
        r += var + ".getDisplay().setLed(" + display.isLed() + ");\n";
        return r;
    }

    private static String toDesktopBundleSource(String var, DesktopBundle bundle) {
        String r = "";
        String v1 = randomVar();
        String v2 = randomVar();
        String desk = "Desktop " + v1 + " = new Desktop();\n" + toDesktopSpecSource(v1, (Desktop)bundle.getDesktop());
        desk += toBasicSpecSource(v1, (Desktop)bundle.getDesktop());
        String mon = "Monitor " + v2 + " = new Monitor();\n" + toDisplaySource(v2, ((Monitor)bundle.getMonitor()).getDisplay());
        mon += toBasicSpecSource(v2, (Monitor)bundle.getMonitor());
        if ( bundle.getDesktop() != null ) r += desk + var + ".setDesktop(" + v1 + ");\n";
        if ( bundle.getMonitor() != null ) r += mon + var + ".setMonitor(" + v2 + ");\n";
        return r;
    }

    public static String toName(ProductSpec spec) {
        if ( spec == null ) return "ProductSpecification ist null";
        return spec.getModel().getFamily().getSeries().getBrand().getName() + " " + spec.getModel().getName();
    }

    /**
     * Returns Group - Brand - Model - (PartNo).
     * <p>
     * @param spec the spech to render.
     * @return Group - Brand - Model - (PartNo).
     */
    public static String toDetailedName(ProductSpec spec) {
        if ( spec == null ) return "ProductSpecification ist null";
        return spec.getModel().getFamily().getSeries().getGroup().getNote() + " - " + toName(spec) + " (" + spec.getPartNo() + ")";
    }

    private static String formatOs(Os os) {
        return (os == null ? "Kein Betriebsystem" : os.getNote());
    }

    private static String formatColor(Color color) {
        return (color == null ? "" : color.getNote());
    }

    public static String toSingleLine(ProductSpec spec) {
        if ( spec == null ) return "ProductSpec is null";
        List<String> r = new ArrayList<>();
        if ( spec instanceof DesktopBundle ) {
            DesktopBundle bundle = (DesktopBundle)spec;
            return "Desktop: " + toSingleLine(bundle.getDesktop()) + ", Monitor: " + toSingleLine(bundle.getMonitor());
        }
        if ( spec instanceof Desktop ) r.addAll(toSingleLineDesktop((Desktop)spec));
        if ( spec instanceof DisplayAble ) r.addAll(toSingleLineDisplayAble(((DisplayAble)spec).getDisplay()));
        if ( spec instanceof BasicSpec ) r.addAll(toSingleLineBasicSpec((BasicSpec)spec));
        if ( spec instanceof Desktop ) r.add(valueToString(((Desktop)spec).getOs()));
        return collectionToString(null, r);
    }

    public static String toSingleHtmlLine(ProductSpec spec) {
        if ( spec == null ) return "ProductSpec is null";
        List<String> r = new ArrayList<>();
        if ( spec instanceof DesktopBundle ) {
            DesktopBundle bundle = (DesktopBundle)spec;
            return "<u>Desktop:</u> " + toSingleLine(bundle.getDesktop()) + ", <u>Monitor:</u> " + toSingleLine(bundle.getMonitor());
        }
        if ( spec instanceof Desktop ) r.addAll(toSingleLineDesktop((Desktop)spec));
        if ( spec instanceof DisplayAble ) r.addAll(toSingleLineDisplayAble(((DisplayAble)spec).getDisplay()));
        if ( spec instanceof BasicSpec ) r.addAll(toSingleLineBasicSpec((BasicSpec)spec));
        if ( spec instanceof Desktop ) r.add(valueToString(((Desktop)spec).getOs()));
        return collectionToString(null, r);
    }

    private static List<String> toSingleLineBasicSpec(BasicSpec spec) {
        List<String> r = new ArrayList<>();
        r.add(spec.getComment());
        r.add(valueToString("Farbe: ", spec.getColor()));
        r.add(collectionToString("Ausstattung: ", spec.getExtras()));
        r.add(collectionToString("Videokonnektor(en) : ", spec.getVideoPorts()));
        return r;
    }

    private static List<String> toSingleLineDesktop(Desktop desktop) {
        List<String> r = new ArrayList<>();
        r.add(toSimpleLine(desktop.getCpu()));
        r.add(valueToString("Memory (in MB): ", desktop.getMemory()));
        r.add(toSimpleLine(desktop.getGpu()));
        r.add(collectionToString("Festplatte(n): ", desktop.getHdds()));
        r.add(collectionToString("Optische(s) Laufwerk(e): ", desktop.getOdds()));
        return r;
    }

    private static List<String> toSingleLineDisplayAble(Display display) {
        List<String> r = new ArrayList<>();
        r.add(valueToString("Display: ", display.getSize()));
        r.add(valueToString(display.getType()));
        r.add((display.isLed() ? "LED" : null));
        r.add(valueToString(display.getResolution()));
        r.add(valueToString(display.getRation()));
        return r;
    }

    private static String valueToString(Object elem) {
        return valueToString(null, elem);
    }

    private static String valueToString(String head, Object elem) {
        if ( elem == null ) return null;
        String s = (head == null ? "" : head);
        if ( elem instanceof INoteModel ) s += ((INoteModel)elem).getNote();
        else s += elem.toString();
        return s;
    }

    private static String collectionToString(String head, Collection elems) {
        if ( elems == null || elems.isEmpty() ) return null;
        String s = (head == null ? "" : head);
        for (Iterator it = elems.iterator(); it.hasNext();) {
            Object elem = it.next();
            if ( elem == null ) continue;
            if ( elem instanceof INoteModel ) s += ((INoteModel)elem).getNote();
            else s += elem.toString();
            if ( it.hasNext() ) s += ", ";
        }
        return s;
    }

    private static String toSimpleLine(Gpu gpu) {
        if ( gpu == null ) return "Gpu is null";
        return gpu.getManufacturer().getNote() + " " + ((gpu.getName() == null || gpu.getName().isEmpty()) ? gpu.getSeries().getNote() + " " + gpu.getModel() : gpu.getName());
    }

    /**
     *
     * @param cpu
     * @return a simple line containing in format {manufacturer} {cpu-name}
     */
    private static String toSimpleLine(Cpu cpu) {
        if ( cpu == null ) return "Cpu is null";
        return cpu.getManufacturer().getNote() + " " + ((cpu.getName() == null || cpu.getName().isEmpty()) ? cpu.getSeries().getNote() + " " + cpu.getModel() : cpu.getName());
    }

    /**
     *
     * @param cpu
     * @return a line containing in format {manufacturer} {cpu-name} {#.## Ghz} {#} Kern(e)
     */
    private static String toFullLine(Cpu cpu) {
        if ( cpu == null ) return "Cpu is null";
        String s = toSimpleLine(cpu);
        s += cpu.getFrequency() != null ? " " + cpu.getFrequency() + " Ghz" : "";
        s += cpu.getCores() != null ? " " + cpu.getCores() + " Kern(e)" : "";
        return s;
    }

    // ----
    public static String toHtml(ProductSpec spec) {
        if ( spec == null ) return "No description found !!";
        String re = toDetailedName(spec) + "<br />";
        if ( spec instanceof DesktopBundle ) re += formatBundleToHtml((DesktopBundle)spec);
        if ( spec instanceof Desktop ) re += formatDesktopToHtml((Desktop)spec);
        if ( spec instanceof DisplayAble ) re += formatDisplayAbleToHtml(((DisplayAble)spec).getDisplay());
        if ( spec instanceof BasicSpec ) re += formatBasicSpecToHtml((BasicSpec)spec);
        return re;
    }

    private static String formatBasicSpecToHtml(BasicSpec spec) {
        String re = collectionToString("<u><b>Videokonnektor/en:</u></b><br />", spec.getVideoPorts()) + "<br />";
        re += collectionToString("<u><b>Extras:</u></b><br />", spec.getExtras()) + "<br />";
        re += "<u><b>Farbe:</u></b><br /><i>" + formatColor(spec.getColor()) + "</i><br />";
        re += "<u><b>Kommentar:</u></b><br />" + spec.getComment() + "<br />";
        return re;
    }

    private static String formatDesktopToHtml(Desktop desktop) {
        String r = "";
        r += "<u><b>Prozessor:<b></u><br /> " + toSimpleLine(desktop.getCpu()) + "<br />";
        r += "<u><b>Arbeitsspeicher:</u></b> " + desktop.getMemory() + "<br />";
        r += collectionToString("<u><b>Festplatten:</u></b> ", desktop.getHdds()) + "<br />";
        r += collectionToString("<u><b>Laufwerke:</u></b> ", desktop.getOdds()) + "<br />";
        r += "<u><b>Grafikkarte:</u></b> " + toSimpleLine(desktop.getGpu()) + "<br />";
        r += "<u><b>Betriebssystem:</u></b> " + formatOs(desktop.getOs()) + "<br />";
        return r;
    }

    private static String formatDisplayAbleToHtml(Display display) {
        String re = "<u><b>Display:</u></b>:<br />" + toSingleLineDisplayAble(display) + "<br />";
        return re;
    }

    private static String formatBundleToHtml(DesktopBundle bundle) {
        String re = "<b><u>Bundle | Desktop:</u></b><br />" + toHtml((Desktop)bundle.getDesktop()) + "<br />";
        re += "<b><u>Bundle | Monitor:</u></b><br />" + toDetailedName(bundle.getMonitor()) + "<br />";
        re += toHtml((Monitor)bundle.getMonitor());
        return re;
    }
}
