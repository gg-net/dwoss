package eu.ggnet.statemachine;

/**
 *
 * @author oliver.guenther
 */
public class TimerStateCharacteristicsFactory implements StateCharacteristicFactory<Timer> {

    @Override
    public StateCharacteristic characterize(Timer t) {
        return new TimerStateCharacteristics(t);
    }
}
