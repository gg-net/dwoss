/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tryout.support;

import java.awt.BorderLayout;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.widget.MainComponent;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = MainComponent.class)
public class TryoutMainPanel2 extends javax.swing.JPanel implements MainComponent {

    /** Creates new form TryoutMainPanel1 */
    public TryoutMainPanel2() {
        initComponents();
    }

    @Override
    public String getLayoutHint() {
        return BorderLayout.CENTER;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
