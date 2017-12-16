package csc413_arkanoid_team3;

import java.awt.Rectangle;


public class Physics {

    public static Boolean doesCollideWith(GameObject objA, GameObject objB) {
        return objA.getBounds().intersects(objB.getBounds());
    }

    public static Boolean doesCollideWith(GameObject obj, Rectangle bounds) {
        return bounds.intersects(obj.getBounds());
    }

    public static Rectangle getIntersection(GameObject objA, GameObject objB) {
        return objA.getBounds().intersection(objB.getBounds());
    }

    public static Boolean isBoundedBy(GameObject objA, GameObject objB) {
        return objA.getBounds().contains(objB.getBounds());
    }

    public static Boolean isBoundedBy(GameObject obj, Rectangle bounds) {
        return bounds.contains(obj.getBounds());
    }

}
