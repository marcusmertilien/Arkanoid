package csc413_rainbowreef_team03;

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
    private static final int TILE_SIZE = 30;
    private static final int WORLD_Y_TILE_COUNT = 14;
    private static final int WORLD_X_TILE_COUNT = 24;
    private static final int WORLD_WIDTH = WORLD_X_TILE_COUNT * TILE_SIZE;
    private static final int WORLD_HEIGHT = WORLD_Y_TILE_COUNT * TILE_SIZE;

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
        GAME_OVER
    };
    private Boolean isRunning;
    private GameState gameState;

    // Input handlers
    private EventManager eventManager;
    private InputHandler inputHandler;
    private SoundManager soundManager;

    // Players
    private static Paddle player1;
    //private static Paddle player2;
    private HashMap<Integer, Controls> p1Keys;
    private HashMap<Integer, Controls> p2Keys;

    // Data collections
    private ArrayList<Paddle> players;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Prop> boulders;
    private ArrayList<Prop> breakableBoulders;
    private ArrayList<Projectile> player1shots;
    private ArrayList<Projectile> player2shots;
    private ArrayList<Explosion> explosions;
    
    // Assets
    public static String ASSET_PATH = "resources/";
    public static String TANK_ASSET_PATH = ASSET_PATH + "tanks/";
    public static String ENV_ASSET_PATH = ASSET_PATH + "environment/";
    public static String SOUND_ASSET_PATH = ASSET_PATH + "sounds/";

    private BufferedImage backgroundBuffer;


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
        gameState = GameState.PLAYING;

        // Get references to singletons.
        inputHandler = InputHandler.getInstance();
        soundManager = SoundManager.getInstance();
        eventManager = EventManager.getInstance();

        // Setup game panel.
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, VIEW_HEIGHT));
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocus();
        this.setOpaque(false);
        this.addKeyListener(inputHandler); // attach input handler to panel

        // Setup player keys.
        setupKeys();

        // Setup map assets.
        setupBackground();

        // Setup data.
        setupData();

        // Setup audio track.
        setupAudio();

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
        players = new ArrayList<Paddle>();
        projectiles = new ArrayList<Projectile>();
        boulders = new ArrayList<Prop>();
        breakableBoulders = new ArrayList<Prop>();
        explosions = new ArrayList<Explosion>();

        // Players
        Point p1Start = new Point(40, 80);
        String p1AssetPath = TANK_ASSET_PATH + "RedPaddle1.png";
        player1 = new Paddle(p1Start.x, p1Start.y, 5, TILE_SIZE, p1AssetPath, p1Keys);
        player1shots = new ArrayList<Projectile>();
        eventManager.addObserver(player1);
        players.add(player1);

        Point p2Start = new Point(WORLD_WIDTH-60, WORLD_HEIGHT-80);
        String p2AssetPath = TANK_ASSET_PATH + "GreenPaddle1.png";
        player2 = new Paddle(p2Start.x, p2Start.y, 5, TILE_SIZE, p2AssetPath, p2Keys);
        player2shots = new ArrayList<Projectile>();
        eventManager.addObserver(player2);
        players.add(player2);

        // Boulders
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            Prop b = new Prop(rX, rY, TILE_SIZE, TILE_SIZE, ENV_ASSET_PATH + "boulder.png");

            // Ensure new boulder isn't placed on a player, and is in bounds.
            if (
                !Physics.collides(player1, b) &&
                !Physics.collides(player2, b) &&
                Physics.bounded(b, BOUNDS)
            ) {
                boulders.add(b);
            } else {
                // If not, redo this itteration.
                i--;
            }
        }

        for (int i = 0; i < 7; i++) {
            int rX = r.nextInt(WORLD_X_TILE_COUNT) * TILE_SIZE;
            int rY = r.nextInt(WORLD_Y_TILE_COUNT) * TILE_SIZE;

            Prop bb = new Prop(rX, rY, TILE_SIZE, TILE_SIZE, ENV_ASSET_PATH + "breakableBoulder.png");

            // Ensure new boulder isn't placed on a player, and is in bounds.
            if (
                !Physics.collides(player1, bb) &&
                !Physics.collides(player2, bb) &&
                Physics.bounded(bb, BOUNDS)
            ) {
                breakableBoulders.add(bb);
            } else {
                // If not, redo this itteration.
                i--;
            }
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

                // Game is being created
                case INITIALIZING:
                {
                    // TODO: Implement initializing view.
                    break;
                }

                // Assets are being loaded
                case LOADING:
                {
                    // TODO: Implement main menu view.
                    break;
                }

                // The option menu is active
                case OPTIONS_MENU:
                {
                    // TODO: Implement options menu view.
                    break;
                }

                // The game is running
                case PLAYING:
                {
                    updateData();
                    checkCollisions();
                    cleanupObjects();

                    break;
                }

                // The in-game pause menu is active
                case PAUSE_MENU:
                {
                    // TODO: Implement pause menu view.
                    break;
                }

                // The game has ended
                case GAME_OVER:
                {
                    // TODO: Implement initializing view.
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
        player1.update(player1shots);
        player2.update(player2shots);

        for (Projectile p : player1shots) { p.update(); }
        for (Projectile p : player2shots) { p.update(); }
        for (Explosion e : explosions){ e.update(); }

    }

    private void checkCollisions() {

        // Player vs bounds.
        if (!Physics.bounded(player1, BOUNDS)) {
            player1.resetLocation();
        }

        if (!Physics.bounded(player2, BOUNDS)) {
            player2.resetLocation();
        }

        // Player vs player.
        if (Physics.collides(player1, player2)) {
            player1.resetLocation();
            player2.resetLocation();
        }
        
        // Player v boulder.
        for(Prop boulder : boulders) {
            if (Physics.collides(boulder, player1)) {
                player1.resetLocation();
            }

            if (Physics.collides(boulder, player2)) {
                player2.resetLocation();
            }
        }
        
        // Player v breakable boulder
        for (Prop bBoulder : breakableBoulders) {
            if (
                Physics.collides(bBoulder, player1) ||
                Physics.collides(bBoulder, player2)
            ) {
                soundManager.playExplosion();
                bBoulder.hide();
            }
        } 

        // Check for collisions on player one shots.
        for (Projectile projectile : player1shots) {

            // Projectile vs player two.
            if (!Physics.bounded(projectile, BOUNDS)){
                soundManager.playExplosion();
                projectile.hide();
                Explosion boom = new Explosion(projectile.x, projectile.y);
                explosions.add(boom);
            }
            
            if(Physics.collides(projectile, player2)) {
                
                soundManager.playExplosion();
                projectile.hide();
                if(damage(player1,player2)){
                }else{
                Explosion boom = new Explosion(player2.x, player2.y);
                explosions.add(boom);
                }
            }       
        

            // Projectile vs breakable boulders.
            for(Prop bBoulder : breakableBoulders) {
                if (Physics.collides(bBoulder, projectile)) {
                    soundManager.playExplosion();
                    bBoulder.hide();
                    projectile.hide();
                    Explosion boom = new Explosion(bBoulder.x, bBoulder.y);
                    explosions.add(boom);
                    //Explode
                }
            }

            // Projectile vs boulders.
            for (Prop boulder : boulders) {
                if (Physics.collides(boulder, projectile)) {
                    soundManager.playExplosion();
                    projectile.hide();
                    Explosion boom = new Explosion(boulder.x, boulder.y);
                    explosions.add(boom);
                }
            }
        }
        

        // Check for collisions on player two shots.
        for (Projectile projectile : player2shots) {

            // Projectile vs player one.
            if (!Physics.bounded(projectile, BOUNDS)){
                soundManager.playExplosion();
                projectile.hide();
                Explosion boom = new Explosion(projectile.x, projectile.y);
                explosions.add(boom);
            }
                    
            if(Physics.collides(projectile, player1)) {
                soundManager.playExplosion();
                projectile.hide();
                if(damage(player2,player1)){
                }else{
                Explosion boom = new Explosion(player1.x, player1.y);
                explosions.add(boom);
                }
            }
        

            // Projectile vs breakable boulders.
            for (Prop bBoulder : breakableBoulders) {
                if (Physics.collides(bBoulder, projectile)) {
                    soundManager.playExplosion();
                    bBoulder.hide();
                    projectile.hide();
                    //Explode
                    Explosion boom = new Explosion(bBoulder.x, bBoulder.y);
                    explosions.add(boom);
                }
            }

            // Projectile vs boulders.
            for (Prop boulder : boulders) {
                if (Physics.collides(boulder, projectile)) {
                    soundManager.playExplosion();
                    projectile.hide();
                    //Explode
                    Explosion boom = new Explosion(boulder.x, boulder.y);
                    explosions.add(boom);
                }
            }
        }   
    }
    

    private void cleanupObjects() {
        // Remove objects if hidden.
        player1shots.removeIf(p -> p.isHidden());
        player2shots.removeIf(p -> p.isHidden());
        breakableBoulders.removeIf(bb -> bb.isHidden());
        explosions.removeIf(e -> e.isHidden());
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

            // Game is being created
            case INITIALIZING:
            {
                // TODO: Implement initializing view.
                break;
            }

            // Assets are being loaded
            case LOADING:
            {
                // TODO: Implement loading view
                break;
            }

            // The option menu is active
            case OPTIONS_MENU:
            {
                // TODO: implement options menu view
                break;
            }

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

            // The in-game pause menu is active
            case PAUSE_MENU:
            {
                // TODO: implement pause menu view
                break;
            }

            // The game has ended
            case GAME_OVER:
            {
                // TODO: Implement Game over view.
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
        for (Prop _b : boulders) _b.draw(g2d);
        for (Prop _bb : breakableBoulders) _bb.draw(g2d);
        for (Projectile _p : player1shots) _p.draw(g2d);
        for (Projectile _p : player2shots) _p.draw(g2d);
        for (Paddle _p: players) _p.draw(g2d);
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
        for (Prop boulder : this.boulders) {
            Point bLoc = boulder.getLocation();
            g2d.fillRect(bLoc.x/scale, bLoc.y/scale, pinSize, pinSize);
        }
        for (Prop boulder : this.breakableBoulders) {
            Point bLoc = boulder.getLocation();
            g2d.fillRect(bLoc.x/scale, bLoc.y/scale, pinSize, pinSize);
        }
        for (Projectile projectile : player1shots) {
            Point pLoc = projectile.getLocation();
            g2d.fillRect(pLoc.x/scale, pLoc.y/scale, projSize, projSize);
        }
        for (Projectile projectile : player2shots) {
            Point pLoc = projectile.getLocation();
            g2d.fillRect(pLoc.x/scale, pLoc.y/scale, projSize, projSize);
        }

        g.drawImage(miniMap, VIEW_WIDTH - (mapWidth + paddingSize), paddingSize, mapWidth, mapHeight, this);
        g.drawImage(miniMap, WINDOW_WIDTH - (mapWidth + paddingSize), paddingSize, mapWidth, mapHeight, this);
    }
    
    private boolean damage(Paddle attacker, Paddle deffender){
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
