package csc413_arkanoid_team3;

import java.util.HashMap;


public class SoundManager {

    private static SoundManager instance;   // the singleton instance

    private static enum SFXType {
        BALL_V_SHIP, BALL_V_BLOCK, BALL_V_GOLD_BLOCK
    }
    private static HashMap<SFXType, AudioTrack> soundBank;
    static {
        soundBank = new HashMap<SFXType, AudioTrack>();
        soundBank.put(SFXType.BALL_V_BLOCK, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-6.wav", false));
        soundBank.put(SFXType.BALL_V_GOLD_BLOCK, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-8.wav", false));
        soundBank.put(SFXType.BALL_V_SHIP, new AudioTrack(GameEngine.SOUND_ASSET_PATH + "arkanoid-sfx-7.wav", false));
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
        AudioTrack shot;

        if (obj instanceof Block) {
            Block _b = (Block) obj;

            if (_b.type == Block.Types.GOLD) {
                soundBank.get(SFXType.BALL_V_GOLD_BLOCK).play();
            } else {
                soundBank.get(SFXType.BALL_V_BLOCK).play();
            }
        } else if (obj instanceof Ship) {
            soundBank.get(SFXType.BALL_V_SHIP).play();
        }
    }

}
