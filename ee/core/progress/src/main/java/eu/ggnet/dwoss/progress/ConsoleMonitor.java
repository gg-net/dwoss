/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.progress;

import eu.ggnet.saft.api.progress.IMonitor;

import javax.enterprise.inject.Alternative;

/**
 *
 * @author AS
 */
@Alternative
public class ConsoleMonitor implements IMonitor {

    public int bar_length = 1000;

    public int barPerTick;

    public int absoluteRemainingTicks;

    @Override
    public IMonitor start() {
        return this;
    }

    @Override
    public IMonitor finish() {
        return this;
    }

    @Override
    public IMonitor title(String name) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits) {
        System.out.print("#");
        return this;
    }

    @Override
    public IMonitor message(String subMessage) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits, String subMessage) {
        return this;
    }

    @Override
    public int getAbsolutRemainingTicks() {
        return absoluteRemainingTicks;
    }


    /*  @Override
     public SubMonitor setWorkRemaining(int workRemaining) {
     throw new UnsupportedOperationException("Not supported yet.");
     }
     */
    public void printBar(int ticks) {
        System.out.print(" [");
        for (int i = 1; i <= ticks; i++) {
            System.out.print("|" + i + "|");
        }
        System.out.print("]\n");

    }

    public static void main(String[] args) {

        ConsoleMonitor console = new ConsoleMonitor();
        SubMonitor subMon = SubMonitor.convert(console, "test", 100);
        subMon.title("subMonNameExample");
        subMon.setWorkRemaining(5);
        subMon.worked(2);
        subMon.setWorkRemaining(10);
        subMon.worked(5);
        subMon.worked(5);

    }
}
