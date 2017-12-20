package eu.ggnet.dwoss.report.tryout;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.DocumentType;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JFrame;

import org.junit.Test;
import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.report.entity.ReportLine;


import eu.ggnet.dwoss.util.MetawidgetConfig;

/**
 *
 * @author oliver.guenther
 */
public class ReportLineMetaWidgetTryout {

    public static void main(String[] args) {
        ReportLine rl = new ReportLine();
        rl.setActual(new Date());
        rl.setContractor(TradeName.EBAY);
        rl.setContractorPartNo("123.131");
        rl.setCustomerId(12322);
        rl.setDescription("AMD E Series E-450 (1.65 Ghz), Memory (in MB): 4096, AMD Radeon HD 6000 Series"
                + "6320, Festplatte(n): 320GB HDD, Optische(s) Laufwerk(e): DVD Super Multi, Display:"
                + "15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: grau, Ausstattung:"
                + "Webcam, WLAN b + g + n, Kartenleser, Videokonnektor(en) : HDMI, VGA, Windows 7"
                + "Home Premium 64");
        rl.setDocumentIdentifier("SR_00001");
        rl.setDocumentType(DocumentType.INVOICE);
        rl.setDossierIdentifier("DW00110");
        rl.setDossierId(110);
        rl.setInvoiceAddress("Max Mustermann, Musterstrasse 22, 20031 Hamburg");
        rl.setMfgDate(new Date());
        rl.setName("Acer Aspire 5250-4504G32Mnkk (NX.RJYED.004)");
        rl.setPartNo("LX.AAA12.312");
        rl.setPositionType(PositionType.UNIT);
        rl.setProductBrand(TradeName.ACER);
        rl.setProductGroup(ProductGroup.NOTEBOOK);
        rl.setProductName("Aspire 5250-4504G32Mnkk");
        rl.setRefurbishId("13213");
        rl.setReportingDate(new Date());
        rl.setSalesChannel(SalesChannel.RETAILER);
        rl.setSerial("AAAAABBBABABABADFSA23423");

        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(true, 2, ProductGroup.class, TradeName.class, SalesChannel.class, DocumentType.class, PositionType.class, ReportLine.WorkflowStatus.class);
        mw.setReadOnly(true);
        mw.setToInspect(rl);
        JFrame view = new JFrame("Details für Reportline(" + rl.getId() + ")");
        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.getContentPane().add(mw);
        view.pack();
        view.setSize(view.getSize().width, view.getSize().height + 50);
        view.setLocation(300, 300);
        view.setVisible(true);
    }
}
