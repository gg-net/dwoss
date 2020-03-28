/*
 * Copyright (C) 2018 GG-Net GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.customer.ui.neo;

import java.util.*;

import javafx.application.Platform;
import javafx.scene.Node;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.dwoss.core.widget.auth.Accessable;


/**
 * Class that wrappes multiple nodes to a specific authorisation to control accessability over these nodes.
 *
 * @author pascal.perau
 */
public class NodeEnabler implements Accessable {

    private final Set<Node> nodes;

    private final Authorisation authorisation;

    /**
     * Constructor, verifies if the supplied object has a method setEnabled(boolean)
     *
     * @param nodes       nodes to be accessable by a specific authorisation
     * @param neededRight authorisation instance to control accessibility to the given nodes
     */
    public NodeEnabler(Authorisation neededRight, Node... nodes) {
        this.authorisation = neededRight;
        this.nodes = new HashSet<>(Arrays.asList(nodes));
    }

    @Override
    public void setEnabled(boolean enable) {
        Platform.runLater(() -> {
            for (Node node : nodes) {
                node.disableProperty().set(!enable);
            }
        });
    }

    @Override
    public Authorisation getNeededRight() {
        return authorisation;
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.nodes);
        hash = 59 * hash + Objects.hashCode(this.authorisation);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final NodeEnabler other = (NodeEnabler)obj;
        if ( !Objects.equals(this.nodes, other.nodes) ) return false;
        if ( !Objects.equals(this.authorisation, other.authorisation) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
