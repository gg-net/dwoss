package eu.ggnet.dwoss.stock.emo;

import java.util.NavigableSet;

import eu.ggnet.dwoss.stock.entity.LogicTransaction;

import lombok.Value;

/**
 * Result for Equilibration.
 * <p>
 * @author oliver.guenther
 */
@Value
public class EquilibrationResult {

    private final NavigableSet<Integer> added;

    private final NavigableSet<Integer> removed;

    private final LogicTransaction transaction;
}
