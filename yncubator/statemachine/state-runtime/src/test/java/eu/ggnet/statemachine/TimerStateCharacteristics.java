package eu.ggnet.statemachine;

/**
 * A class that uses equals an hashCode to symbolize different and equal states of a Timer.
 *
 *
 * @author oliver.guenther
 */
public final class TimerStateCharacteristics extends Timer implements StateCharacteristic<Timer> {

    public TimerStateCharacteristics(boolean on, boolean plugedIn, boolean upSideDown) {
        this.on = on;
        this.plugedIn = plugedIn;
        this.upSideDown = upSideDown;
    }

    public TimerStateCharacteristics(Timer timer) {
        this(timer.on,timer.plugedIn,timer.upSideDown);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.on ? 1 : 0);
        hash = 73 * hash + (this.plugedIn ? 1 : 0);
        hash = 73 * hash + (this.upSideDown ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimerStateCharacteristics other = (TimerStateCharacteristics) obj;
        if (this.on != other.on) {
            return false;
        }
        if (this.plugedIn != other.plugedIn) {
            return false;
        }
        if (this.upSideDown != other.upSideDown) {
            return false;
        }
        return true;
    }

    public String toHtml() {
        return "<div>Timer:"
                + "<ul>"
                + "<li>on=" + on + "</li>"
                + "<li>pulgedIn=" + plugedIn +"</li>"
                + "<li>upSideDown=" + upSideDown + "</li>"
                + "<ul>"
                + "</div>";
    }

}
