package eu.ggnet.saft.api.ui;

/**
 * May be implemented by a Pane or JPanel, is called by using Ui.choice*, if ok is pressed.
 *
 * @deprecated use {@link ResultProducer }
 */
@Deprecated
public interface OnOk {

    /**
     * Is called before a closing of surrounding element, by Ok pressing.
     *
     * @return true if the closing operation may continue as allowed, or false if it should be stopped.
     */
    @Deprecated
    boolean onOk();

}
