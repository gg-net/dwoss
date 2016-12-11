package eu.ggnet.statemachine;

/**
 *
 * @author oliver.guenther
 */
public class TimerTransitions {

    public final static StateTransition<Timer> TURN_ON = new StateTransition<Timer>("TURN_ON") {
        @Override
        public void apply(Timer state) {
            state.setOn(true);
        }
    };
    public final static StateTransition<Timer> TURN_OFF = new StateTransition<Timer>("TURN_OFF") {
        @Override
        public void apply(Timer state) {
            state.setOn(false);
        }
    };
    public final static StateTransition<Timer> PLUG_IN = new StateTransition<Timer>("PLUG_IN") {
        @Override
        public void apply(Timer state) {
            state.setPlugedIn(true);
        }
    };
    public final static StateTransition<Timer> PLUG_OUT = new StateTransition<Timer>("PLUG_OUT") {
        @Override
        public void apply(Timer state) {
            state.setPlugedIn(false);
        }
    };
    public final static StateTransition<Timer> TURN_AROUND = new StateTransition<Timer>("TURN_AROUND") {
        @Override
        public void apply(Timer state) {
            state.setUpSideDown(!state.isUpSideDown());
        }
    };
}
