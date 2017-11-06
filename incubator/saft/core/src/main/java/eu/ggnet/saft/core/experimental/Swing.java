/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.saft.core.experimental;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.JPanel;

import javafx.scene.layout.Pane;

/**
 * Handles Swing elements on Saft.
 * This class has no impact how the emelemts are wrapped, only that the elements are based on Swing.
 *
 * @author oliver.guenther
 */
public class Swing<A, B> {

    /*
    I - 4 Fälle:
    a. nur zeigen. Ui consumiert nix und prodziert kein result
    b. consumer ui of type v
    c. result producer of type r
    d. conumer and result producer of type v,r

    II - 3. Uis
    a. Swing JPanel
    b. JavaFx Pane
    c. JavaFxml + Controller Class


    Examples:
    Ui.fx().parrent().id("blaa").eval(fdsafdsafddsa);

    Ui.swing().show(()->Demo());

     */
    // Swing  Ia
    public static <V extends JPanel> void show(Callable<V> callable) {
    }

    // Swing Ib
    public static <P, V extends Consumer<P>, JPanel> void show(Callable<P> preProducer, Callable<V> callable) {
    }

    // Swing Ic
    public static <T, V extends ResultProducer<T>, JPanel> Optional<T> eval(Callable<V> callable) {
        return null;
    }

    public static <T, P, V extends Consumer<P> & ResultProducer<T>, JPanel> Optional<T> eval(Callable<P> preProducer, Callable<V> callable) {
        return null;
    }

    public static <V extends JPanel> Swing<V, Void> swing(Callable<V> callable) {
        return null;
    }

    public Optional<B> eval() {
        return null;
    }

    public static <T, V extends ResultProducer<T>, JPanel> Swing<V, T> swingIt(Callable<V> callable) {
        return null;
    }

    public static <V extends Pane> void fx(Callable<V> callable) {

    }

//    public static <V extends JPanel> void show(Callable<V> callable) {
//
//    }
    private static class Demo extends JPanel implements ResultProducer<String> {

        @Override
        public String getResult() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static void main(String[] args) {
//        eval(() -> new Demo()).ifPresent(s -> System.out.println(s.toLowerCase()));
        swingIt(() -> new Demo()).eval().get();
    }

}
