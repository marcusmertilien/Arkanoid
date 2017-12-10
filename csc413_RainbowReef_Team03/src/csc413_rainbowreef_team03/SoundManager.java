package csc413_rainbowreef_team03;


public class SoundManager {

    private static SoundManager instance = null;
    private AudioTrack soundtrack;


    // Constructors
    // ============

    protected SoundManager() { }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }

        return instance;
    }


    // Public API
    // ==========

    public void playSoundtrack() {
        this.soundtrack = new AudioTrack(GameEngine.SOUND_ASSET_PATH + "the_black_kitty.wav", true);
        this.soundtrack.setVolume(0.5f);
        this.soundtrack.play();
    }

    public void stopSoundtrack() {
        this.soundtrack.stop();
    }

    public void playShot() {
        // TODO: implement cloneable in AudioTrack
        // - create a soundboard (palette) of all game sounds, and clone as needed.
        AudioTrack shot = new AudioTrack(GameEngine.SOUND_ASSET_PATH + "shot-2.wav", false);
        shot.setVolume(0.3f);
        shot.play();
    }
    
    public void playExplosion() {
        // TODO: implement cloneable in AudioTrack
        // - create a soundboard (palette) of all game sounds, and clone as needed.
        AudioTrack shot = new AudioTrack(GameEngine.SOUND_ASSET_PATH + "Explosion.wav", false);
        shot.setVolume(0.3f);
        shot.play();
    }

}
