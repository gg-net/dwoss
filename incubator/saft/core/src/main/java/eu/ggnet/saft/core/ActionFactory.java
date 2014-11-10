package eu.ggnet.saft.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Action;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A Factory for Actions.
 * Implementations should be discoverable thought the lookup.
 * <p/>
 * @author oliver.guenther
 */
public interface ActionFactory {

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class MetaAction {

        private final Action action;

        private final List<String> menuNames = new ArrayList<>();

        private final boolean toolbar;

        public MetaAction(String menu, Action action) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            this.toolbar = false;
        }

        public MetaAction(String menu, Action action, boolean showOnToolbar) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            this.toolbar = showOnToolbar;
        }

        public MetaAction(String menu, String subMenu, Action action) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            menuNames.add(Objects.requireNonNull(subMenu, "Menu name must not be null"));
            this.toolbar = false;
        }
    }

    List<MetaAction> createMetaActions();
}
