package eu.ggnet.dwoss.common.ui;

/**
 *
 * @author oliver.guenther
 */
public class ClassWithSetEnabled {

    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ClassWithSetEnabled{" + "enabled=" + enabled + '}';
    }

}
