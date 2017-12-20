package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.imageio.ImageIO;


public class GameEngine extends JPanel implements Runnable, Observer {

    // Game world size.
    public static final int WINDOW_BORDER_WIDTH = 5;
    public static final int GAME_WINDOW_WIDTH = 450;
    public static final int GAME_WINDOW_HEIGHT = 500;
    public static final int UI_PANEL_WIDTH = 200;
    public static final int UI_PANEL_HEIGHT = 200;
    public static final int MAIN_WINDOW_WIDTH = GAME_WINDOW_WIDTH + UI_PANEL_WIDTH;
    public static final int MAIN_WINDOW_HEIGHT = GAME_WINDOW_HEIGHT;

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
    
    private ArrayList<Explode> explosions;

    // Game interface controls.
    private HashMap<Integer, GameActions> gameControls;

    // Assets
    public static String ASSET_PATH = "resources/";
    public static String ENEMIES_ASSET_PATH = ASSET_PATH + "enemies/";
    public static String SHIP_PATH = ASSET_PATH + "ship/";
    public static String STAGE_BG_PATH = ASSET_PATH + "stage-background/";
    public static String SOUND_ASSET_PATH = ASSET_PATH + "sounds/";
    public static String GENERAL_ASSET_PATH = ASSET_PATH + "general/";
    public static String BALL_ASSET_PATH = ASSET_PATH + "ball/";
    public static String POWERUPS_ASSET_PATH = ASSET_PATH + "power-ups/";
    
    //Moving Away From Test Data
    private Player player1;

    // Test data
    private Player testShip;
    private Stage testStage;
    private Ball testBall;
    private int testScore;
    private int testLives;
    private ArrayList<PowerUp> testPowerUps;
    private PowerUp testActivePowerUp;


    // Load static assets
    // TODO: move this somewhere else.
    private static BufferedImage logoImage;
    private static BufferedImage splashLogo;

