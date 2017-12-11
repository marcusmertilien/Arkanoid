package csc413_rainbowreef_team03;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Point;


public abstract class GameObject {

    protected static int nextId = 1; // id for next object
    protected int _id;               // current object id

    protected int x;                 // horizontal position
    protected int y;                 // vertical position

    protected int prevX;             // Previous x position
    protected int prevY;             // Previous y position

    protected int xSpeed;            // Horizontal speed
    //protected int ySpeed;            // Vertical speed

    protected int direction;         // direction the object is pointed in [0, 360]
    protected int speed;             // current speed in direction.

    protected boolean isVisible;     // dictates if the object is visible
    protected boolean isSolid;       // dictates if the object is solid

    protected int height = 0;
    protected int width = 0;

    protected BufferedImage sprite;


    // Constructors
    // ============
    public GameObject(
        int x,
        int y,
        int xSpeed,
        int ySpeed,
        int speed,
        boolean isSolid,
        boolean isVisible
    ) {
        // Stripe game object with an ID.
        this._id = ++GameObject.nextId;

        // Set basic props.
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.xSpeed = xSpeed;
        //this.ySpeed = ySpeed;
        this.speed = speed;

        // Set flags.
        this.isSolid = isSolid;
        this.isVisible = isVisible;
    }

    // Abtract interface
    // =================
    abstract void draw(Graphics2D g2d);

    // Acess
    // =====
    public int getId() { return _id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getXSpeed() { return xSpeed; }
    //public int getYSpeed() { return ySpeed; }
    public int getSpeed() { return speed; }
    public int getHeight() { return height; }
    public int getWidth() { return width; }

    // Mutate
    // ======
    public void setX(int x) { x = x; }
    public void setY(int y) { y = y; }
    public void setXSpeed(int xSpeed) { xSpeed = xSpeed; }
    public void setYSpeed(int ySpeed) { ySpeed = ySpeed; }
    public void setSpeed(int speed) { speed = speed; }
    public void setHeight(int height) { height = height; }
    public void setWidth(int width) { width = width; }

    // Visiblity
    // =========
    public boolean isVisible() { return isVisible; }
    public boolean isHidden() { return !(isVisible); }
    public void hide() { isVisible = false; }
    public void show() { isVisible = true; }

    // Movement
    // ========
    public void bounce() {
        xSpeed = -xSpeed;
        //ySpeed = -ySpeed;
    }

    public void stop() {
        xSpeed = 0;
        //ySpeed = 0;
    }

    public void resetLocation() {
        x = prevX;
        y = prevY;
    }

    // Meta data
    // =========
    public Rectangle getBound() {
        return new Rectangle(x, y, width, height);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public Point getCenterLocation() {
        return new Point(x+width/2, y+width/2);
    }
}
