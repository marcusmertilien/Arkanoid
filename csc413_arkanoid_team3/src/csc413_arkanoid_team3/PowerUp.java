package csc413_arkanoid_team3;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PowerUp extends Prop {
    // PowerUp Types
    public Type type;
    public enum Type {
        DEFAULT
    };
    
    static{
        try {
            // Add static asset load. 
        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }

    public PowerUp(int x, int y) {
        // Init PowerUp here.
        super(x,y,0,0);
    }
    
}
