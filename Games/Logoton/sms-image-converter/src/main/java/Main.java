import com.igormaznitsa.LogotipGrabber.LogotipGrabber;
import com.igormaznitsa.applet.AppletEmulator;

import javax.swing.*;

public class Main {
  public static void main(final String... args) {
    System.setProperty("sun.java2d.uiScale", "2x");

    SwingUtilities.invokeLater(() -> {
      final AppletEmulator emulator = new AppletEmulator(LogotipGrabber.class, "SmsImageConverter", 640, 380, "/", name -> {
        switch (name) {
          case "list":
            return "-Nokia logotip+72x14+1-Nokia picture+72x28+2-Siemens screensaver+101x64+3";
          case "background":
            return "DEE3F7";
          case "imageurl":
            return "test.jpg";
          case "imagelength":
            return "31698";
          case "outurl":
            return "http://127.0.0.1";
          default:
            return null;
        }
      });
      emulator.setVisible(true);
    });
  }
}
