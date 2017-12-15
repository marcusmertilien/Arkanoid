/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc413_tankgame_team03;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.ArrayList;


public class Explosion extends GameObject {

    private static int EXPLOSION_SIZE = 30;
    private static int COOL_DOWN_MAX = 16;
    private int animationTimer = COOL_DOWN_MAX;

    private static ArrayList<BufferedImage> frames;
    static {
        frames = new ArrayList<BufferedImage>();

        try {
            BufferedImage asset;
            BufferedImage temp;
            Image tempScaledImage;
            ClassLoader cl = GameEngine.class.getClassLoader();
            Graphics2D g2d;
            String filePath;

            for (int i = 1; i <= 8; i++) {
                filePath = GameEngine.EXPLOSION_PATH + i + ".png";
                asset = ImageIO.read(cl.getResource(filePath));
                tempScaledImage = asset.getScaledInstance(EXPLOSION_SIZE,-1,Image.SCALE_SMOOTH);
                temp = new BufferedImage(EXPLOSION_SIZE, EXPLOSION_SIZE, BufferedImage.TYPE_INT_ARGB);
                g2d = temp.createGraphics();
                g2d.drawImage(tempScaledImage, 0, 0, null);
                g2d.dispose();
                frames.add(temp);
            }

        } catch (IOException ex) {
            Logger.getLogger(Explosion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Explosion(int x, int y) {
        super(x, y, EXPLOSION_SIZE, EXPLOSION_SIZE, true, true);
        sprite = frames.get(0);
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.drawImage(this.sprite, this.x, this.y, EXPLOSION_SIZE, EXPLOSION_SIZE, null);
    }

    void update() {
        if (animationTimer == 0) {
            this.isVisible = false;
        } else {
            this.sprite = frames.get(--animationTimer/2);
        }
    }
}
