package com.taltechleon.sudoku;

import com.taltechleon.sudoku.ui.SudokuUiFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Starter {
  public static void main(final String... args) {
    SwingUtilities.invokeLater(() -> {
      try {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      final SudokuUiFrame frame = new SudokuUiFrame();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
