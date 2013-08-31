package mage.client.util;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import mage.client.constants.Constants;
import mage.client.dialog.PreferencesDialog;
import org.apache.log4j.Logger;

/**
 * Manager class for playing audio files.
 *
 * @author nantuko
 */
public class AudioManager {

    private static final Logger log = Logger.getLogger(AudioManager.class);

    /**
     * AudioManager singleton.
     */
    private static AudioManager audioManager = null;


    public static AudioManager getManager() {
        if (audioManager == null) {
            audioManager = new AudioManager();
            audioManager.nextPageClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPrevPage.wav"); //sounds better than OnNextPage
            audioManager.prevPageClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPrevPage.wav");
            audioManager.anotherTabClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnNextPage.wav");
            audioManager.nextPhaseClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnNextPhase.wav");
            audioManager.endTurnClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnEndTurn.wav");
            audioManager.tapPermanentClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnTapPermanent.wav");
            audioManager.summonClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnSummon.wav");
            audioManager.diedCreatureClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnSummon-.wav");
            audioManager.drawClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnDraw.wav");
            audioManager.buttonOkClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnButtonOk.wav");
            audioManager.buttonCancelClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnButtonCancel.wav");
            audioManager.attackClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnAttack.wav");
            audioManager.blockClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnBlock.wav");
            audioManager.addPermanentClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnAddPermanent.wav");
            audioManager.addArtifactClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnAddArtifact.wav");
            audioManager.updateStackClip = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnStackNew.wav");
            audioManager.onHover = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnHover.wav");

            audioManager.playerJoinedTable = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPlayerJoinedTable.wav");
            audioManager.playerSubmittedDeck = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPlayerSubmittedDeck.wav");
            audioManager.playerLeft = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPlayerLeft.wav");
            audioManager.playerWon = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPlayerWon.wav");
            audioManager.playerLost = audioManager.loadClip(Constants.BASE_SOUND_PATH + "OnPlayerLost.wav");
        }
        return audioManager;
    }

    public static void playNextPage() {
        checkAndPlayClip(getManager().nextPageClip);
    }

    public static void playPrevPage() {
        checkAndPlayClip(getManager().prevPageClip);
    }

    public static void playAnotherTab() {
        checkAndPlayClip(getManager().anotherTabClip);
    }

    public static void playNextPhase() {
        checkAndPlayClip(getManager().nextPhaseClip);
    }

    public static void playEndTurn() {
        checkAndPlayClip(getManager().endTurnClip);
    }

    public static void playTapPermanent() {
        checkAndPlayClip(getManager().tapPermanentClip);
    }

    public static void playSummon() {
        checkAndPlayClip(getManager().summonClip);
    }

    public static void playDiedCreature() {
        checkAndPlayClip(getManager().diedCreatureClip);
    }

    public static void playDraw() {
        checkAndPlayClip(getManager().drawClip);
    }

    public static void playButtonOk() {
        checkAndPlayClip(getManager().buttonOkClip);
    }

    public static void playButtonCancel() {
        checkAndPlayClip(getManager().buttonCancelClip);
    }

    public static void playAttack() {
        checkAndPlayClip(getManager().attackClip);
    }

    public static void playBlock() {
        checkAndPlayClip(getManager().blockClip);
    }

    public static void playAddPermanent() {
        checkAndPlayClip(getManager().addPermanentClip);
    }

    public static void playAddArtifact() {
        checkAndPlayClip(getManager().addArtifactClip);
    }

    public static void playStackNew() {
        checkAndPlayClip(getManager().updateStackClip);
    }

    public static void playOnHover() {
        checkAndPlayClip(getManager().onHover);
    }

    public static void playPlayerJoinedTable() {
        checkAndPlayClip(getManager().playerJoinedTable);
    }

    public static void playPlayerSubmittedDeck() {
        checkAndPlayClip(getManager().playerSubmittedDeck);
    }

    public static void playPlayerLeft() {
        checkAndPlayClip(getManager().playerLeft);
    }

    public static void playPlayerLost() {
        checkAndPlayClip(getManager().playerLost);
    }

    public static void playPlayerWon() {
        checkAndPlayClip(getManager().playerWon);
    }

    private static void checkAndPlayClip(Clip clip) {
        try {
            if (clip != null) {
                String soundsOn = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_SOUNDS_ON, "true");
                if (soundsOn.equals("true")) {
                    audioManager.play(clip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(final Clip clip) {
        new Thread(new Runnable() {
            public void run() {
                clip.setFramePosition(0);
                clip.start();
            }
        }).run();
    }

    private Clip loadClip(String filename) {
        try {
            File soundFile = new File(filename);
            AudioInputStream soundIn = AudioSystem
                    .getAudioInputStream(soundFile);
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED,
                    16, 2, 4, AudioSystem.NOT_SPECIFIED, true);
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(soundIn);

            return clip;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("Couldn't load sound: " + filename + ".");
        }

        return null;
    }

    private Clip nextPageClip = null;
    private Clip prevPageClip = null;
    private Clip anotherTabClip = null;
    private Clip nextPhaseClip = null;
    private Clip endTurnClip = null;
    private Clip tapPermanentClip = null;
    private Clip summonClip = null;
    private Clip diedCreatureClip = null;
    private Clip drawClip = null;
    private Clip buttonOkClip = null;
    private Clip buttonCancelClip = null;
    private Clip attackClip = null;
    private Clip blockClip = null;
    private Clip addPermanentClip = null;
    private Clip addArtifactClip = null;
    private Clip updateStackClip = null;
    private Clip onHover = null;

    private Clip playerJoinedTable = null;
    private Clip playerSubmittedDeck = null;
    private Clip playerLeft = null;
    private Clip playerWon = null;
    private Clip playerLost = null;
}
