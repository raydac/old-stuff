package stub.com.audio;

public class ContinuousAudioDataStream extends AudioDataStream {
  public ContinuousAudioDataStream(AudioData data) {
    super(data);
  }

  public int read() {
    int i = super.read();

    if (i == -1) {
      reset();
      i = super.read();
    }

    return i;
  }

  public int read(byte ab[], int i1, int j) {

    int k;

    for (k = 0; k < j; ) {
      int i2 = super.read(ab, i1 + k, j - k);
      if (i2 >= 0) k += i2;
      else reset();
    }

    return k;
  }
}