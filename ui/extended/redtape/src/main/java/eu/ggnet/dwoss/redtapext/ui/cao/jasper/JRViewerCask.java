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
package eu.ggnet.dwoss.redtapext.ui.cao.jasper;

import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.event.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JRViewer;

import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.DocumentSupporter;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ResultProducer;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * This Class is to view a Jasper Report with a modified {@link JRViewerToolbar}.
 * This modified toolbar allowes it to send a E-Mail and/or to render the document.
 * <p/>
 * @author bastian.venz
 */
// TODO: JRViewer is deprecated, but the new version makes our print button handling more complicated. So if we every what to change something, move to our own
// javafx implementation. (Or if someone else did this)
public class JRViewerCask extends JRViewer implements ResultProducer<JRViewerCask> {

    private boolean correctlyBriefed = false;

    public JRViewerCask(final JasperPrint jasperPrint, final Document document, final DocumentViewType viewType, boolean canEmaild) {
        super(jasperPrint);
        for (ActionListener actionListener : this.btnPrint.getActionListeners()) {
            this.btnPrint.removeActionListener(actionListener);
        }
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P ) {
                    try {
                        JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        JasperPrintManager.printReport(jasperPrint, true);
                        correctlyBriefed = true;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(JRViewerCask.this, getBundleString("error.printing") + "\n" + ex.getMessage());
                    } finally {
                        JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        if ( canEmaild ) {
            this.tlbToolBar.add(new JButton(new AbstractAction("Per Mail Senden") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    final MailInfoDialog infoDialog = new MailInfoDialog(SwingUtilities.getWindowAncestor(JRViewerCask.this));

                    new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            infoDialog.setVisible(true);
                            lookup(DocumentSupporter.class).mail(document, viewType);
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                                correctlyBriefed = true;
                                infoDialog.setFinish();
                                JOptionPane.showMessageDialog(JRViewerCask.this, "Mail wurde versendet");
                                infoDialog.dispose();
                            } catch (InterruptedException | ExecutionException | HeadlessException ex) {
                                Ui.handle(ex);
                            } finally {
                                JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    }.execute();
                }
            }));
        } else {
            JButton jButton = new JButton("Keine E-Mail hinterlegt!");
            jButton.setEnabled(false);
            this.tlbToolBar.add(jButton);
        }

        this.btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    btnPrint.setEnabled(false);
                    JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    JasperPrintManager.printReport(jasperPrint, true);
                    correctlyBriefed = true;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(JRViewerCask.this, getBundleString("error.printing") + "\n" + ex.getMessage());
                } finally {
                    JRViewerCask.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    btnPrint.setEnabled(true);
                }
            }
        });
    }

    public boolean isCorrectlyBriefed() {
        return correctlyBriefed;
    }

    @Override
    public JRViewerCask getResult() {
        return this;
    }
}
