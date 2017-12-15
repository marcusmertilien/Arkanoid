package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;


public abstract class Actor extends GameObject {

    protected int xSpeed;   // horizontal speed
    protected int ySpeed;   // vertical speed
    protected int speed;    // base speed of actor

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
        int speed
    ) {
        super(x, y, width, height, true);

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.speed = speed;
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
        g2d.drawImage(sprite, x, y, width, height, null);
    }
}
