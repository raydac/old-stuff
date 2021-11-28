package stub.com.audio;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class AudioPlayer extends Thread {
  private static boolean DEBUG = false /*true*/;
  public static final AudioPlayer player = getAudioPlayer();
  private AudioDevice devAudio;

  private AudioPlayer() {
    super("Stub Audio Player");
    this.setDaemon(true);
    if (DEBUG) {
      System.out.println("> AudioPlayer private constructor");
    }
    devAudio = AudioDevice.device;
    devAudio.open();
    if (DEBUG) {
      System.out.println("< AudioPlayer private constructor completed");
    }
  }

  private static AudioPlayer getAudioPlayer() {

    if (DEBUG) {
      System.out.println("> AudioPlayer.getAudioPlayer()");
    }
    AudioPlayer audioPlayer;
    PrivilegedAction action = new PrivilegedAction() {
      public Object run() {
        Thread t = new AudioPlayer();
        t.setPriority(MAX_PRIORITY);
        t.setDaemon(true);
        t.start();
        return t;
      }
    };
    audioPlayer = (AudioPlayer) AccessController.doPrivileged(action);
    return audioPlayer;
  }

  public synchronized void start(InputStream in) {

    if (DEBUG) {
      System.out.println("> AudioPlayer.start");
      System.out.println("  InputStream = " + in);
    }
    devAudio.openChannel(in);
    notify();
    if (DEBUG) {
      System.out.println("< AudioPlayer.start completed");
    }
  }

  public synchronized void stop(InputStream in) {

    if (DEBUG) {
      System.out.println("> AudioPlayer.stop");
    }

    devAudio.closeChannel(in);
    if (DEBUG) {
      System.out.println("< AudioPlayer.stop completed");
    }
  }

  public void run() {

    devAudio.play();
    if (DEBUG) {
      System.out.println("AudioPlayer mixing loop.");
    }
    while (true) {
      try {
        Thread.sleep(5000);
        //wait();
      } catch (Exception e) {
        break;
        // interrupted
      }
    }
    if (DEBUG) {
      System.out.println("AudioPlayer exited.");
    }

  }
}
