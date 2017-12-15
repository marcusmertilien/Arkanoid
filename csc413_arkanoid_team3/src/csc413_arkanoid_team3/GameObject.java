package csc413_arkanoid_team3;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Graphics2D;


public abstract class GameObject extends Rectangle {

    protected static int nextId = 1; // id for next object
    protected int _id;               // current object id

    protected int x;                 // horizontal position
    protected int y;                 // vertical position

    protected int previousX;             // Previous x position
    protected int previousY;             // Previous y position

    protected boolean isVisible;     // dictates if the object is visible

    protected int width;             // Object width
    protected int height;            // Object height

    protected BufferedImage sprite;  // the image to be drawn each frame


    // Constructors
    // ============

    public GameObject(
        int x,
        int y,
        int width,
        int height,
        boolean isVisible
    ) {
        // Stripe each game object with an Id.
        this._id = ++GameObject.nextId;

        // Set basic props.
        this.x = this.previousX = x;
        this.y = this.previousY = y;
        this.width = width;
        this.height = height;

        // Set flags.
        this.isVisible = isVisible;
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