    static {
        try {
            // Load Arkanoid logo.
            ClassLoader cl = GameEngine.class.getClassLoader();
            logoImage = ImageIO.read(cl.getResource(GameEngine.GENERAL_ASSET_PATH + "logo.png"));
            splashLogo = ImageIO.read(cl.getResource(GameEngine.GENERAL_ASSET_PATH + "Splash.png"));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
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
        gameState = GameState.MAIN_MENU;

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
        gameControls.put(KeyEvent.VK_BACK_SPACE, GameActions.START);
        gameControls.put(KeyEvent.VK_M, GameActions.MUSIC_STOP);
    }

    private void _setupGameData() {

        explosions = new ArrayList<Explode>();
        
        
        // TODO: we'll likely need to so _something_ here.
        this.testShip = new Player(200, 420, p1Keys);
        this.testStage = new Stage(Stage.Rounds.ROUND_1);
        this.testBall = new Ball(205, 400);
        this.testBall.xSpeed = 3;
        this.testBall.ySpeed = -3;

        // Listen from game engine.
        eventManager.addObserver(this);

        // Ship
        testShip = new Player(200, 450, p1Keys);
        eventManager.addObserver(testShip);

        // Stage
        testStage = new Stage(Stage.Rounds.ROUND_3);

        // Ball
        testBall = new Ball(205, 440);
        testBall.xSpeed = 2;
        testBall.ySpeed = -2;

        // Score and lives
        testScore = 0;
        testLives = 1;

        // Powerups
        testPowerUps = new ArrayList<PowerUp>();
    }

    private void _setupGameAudio() {
        soundManager.playBgMusic();
    }

    // ==========
    // Update API
    // ==========

    private void gameLoop() {

        long lastFrameTime = System.nanoTime(); // previous frame time
        long lastFps = 0;                       // previous frame fps
        long fps = 0;                           // frames per second
        
        // Main application loop...
        while (isRunning) {

            // Switch on current GameState.
            switch (gameState) {
                case INITIALIZING:
                    break;
                case MAIN_MENU:
                    break;
                case PLAYING:
                    _updateData();
                    _checkCollisions();
                    _cleanupObjects();
                    _checkState();
                    break;
                case TESTING_DRAWING:
                    // Test area
                    updateData();
                    this.testShip.update();
                    this.testBall.update();
                    for (PowerUp _p : powerups) { _p.update(); }
                    checkCollisions();
                    cleanupObjects();

                case PAUSE_MENU:
                    break;
                case GAME_OVER:
                    // isRunning = false;
                    break;
                case EXITING:
                    System.exit(0);
                    break;
                default:
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


    private void _updateData() {
        for (Explode _e : explosions){ _e.update();
        for (PowerUp _p : testPowerUps) _p.update();
        testShip.update();
        testBall.update();
    }

    private void _checkCollisions() {
        // Check ship vs side walls.
        if (testShip.x < 16 || (testShip.width + testShip.x + 16) > GAME_WINDOW_WIDTH) {
            testShip.resetLocation();
        }

        // Check ball vs side walls.
        if (testBall.x < 16 || (testBall.x + testBall.width + 16) > GAME_WINDOW_WIDTH) {
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
            // Calculate new ball speed based on contact point with ship.
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
            // Check power up vs lower bound.
            if (_p.y > GAME_WINDOW_HEIGHT-42)
                _p.hide();

            // Check power up vs ship.
            if (Physics.doesCollideWith(_p, testShip)) {
                _p.hide();
                testActivePowerUp = _p;
                soundManager.playPowerUpCollision();
            }
        }
    }

    private void _cleanupObjects() {
        testStage.blocks.removeIf(_b -> _b.isHidden());
        testPowerUps.removeIf(_p -> _p.isHidden());
        explosions.removeIf(e -> e.isHidden());
    }

    private void _checkState() {
        // Transition to game over if all lives are lost
        if (testLives == 0) {
            gameState = GameState.GAME_OVER;
            soundManager.stopBgMusic();
            soundManager.playGameOverMusic();
        }
    }


    // ========
    // Draw API
    // ========


    // *** JPanel.paintComponent
    @Override
    protected void paintComponent(Graphics g) {

        BufferedImage windowBuffer = new BufferedImage(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) windowBuffer.getGraphics();

        // Draw based on current GameState.
        switch (gameState) {
            case MAIN_MENU:
                _drawSplash(g2d);
                g2d.dispose();
                break;
            case PLAYING:
                _drawGameWorld(g2d);
                _drawUIPanel(g2d);
                break;

            case TESTING_DRAWING:
            {
                // Test stage draw.
                Graphics2D g2d = (Graphics2D) gameAreaBuffer.getGraphics();

                this.testStage.draw(g2d);
                this.testBall.draw(g2d);
                for (PowerUp _p : powerups) {
                    _p.draw(g2d);
                }
                this.testShip.draw(g2d);
                drawUIPanel(g2d);
                
                g2d = (Graphics2D) gameAreaBuffer.getGraphics();
                drawFXObjects(g2d);
                g2d.dispose();

                // Draw window.
                g.drawImage(gameAreaBuffer, 0, 0, this);

            case PAUSE_MENU:
                _drawGameWorld(g2d);
                _drawUIPanel(g2d);
                _drawUIPause(g2d);
                break;
              
            case GAME_OVER:
                _drawGameOverScreen(g2d);
                break;
            case EXITING:
                break;
            default:
                // Somehow, we have a bad enum...
                System.out.println("GameEngine::paintComponent Error: bad enum");
                break;
        }

        // Drw current frame.
        g.drawImage(windowBuffer, 0, 0, this);
    }

    private void _drawGameWorld(Graphics2D g2d) {
        testStage.draw(g2d);
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
    

    private void drawFXObjects(Graphics2D g2d) {
        for (Explode _e : explosions) _e.draw(g2d);
    }
      
    private void _drawSplash(Graphics2D g){

        // Draw logo.
        int xPos = (MAIN_WINDOW_WIDTH/2) - (splashLogo.getWidth()/2);
        g.drawImage(splashLogo, xPos, 40, splashLogo.getWidth(), splashLogo.getHeight(), this);

        // Draw start messaging.

        String msg = "Press <Backspace> To Start";
        String msg2 = "Press <P> To Toggle Music";

        g.setFont(new Font("Courier", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();

        int stringWidth = fm.stringWidth(msg);
        int stringWidth2 = fm.stringWidth(msg2);
        int string2Ascent = fm.getAscent();

        int stringX2 = getWidth() /2 - stringWidth2 /2;
        int stringY2 = stringY+string2Ascent;
        
        //On Enter Change GameState
        HashMap<Controls, Boolean> buttonStates = testShip.getButtonStates();
        if(buttonStates.get(Controls.START)){
            gameState = GameState.TESTING_DRAWING;
        }
        
        g.drawString(msg,stringX,stringY);
        //g.drawString(msg2,stringX2,stringY2);
        

        int stringX2 = (MAIN_WINDOW_WIDTH/2) - (stringWidth2/2);
        int stringY2 = stringY+string2Ascent+10;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(msg, stringX, stringY);
        g.drawString(msg2, stringX2, stringY2);

    }
     
    private void _drawGameOverScreen(Graphics2D g){
        String msg = "GAME OVER!";
        g.setColor(Color.BLACK);
        g.fillRect(0,0, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        
        // Set font for rendering stats.
        g.setColor(Color.RED);
        g.setFont(new Font("Courier", Font.BOLD, 36));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(msg);
        int stringHeight = fm.getAscent();

        int x = getWidth() /2 - stringWidth/2;
        int y = getHeight() /2 + stringHeight/2;

        g.drawString(msg,x,y);
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

                // Start if on splash screen.
                if (buttonPressed == GameActions.START && gameState == GameState.MAIN_MENU) {
                    gameState = GameState.PLAYING;
                }

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
