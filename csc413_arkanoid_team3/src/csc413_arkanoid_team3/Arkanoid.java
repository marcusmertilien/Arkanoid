package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class Arkanoid {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                // Initialize and start the app.
                GameEngine engine = new GameEngine();

                // Setup parent frame.
                JFrame frame = new JFrame();
                frame.setPreferredSize(new Dimension(GameEngine.MAIN_WINDOW_WIDTH, GameEngine.GAME_WINDOW_HEIGHT));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setIgnoreRepaint(true);

                // Setup and run engine.
                engine.init(frame);
                engine.run();
            }
        });
    }
}
