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
package eu.ggnet.dwoss.common.ui.saftwrap;

import java.util.function.Consumer;

import javax.swing.JPanel;

import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.ui.ResultProducer;

public class OkCancelConsumerVetoResult<V, U, T extends JPanel & ResultProducer<V> & VetoableOnOk & Consumer<U>> extends AbstractOkCancelPanelWrapper<T> implements ResultProducer<Reply<V>>, Consumer<U> {

    private T panel;

    public OkCancelConsumerVetoResult(T panel) {
        super(panel);
        this.panel = panel;
        this.vetoableOnOk = panel;
    }

    @Override
    public Reply<V> getResult() {
        return okPressed ? Reply.success(panel.getResult()) : Reply.failure("Cancel pressed");
    }

    @Override
    public void accept(U t) {
        panel.accept(t);
    }

}
