/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc413_tankgame_team03;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PowerUp extends Prop {
    //PowerUp Types
    public Type type;
    public enum Type{
        speed, damage, life
    }

    private static BufferedImage speed;
    private static BufferedImage damage;
    private static BufferedImage life;
    static{
        try {
            ClassLoader cl = Tank.class.getClassLoader();
            BufferedImage rawAsset;
            Image tempScaledImage;
            Graphics2D g2d;
            int tileSize = GameEngine.TILE_SIZE/2;

            // Open and set normal boulder asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.POWERUP_PATH + "SpeedPowerUp.png"));
            tempScaledImage = rawAsset.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            speed = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            g2d = speed.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();

            // Open and set breakable boulder asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.POWERUP_PATH + "DamagePowerUp.png"));
            tempScaledImage = rawAsset.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            damage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            g2d = damage.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();
            
            // Open and set breakable boulder asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.POWERUP_PATH + "LifePowerUp.png"));
            tempScaledImage = rawAsset.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            life = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            g2d = life.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();
            
        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }
    public PowerUp(int x, int y, Type powerUpType){
        super(x, y, GameEngine.TILE_SIZE, GameEngine.TILE_SIZE);
        this.type = powerUpType;
        //this.sprite = (this.type == Type.speed) ? speed : damage : life;
        switch(powerUpType){
            case life:
                this.sprite = life;
                break;
            case damage:
                this.sprite = damage;
                break;
            case speed:
                this.sprite = speed;
                break;
        }
    }
    
}
