package eu.ggnet.dwoss.spec.format;

import java.util.Iterator;

import eu.ggnet.dwoss.spec.entity.piece.Gpu;

/**
 *
 * @author pascal.perau
 */
public abstract class GpuFormater {

    /**
     * Generate the source for a new Cpu object.
     * The semicolon at the end will not be set.
     * 
     * @param gpu the gpu
     * @return a formated String
     */
    public static String toSource(Gpu gpu){
        if(gpu == null) return "Gpu ist null";
        String types = "";
        for (Iterator<Gpu.Type> it = gpu.getTypes().iterator(); it.hasNext();) {
            types += "Gpu.Type." + it.next();
            if (it.hasNext()) types += ", ";
        }
        return "new Gpu(Gpu.Series." + gpu.getSeries()
                + ", EnumSet.of(" + types +")"
                + ", \"" + gpu.getModel() + "\")";
    }

}
