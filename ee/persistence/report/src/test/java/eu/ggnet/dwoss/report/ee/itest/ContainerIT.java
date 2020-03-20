package eu.ggnet.dwoss.report.ee.itest;

import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGeneratorOperation;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ContainerIT extends ArquillianProjectArchive {

    @EJB
    private ReportAgent reportAgent;

    @Inject
    private ReportLineGeneratorOperation generator;

    @Test
    public void testPersistence() {
        generator.makeReportLines(100);
        List<ReportLine> lines = reportAgent.findAll(ReportLine.class);
        assertNotNull(lines);
        assertFalse(lines.isEmpty());
    }
}
