package csc413_rainbowreef_team03;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;


public class Projectile extends GameObject {

    public static int PROJECTILE_SIZE = 10;
    public static int PROJECTILE_SPEED = 5;


    // Constructors
    // ============
    public Projectile(
        int direction,
        int x,
        int y,
        int xSpeed
        //int ySpeed
    ) {
        // Use super class constructor.
        // Provide all, and set visible and solid to true.
        super(x, y, xSpeed,0,PROJECTILE_SPEED, true, true);
        this.direction = direction;

        // Setup test image.
        try {
            ClassLoader cl = Projectile.class.getClassLoader();
            String path = "resources/projectiles/TankBullet.png";
            BufferedImage asset = ImageIO.read(cl.getResource(path));

            // Create temp scaled image.
            Image tempScaledImage = asset.getScaledInstance(PROJECTILE_SIZE, -1, Image.SCALE_SMOOTH);
            sprite = new BufferedImage(PROJECTILE_SIZE, PROJECTILE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = sprite.createGraphics();

            // Now store the scaled version as our asset.
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();

            // Set new width and height.
            width = sprite.getWidth();
            height = sprite.getWidth();

        } catch (Exception e) {
            System.out.print("No resources are found\n");
        }
    }


    // API
    // ===
    @Override
    public void draw(Graphics2D g2d) {
        if (this.isVisible()) {
            //if (DebugState.showBoundsActive) {
            //    _debugDraw(g2d);
            //} else {
                _draw(g2d);
            //}
        }
    }

    private void _debugDraw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(direction), (x+PROJECTILE_SIZE/2), (y+PROJECTILE_SIZE/2));
        g2d.setTransform(at);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, width, height);
        g2d.drawImage(sprite, x+1, y+1, width-2, height-2, null);
    }

    private void _draw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(direction), (x+PROJECTILE_SIZE/2), (y+PROJECTILE_SIZE/2));
        g2d.setTransform(at);
        g2d.drawImage(sprite, x, y, width, height, null);
    }

    public void update() {
        switch (direction) {
            case 0:
                //y += ySpeed-speed;
                break;

            case 45:
                x += xSpeed+speed;
                //y += ySpeed-speed;
                break;

            case 90:
                x += xSpeed+speed;
                break;

            case 135:
                x += xSpeed+speed;
                //y += ySpeed+speed;
                break;

            case 180:
               // y += ySpeed+speed;
                break;

            case 225:
                x += xSpeed-speed;
                //y += ySpeed+speed;
                break;

            case 270:
                 x += xSpeed-speed;
                 break;

            case 315:
                x += xSpeed-speed;
                //y += ySpeed-speed;
                break;

            default:
                break;
        }
    }
}
