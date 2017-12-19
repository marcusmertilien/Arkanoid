package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;


public class GameEngine extends JPanel implements Runnable {

    // Game world size.
    private static final int WINDOW_BORDER_WIDTH = 5;
    private static final int GAME_WINDOW_WIDTH = 448;
    private static final int GAME_WINDOW_HEIGHT = 480;
    private static final int UI_PANEL_WIDTH = 200;
    private static final int UI_PANEL_HEIGHT = 200;
    private static final int MAIN_WINDOW_WIDTH = GAME_WINDOW_WIDTH + UI_PANEL_WIDTH;
    private static final int MAIN_WINDOW_HEIGHT = GAME_WINDOW_HEIGHT;

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
    public static String POWERUPS_ASSET_PATH = ASSET_PATH + "power-ups/";

    // Test data
    private Player testShip;
    private Stage testStage;
    private Ball testBall;
    private ArrayList<PowerUp> powerups;
    private int testScore;

    // TODO: move this somewhere else.
    private static BufferedImage logoImage;
    static {
        try {
            ClassLoader cl = GameEngine.class.getClassLoader();
            logoImage = ImageIO.read(cl.getResource(GameEngine.GENERAL_ASSET_PATH + "logo.png"));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

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
                frame.setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH, GAME_WINDOW_HEIGHT));
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
        this.setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH, GAME_WINDOW_HEIGHT));
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
        testShip = new Player(200, 420, p1Keys);
        this.testStage = new Stage(Stage.Rounds.ROUND_3);
        testBall = new Ball(205, 400);
        testBall.xSpeed = 2;
        testBall.ySpeed = -2;

        powerups = new ArrayList<PowerUp>();

        for (int i = 0; i < PowerUp.Types.values().length; i++) {
            powerups.add(new PowerUp(100 + (32*i), 25, PowerUp.Types.values()[i]));
        }

        eventManager.addObserver(testShip);
    }

    private void _setupGameAudio() {
        // TODO: maybe implment soundtrack;
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
                    testShip.update();
                    testBall.update();
                    for (PowerUp _p : powerups) { _p.update(); }
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
        if (testShip.x < 16 || (testShip.width + testShip.x + 16) > GAME_WINDOW_WIDTH) {
            testShip.resetLocation();
        }

        // Check x axis boundry collision.
        if (testBall.x < 16 || testBall.x > GAME_WINDOW_WIDTH -16) {
            testBall.resetLocation();
            testBall.xSpeed = -(testBall.xSpeed);
        }

        // Check y axis boundery collsion.
        if (testBall.y < 16 || testBall.y > GAME_WINDOW_HEIGHT-16) {
            testBall.resetLocation();
            testBall.ySpeed = -(testBall.ySpeed);
        }

        // Check ship vs ball collision.
        if (Physics.doesCollideWith(testBall, testShip)) {
            int normalizedBallPosition = testBall.x - testShip.x;
            int ballCenter = normalizedBallPosition + testBall.width;
            int shipCenter = testShip.width/2;
            int tempX = (ballCenter - shipCenter)/4;

            testBall.resetLocation();
            testBall.ySpeed = -testBall.ySpeed;
            testBall.xSpeed += (tempX < testBall.speed) ? tempX : testBall.speed;

            this.soundManager.playBallCollision(testShip);
        }

        // Check for ball collisions with block.
        for (Block _b : this.testStage.blocks) {
            if (Physics.doesCollideWith(_b, testBall)) {
                // Find intersection
                Rectangle _i = Physics.getIntersection(testBall, _b);

                if (_i.y == _b.y) {
                    testBall.ySpeed = -testBall.ySpeed;
                }

                if (_i.y == testBall.y) {
                    testBall.ySpeed = -testBall.ySpeed;
                }

                if (_i.x == _b.x) {
                    testBall.xSpeed = -testBall.xSpeed;
                }

                if(_i.x == testBall.x) {
                    testBall.xSpeed = -testBall.xSpeed;
                }

                this.soundManager.playBallCollision(_b);
                this.testScore += _b.registerHit();

                break;
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
            GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        // The game area's buffer.
        BufferedImage gameAreaBuffer = new BufferedImage(
            GAME_WINDOW_WIDTH + UI_PANEL_WIDTH, GAME_WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
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
                testBall.draw(g2d);
                for (PowerUp _p : powerups) {
                    _p.draw(g2d);
                }
                testShip.draw(g2d);
                drawUIPanel(g2d);

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
        g2d.drawImage(logoImage, GAME_WINDOW_WIDTH+10, 15, 180, 40, null);

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("1UP", GAME_WINDOW_WIDTH+10, 75);

        // Set font for rendering stats.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("" + this.testScore, GAME_WINDOW_WIDTH+10, 90);
    }

}
