package eu.ggnet.statemachine;

import org.junit.Test;

import static eu.ggnet.statemachine.TimerStates.*;
import static eu.ggnet.statemachine.TimerTransitions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author oliver.guenther
 */
public class StateMachineTest {

    public static StateMachine<Timer> createTimeMachine() {
        StateMachine<Timer> g = new StateMachine<>(new TimerStateCharacteristicsFactory());
        g.add(OFF, TURN_ON, STOPPED_ON);
        g.add(OFF, PLUG_IN, STOPPED_PLUGEDIN);
        g.add(STOPPED_PLUGEDIN, TURN_ON, RUNNING);
        g.add(STOPPED_ON, PLUG_IN, RUNNING);
        g.add(RUNNING, TURN_OFF, STOPPED_PLUGEDIN);
        g.add(RUNNING, PLUG_OUT, STOPPED_ON);
        g.add(STOPPED_ON, TURN_OFF, OFF);
        g.add(STOPPED_PLUGEDIN, PLUG_OUT, OFF);
        g.add(OFF, TURN_AROUND, OFF);
        g.add(STOPPED_ON, TURN_AROUND, STOPPED_ON);
        g.add(STOPPED_PLUGEDIN, TURN_AROUND, STOPPED_PLUGEDIN);
        g.add(RUNNING, TURN_AROUND, RUNNING);
        return g;
    }

    @Test
    public void testMachine() throws InterruptedException {
        StateMachine<Timer> g = createTimeMachine();

        Timer t = new Timer(false, false, false);
        assertEquals("Timer should be OFF", OFF, g.getState(t));

        g.stateChange(t, TURN_ON);
        assertEquals(STOPPED_ON, g.getState(t));

        try {
            g.stateChange(t, TURN_ON);
            fail("TURN_ON on State STOPPED_ON is not possible");
        } catch (IllegalArgumentException e) {
        }

        g.stateChange(t, PLUG_IN);
        assertEquals(RUNNING, g.getState(t));
        g.stateChange(t, TURN_AROUND);
        assertEquals(RUNNING, g.getState(t));

        g.stateChange(t, TURN_OFF);
        assertEquals(STOPPED_PLUGEDIN, g.getState(t));
        g.stateChange(t, TURN_AROUND);
        assertEquals(STOPPED_PLUGEDIN, g.getState(t));
        g.stateChange(t, PLUG_OUT);
        assertEquals(OFF, g.getState(t));
        g.stateChange(t, TURN_AROUND);
        assertEquals(OFF, g.getState(t));
    }

}
