package eu.ggnet.saft.api.progress;

/**
 * Wrapper for progross with a minimal api
 * Documentation may follow if in heavy usage
 *
 * @author oliver.guenther
 */
public interface IMonitor {

    /**
     * Starts the process of monitor.
     * <p>
     * @return itself.
     */
    IMonitor start();

    /**
     * Name of monitor.
     * <p/>
     * @param name
     * @return itself.
     */
    IMonitor title(String name);

    /**
     * Amount of tasks, which are done.
     * <p/>
     * @param workunits
     * @return itself.
     */
    IMonitor worked(int workunits);

    /**
     * Name of tasks, which are done.
     * <p/>
     * @param subMessage
     * @return itself.
     */
    IMonitor message(String subMessage);

    /**
     * Amount and name of the tasks, which are done.
     * <p/>
     * @param workunits
     * @param subMessage
     * @return itself.
     */
    IMonitor worked(int workunits, String subMessage);

    /**
     * Monitor at end.
     * <p>
     * @return itself.
     */
    IMonitor finish();

    /**
     * Returns the remaining ticks for internal work. This method must return an absolut or relative value of how many ticks are avaiable.
     * It must be ensured, that these ticks are only becoming less, not more. (Or the resultig presentation will be useless)
     *
     * @return the remaining ticks for internal work
     */
    int getAbsolutRemainingTicks();
}
