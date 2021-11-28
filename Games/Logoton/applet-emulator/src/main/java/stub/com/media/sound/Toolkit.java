package stub.com.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteOrder;

public final class Toolkit {
  public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream ais) {
    AudioFormat af = ais.getFormat();

    if ((!af.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) &&
            (!af.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) {

      try {
        AudioFormat newFormat =
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        af.getSampleRate(),
                        16,
                        af.getChannels(),
                        af.getChannels() * 2,
                        af.getSampleRate(),
                        ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
        ais = AudioSystem.getAudioInputStream(newFormat, ais);
      } catch (Exception e) {
        e.printStackTrace();
        ais = null;
      }
    }

    return ais;
  }

}
