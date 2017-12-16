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
    private static final int WINDOW_BORDER_WIDTH = 5;
    private static final int WINDOW_WIDTH        = 448;
    private static final int WINDOW_HEIGHT       = 480;

    // Game loop constants.
    private static final int TARGET_FPS     = 60;
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
        TESTING_DRAWING,
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
    public static String SHIP_PATH = ASSET_PATH + "ship/";
    public static String STAGE_BG_PATH = ASSET_PATH + "stage-background/";
    public static String SOUND_ASSET_PATH = ASSET_PATH + "sounds/";
    public static String GENERAL_ASSET_PATH = ASSET_PATH + "general/";
    public static String BALL_ASSET_PATH = ASSET_PATH + "ball/";

    // Test data
    private Player testShip;
    private Stage testStage;
    private Ball testBall;
    private int testScore = 100;

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
        gameState = GameState.TESTING_DRAWING;

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
        this.testShip = new Player(200, 420, p1Keys);
        this.testStage = new Stage(Stage.Rounds.ROUND_1);
        this.testBall = new Ball(205, 400);
        this.testBall.xSpeed = 3;
        this.testBall.ySpeed = -3;
        eventManager.addObserver((Observer) this.testShip);
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
                case TESTING_DRAWING:
                    // Test area
                    this.testShip.update();
                    this.testBall.update();
                    checkCollisions();
                    cleanupObjects();
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
                System.out.println(e.toString());
            }
        }
    }


    // Update data layer
    // =================
    private void updateData() {
        // Update application data.
    }

    private void checkCollisions() {
        // Check ship vs boundry collision.
        if (this.testShip.x < 24 || (testShip.width + testShip.x + 24) > WINDOW_WIDTH) {
            this.testShip.resetLocation();
        }

        // Check x axis boundry collision.
        if (this.testBall.x < 24 || this.testBall.x > WINDOW_WIDTH -24) {
            this.testBall.resetLocation();
            this.testBall.xSpeed = -(this.testBall.xSpeed);
        }

        // Check y axis boundery collsion.
        if (this.testBall.y < 24 || this.testBall.y > WINDOW_HEIGHT-24) {
            this.testBall.resetLocation();
            this.testBall.ySpeed = -(this.testBall.ySpeed);
        }

        // Check ship vs ball collision.
        if (Physics.doesCollideWith(this.testBall, this.testShip)) {
            this.testBall.resetLocation();
            this.testBall.ySpeed = -(this.testBall.ySpeed);
        }

        // Check for ball collisions with block.
        for (Block _b : this.testStage.blocks) {
            if (Physics.doesCollideWith(_b, this.testBall)) {
                // Find intersection
                Rectangle _i = Physics.getIntersection(this.testBall, _b);

                // Detect which side contact was made, and flip that axis.
                if ((_i.y == _b.y) || (_i.y == this.testBall.y)) {
                    this.testBall.resetLocation();
                    this.testBall.ySpeed = -this.testBall.ySpeed;
                } else if ((_i.x == _b.x) || (_i.x == this.testBall.x)) {
                    this.testBall.resetLocation();
                    this.testBall.xSpeed = -this.testBall.xSpeed;
                }

                this.testScore += _b.registerHit();
            }
        }
    }


    private void cleanupObjects() {
        // Object cleanup.
        this.testStage.blocks.removeIf(_b -> _b.isHidden());
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
            case TESTING_DRAWING:
                // Test stage draw.
                Graphics2D g2d = (Graphics2D) gameAreaBuffer.getGraphics();
                this.testStage.draw(g2d);
                g2d.dispose();

                g2d = (Graphics2D) gameAreaBuffer.getGraphics();
                this.testBall.draw(g2d);
                g2d.dispose();

                g2d = (Graphics2D) gameAreaBuffer.getGraphics();
                this.testShip.draw(g2d);
                g2d.dispose();

                g2d = (Graphics2D) gameAreaBuffer.getGraphics();
                drawUIPanel(g2d);
                g2d.dispose();

                // Draw window.
                g.drawImage(gameAreaBuffer, 0, 0, this);
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

    private void drawUIPanel(Graphics2D g2d) {

        // Set font for rendering stats.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 18));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw player 1's stats.
        g2d.drawString("" + this.testScore, 25, 35);
    }

}
