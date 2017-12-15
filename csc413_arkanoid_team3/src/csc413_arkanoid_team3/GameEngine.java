package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;


public class GameEngine extends JPanel implements Runnable {
    
    // Game world size.
    // Define stage window's size.

    // View window size.
    private static final int WINDOW_BORDER_WIDTH = 5;
    private static final int WINDOW_WIDTH        = 448;
    private static final int WINDOW_HEIGHT       = 480;

    // Game loop constants.
    private static final int TARGET_FPS     = 30;
    private static final long ONE_SECOND_NS = 1000000000;
    private static final long OPTIMAL_TIME  = ONE_SECOND_NS / TARGET_FPS;

    // Game state
    private static enum GameState {
        INITIALIZING,
        LOADING,
        MAIN_MENU,
        OPTIONS_MENU,
        PAUSE_MENU,
        PLAYING,
        GAME_OVER,
        TESTING_STAGE_BG,
        EXITING
    };
    private Boolean isRunning;
    private GameState gameState;

    // Input handlers
    private EventManager eventManager;
    private InputHandler inputHandler;
    private SoundManager soundManager;

    // Players
    private HashMap<Integer, Controls> p1Keys;
    private HashMap<Integer, Controls> p2Keys;

    // Assets
    public static String ASSET_PATH = "resources/";
    public static String STAGE_BG_PATH = ASSET_PATH + "stage-background/";
    public static String SOUND_ASSET_PATH = ASSET_PATH + "sounds/";

    // Test data
    private Stage testStage;


    // Entry point
    // ===========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                // Initialize and start the app.
                GameEngine engine = new GameEngine();

                // Setup parent frame.
                JFrame frame = new JFrame();
                frame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // Setup and run engine.
                engine.init(frame);
                engine.run();
            }
        });
    }

    // *** Runnable.run
    @Override
    public void run() {
        // Create a new thread, and start the game loop...
        new Thread() {
            public void run() {
                gameLoop();
            }
        }.start();

    }

    // Init
    // ====
    public void init(JFrame frame) {
        isRunning = true;

        // Active test mode for BG.
        gameState = GameState.TESTING_STAGE_BG;

        inputHandler = InputHandler.getInstance();
        soundManager = SoundManager.getInstance();
        eventManager = EventManager.getInstance();

        // Setup game panel.
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocus();
        this.setOpaque(false);
        this.addKeyListener(inputHandler); // attach input handler to panel

        // Setup player keys.
        _setupControls();

        // Setup data.
        _setupGameData();

        // Setup audio track.
        _setupGameAudio();

        // Add game panel instance to parent frame.
        frame.add(this, BorderLayout.CENTER);
    }

    private void _setupControls() {
        // Setup player 1 key mapping
        p1Keys = new HashMap<Integer, Controls>();
        p1Keys.put(KeyEvent.VK_LEFT, Controls.LEFT);
        p1Keys.put(KeyEvent.VK_RIGHT, Controls.RIGHT);
        p1Keys.put(KeyEvent.VK_ENTER, Controls.SHOOT);

        // Setup player 2 key mapping (Do we still need 2 player?)
        p2Keys = new HashMap<Integer, Controls>();
        p2Keys.put(KeyEvent.VK_F, Controls.LEFT);
        p2Keys.put(KeyEvent.VK_H, Controls.RIGHT);
        p2Keys.put(KeyEvent.VK_SPACE, Controls.SHOOT);
    }

    private void _setupGameData() {
        // TODO: we'll likely need to so _something_ here.
         this.testStage = new Stage(Stage.Rounds.ROUND_2);
    }

    private void _setupGameAudio() {
        if (DebugState.playSoundtrackActive)
            soundManager.playSoundtrack();
    }

    // Game loop
    // =========
    private void gameLoop() {

        long lastFrameTime = System.nanoTime(); // previous frame time
        long lastFps = 0;                       // previous frame fps
        long fps = 0;                           // frames per second

        // Main application loop...
        while (isRunning) {

            // Switch on current GameState.
            switch (gameState) {
                case INITIALIZING:
                    // Game open
                    break;
                case PLAYING:
                    // Main game update
                    break;
                case TESTING_STAGE_BG:
                    // Test area
                    break;
                case EXITING:
                    // Game closing
                    isRunning = false;
                    break;
                default:
                    // Somehow, we have a bad enum...
                    System.out.println("GameEngine::gameLoop Error: bad enum");
                    break;
            }

            // Draw application.
            repaint();

            // FPS tracking.
            long now = System.nanoTime();            // new frame time
            long updateLength = now - lastFrameTime; // diff between frames

            // Update counters for new frame.
            lastFrameTime = now;
            lastFps += updateLength;
            fps++;

            // Show fps counter once per second.
            if (DebugState.showFPSActive && lastFps >= ONE_SECOND_NS) {
                System.out.println("GameEngine::gameLoop - fps: " + fps);
                fps = lastFps = 0;
            }

            try {
                // Sleep until the next frame is due.
                Thread.sleep((lastFrameTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
            } catch (Exception e) {
                // We should probably do something graceful here...
            }
        }
    }


    // Update data layer
    // =================
    private void updateData() {
        // Update application data.
    }

    private void checkCollisions() {
        // Check stage's items again instance ball and paddle.
    }


    private void cleanupObjects() {
        // Object cleanup.
    }


    // *** JPanel.paintComponent
    @Override
    protected void paintComponent(Graphics g) {

        // The window's double buffer.
        BufferedImage windowBuffer = new BufferedImage(
            WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        // The game area's buffer.
        BufferedImage gameAreaBuffer = new BufferedImage(
            WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        // Draw based on current GameState.
        switch (gameState) {
            case INITIALIZING:
                // Game open
                // TODO: draw splash screen.
                break;
            case PLAYING:
                // Main application loop here
                break;
            case TESTING_STAGE_BG:
                // Test stage draw.
                Graphics2D g2d = (Graphics2D) windowBuffer.getGraphics();
                this.testStage.draw(g2d);
                g2d.dispose();

                // Draw window.
                g.drawImage(windowBuffer, 0, 0, this);
                break;
            case EXITING:
                // Application exiting....
                break;
            default:
                // Somehow, we have a bad enum...
                System.out.println("GameEngine::gameLoop Error: bad enum");
                break;

        }
    }

    private void _drawViews(Graphics2D g2d, BufferedImage gameWorldBuffer) {
        // Assess if we actually needs x2 players here, might be cool but more work.
    }

    private void _drawBackground(Graphics2D g2d) {
        // Draw the stages background.
    }

    private void _drawGameObjects(Graphics2D g2d) {
        // Draw the stage's objects.
    }

    private void _drawUIPanel(Graphics g) {
        // UI is defined as anything around the central game panel.
    }

}
