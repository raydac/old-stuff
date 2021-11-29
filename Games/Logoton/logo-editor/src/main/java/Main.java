import com.igormaznitsa.LogoEditor.LogoEditor;
import com.igormaznitsa.applet.AppletEmulator;

import javax.swing.*;

public class Main {
  public static void main(final String... args) {
    System.setProperty("sun.java2d.uiScale", "2x");

    SwingUtilities.invokeLater(() -> {
      final AppletEmulator emulator = new AppletEmulator(LogoEditor.class, "RNokiaIconMaker", 450, 300, "/", name -> {
        switch (name) {
          case "image" :
            return "i.bmp";
          case "imagelen" :
            return "230";
          case "background":
            return "DEE3F7";
          case "sendcmd":
            return "";
          case "okpage":
            return "ok.htm";
          default:
            return null;
        }
      });
      emulator.setVisible(true);
    });
  }
}
