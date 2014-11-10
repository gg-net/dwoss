package eu.ggnet.dwoss.spec.format;

import java.util.Iterator;

import eu.ggnet.dwoss.spec.entity.piece.Cpu;

public class CpuFormater {

    /**
     * Generate the source for a new Cpu object.
     * The semicolon at the end will not be set.
     * <p>
     * @param cpu the cpu to format
     * @return a string representation of the cpu
     */
    public static String toSource(Cpu cpu) {
        if ( cpu == null ) return "Cpu is null";
        String types = "";
        for (Iterator<Cpu.Type> it = cpu.getTypes().iterator(); it.hasNext();) {
            types += "Cpu.Type." + it.next();
            if ( it.hasNext() ) types += ", ";
        }
        return "new Cpu(Cpu.Series." + cpu.getSeries()
                + ", EnumSet.of(" + types + ")"
                + ", \"" + cpu.getModel() + "\")";
    }
}
