package csc413_tankgame_team03;

import java.awt.Rectangle;


public class Physics {
    public static Boolean collides(GameObject objA, GameObject objB) {
        return objA.getBound().intersects(objB.getBound());
    }

    public static Boolean collides(GameObject obj, Rectangle bounds) {
        return bounds.intersects(obj.getBound());
    }

    public static Boolean bounded(GameObject objA, GameObject objB) {
        return objA.getBound().contains(objB.getBound());
    }

    public static Boolean bounded(GameObject obj, Rectangle bounds) {
        return bounds.contains(obj.getBound());
    }
}
