package csc413_arkanoid_team3;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class AudioTrack {

    private Clip clip;  // the audio clip to be played
    private Boolean shouldLoop;


    // Constructors
    // ============

    public AudioTrack(String soundFilePath, Boolean _shouldLoop) {
        try {
            ClassLoader cl = AudioTrack.class.getClassLoader();
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(
                cl.getResource(soundFilePath)
            );
            clip = AudioSystem.getClip();
            clip.open(soundStream);
            shouldLoop = _shouldLoop;
        } catch(Exception e) {
            System.out.println("AudioTrack::AudioTrack - error opening file");
        }

        // Set loop state.
        setVolume(0.3f);
    }

    public AudioTrack(String soundFilePath) {
        this(soundFilePath, false);
    }


    // Public API
    // ==========

    public void play() {
        clip.setFramePosition(0);
        clip.loop(shouldLoop ? Clip.LOOP_CONTINUOUSLY : 0);
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public float getVolume() {
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        return (float) Math.pow(10f, volume.getValue()/20f);
    }

    public void setVolume(float newVolume) {
        if (newVolume < 0f || newVolume > 1f) {
            return;
        }

        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(20f * (float) Math.log10(newVolume));
    }

}
