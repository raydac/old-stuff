import com.igormaznitsa.applet.AppletEmulator;
import ru.da.rrg.TonePlayer.TonePlayer;

import javax.swing.*;
import java.io.File;

public class Main {

  public static void main(final String... args) {
    System.setProperty("sun.java2d.uiScale", "2x");

    final File ottFolder = new File(".","ott");
    if (!ottFolder.isDirectory()) {
      System.err.println("Can't find OTT folder, should be placed in start folder: "+ottFolder.getAbsolutePath());
      System.exit(1);
    }
    final File ottFile = ToneChooser.chooseFile(ottFolder);
    if (ottFile == null) {
      System.out.println("Canceled");
      System.exit(0);
    } else {
      System.out.println("Chosen ott file: " + ottFile);
    }

    SwingUtilities.invokeLater(() -> {
      final AppletEmulator emulator = new AppletEmulator(TonePlayer.class, "RToneComposer", 190, 178, "/", name -> {
        switch (name) {
          case "cfgfile":
            return "nokia3310.cfg";
          case "tonefile":
            return ottFile.getAbsolutePath();
          default:
            return null;
        }
      });
      emulator.setVisible(true);
    });
  }
}
