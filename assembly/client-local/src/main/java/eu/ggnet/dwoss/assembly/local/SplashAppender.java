package eu.ggnet.dwoss.assembly.local;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public class SplashAppender extends AppenderSkeleton {

    private String s1 = "Loading ";

    private String s2 = "";

    private String s3 = "";

    private SplashScreen splash;

    private Graphics2D graphics;

    private void renderSplashFrame() {
        int size = 35;
        if ( s1.length() > size && s2.length() > size && s3.length() > size ); else if ( s1.length() > size && s2.length() > size ) s3 += ".";
        else if ( s1.length() > size ) s2 += ".";
        else s1 += ".";
        graphics.drawString(s1, 5, 15);
        graphics.drawString(s2, 5, 30);
        graphics.drawString(s3, 5, 45);
        splash.update();
    }

    @Override
    protected void append(LoggingEvent event) {
        if ( getThreshold() != null && getThreshold().toInt() > event.getLevel().toInt() ) return;
        if ( splash == null ) {
            splash = SplashScreen.getSplashScreen();
            if ( splash == null ) return;
        }
        if ( !splash.isVisible() ) return;
        if ( graphics == null ) {
            graphics = splash.createGraphics();
            if ( graphics == null ) return;
            graphics.setComposite(AlphaComposite.Clear);
            graphics.fillRect(0, 0, 100, 100);
            graphics.setPaintMode();
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        }
        renderSplashFrame();
    }

    @Override
    public void close() {
        // nothing to do;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
