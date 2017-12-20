package csc413_arkanoid_team3;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class AudioTrack {

    // Class fields
    // ============

    private Clip clip;           // the audio clip to be played
    private Boolean shouldLoop;  // Whether the clip loops
    private int currentLocation; // the location of the current pause point


    // Constructors
    // ============

    public AudioTrack(String soundFilePath, Boolean _shouldLoop) {
        try {
            // Try to load the provided audio file.
            ClassLoader cl = AudioTrack.class.getClassLoader();
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(
                cl.getResource(soundFilePath)
            );
            clip = AudioSystem.getClip();
            clip.open(soundStream);
            shouldLoop = _shouldLoop;
        } catch(Exception e) {
            System.out.println("AudioTrack::AudioTrack - error opening file");
            System.out.println(e.toString());
        }

        // Set default volume.
        setVolume(0.3f);
    }

    public AudioTrack(String soundFilePath) {
        this(soundFilePath, false);
    }


    // Public API
    // ==========

    public void play() {
        currentLocation = 0;
        clip.setFramePosition(currentLocation);
        clip.loop(shouldLoop ? Clip.LOOP_CONTINUOUSLY : currentLocation);
    }

    public void stop() {
        clip.stop();
    }

    public void pause() {
        // If the clip has been previously paused, resume play from current location.
        if (currentLocation == 0) {
            clip.stop();
            currentLocation = clip.getFramePosition();
        } else {
            clip.setFramePosition(currentLocation);
            clip.loop(shouldLoop ? Clip.LOOP_CONTINUOUSLY : 0);
            currentLocation = 0;
        }
    }


    // Volume API
    // ==========

    public float getVolume() {
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        return (float) Math.pow(10f, volume.getValue()/20f);
    }

    public void setVolume(float newVolume) {
        if (newVolume < 0f || newVolume > 1f)
            return;

        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(20f * (float) Math.log10(newVolume));
    }

}
