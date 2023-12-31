/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.misc.ee.PersistenceValidator;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

@Dependent
public class DatabaseValidationAction extends AbstractAction {

    public DatabaseValidationAction() {
        super("Datenbank Validieren");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CompletableFuture.supplyAsync(() -> Progressor.global().run("Datenbankvalidatiaon", () -> Dl.remote().lookup(PersistenceValidator.class).validateDatabase()))
                .thenAccept(fj -> {
                    if ( fj == null ) {
                        Ui.build().alert("Datenbank ist valid");
                    } else {
                        FileUtil.osOpen(fj.toTemporaryFile());
                    }
                }).handle(Ui.handler());
    }
}
