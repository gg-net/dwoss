package eu.ggnet.dwoss.misc.op.movement;

import lombok.Data;

/**
 * A sub line for the movement report
 */
@Data
public class MovementSubline {

    private final int amount;
    private final String description;
    private final String refurbishId;
    private final String stock;
    private final boolean wrappedToShip;

}
