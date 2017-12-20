/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc413_arkanoid_team3;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.ArrayList;


public class Explode extends GameObject {
    
    public Type type;
    public enum Type{
        SHIP,ENEMY
    }

    private static int EXPLOSION_SIZE = 30;
    private static int COOL_DOWN_MAX_E = 24;
    private static int COOL_DOWN_MAX_S=8;
    private int animationTimerE = COOL_DOWN_MAX_E;
    private int animationTimerS = COOL_DOWN_MAX_S;

    private static ArrayList<BufferedImage> ship;
    private static ArrayList<BufferedImage> enemy;
    static {
        ship = new ArrayList<BufferedImage>();
        enemy = new ArrayList<BufferedImage>();

        try {
            BufferedImage asset;
            BufferedImage temp;
            Image tempScaledImage;
            ClassLoader cl = GameEngine.class.getClassLoader();
            Graphics2D g2d;
            String filePath;

            for (int i = 1; i <= 4; i++) {
                filePath = GameEngine.SHIP_PATH +"shipExplosion"+ i + ".png";
                asset = ImageIO.read(cl.getResource(filePath));
                tempScaledImage = asset.getScaledInstance(EXPLOSION_SIZE,-1,Image.SCALE_SMOOTH);
                temp = new BufferedImage(EXPLOSION_SIZE, EXPLOSION_SIZE, BufferedImage.TYPE_INT_ARGB);
                g2d = temp.createGraphics();
                g2d.drawImage(tempScaledImage, 0, 0, null);
                g2d.dispose();
                ship.add(temp);
            }
            
            for (int i=1; i<=12; i++){
                filePath = GameEngine.ENEMIES_ASSET_PATH+"explosion" + i + ".png";
                asset = ImageIO.read(cl.getResource(filePath));
                tempScaledImage = asset.getScaledInstance(EXPLOSION_SIZE,-1,Image.SCALE_SMOOTH);
                temp = new BufferedImage(EXPLOSION_SIZE, EXPLOSION_SIZE, BufferedImage.TYPE_INT_ARGB);
                g2d = temp.createGraphics();
                g2d.drawImage(tempScaledImage, 0, 0, null);
                g2d.dispose();
                enemy.add(temp);
            }

        } catch (IOException ex) {
            Logger.getLogger(Explode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Explode(int x, int y, Type explosionType) {
        super(x, y, EXPLOSION_SIZE, EXPLOSION_SIZE, true);
        this.type = explosionType;
        this.sprite = (this.type == Type.SHIP) ? ship.get(0) : enemy.get(0);
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.drawImage(this.sprite, this.x, this.y, EXPLOSION_SIZE, EXPLOSION_SIZE, null);
    }

    void update() {
        switch(type){
            case ENEMY:
                if (animationTimerE == 0) {
                    this.hide();
                } else {
                    this.sprite = enemy.get(--animationTimerE/2);
                }
                break;
            case SHIP:
            
                if (animationTimerS == 0) {
                    this.hide();
                } else {
                    this.sprite = enemy.get(--animationTimerS/2);
                }
                break;
        }
    }
}

//For adding an explosion to an object in gameEngine
//explosions.add(new Explode(this.testBall.x,this.testBall.y,Explode.Type.ENEMY));
