package eu.ggnet.dwoss.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import com.toedter.calendar.JDateChooser;


/**
 * A simple Dialog to set a range between two dates.
 * @author oliver.guenther
 */
public class DateRangeChooserDialog extends javax.swing.JDialog {

    /** Creates new form CreateAccountancyXmlDataDialog */
    public DateRangeChooserDialog(java.awt.Window parent) {
        super(parent);
        setModalityType(ModalityType.DOCUMENT_MODAL);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public boolean isOk() {
        return this.ok;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new BindingGroup();

        buttonOK = new JButton();
        buttonCancel = new JButton();
        labelBeginDate = new JLabel();
        labelEndDate = new JLabel();
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choise of period");
        setIconImage(null);
        setIconImages(null);
        setMinimumSize(new Dimension(100, 100));
        setResizable(false);

        buttonOK.setText("OK");
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        labelBeginDate.setText("Start Datum:");
        labelBeginDate.setMaximumSize(new Dimension(75, 14));
        labelBeginDate.setMinimumSize(new Dimension(75, 14));
        labelBeginDate.setPreferredSize(new Dimension(100, 14));

        labelEndDate.setText("End Datum:");
        labelEndDate.setMaximumSize(new Dimension(75, 14));
        labelEndDate.setMinimumSize(new Dimension(75, 14));
        labelEndDate.setPreferredSize(new Dimension(100, 14));

        Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${start}"), startDateChooser, BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${end}"), endDateChooser, BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelBeginDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(startDateChooser, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelEndDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(endDateChooser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonOK, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(labelBeginDate, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(labelEndDate, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        this.ok = true;
        setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonCancel;
    private JButton buttonOK;
    private JDateChooser endDateChooser;
    private JLabel labelBeginDate;
    private JLabel labelEndDate;
    private JDateChooser startDateChooser;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private boolean ok = false;

    private Date start;

    private Date end;

    public static void main(String[] args) {
        DateRangeChooserDialog dialog = new DateRangeChooserDialog(null);
        dialog.setVisible(true);
        if (dialog.isOk()) System.out.println(dialog.getStart() + " | " + dialog.getEnd());
        System.exit(0);
    }
}
