package eu.ggnet.dwoss.spec.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import static javax.persistence.CascadeType.*;

/**
 * Represents a combination of individual {@link Desktop} and {@link Monitor} packed.
 * @author bastian.venz
 */
// TODO: Ensure that the Specs are only the Considered Details -- Still in consideration? - PP
// Persistence Hint: Not in Spec
@Entity
public class DesktopBundle extends ProductSpec {
        
    @XmlElement(type = Desktop.class)
    @NotNull
    @ManyToOne(cascade={DETACH,MERGE,REFRESH,PERSIST},optional=false)
    private ProductSpec desktop;

    @XmlElement(type = Monitor.class)
    @NotNull
    @ManyToOne(cascade={DETACH,MERGE,REFRESH,PERSIST},optional=false)
    private ProductSpec monitor;

    public DesktopBundle() {
    }

    public DesktopBundle(Desktop desktop, Monitor monitor) {
        this.desktop = desktop;
        this.monitor = monitor;
    }

    public ProductSpec getDesktop() {
        return desktop;
    }

    public void setDesktop(ProductSpec desktop) {
        this.desktop = desktop;
    }

    public ProductSpec getMonitor() {
        return monitor;
    }

    public void setMonitor(ProductSpec monitor) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        return "DesktopBundle{"+ super.toString() + ",desktop=" + desktop + ", monitor=" + monitor + '}';
    }

}
