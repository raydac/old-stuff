package stub.com.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;

public final class DataPusher implements Runnable {

  private static final int AUTO_CLOSE_TIME = 5000;

  private final SourceDataLine source;
  private final AudioFormat format;
  private final AudioInputStream ais;
  private final byte[] audioData;
  private final int audioDataByteLength;
  private int pos;
  private int newPos = -1;
  private boolean looping;

  private Thread pushThread = null;
  private int wantedState;
  private int threadState;

  private final int STATE_NONE = 0;
  private final int STATE_PLAYING = 1;
  private final int STATE_WAITING = 2;
  private final int STATE_STOPPING = 3;
  private final int STATE_STOPPED = 4;
  private final int BUFFER_SIZE = 16384;

  public DataPusher(SourceDataLine sourceLine, AudioFormat format, byte[] audioData, int byteLength) {
    this(sourceLine, format, null, audioData, byteLength);
  }

  public DataPusher(SourceDataLine sourceLine, AudioInputStream ais) {
    this(sourceLine, ais.getFormat(), ais, null, 0);
  }

  private DataPusher(final SourceDataLine source, final AudioFormat format,
                     final AudioInputStream ais, final byte[] audioData,
                     final int audioDataByteLength) {
    this.source = source;
    this.format = format;
    this.ais = ais;
    this.audioDataByteLength = audioDataByteLength;
    this.audioData = audioData == null ? null : Arrays.copyOf(audioData,
            audioData.length);
  }

  public synchronized void start() {
    start(false);
  }

  public synchronized void start(boolean loop) {
    try {
      if (threadState == STATE_STOPPING) {
        stop();
      }
      looping = loop;
      newPos = 0;
      wantedState = STATE_PLAYING;
      if (!source.isOpen()) {
        source.open(format);
      }
      source.flush();
      source.start();
      if (pushThread == null) {
        pushThread = new Thread(this, "sound-push-thread");
        pushThread.setDaemon(true);
        pushThread.start();
      }
      notifyAll();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void stop() {
    if (threadState == STATE_STOPPING
            || threadState == STATE_STOPPED
            || pushThread == null) {
      return;
    }
    wantedState = STATE_WAITING;
    if (source != null) {
      source.flush();
    }
    notifyAll();
    int maxWaitCount = 50; // 5 seconds
    while ((maxWaitCount-- >= 0) && (threadState == STATE_PLAYING)) {
      try {
        wait(100);
      } catch (InterruptedException e) {
      }
    }
  }

  synchronized void close() {
    if (source != null) {
      source.close();
    }
  }

  @Override
  public void run() {
    byte[] buffer = null;
    boolean useStream = (ais != null);
    if (useStream) {
      buffer = new byte[BUFFER_SIZE];
    } else {
      buffer = audioData;
    }
    while (wantedState != STATE_STOPPING) {
      //try {
      if (wantedState == STATE_WAITING) {
        // wait for 5 seconds - maybe the clip is to be played again
        try {
          synchronized (this) {
            threadState = STATE_WAITING;
            wantedState = STATE_STOPPING;
            wait(AUTO_CLOSE_TIME);
          }
        } catch (InterruptedException ie) {
        }
        continue;
      }
      if (newPos >= 0) {
        pos = newPos;
        newPos = -1;
      }
      threadState = STATE_PLAYING;
      int toWrite = BUFFER_SIZE;
      if (useStream) {
        try {
          pos = 0; // always write from beginning of buffer
          // don't use read(byte[]), because some streams
          // may not override that method
          toWrite = ais.read(buffer, 0, buffer.length);
        } catch (java.io.IOException ioe) {
          // end of stream
          toWrite = -1;
        }
      } else {
        if (toWrite > audioDataByteLength - pos) {
          toWrite = audioDataByteLength - pos;
        }
        if (toWrite == 0) {
          toWrite = -1; // end of "stream"
        }
      }
      if (toWrite < 0) {
        if (!useStream && looping) {
          pos = 0;
          continue;
        }
        wantedState = STATE_WAITING;
        source.drain();
        continue;
      }
      int bytesWritten = source.write(buffer, pos, toWrite);
      pos += bytesWritten;
    }
    threadState = STATE_STOPPING;
    source.flush();
    source.stop();
    source.flush();
    source.close();
    threadState = STATE_STOPPED;
    synchronized (this) {
      pushThread = null;
      notifyAll();
    }
  }
} // class DataPusher
