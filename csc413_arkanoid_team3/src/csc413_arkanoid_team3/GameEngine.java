package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;


public class GameEngine extends JPanel implements Runnable, Observer {

    // Game world size.
    private static final int WINDOW_BORDER_WIDTH = 5;
    private static final int GAME_WINDOW_WIDTH = 448;
    private static final int GAME_WINDOW_HEIGHT = 500;
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

    // Game interface controls.
    private HashMap<Integer, GameActions> gameControls;

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
    
    private int testScore;
    private int testLives;

    private ArrayList<PowerUp> testPowerUps;
    private PowerUp testActivePowerUp;

    // TODO: move this somewhere else.
    private static BufferedImage logoImage;
    static {
        try {
            // Load Arkanoid logo.
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
                frame.setIgnoreRepaint(true);

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
        gameState = GameState.PLAYING;

        // Application control.
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

        // Setup game action controls.
        gameControls = new HashMap<Integer, GameActions>();
        gameControls.put(KeyEvent.VK_P, GameActions.PAUSE);
        gameControls.put(KeyEvent.VK_ESCAPE, GameActions.EXIT);
        gameControls.put(KeyEvent.VK_1, GameActions.START);
        gameControls.put(KeyEvent.VK_M, GameActions.MUSIC_STOP);
    }

    private void _setupGameData() {
        // Listen from game engine.
        eventManager.addObserver(this);

        // Ship
        testShip = new Player(200, 450, p1Keys);
        eventManager.addObserver(testShip);

        // Stage
        this.testStage = new Stage(Stage.Rounds.ROUND_3);

        // Ball
        testBall = new Ball(205, 440);
        testBall.xSpeed = 2;
        testBall.ySpeed = -2;

        // Score and lives
        testScore = 0;
        testLives = 5;

        // Powerups
        testPowerUps = new ArrayList<PowerUp>();
    }

    private void _setupGameAudio() {
        soundManager.playBgMusic();
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
                    // Test area
                    updateData();
                    checkCollisions();
                    cleanupObjects();
                    break;
                case PAUSE_MENU:
                    break;
                case EXITING:
                    System.exit(0);
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
        for (PowerUp _p : testPowerUps) _p.update();
        testShip.update();
        testBall.update();
    }

    private void checkCollisions() {
        // Check ship vs side walls.
        if (testShip.x < 16 || (testShip.width + testShip.x + 16) > GAME_WINDOW_WIDTH) {
            testShip.resetLocation();
        }

        // Check ball vs side walls.
        if (testBall.x < 16 || testBall.x > GAME_WINDOW_WIDTH -16) {
            testBall.resetLocation();
            testBall.xSpeed = -(testBall.xSpeed);
        }

        // Check ball vs ceiling.
        if (testBall.y < 16) {
            testBall.resetLocation();
            testBall.ySpeed = -(testBall.ySpeed);
        }

        // Check ball vs gutter.
        if (testBall.y > GAME_WINDOW_HEIGHT-32) {
            testBall.resetLocation();
            testBall.ySpeed = -(testBall.ySpeed);
            testLives--;
        }


        // Check ship vs ball.
        if (Physics.doesCollideWith(testBall, testShip)) {
            // Claculate new ball speed based on contact point with padd
            int ballCenter = (testBall.x - testShip.x) + (testBall.width/2);
            int shipCenter = (testShip.width/2);
            int newXspeed = (ballCenter - shipCenter);

            // Reset location.
            testBall.resetLocation();

            // Use new ball speed.
            testBall.ySpeed = -testBall.ySpeed;
            testBall.xSpeed = newXspeed/8;

            // Allow paddle movement to contribute to reflected speed.
            testBall.xSpeed += testShip.xSpeed/2;

            // Trigger SFX.
            soundManager.playBallCollision(testShip);

            if (DebugState.showPaddleActive) {
                System.out.printf(
                    "ball center: %d, ship center: %d\nnew ball x: %d, new ball y: %d\n",
                    ballCenter, shipCenter, testBall.xSpeed, testBall.ySpeed
                );
            }
        }

        // Check for ball vs block.
        for (Block _b : this.testStage.blocks) {
            if (Physics.doesCollideWith(_b, testBall)) {
                // Reset location before calculating.
                testBall.resetLocation();

                // Calculate new x,y speeds based on contact with block
                if (testBall.y <= _b.y) {
                    // Contact top.
                    testBall.ySpeed = -Math.abs(testBall.ySpeed);
                }
                else if (testBall.y >= _b.y + _b.height) {
                    // COntact bottom.
                    testBall.ySpeed = Math.abs(testBall.ySpeed);
                }
                else if (testBall.x <= _b.x) {
                    // Contact left.
                    testBall.xSpeed = -Math.abs(testBall.xSpeed);
                }
                else if (testBall.x >= _b.x + _b.width) {
                    // Contact right.
                    testBall.xSpeed = Math.abs(testBall.ySpeed);
                }

                // Updare user score.
                this.testScore += _b.registerHit();

                // Play SFX.
                this.soundManager.playBallCollision(_b);

                // Test for powerups, create a random type on every brick break.
                if (_b.isHidden()) {
                    testPowerUps.add(new PowerUp(
                        _b.x, _b.y, PowerUp.Types.values()[new Random().nextInt(PowerUp.Types.values().length)]
                    ));
                }

                // Only register one collision per cycle.
                break;
            }
        }

        for (PowerUp _p: testPowerUps) {
            // Check powerup vs lower bound.
            if (_p.y > GAME_WINDOW_HEIGHT-42)
                _p.hide();

            // Check powerup vs ship.
            if (Physics.doesCollideWith(_p, testShip)) {
                _p.hide();
                testActivePowerUp = _p;
                soundManager.playPowerUpCollision();
            }
        }
    }


    private void cleanupObjects() {
        // Object cleanup.
        testStage.blocks.removeIf(_b -> _b.isHidden());
        testPowerUps.removeIf(_p -> _p.isHidden());
    }


    // *** JPanel.paintComponent
    @Override
    protected void paintComponent(Graphics g) {

        BufferedImage windowBuffer = new BufferedImage(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) windowBuffer.getGraphics();

        // Draw based on current GameState.
        switch (gameState) {
            case INITIALIZING:
                // Game open
                // TODO: draw splash screen.
                break;
            case PLAYING:
                _drawBackground(g2d);
                _drawGameObjects(g2d);
                _drawUIPanel(g2d);
                break;
            case PAUSE_MENU:
                _drawBackground(g2d);
                _drawGameObjects(g2d);
                _drawUIPanel(g2d);
                _drawUIPause(g2d);
                break;
            case EXITING:
                break;
            default:
                // Somehow, we have a bad enum...
                System.out.println("GameEngine::gameLoop Error: bad enum");
                break;
        }

        // Drw current frame.
        g.drawImage(windowBuffer, 0, 0, this);
    }

    private void _drawViews(Graphics2D g2d, BufferedImage gameWorldBuffer) {
        // Assess if we actually needs x2 players here, might be cool but more work.
    }

    private void _drawBackground(Graphics2D g2d) {
        testStage.draw(g2d);
    }

    private void _drawGameObjects(Graphics2D g2d) {
        testBall.draw(g2d);
        testShip.draw(g2d);
        for (PowerUp _p : testPowerUps) _p.draw(g2d);
    }

    private void _drawUIPanel(Graphics2D g2d) {
        int commonXoffset = GAME_WINDOW_WIDTH+10;

        // Draw branding.
        g2d.drawImage(logoImage, commonXoffset, 15, 180, 40, null);

        // Draw lives count.
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("1UP x" + testLives, commonXoffset, 75);

        // Draw Score.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("" + this.testScore, commonXoffset, 90);

        // Draw powerup type if active.
        if (testActivePowerUp != null) {
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Courier", Font.BOLD, 14));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString("Active Power-up", commonXoffset, 110);
            g2d.drawString(testActivePowerUp.type.name(), commonXoffset, 125);
        }
    }

    private void _drawUIPause(Graphics2D g2d) {
        int commonXoffset = GAME_WINDOW_WIDTH+10;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("GAME PAUSED", commonXoffset + 40, MAIN_WINDOW_HEIGHT - 40);
    }

    @Override
    public void update(Observable obj, Object e) {

        // onKey event
        if (e instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) e;
            int keyCode = ke.getKeyCode();
            int keyId = ke.getID();
            GameActions buttonPressed = gameControls.get(keyCode);

            // Only react to buttons the game engine is interested in.
            if (buttonPressed == null) {
                return;
            }

            if (keyId == KeyEvent.KEY_PRESSED) {
                // Toggle game pause on P press.
                if (buttonPressed == GameActions.PAUSE) {
                    if (gameState != GameState.PAUSE_MENU) {
                        gameState = GameState.PAUSE_MENU;
                        soundManager.pauseBgMusic();
                    } else {
                        gameState = GameState.PLAYING;
                        soundManager.pauseBgMusic();
                    }
                }

                // Toggle music on M press.
                if (buttonPressed == GameActions.MUSIC_STOP) {
                    // Note: this will toggle play/pause depending on clip state.
                    soundManager.pauseBgMusic();
                }

                // Exit game on escape press.
                if (buttonPressed == GameActions.EXIT) {
                    gameState = GameState.EXITING;
                }
            }


        }
    }

}
