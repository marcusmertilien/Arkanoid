package csc413_tankgame_team03;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;


public abstract class Prop extends GameObject { 


    // Constructors
    // ============
    public Prop(int x, int y, int width, int height) {
        super(x, y, width, height, true, true);
    }


    // Draw API
    // ========
    @Override
    public void draw(Graphics2D g2d) {
        if (DebugState.showBoundsActive)
            _debugDraw(g2d);

        _draw(g2d);
    }

    private void _debugDraw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x-1, y-1, width+2, height+2);
    }

    private void _draw(Graphics2D g2d) {
        g2d.drawImage(sprite, x, y, width, height, null);
    }
}
