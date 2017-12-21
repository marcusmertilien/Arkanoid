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
        MAIN_MENU,
        GAME_RUNNING,
        PAUSE_MENU,
        ROUND_CHANGE,
        PLAYER_DIED,
        GAME_OVER,
        GAME_WON,
        EXITING
    };
    private Boolean isRunning;
    private GameState gameState;

    // Input handlers
    private EventManager eventManager;
    private InputHandler inputHandler;
    private SoundManager soundManager;

    // Player controls.
    private HashMap<Integer, Controls> p1Keys;

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

    // Default positions
    private static final int DEFAULT_SHIP_X = 200;
    private static final int DEFAULT_SHIP_Y = 450;
    private static final int DEFAULT_BALL_X = 205;
    private static final int DEFAULT_BALL_Y = 400;

    // Test data
    private Player player;
    private Stage currentStage;
    private Ball ball;

    private ArrayList<PowerUp> powerUps;
    private ArrayList<Enemy> enemies;
    private PowerUp activePowerUp;
    private ArrayList<Explode> explosions;
    private ArrayList<Projectile> projectiles;

    // Load static assets
    // TODO: move this somewhere else.
    private static BufferedImage logoImage;
    private static BufferedImage splashLogo;

    static {
        try {
            // Load Arkanoid logo assets.
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
        p1Keys.put(KeyEvent.VK_SPACE, Controls.SHOOT);
        p1Keys.put(KeyEvent.VK_BACK_SPACE, Controls.START);

        // Setup game action controls.
        gameControls = new HashMap<Integer, GameActions>();
        gameControls.put(KeyEvent.VK_P, GameActions.PAUSE);
        gameControls.put(KeyEvent.VK_ESCAPE, GameActions.EXIT);
        gameControls.put(KeyEvent.VK_1, GameActions.START);
        gameControls.put(KeyEvent.VK_M, GameActions.MUSIC_STOP);
    }

    private void _setupGameData() {

        // TODO: we'll likely need to so _something_ here.
        player = new Player(200, 420, p1Keys);
        currentStage = new Stage(Stage.Rounds.ROUND_1);
        ball = new Ball(205, 400);
        ball.xSpeed = 3;
        ball.ySpeed = -3;

        // Listen from game engine.
        eventManager.addObserver(this);

        // Ship
        player = new Player(DEFAULT_SHIP_X, DEFAULT_SHIP_Y, p1Keys);
        eventManager.addObserver(player);

        // Stage
        currentStage = new Stage(Stage.Rounds.ROUND_1);

        // Ball
        ball = new Ball(DEFAULT_BALL_X, DEFAULT_BALL_Y);
        ball.xSpeed = Ball.BALL_SPEED;
        ball.ySpeed = -Ball.BALL_SPEED;

        // Data collections.
        powerUps = new ArrayList<PowerUp>();
        enemies = new ArrayList<Enemy>();
        explosions = new ArrayList<Explode>();
        projectiles = new ArrayList<Projectile>();

        // enemies.add(new Enemy(220, 300, Enemy.Types.GREEN));
        // enemies.add(new Enemy(120, 300, Enemy.Types.BLUE));
        // enemies.add(new Enemy(320, 300, Enemy.Types.RED));
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
                case MAIN_MENU:
                case PAUSE_MENU:
                case ROUND_CHANGE:
                case PLAYER_DIED:
                    // Defaults, no update needed.
                    break;
                case GAME_RUNNING:
                    _updateData();
                    _checkCollisions();
                    _cleanupObjects();
                    _checkState();
                    break;
                case GAME_OVER:
                    _resetPlayer();
                    _resetStage();
                    break;
                case GAME_WON:
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

        player.update(projectiles);
        ball.update();
        
        for (Explode _e : explosions) _e.update();
        for (PowerUp _p : powerUps) _p.update();
        for (Enemy _e: enemies) _e.update();
        for (Projectile _p : projectiles) _p.update();
    }

    private void _checkCollisions() {
        // Check ball vs gutter.
        if (ball.y > GAME_WINDOW_HEIGHT-32) {
            player.decrementLives();
            gameState = GameState.PLAYER_DIED;
            soundManager.stopBgMusic();
            soundManager.playGutterCollision();
            _resetStage();
        }

        // Check ship vs side walls.
        if (player.x < 16 || (player.width + player.x + 16) > GAME_WINDOW_WIDTH) {
            player.resetLocation();
        }

        // Check ball vs side walls.
        if (ball.x < 16 || (ball.x + ball.width + 16) > GAME_WINDOW_WIDTH) {
            ball.resetLocation();
            ball.xSpeed = -(ball.xSpeed);
        }

        // Check ball vs ceiling.
        if (ball.y < 16) {
            ball.resetLocation();
            ball.ySpeed = -(ball.ySpeed);
        }

        // Check player vs ball.
        if (Physics.doesCollideWith(ball, player)) {
            // Calculate new ball speed based on contact point with player.
            int ballCenter = (ball.x - player.x) + (ball.width/2);
            int shipCenter = (player.width/2);
            int newXspeed = (ballCenter - shipCenter);

            // Reset location.
            ball.resetLocation();
            ball.incrementBounce();

            // Use new ball speed.
            ball.ySpeed = -ball.ySpeed;
            ball.xSpeed = newXspeed/8;

            // Allow paddle movement to contribute to reflected speed.
            ball.xSpeed += player.xSpeed/2;

            // Trigger SFX.
            soundManager.playBallCollision(player);

            if (DebugState.showPaddleActive) {
                System.out.printf(
                    "ball center: %d, ship center: %d\nnew ball x: %d, new ball y: %d\n",
                    ballCenter, shipCenter, ball.xSpeed, ball.ySpeed
                );
            }
        }
        
        //Check Block vs Projectile
        for (Block _b : this.currentStage.blocks) {
        for (Projectile _p :this.projectiles) {
            if (Physics.doesCollideWith(_b, _p)) {
                _b.hide();
                _p.hide();

                player.score += _b.registerHit();
                soundManager.playBallCollision(_b);
            }
        }}

        // Check for ball vs block.
        for (Block _b : this.currentStage.blocks) {
            if (Physics.doesCollideWith(_b, ball)) {

                // Reset location before calculating.
                ball.resetLocation();

                // Calculate new x,y speeds based on contact with block
                if (ball.y <= _b.y) {
                    // Contact top.
                    ball.ySpeed = -Math.abs(ball.ySpeed);
                }
                else if (ball.y >= _b.y + _b.height) {
                    // Contact bottom.
                    ball.ySpeed = Math.abs(ball.ySpeed);
                }
                else if (ball.x <= _b.x) {
                    // Contact left.
                    ball.xSpeed = -Math.abs(ball.xSpeed);
                }
                else if (ball.x >= _b.x + _b.width) {
                    // Contact right.
                    ball.xSpeed = Math.abs(ball.ySpeed);
                }

                // Update user score.
                player.score += _b.registerHit();

                // Play SFX.
                soundManager.playBallCollision(_b);

                if (_b.isHidden()) {

                     // Test for power ups, create a random type on every brick break.
                    powerUps.add(new PowerUp(
                        _b.x, _b.y, PowerUp.Types.values()[new Random().nextInt(PowerUp.Types.values().length)]
                    ));

                    // Add new explosion.
                    explosions.add(new Explode(_b.x, _b.y, Explode.Type.ENEMY));
                }

                // Only register one collision per cycle.
                break;
            }
        }
        
        //Enemy vs. Ball
        for (Enemy _e : enemies) {
            if (Physics.doesCollideWith(_e, ball)) {
                ball.resetLocation();

                // Calculate new x,y speeds based on contact with block
                if (ball.y <= _e.y) {
                    // Contact top.
                    ball.ySpeed = -Math.abs(ball.ySpeed);
                }
                else if (ball.y >= _e.y + _e.height) {
                    // Contact bottom.
                    ball.ySpeed = Math.abs(ball.ySpeed);
                }
                else if (ball.x <= _e.x) {
                    // Contact left.
                    ball.xSpeed = -Math.abs(ball.xSpeed);
                }
                else if (ball.x >= _e.x + _e.width) {
                    // Contact right.
                    ball.xSpeed = Math.abs(ball.ySpeed);
                }

                _e.registerHit();

                // Play SFX.
                this.soundManager.playBallCollision(_e);

                // Only register one collision per update cycle.
                break;
            }
        }

        for (PowerUp _p: powerUps) {
            // Check powerup vs lower bound.
            if (_p.y > GAME_WINDOW_HEIGHT-42)
                _p.hide();

            // Check powerup vs player.
            if (Physics.doesCollideWith(_p, player)) {
                _p.hide();
                player.powerUp(_p);
                activePowerUp = _p;
                soundManager.playPowerUpCollision();
            }
        }
    }

    private void _cleanupObjects() {
        currentStage.blocks.removeIf(_b -> _b.isHidden());
        powerUps.removeIf(_p -> _p.isHidden());
        explosions.removeIf(_e -> _e.isHidden());
        enemies.removeIf(_e -> _e.isHidden());
        projectiles.removeIf(_p -> _p.isHidden());
    }

    private void _checkState() {
        // Transition to game over if all lives are lost
        if (player.getLives() == 0) {

            // Move to GAME_OVER state.
            gameState = GameState.GAME_OVER;
            soundManager.stopBgMusic();
            soundManager.playGameOverMusic();

        } else if (currentStage.blocks.isEmpty()) {

            Stage.Rounds nextStage = currentStage.round.next();

            if (nextStage != null) {
                // Move to ROUND_CHANGE state.
                gameState = GameState.ROUND_CHANGE;

                // Create the next stage.
                currentStage = new Stage(currentStage.round.next());

                // Reset the game objects.
                _resetStage();
            } else {
                gameState = GameState.GAME_WON;
            }
        }
    }

    private void _resetPlayer() {
        player.reset();
        currentStage = new Stage(Stage.Rounds.ROUND_1);
    }

    private void _resetStage() {
        // Reset ship for new round.
        player.x = DEFAULT_SHIP_X;
        player.y = DEFAULT_SHIP_Y;

        // Rest ball's position and speed.
        ball.x = DEFAULT_BALL_X;
        ball.y = DEFAULT_BALL_Y;
        ball.xSpeed = Ball.BALL_SPEED;
        ball.ySpeed = -Ball.BALL_SPEED;

        // Reset data collections.
        enemies.clear();
        projectiles.clear();
        explosions.clear();
        powerUps.clear();
    }


    // ========
    // Draw API
    // ========


    // *** JPanel.paintComponent
    @Override
    protected void paintComponent(Graphics g) {

        // Create window buffer.
        BufferedImage windowBuffer = new BufferedImage(
            MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = (Graphics2D) windowBuffer.getGraphics();

        // Draw based on current GameState.
        switch (gameState) {
            case MAIN_MENU:
                _drawMainMenu(g2d);
                break;
            case GAME_RUNNING:
                _drawGameWorld(g2d);
                _drawGameUI(g2d);
                break;
            case PAUSE_MENU:
                _drawGameWorld(g2d);
                _drawGameUI(g2d);
                _drawPauseMenu(g2d);
                break;
            case PLAYER_DIED:
                _drawSplashScreen(g2d, "YOU DIED!", "Press <1> To Continue", Color.RED);
                break;
            case ROUND_CHANGE:
                String formattedTitle = currentStage.round.name().replaceAll("_"," ");
                _drawSplashScreen(g2d, formattedTitle, "Press <1> To Start Round", Color.BLUE);
                break;
            case GAME_WON:
                _drawSplashScreen(g2d, "YOU WON!", "Press <1> To Return to Menu", Color.GREEN);
                break;
            case GAME_OVER:
                _drawSplashScreen(g2d, "GAME OVER!", "Press <1> To Return to Menu", Color.RED);
                break;
            case EXITING:
                break;
            default:
                // Somehow, we have a bad enum...
                System.out.println("GameEngine::paintComponent Error: bad enum");
                break;
        }

        // Dispose buffer.
        g2d.dispose();

        // Draw current frame.
        g.drawImage(windowBuffer, 0, 0, this);
    }

    private void _drawGameWorld(Graphics2D g2d) {
        // Draw player and world.
        currentStage.draw(g2d);
        ball.draw(g2d);
        player.draw(g2d);

        // Draw game object collections.
        for (Enemy _e: enemies) _e.draw(g2d);
        for (PowerUp _p : powerUps) _p.draw(g2d);
        for (Explode _e : explosions) _e.draw(g2d);
        for (Projectile _p : projectiles) _p.draw(g2d);
    }

    private void _drawGameUI(Graphics2D g2d) {
        int commonXoffset = GAME_WINDOW_WIDTH+10;

        // Draw branding.
        g2d.drawImage(logoImage, commonXoffset, 15, 180, 40, null);

        // Draw lives count.
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("1UP x" + player.getLives(), commonXoffset, 75);

        // Draw Score.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("" + player.score, commonXoffset, 90);

        // Draw power up type if active.
        if (activePowerUp != null) {
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Courier", Font.BOLD, 14));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString("Active Power-up", commonXoffset, 110);
            g2d.drawString(activePowerUp.type.name(), commonXoffset, 125);
        }
    }

    private void _drawPauseMenu(Graphics2D g2d) {
        int commonXoffset = GAME_WINDOW_WIDTH+10;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 15));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("GAME PAUSED", commonXoffset + 40, MAIN_WINDOW_HEIGHT - 40);
    }

    private void _drawMainMenu(Graphics2D g2d) {
        int commonYoffset = 400;

        // Draw logo.
        int xPos = (MAIN_WINDOW_WIDTH/2) - (splashLogo.getWidth()/2);
        g2d.drawImage(splashLogo, xPos, 40, splashLogo.getWidth(), splashLogo.getHeight(), this);

        // Drawing messaging.
        String[] messages = new String[] {
            "Press <1> To Start",
            "Press <M> To Toggle Music",
            "Press <P> To Pause Game",
            "Press <ESCAPE> To Quit Game"
        };

        g2d.setFont(new Font("Courier", Font.BOLD, 16));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2d.getFontMetrics();

        for (int i = 0; i < messages.length; i++) {
            String _message = messages[i];
            int width = fm.stringWidth(_message);
            int x = (MAIN_WINDOW_WIDTH/2) - (width/2);
            int y = commonYoffset + (i*20);

            g2d.drawString(_message, x, y);
        }
    }

    private void _drawSplashScreen(Graphics2D g2d, String title, String cta, Color titleColor) {
        int commonYoffset = 400;

        // Draw bg.
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Drawing messaging.
        String[] messages = new String[] {
            title,
            cta,
            "Press <ESCAPE> To Quit Game"
        };

        for (int i = 0; i < messages.length; i++) {
            if (i == 0) {
                g2d.setColor(titleColor);
                g2d.setFont(new Font("Courier", Font.BOLD, 36));
            } else {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Courier", Font.BOLD, 16));
            }

            FontMetrics fm = g2d.getFontMetrics();
            String _message = messages[i];
            int width = fm.stringWidth(_message);
            int x = (MAIN_WINDOW_WIDTH/2) - (width/2);
            int y = (i==0) ? 60 : (commonYoffset + (i-1)*(20));

            g2d.drawString(_message, x, y);
        }
    }

    @Override
    public void update(Observable obj, Object e) {

        // onKey event
        if (e instanceof KeyEvent) {

            KeyEvent ke = (KeyEvent) e;
            GameActions buttonPressed = gameControls.get(ke.getKeyCode());

            // Only react to buttons the game engine is interested in.
            if (buttonPressed == null) {
                return;
            }

            if (ke.getID() == KeyEvent.KEY_PRESSED) {

                // Toggle game pause on P press.
                if (buttonPressed == GameActions.PAUSE && gameState != GameState.MAIN_MENU) {

                    // Toggle the game state on pause.
                    gameState = (gameState != GameState.PAUSE_MENU) ?
                                GameState.PAUSE_MENU :
                                GameState.GAME_RUNNING;

                    // Pause bg music.
                    soundManager.pauseBgMusic();
                }

                // Start press, used to progress forwards to next state.
                if (buttonPressed == GameActions.START) {
                    // If on the main menu, start the first round.
                    if (gameState == GameState.MAIN_MENU)
                        gameState = GameState.GAME_RUNNING;

                    // If on the main menu, start the first round.
                    if (gameState == GameState.PLAYER_DIED) {
                        soundManager.playBgMusic();
                        gameState = GameState.GAME_RUNNING;
                    }

                    // If on the round change screen, start the next round.
                    if (gameState == GameState.ROUND_CHANGE)
                        gameState = GameState.GAME_RUNNING;

                    // If on the round change screen, start the next round.
                    if (gameState == GameState.GAME_OVER)
                        gameState = GameState.MAIN_MENU;
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
