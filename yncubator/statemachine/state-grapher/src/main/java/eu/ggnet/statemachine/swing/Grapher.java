/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.statemachine.swing;

import java.awt.*;
import java.util.*;

import javax.swing.JFrame;

import eu.ggnet.statemachine.*;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.*;

/**
 * Class for displaing Ui Informations.
 * 
 * @author oliver.guenther
 */
public class Grapher {

    /**
     * Show an exact subgraph of the suplied states.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the state machine
     * @param states       the states to display
     */
    public static <T> void showExact(final StateMachine<T> stateMachine, State<T>... states) {
        showExact(stateMachine, null, states);
    }

    /**
     * Show an exact subgraph of the suplied states.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the state machine
     * @param formater     an optional formater
     * @param states       the states to display
     */
    public static <T> void showExact(final StateMachine<T> stateMachine, final StateFormater<T> formater, State<T>... states) {
        StateMachine small = new StateMachine(stateMachine);
        Set<State<T>> subStates = new HashSet<>(Arrays.asList(states));
        for (Link<T> link : stateMachine.getLinks()) {
            if ( subStates.contains(link.getSource()) && subStates.contains(link.getDestination()) ) {
                small.add(link);
            }
        }
        showFull(small, formater);
    }

    /**
     * Show a greedy subgraph of the suplied states.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the state machine
     * @param states       the states and all directly connected to display
     */
    public static <T> void showGreedy(final StateMachine<T> stateMachine, State<T>... states) {
        showGreedy(stateMachine, null, states);
    }

    /**
     * Show a greedy subgraph of the suplied states.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the state machine
     * @param formater     an optional formater
     * @param states       the states and all directly connected to display
     */
    public static <T> void showGreedy(final StateMachine<T> stateMachine, final StateFormater<T> formater, State<T>... states) {
        StateMachine small = new StateMachine(stateMachine);
        Set<State<T>> subStates = new HashSet<>(Arrays.asList(states));
        for (Link<T> link : stateMachine.getLinks()) {
            if ( subStates.contains(link.getSource()) || subStates.contains(link.getDestination()) ) {
                small.add(link);
            }
        }
        showFull(small, formater);
    }

    /**
     * Show the full StateMachine with a special formater.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the statemachine to show
     */
    public static <T> void showFull(final StateMachine<T> stateMachine) {
        showFull(stateMachine, null);
    }

    /**
     * Show the full StateMachine with a special formater.
     * 
     * @param <T>          type of state machine
     * @param stateMachine the statemachine to show
     * @param formater     an optional formater
     */
    public static <T> void showFull(final StateMachine<T> stateMachine, final StateFormater<T> formater) {
        DirectedGraph<State<T>, String> g = new DirectedSparseMultigraph<>();
        int i = 0;
        for (Link<T> link : stateMachine.getLinks()) {
            // TODO: A Graph needs for each transition a unique id. A StateMachine not. So we build it here.
            g.addEdge("[" + (i++) + "] " + link.getTransition().toString(), link.getSource(), link.getDestination());
        }

        FRLayout<State<T>, String> layout = new FRLayout<>(g);
//        layout.setRepulsionMultiplier(2);
//        layout.setMaxIterations(20);
        layout.setSize(new Dimension(1100, 950)); // sets the initial size of the space
        VisualizationViewer<State<T>, String> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(1280, 1024)); //Sets the viewing area size

        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

//                final VisualizationModel<String,Number> visualizationModel =
//            new DefaultVisualizationModel<String,Number>(layout, preferredSize);
        // this class will provide both label drawing and vertex shapes
        VertexLabelAsShapeRenderer<State<T>, String> vlasr = new VertexLabelAsShapeRenderer<>(vv.getRenderContext());
//
//        // customize the render context
        if ( formater != null ) {
            vv.getRenderContext().setVertexLabelTransformer((state) -> {
                return formater.toHtml(state);
            });
            vv.setVertexToolTipTransformer((state) -> {
                return formater.toToolTipHtml(state);
            });
        }

        vv.getRenderContext().setVertexShapeTransformer(vlasr);
        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.RED));
        vv.getRenderContext().setEdgeDrawPaintTransformer((input) -> {
            return Color.DARK_GRAY;
        });
        vv.getRenderContext().setEdgeStrokeTransformer((input) -> {
            return new BasicStroke(2.5f);
        });

        // customize the renderer
        vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<State<T>, String>(Color.LIGHT_GRAY, Color.WHITE, true));
        vv.getRenderer().setVertexLabelRenderer(vlasr);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        vv.addKeyListener(gm.getModeKeyListener());

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(vv, BorderLayout.CENTER);
        frame.getContentPane().add(gm.getModeComboBox(), BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
}


