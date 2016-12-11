package eu.ggnet.statemachine;

/**
 *
 * @author oliver.guenther
 */
public class Timer {

    protected boolean on;

    protected boolean plugedIn;
    
    protected boolean upSideDown;

    public Timer() {
    }
    
    public Timer(boolean on, boolean plugedIn, boolean upSideDown) {
        this.on = on;
        this.plugedIn = plugedIn;
        this.upSideDown = upSideDown;
    }
    
    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isPlugedIn() {
        return plugedIn;
    }

    public void setPlugedIn(boolean plugedIn) {
        this.plugedIn = plugedIn;
    }

    public boolean isUpSideDown() {
        return upSideDown;
    }

    public void setUpSideDown(boolean upSideDown) {
        this.upSideDown = upSideDown;
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
        final Timer other = (Timer) obj;
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

    @Override
    public String toString() {
        return "Timer{" + "on=" + on + ", plugedIn=" + plugedIn + ", upSideDown=" + upSideDown + '}';
    }

}
