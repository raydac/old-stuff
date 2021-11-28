import com.igormaznitsa.applet.AppletEmulator;
import ru.da.rrg.RNokiaIconMaker.IconMaker;

import javax.swing.*;

public class Main {
  public static void main(final String... args) {
    System.setProperty("sun.java2d.uiScale", "2x");

    SwingUtilities.invokeLater(() -> {
      final AppletEmulator emulator = new AppletEmulator(IconMaker.class, "RNokiaPosterEditor", 463, 248, "/", name -> {
        switch (name) {
          case "image":
            return null;
          case "sendcmd":
            return "";
          case "okpage":
            return "ttt.htm";
          default:
            return null;
        }
      });
      emulator.setVisible(true);
    });
  }
}
