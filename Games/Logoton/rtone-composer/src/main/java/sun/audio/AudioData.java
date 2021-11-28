package sun.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AudioData {
  private static final AudioFormat DEFAULT_FORMAT =
          new AudioFormat(AudioFormat.Encoding.ULAW,
                  8000,   // sample rate
                  8,      // sample size in bits
                  1,      // channels
                  1,      // frame size in bytes
                  8000,   // frame rate
                  true); // bigendian (irrelevant for 8-bit data)

  AudioFormat format;   // carry forth the format array amusement
  byte buffer[];

  public AudioData(byte buffer[]) {

    this.buffer = buffer;
    this.format = DEFAULT_FORMAT;
    try {
      AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer));
      this.format = ais.getFormat();
      ais.close();
    } catch (IOException e) {
    } catch (UnsupportedAudioFileException e1) {
    }
  }

  AudioData(AudioFormat format, byte[] buffer) {

    this.format = format;
    this.buffer = buffer;
  }
}