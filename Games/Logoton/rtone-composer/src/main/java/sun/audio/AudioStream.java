package sun.audio;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioStream extends FilterInputStream {

  protected AudioInputStream ais = null;
  protected AudioFormat format = null;
  protected MidiFileFormat midiformat = null;
  protected InputStream stream = null;

  public AudioStream(InputStream in) throws IOException {
    super(in);

    stream = in;

    if (in.markSupported() == false) {
      stream = new BufferedInputStream(in, 1024);
    }

    try {
      ais = AudioSystem.getAudioInputStream(stream);
      format = ais.getFormat();
      this.in = ais;

    } catch (UnsupportedAudioFileException e) {
      try {
        midiformat = MidiSystem.getMidiFileFormat(stream);

      } catch (InvalidMidiDataException e1) {
        throw new IOException("could not create audio stream from input stream");
      }
    }
  }

  public AudioData getData() throws IOException {
    int length = getLength();

    if (length < 1024 * 1024) {
      byte[] buffer = new byte[length];
      try {
        ais.read(buffer, 0, length);
      } catch (IOException ex) {
        throw new IOException("Could not create AudioData Object");
      }
      return new AudioData(format, buffer);
    }
    throw new IOException("could not create AudioData object");
  }

  public int getLength() {

    if (ais != null && format != null) {
      return (int) (ais.getFrameLength() *
              ais.getFormat().getFrameSize());

    } else if (midiformat != null) {
      return (int) midiformat.getByteLength();

    } else {
      return -1;
    }
  }
}