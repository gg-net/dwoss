package eu.ggnet.dwoss.common;

import eu.ggnet.saft.api.auth.Accessable;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_IMAGE_IDS;

/**
 *
 * @author oliver.guenther
 */
public class ClassWithSetEnabledAndImageIds extends ClassWithSetEnabled implements Accessable {

    @Override
    public AtomicRight getNeededRight() {
        return IMPORT_IMAGE_IDS;
    }

}
