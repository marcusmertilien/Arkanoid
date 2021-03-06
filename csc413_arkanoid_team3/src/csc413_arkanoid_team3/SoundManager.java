package csc413_arkanoid_team3;

import java.util.HashMap;


public class SoundManager {

    // Class Constants
    // ===============

    private static enum Type {
        BALL_V_SHIP,
        BALL_V_BLOCK,
        BALL_V_GOLD_BLOCK,
        BALL_V_ENEMY,
        BALL_V_GUTTER,
        POWERUP_V_SHIP,
        PROJECTILE_V_BLOCK,
        MENU_MUSIC,
        MENU_SELECT,
        BG_MUSIC,
        GAME_OVER
    }

    private static final HashMap<Type, AudioTrack> SOUND_BANK;
    static {
        SOUND_BANK = new HashMap<Type, AudioTrack>();
        SOUND_BANK.put(Type.BALL_V_GUTTER, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-10.wav", false));
        SOUND_BANK.put(Type.PROJECTILE_V_BLOCK, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-1.wav", false));
        SOUND_BANK.put(Type.GAME_OVER, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-game-over.wav", false));
        SOUND_BANK.put(Type.MENU_MUSIC, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-menu.wav", true));
        SOUND_BANK.put(Type.MENU_SELECT, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-9.wav", false));
        SOUND_BANK.put(Type.BG_MUSIC, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-bg.wav", true));
    }


    // Class Fields
    // ============

    private static SoundManager instance;   // the singleton instance


    // Constructors
    // ============

    protected SoundManager() { }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }

        return instance;
    }

    public void playBallCollision(GameObject obj) {
        if (obj instanceof Block) {
            Block _b = (Block) obj;

            if (_b.type == Block.Types.GOLD) {
                (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-8.wav", false)).play();
            } else {
                (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-6.wav", false)).play();
            }

        } else if (obj instanceof Ship) {
            // If the collision type was a enemy
            (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-7.wav", false)).play();
        } else if (obj instanceof Enemy) {
            // If the collision type was a enemy
            (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-5.wav", false)).play();
        }
    }

    public void playPowerUp() {
        (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-4.wav", false)).play();
    }

    public void playOutOfBounds() {
        (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-10.wav", false)).play();
    }

    public void playProjectile() {
        (new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-1.wav", false)).play();
    }

    public void playMenuMusic() {
        SOUND_BANK.get(Type.BG_MUSIC).stop();
        SOUND_BANK.get(Type.MENU_MUSIC).play();
    }

    public void stopMenuMusic() {
        SOUND_BANK.get(Type.MENU_MUSIC).stop();
    }

    public void playMenuSelect() {
        SOUND_BANK.get(Type.MENU_SELECT).play();
    }

    public void playBgMusic() {
        SOUND_BANK.get(Type.MENU_MUSIC).stop();
        SOUND_BANK.get(Type.BG_MUSIC).play();
    }

    public void stopBgMusic() {
        SOUND_BANK.get(Type.BG_MUSIC).stop();
    }

    public void pauseBgMusic() {
        SOUND_BANK.get(Type.BG_MUSIC).pause();
    }

    public void playGameOverMusic() {
        SOUND_BANK.get(Type.GAME_OVER).play();
    }

}
