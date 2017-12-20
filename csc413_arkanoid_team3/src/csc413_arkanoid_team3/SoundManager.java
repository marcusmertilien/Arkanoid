package csc413_arkanoid_team3;

import java.util.HashMap;


public class SoundManager {

    private static SoundManager instance;   // the singleton instance

    private static enum Type {
        BALL_V_SHIP,
        BALL_V_BLOCK,
        BALL_V_GOLD_BLOCK,
        POWERUP_V_SHIP,
        BG_MUSIC,
        GAME_OVER
    }

    private static HashMap<Type, AudioTrack> soundBank;
    static {
        soundBank = new HashMap<Type, AudioTrack>();
        soundBank.put(Type.BALL_V_BLOCK, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-6.wav", false));
        soundBank.put(Type.BALL_V_GOLD_BLOCK, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-8.wav", false));
        soundBank.put(Type.BALL_V_SHIP, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-7.wav", false));
        soundBank.put(Type.POWERUP_V_SHIP, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-4.wav", false));
        soundBank.put(Type.GAME_OVER, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "game-over.wav", false));
        soundBank.put(Type.BG_MUSIC, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "bg-music.wav", true));
    }


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
                soundBank.get(Type.BALL_V_GOLD_BLOCK).play();
            } else {
                soundBank.get(Type.BALL_V_BLOCK).play();
            }
        } else if (obj instanceof Ship) {
            soundBank.get(Type.BALL_V_SHIP).play();
        }
    }

    public void playPowerUpCollision() {
        soundBank.get(Type.POWERUP_V_SHIP).play();
    }

    public void playGameOverMusic() {
        soundBank.get(Type.GAME_OVER).play();
    }

    public void playBgMusic() {
        soundBank.get(Type.BG_MUSIC).play();
    }

    public void stopBgMusic() {
        soundBank.get(Type.BG_MUSIC).stop();
    }

    public void pauseBgMusic() {
        soundBank.get(Type.BG_MUSIC).pause();
    }

}
