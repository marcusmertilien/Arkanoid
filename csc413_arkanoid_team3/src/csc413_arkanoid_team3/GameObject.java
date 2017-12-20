package csc413_arkanoid_team3;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Graphics2D;


public abstract class GameObject extends Rectangle {

    // Class fields
    // ============

    private static int nextId = 1;   // id for next object
    private int _id;                 // current object id

    protected int previousX;         // Previous x position
    protected int previousY;         // Previous y position

    private boolean isVisible;       // dictates if the object is visible
    protected BufferedImage sprite;  // the image to be drawn each frame.


    // Constructors
    // ============

    public GameObject(
        int x,
        int y,
        int width,
        int height,
        boolean isVisible
    ) {
        // Setup rectangle props
        super(x, y, width, height);
        // Stripe each game object with an Id.
        this._id = ++GameObject.nextId;
        // Set flags.
        this.isVisible = isVisible;
        this.previousX = x;
        this.previousY = y;
    }


    // Abtract interface
    // =================

    abstract void draw(Graphics2D g2d);


    // Access
    // ======

    public int getId() {
        return _id;
    }


    // Visiblity
    // =========

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isHidden() {
        return !isVisible;
    }

    public void hide() {
        isVisible = false;
    }

    public void show() {
        isVisible = true;
    }


    // Movement
    // ========

    public void resetLocation() {
        x = previousX;
        y = previousY;
    }

}
