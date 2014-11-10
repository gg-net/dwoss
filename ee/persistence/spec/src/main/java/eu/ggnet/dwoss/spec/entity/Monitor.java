package eu.ggnet.dwoss.spec.entity;

import java.util.EnumSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.spec.entity.piece.Display;

import static eu.ggnet.dwoss.spec.entity.ProductSpec.Extra.*;
import static javax.persistence.CascadeType.*;

/**
 * Represents a Monitor.
 * @author pascal.perau
 */
@Entity
public class Monitor extends BasicSpec implements DisplayAble {

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Display display;

    Monitor(long id) {
        super(id);
    }

    public Monitor(Display display) {
        this.display = display;
    }

    public Monitor() {
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(KAMERA, THREE_D, PIVOT, HIGHT_CHANGEABLE, SPEAKERS, TOUCH);
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(Display resolutionData) {
        this.display = resolutionData;
    }

    @Override
    public String toString() {
        return "Monitor{" + super.toString() + ", display=" + display + '}';
    }
}
