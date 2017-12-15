
package csc413_tankgame_team03;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public abstract class Actor extends GameObject {

    protected int xSpeed;
    protected int ySpeed;
    protected int speed;
    protected int direction;

    protected SoundManager soundManager;


    // Constructors
    // ============
    public Actor(
        int x,
        int y,
        int width,
        int height,
        int xSpeed,
        int ySpeed,
        int speed,
        int direction
    ) {
        super(x, y, width, height, true, true);

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.speed = speed;
        this.direction = direction;
        this.soundManager = SoundManager.getInstance();
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
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(direction), (x+width/2), (y+height/2));
        g2d.setTransform(at);
        g2d.drawImage(sprite, x, y, width, height, null);
    }
}
