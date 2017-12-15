package csc413_arkanoid_team3;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import javax.imageio.ImageIO;


public class GameEngine extends JPanel implements Runnable {
    
    // Game world size.
    public static final int TILE_SIZE = 30;
    public static final int WORLD_Y_TILE_COUNT = 14;
    public static final int WORLD_X_TILE_COUNT = 24;
    public static final int WORLD_WIDTH = WORLD_X_TILE_COUNT * TILE_SIZE;
    public static final int WORLD_HEIGHT = WORLD_Y_TILE_COUNT * TILE_SIZE;

    // Player screen size.
    private static final int VIEW_SIZE = WORLD_HEIGHT;
    private static final int VIEW_WIDTH = VIEW_SIZE;
    private static final int VIEW_HEIGHT = VIEW_SIZE;

    // View window size.
    private static final int WINDOW_BORDER_WIDTH = 5;
    private static final int WINDOW_WIDTH = 2 * VIEW_WIDTH + WINDOW_BORDER_WIDTH;
    private static final int WINDOW_HEIGHT = VIEW_HEIGHT;
    private static final Rectangle BOUNDS = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT-20);

    // Game loop constants.
    private static final int TARGET_FPS = 30;
    private static final long ONE_SECOND_NS = 1000000000;
    private static final long OPTIMAL_TIME = ONE_SECOND_NS / TARGET_FPS;

    // Game state
    private static enum GameState {
        INITIALIZING,
        LOADING,
        MAIN_MENU,
        OPTIONS_MENU,
        PAUSE_MENU,
        PLAYING,
        GAME_OVER,
        DRAW_BG
    };
    private Boolean isRunning;
    private GameState gameState;

    // Input handlers
    private EventManager eventManager;
    private InputHandler inputHandler;
    private SoundManager soundManager;

    // Players
    private static Tank player1;
    private static Tank player2;
    private HashMap<Integer, Controls> p1Keys;
    private HashMap<Integer, Controls> p2Keys;

    // Data collections
    private ArrayList<Tank> players;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Boulder> boulders;
    private ArrayList<Projectile> player1Shots;
    private ArrayList<Projectile> player2Shots;
    private ArrayList<Explosion> explosions;
    private ArrayList<PowerUp> powerUps;

    // Assets
    public static String ASSET_PATH = "resources/";
    public static String TANK_ASSET_PATH = ASSET_PATH + "tanks/";
    public static String ENV_ASSET_PATH = ASSET_PATH + "environment/";
    public static String SOUND_ASSET_PATH = ASSET_PATH + "sounds/";
    public static String EXPLOSION_PATH = ASSET_PATH + "explosion/";
    public static String STAGE_BG_PATH = ASSET_PATH + "stage-background/";
    public static String POWERUP_PATH = ASSET_PATH + "powerUps/";
    private BufferedImage backgroundBuffer;


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
        // Setup running flags.
        isRunning = true;
        gameState = GameState.DRAW_BG;

        // Get references to singletons.
        // inputHandler = InputHandler.getInstance();
        // soundManager = SoundManager.getInstance();
        // eventManager = EventManager.getInstance();

        // Setup game panel.
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, VIEW_HEIGHT));
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocus();
        this.setOpaque(false);
        this.addKeyListener(inputHandler); // attach input handler to panel

        this.testStage = new Stage(Stage.Rounds.ROUND_5);

        // // Setup player keys.
        // setupKeys();

        // // Setup map assets.
        // setupBackground();

        // // Setup data.
        // setupData();

        // // Setup audio track.
        // setupAudio();

        // Add game panel instance to parent frame.
        frame.add(this, BorderLayout.CENTER);
    }

    private void setupKeys() {
        // Setup player 1 key mapping
        p1Keys = new HashMap<Integer, Controls>();
        p1Keys.put(KeyEvent.VK_LEFT, Controls.LEFT);
        p1Keys.put(KeyEvent.VK_RIGHT, Controls.RIGHT);
        p1Keys.put(KeyEvent.VK_UP, Controls.UP);
        p1Keys.put(KeyEvent.VK_DOWN, Controls.DOWN);
        p1Keys.put(KeyEvent.VK_ENTER, Controls.SHOOT);

        // Setup player 2 key mapping.
        p2Keys = new HashMap<Integer, Controls>();
        p2Keys.put(KeyEvent.VK_F, Controls.LEFT);
        p2Keys.put(KeyEvent.VK_H, Controls.RIGHT);
        p2Keys.put(KeyEvent.VK_T, Controls.UP);
        p2Keys.put(KeyEvent.VK_G, Controls.DOWN);
        p2Keys.put(KeyEvent.VK_SPACE, Controls.SHOOT);
    }

    private void setupBackground() {
        // Setup background map.
        try {
            // Create new global buffer for background.
            backgroundBuffer = new BufferedImage(
                WORLD_WIDTH, WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = (Graphics2D) backgroundBuffer.getGraphics();
            ClassLoader cl = GameEngine.class.getClassLoader();
            Image sand = ImageIO.read(cl.getResource(ENV_ASSET_PATH + "sand.png"));

            // Build background map.
            for (int i = 0; i <= WORLD_Y_TILE_COUNT; i++) {
            for (int j = 0; j <= WORLD_X_TILE_COUNT; j++) {
                g2d.drawImage(sand, j*TILE_SIZE, i*TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
            }}
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setupData() {
        // Collections.
        players = new ArrayList<Tank>();
        projectiles = new ArrayList<Projectile>();
        explosions = new ArrayList<Explosion>();
        boulders = new ArrayList<Boulder>();
        powerUps = new ArrayList<PowerUp>();

        // Players
        Point p1Start = new Point(40, 80);
        player1 = new Tank(p1Start.x, p1Start.y, 5, TILE_SIZE, p1Keys, Tank.Colors.RED);
        player1Shots = new ArrayList<Projectile>();
        eventManager.addObserver(player1);
        players.add(player1);

        Point p2Start = new Point(WORLD_WIDTH-60, WORLD_HEIGHT-80);
        player2 = new Tank(p2Start.x, p2Start.y, 5, TILE_SIZE, p2Keys, Tank.Colors.GREEN);
        player2Shots = new ArrayList<Projectile>();
        eventManager.addObserver(player2);
        players.add(player2);

        // Boulders
        Random r = new Random();
        for (int i = 0; i < 30; i++) {
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            Boulder.Type type = (i % 3 != 0 ) ? Boulder.Type.DEFAULT : Boulder.Type.BREAKABLE;
            Boulder b = new Boulder(rX, rY, type);

            if (!Physics.collides(player1, b) && !Physics.collides(player2, b) && Physics.bounded(b, BOUNDS)) {
                boulders.add(b);
            } else {
                i--;
            }
        }
        for(int i=0; i<3; i++){
            
            PowerUp.Type type =  PowerUp.Type.life;
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            PowerUp power = new PowerUp(rX,rY,type);
            powerUps.add(power);
        }
        
        for(int i=0; i<3; i++){
            
            PowerUp.Type type =  PowerUp.Type.damage;
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            PowerUp power = new PowerUp(rX,rY,type);
            powerUps.add(power);
        }
        for(int i=0; i<3; i++){
            
            PowerUp.Type type =  PowerUp.Type.speed;
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            PowerUp power = new PowerUp(rX,rY,type);
            powerUps.add(power);
        }
        
        
    }

    private void setupAudio() {
        if (DebugState.playSoundtrackActive) {
            soundManager.playSoundtrack();
        }
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

                // The game is running
                case PLAYING:
                {
                    updateData();
                    checkCollisions();
                    cleanupObjects();

                    break;
                }

                case DRAW_BG:
                {
                    break;
                }

                default:
                {
                    // Somehow, we have a bad enum...
                    System.out.println("GameEngine::gameLoop Error: bad enum");
                    break;
                }
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
        // Update actors
        player1.update(player1Shots);
        player2.update(player2Shots);

        // Update game objects.
        for (Projectile _p : player1Shots) { _p.update(); }
        for (Projectile _p : player2Shots) { _p.update(); }
        for (Explosion _e : explosions){ _e.update(); }
    }

    private void checkCollisions() {

        // Player vs bounds.
        if (!Physics.bounded(player1, BOUNDS)) player1.resetLocation();
        if (!Physics.bounded(player2, BOUNDS)) player2.resetLocation();

        // Player vs player.
        if (Physics.collides(player1, player2)) {
            player1.resetLocation();
            player2.resetLocation();
        }
        
        //Player vs PowerUps
        for (PowerUp p : powerUps){
            if(Physics.collides(p,player1)){
                player1.powerUp(p);
                p.hide();
            }
            if(Physics.collides(p,player2)){
                player1.powerUp(p);
                p.hide();
            }
            
        }
        
        // Boulders
        for (Boulder b : boulders) {
            // Check standard boulder.
            if (b.type == Boulder.Type.DEFAULT) {
                if (Physics.collides(b, player1)) {
                    player1.resetLocation();
                } else if (Physics.collides(b, player2)) {
                    player2.resetLocation();
                }

            }

            // Check breakables.
            else if (b.type == Boulder.Type.BREAKABLE) {
                if (Physics.collides(b, player1)) {
                    b.hide();
                    soundManager.playExplosion();
                } else if (Physics.collides(b, player2)) {
                    b.hide();
                    soundManager.playExplosion();
                }
            }
        }

        // Check for collisions on player one shots.
        for (Projectile p : player1Shots) {

            // Check projectile bounds.
            if (!Physics.bounded(p, BOUNDS)) {
                p.hide();
                soundManager.playExplosion();
            }
            
            // Check hit on player 2.
            else if (Physics.collides(p, player2)) {
                p.hide();
                damage(player1, player2);
                explosions.add(new Explosion(player2.x, player2.y));
                soundManager.playExplosion();
            }

            // Check boulder collisons.
            else {

                for (Boulder b : boulders) {
                    if (Physics.collides(b, p)) {
                        p.hide();
                        explosions.add(new Explosion(b.x, b.y));
                        soundManager.playExplosion();

                        if (b.type == Boulder.Type.BREAKABLE) {
                            b.hide();
                            soundManager.playExplosion();
                        }
                    }
                }

            }

        }

        // Check for collisions on player two shots.
        for (Projectile p : player2Shots) {

            // Check projectile bounds.
            if (!Physics.bounded(p, BOUNDS)) {
                p.hide();
                soundManager.playExplosion();
            }

            // Check hit on player 1.
            else if (Physics.collides(p, player1)) {
                p.hide();
                damage(player1, player2);
                explosions.add(new Explosion(player1.x, player1.y));
                soundManager.playExplosion();
            }

            // Check boulder collisons.
            else {

                for (Boulder b : boulders) {
                    if (Physics.collides(b, p)) {
                        p.hide();
                        explosions.add(new Explosion(b.x, b.y));
                        soundManager.playExplosion();

                        if (b.type == Boulder.Type.BREAKABLE) {
                            b.hide();
                            soundManager.playExplosion();
                        }
                    }
                }

            }

        }
    }


    private void cleanupObjects() {
        // Remove objects if hidden.
        player1Shots.removeIf(p -> p.isHidden());
        player2Shots.removeIf(p -> p.isHidden());
        boulders.removeIf(b -> b.isHidden());
        explosions.removeIf(e -> e.isHidden());
        powerUps.removeIf(p -> p.isHidden());
    }


    // *** JPanel.paintComponent
    @Override
    protected void paintComponent(Graphics g) {

        BufferedImage windowBuffer = new BufferedImage(
            WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        BufferedImage gameWorldBuffer = new BufferedImage(
            WORLD_WIDTH, WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        // Draw based on current GameState.
        switch (gameState) {

            // The game is running
            case PLAYING:
            {
                Graphics2D g2d = (Graphics2D) gameWorldBuffer.getGraphics();
                drawBackground(g2d);
                g2d.dispose();

                g2d = (Graphics2D) gameWorldBuffer.getGraphics();
                drawGameObjects(g2d);
                g2d.dispose();

                g2d = (Graphics2D) gameWorldBuffer.getGraphics();
                drawFXObjects(g2d);
                g2d.dispose();

                g2d = (Graphics2D) windowBuffer.getGraphics();
                drawViews(g2d, gameWorldBuffer);
                g2d.dispose();

                g2d = (Graphics2D) windowBuffer.getGraphics();
                drawMiniMaps(g2d);
                g2d.dispose();

                g2d = (Graphics2D) windowBuffer.getGraphics();
                drawUIPanel(g2d);
                g2d.dispose();

                // Draw contents of buffer.
                g.drawImage(windowBuffer, 0, 0, this);

                break;
            }

            case DRAW_BG:
            {
                Graphics2D g2d = (Graphics2D) windowBuffer.getGraphics();
                this.testStage.draw(g2d);
                g2d.dispose();

                // Draw contents of buffer.
                g.drawImage(windowBuffer, 0, 0, this);

                break;
            }

            default:
            {
                // Somehow, we have a bad enum...
                System.out.println("GameEngine::gameLoop Error: bad enum");
                break;
            }

        }
    }

    private void drawViews(Graphics2D g2d, BufferedImage gameWorldBuffer) {

        Point p1Loc = player1.getCenterLocation();
        Point p2Loc = player2.getCenterLocation();

        int p1ViewX, p1ViewY;
        int p2ViewX, p2ViewY;

        p1ViewX = (p1Loc.x-VIEW_SIZE/2 < 0) ? 0 :
                  (p1Loc.x+VIEW_SIZE/2 > WORLD_WIDTH) ? WORLD_WIDTH - VIEW_SIZE :
                  (p1Loc.x-VIEW_SIZE/2);

        p1ViewY = (p1Loc.y-VIEW_SIZE/2 < 0) ? 0 :
                  (p1Loc.y+VIEW_SIZE/2 > WORLD_HEIGHT) ? WORLD_HEIGHT - VIEW_SIZE :
                  (p1Loc.y-VIEW_SIZE/2);

        p2ViewX = (p2Loc.x-VIEW_SIZE/2 < 0) ? 0 :
                  (p2Loc.x+VIEW_SIZE/2 > WORLD_WIDTH) ? WORLD_WIDTH - VIEW_SIZE :
                  (p2Loc.x-VIEW_SIZE/2);

        p2ViewY = (p2Loc.y-VIEW_SIZE/2 < 0) ? 0 :
                  (p2Loc.y+VIEW_SIZE/2 > WORLD_HEIGHT) ? WORLD_HEIGHT - VIEW_SIZE :
                  (p2Loc.y-VIEW_SIZE/2);


        BufferedImage p1View = gameWorldBuffer.getSubimage(p1ViewX, p1ViewY, VIEW_SIZE, VIEW_SIZE);
        BufferedImage p2View = gameWorldBuffer.getSubimage(p2ViewX, p2ViewY, VIEW_SIZE, VIEW_SIZE);

        g2d.drawImage(p1View, 0, 0, this);
        g2d.drawImage(p2View, VIEW_SIZE+WINDOW_BORDER_WIDTH, 0, this);
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.drawImage(backgroundBuffer, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, null);
    }

    private void drawGameObjects(Graphics2D g2d) {
        for (PowerUp _p : powerUps) _p.draw(g2d);
        for (Boulder _b : boulders) _b.draw(g2d);
        for (Projectile _p : player1Shots) _p.draw(g2d);
        for (Projectile _p : player2Shots) _p.draw(g2d);
        for (Tank _p: players) _p.draw(g2d);
        
    }

    private void drawFXObjects(Graphics2D g2d) {
        for (Explosion _e : explosions) _e.draw(g2d);
    }

    private void drawUIPanel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Set font for rendering stats.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 18));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw player 1's stats.
        g2d.drawString("p1 score: " + player1.score, 30, 30);
        g2d.drawString("p1 health: " + player1.health, 30, 50);
        g2d.drawString("p1 lives: " + player1.lives, 30, 70);

        // Draw player 2's stats.
        g2d.drawString("p2 score: " + player2.score, (VIEW_WIDTH + 35), 30);
        g2d.drawString("p2 health: " + player2.health, (VIEW_WIDTH + 35), 50);
        g2d.drawString("p2 lives: " + player2.lives, (VIEW_WIDTH + 35), 70);
    }

    private void drawMiniMaps(Graphics2D g) {
        // Size values.
        int paddingSize = 20;
        int pinSize = 3;
        int projSize = 2;

        // The scale difference between the game's map, and the mini map.
        int scale = 10;
        int mapWidth = WORLD_WIDTH/scale;
        int mapHeight = WORLD_HEIGHT/scale;

        BufferedImage miniMap = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) miniMap.getGraphics();

        // Draw minimap.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, mapWidth+2, mapHeight+2);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(1, 1, mapWidth, mapHeight);
        g2d.setStroke(new BasicStroke(1));

        // Add player1's location.
        Point p1Loc = player1.getLocation();
        g2d.setColor(Color.RED);
        g2d.fillOval(p1Loc.x/scale, p1Loc.y/scale, pinSize, pinSize);

        // Add player2's location.
        Point p2Loc = player2.getLocation();
        g2d.setColor(Color.GREEN);
        g2d.fillOval(p2Loc.x/scale, p2Loc.y/scale, pinSize, pinSize);

        // Add locations of boulders.
        g2d.setColor(Color.GRAY);
        for (Boulder boulder : boulders) {
            Point bLoc = boulder.getLocation();
            g2d.fillRect(bLoc.x/scale, bLoc.y/scale, pinSize, pinSize);
        }
        for (Projectile projectile : player1Shots) {
            Point pLoc = projectile.getLocation();
            g2d.fillRect(pLoc.x/scale, pLoc.y/scale, projSize, projSize);
        }
        for (Projectile projectile : player2Shots) {
            Point pLoc = projectile.getLocation();
            g2d.fillRect(pLoc.x/scale, pLoc.y/scale, projSize, projSize);
        }

        g.drawImage(miniMap, VIEW_WIDTH - (mapWidth + paddingSize), paddingSize, mapWidth, mapHeight, this);
        g.drawImage(miniMap, WINDOW_WIDTH - (mapWidth + paddingSize), paddingSize, mapWidth, mapHeight, this);
    }
    
    private boolean damage(Tank attacker, Tank deffender){
        int pointDif = 20; //PointDifferential
        //Damage Health
        deffender.health -= pointDif;
        //Increment Score
        attacker.score += pointDif;
        
        if(deffender.health == 0){
            //explode
            Explosion boom = new Explosion(deffender.getX(), deffender.getY());
            explosions.add(boom);
            deffender.lives--;
            deffender.health = 100;
            deffender.goHome();
            return true;
        }   
        return false;
    }
}
