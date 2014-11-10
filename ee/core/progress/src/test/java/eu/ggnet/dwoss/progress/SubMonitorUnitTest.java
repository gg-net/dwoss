package eu.ggnet.dwoss.progress;

import eu.ggnet.dwoss.progress.SubMonitor;

import org.junit.Test;


/**
 *
 */
public class SubMonitorUnitTest {

    @Test
    public void testBarValues1() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m);
        sm.start();
        m.testRemaining(1000, 1);
        sm.setWorkRemaining(5);
        sm.worked(2);
        m.testRemaining(600, 1);
        sm.setWorkRemaining(10);
        sm.worked(6);
        m.testRemaining(240, 1);
        sm.worked(4);
        m.testRemaining(0, 1);

    }

    @Test
    public void testBarValues2() {
        JUnitMonitor m2 = new JUnitMonitor(10);
        SubMonitor sm = SubMonitor.convert(m2);
        m2.testRemaining(10, 1);
        sm.setWorkRemaining(20);
        sm.worked(6);
        m2.testRemaining(7, 1);
        sm.setWorkRemaining(10);
        sm.worked(6);
        m2.testRemaining(3, 2);
        sm.worked(4);
        m2.testRemaining(0, 1);

    }

    @Test
    public void testBarSmall() {
        JUnitMonitor jm = new JUnitMonitor(10);
        SubMonitor subM = SubMonitor.convert(jm);
        subM.setWorkRemaining(1000);
        for (int i = 0; i < 600; i++) {
            subM.worked(1);
        }
        jm.testRemaining(4, 1);
    }

    @Test
    public void testNewChild() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m);
        m.testRemaining(1000, 1);
        sm.setWorkRemaining(100);
        sm.start();
        m.testRemaining(1000, 2);
        sm.worked(20);
        m.testRemaining(800, 2);

        SubMonitor child1 = SubMonitor.convert(sm.newChild(50));
        child1.setWorkRemaining(100);
        child1.worked(10);
        m.testRemaining(750, 2);
        child1.worked(40);
        m.testRemaining(550, 2);
//    child1.message(50);   // idealfall
//    m.testRemaining(300, 2); //idealfall
        child1.finish();

        m.testRemaining(300, 2);

        sm.worked(20);
        m.testRemaining(100, 2);

        SubMonitor child2 = SubMonitor.convert(sm.newChild(10));
        child2.setWorkRemaining(500);
        child2.worked(50);
        m.testRemaining(90, 2);
        child2.finish();

        m.testRemaining(0, 2);
    }

    @Test
    public void testBar() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m);
        sm.title("Der Testtask");
        sm.start();
        sm.message("Message One");
        sm.setWorkRemaining(1000);
        for (int i = 0; i < 400; i++) {
            sm.worked(1);
        }
        m.testConsumed(400, 2);
        sm.setWorkRemaining(20);
        sm.message("Message Two");
        for (int i = 0; i < 10; i++) {
            sm.worked(1);
        }
        m.testRemaining(300, 1);
        sm.message("Message Three");
        sm.setWorkRemaining(100);
        for (int i = 0; i < 70; i++) {
            sm.worked(1);
        }
        m.testRemaining(90, 1);
        sm.message("Message Four");
        sm.finish();
    }

    @Test
    public void testConvert() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m, "Bla", 100);
        sm.start();
        m.testRemaining(1000, 1);
        sm.worked(50);
        m.testRemaining(500, 1);
    }

    @Test
    public void testConvert2() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m, 100);
        sm.start();
        m.testRemaining(1000, 1);
        sm.worked(50);
        m.testRemaining(500, 1);
    }

    @Test
    public void testChildChain() {
        JUnitMonitor m = new JUnitMonitor(1000);
        SubMonitor sm = SubMonitor.convert(m);
        sm.setWorkRemaining(500);
        sm.start();
        sm.worked(100);
        m.testRemaining(800, 2);
        SubMonitor c1 = SubMonitor.convert(sm.newChild(200));
        c1.setWorkRemaining(800);
        c1.worked(100);
        m.testRemaining(750, 2);
        c1.worked(100);
        m.testRemaining(700, 2);
        SubMonitor c2 = SubMonitor.convert(c1.newChild(400));
        c2.setWorkRemaining(100);
        c2.worked(50);
        m.testRemaining(600, 1);
        c2.finish();
        m.testRemaining(500, 1);
    }
}
