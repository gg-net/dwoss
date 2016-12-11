package eu.ggnet.statemachine;

/**
 *
 * @author oliver.guenther
 */
public class TimerStates {

    public final static State RUNNING = new State("RUNNING", new TimerStateCharacteristics(true, true, false), new TimerStateCharacteristics(true, true, true));
    public final static State OFF = new State("OFF", new TimerStateCharacteristics(false, false, false), new TimerStateCharacteristics(false, false, true));
    public final static State STOPPED_PLUGEDIN = new State("STOPPED_PLUGEDIN", new TimerStateCharacteristics(false, true, false), new TimerStateCharacteristics(false, true, true));
    public final static State STOPPED_ON = new State("STOPPED_ON", new TimerStateCharacteristics(true, false, false), new TimerStateCharacteristics(true, false, true));

}
