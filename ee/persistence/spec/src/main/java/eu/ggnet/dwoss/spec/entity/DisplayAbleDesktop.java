package eu.ggnet.dwoss.spec.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.spec.entity.piece.*;

import static javax.persistence.CascadeType.*;

/**
 * Represents an Desktoplike Instance with a Display
 *
 * @author oliver.guenther
 */
@Entity
public abstract class DisplayAbleDesktop extends Desktop implements DisplayAble {

    public DisplayAbleDesktop() {
        display = new Display();
    }

    public DisplayAbleDesktop(Os os, Cpu cpu, List<Hdd> hdds, Gpu gpu, List<Odd> odds, int memory, Set<Extra> extras, Display display) {
        super(os, cpu, hdds, gpu, odds, memory, extras);
        this.display = display;
    }

    @NotNull
    @Valid
    @ManyToOne(cascade={DETACH,MERGE,REFRESH,PERSIST},optional=false)
    private Display display;

    @Override
    public Display getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(Display display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + " + display=" + display + '}';
    }

}
