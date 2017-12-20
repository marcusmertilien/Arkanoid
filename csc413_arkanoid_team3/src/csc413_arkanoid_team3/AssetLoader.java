package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;

public class AssetLoader {

    public static BufferedImage getScaledInstance(BufferedImage source, int width, int height) {
        BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Image scaledImage = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = destImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);

        return destImage;
    }

    public static BufferedImage load(String filePath, int scale) {
        ClassLoader cl = GameEngine.class.getClassLoader();
        BufferedImage imageAsset = new BufferedImage(1,1,Image.SCALE_SMOOTH);

        try {
            // Fetch raw asset.
            BufferedImage rawAsset = ImageIO.read(cl.getResource(filePath));

            // Calculate new width and height.
            int width = scale*rawAsset.getWidth();
            int height = scale*rawAsset.getHeight();
            imageAsset = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = imageAsset.createGraphics();

            // Scale image based on arguyment.
            Image tempScaledImage = rawAsset.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // Draw scaled instance to return image.
            g2d.drawImage(tempScaledImage, 0, 0, null);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return imageAsset;
    }

}
