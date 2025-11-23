package com.taltechleon.sudoku.ui;

import com.taltechleon.sudoku.model.SudokuModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

public class SudokuUiFrame extends JFrame {

  private static final int MINIMAL_ALLOWED_NUMBER_OF_CLUES = 17;
  private final FileFilter SUDOKU_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".sudoku");
    }

    @Override
    public String getDescription() {
      return "Sudoku files (*.sudoku)";
    }
  };
  private final FileFilter PNG_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".png");
    }

    @Override
    public String getDescription() {
      return "PNG image files (*.png)";
    }
  };
  private final SudokuUiField sudokuField;

  public SudokuUiFrame() {
    super("Sudoku dashboard");
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    final JPanel mainPanel = new JPanel(new BorderLayout());

    this.sudokuField = new SudokuUiField();
    this.sudokuField.setFont(new Font("Arial", Font.BOLD, 12));
    final JPanel sudokuFramedPanel = new JPanel(new BorderLayout());
    sudokuFramedPanel.add(this.sudokuField, BorderLayout.CENTER);
    sudokuFramedPanel.setBorder(BorderFactory.createTitledBorder("Sudoku field"));

    mainPanel.add(sudokuFramedPanel, BorderLayout.CENTER);
    mainPanel.add(makeButtonPanel(), BorderLayout.EAST);

    this.setContentPane(mainPanel);

    this.pack();
  }

  private JButton makeButton(final String title, final ActionListener actionListener) {
    final JButton button = new JButton(title);
    button.setHorizontalAlignment(JButton.LEFT);
    button.addActionListener(actionListener);
    return button;
  }

  private JToggleButton makeToggleButton(final String title, final ActionListener actionListener) {
    final JToggleButton button = new JToggleButton(title);
    button.setHorizontalAlignment(JButton.LEFT);
    button.addActionListener(actionListener);
    return button;
  }

  private JPanel makeButtonPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    final GridBagConstraints gbl =
        new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0);

    panel.add(makeButton("Clear", e -> {
      this.sudokuField.clear();
      this.repaint();
    }), gbl);

    panel.add(makeButton("Load", e -> {
      final JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogTitle("Open Sudoku data file");
      chooser.setAcceptAllFileFilterUsed(true);
      chooser.addChoosableFileFilter(SUDOKU_FILE_FILTER);
      chooser.setFileFilter(SUDOKU_FILE_FILTER);

      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        final File file = chooser.getSelectedFile();
        try {
          final String loadedText = Files.readString(file.toPath(), StandardCharsets.UTF_8);
          try {
            final SudokuModel parsed = new SudokuModel(3, 3);
            parsed.loadFromText(loadedText);
            this.sudokuField.updateForSolver(parsed);
          } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Sudoku file format", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (IOException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, "Can't load Sudoku file for IO error", "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }), gbl);

    panel.add(makeButton("Save", e -> {
      final JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogTitle("Save Sudoku data file");
      chooser.addChoosableFileFilter(SUDOKU_FILE_FILTER);
      chooser.setFileFilter(SUDOKU_FILE_FILTER);

      if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File saveFile = chooser.getSelectedFile();
        if (!saveFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".sudoku")) {
          saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".sudoku");
        }
        try {
          Files.writeString(saveFile.toPath(), this.sudokuField.makeSolver().cellsAsText(),
              StandardCharsets.UTF_8);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Can't write sudoku file for IO error!", "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }), gbl);

    panel.add(Box.createVerticalStrut(8), gbl);

    panel.add(makeToggleButton("Indicate variants", e -> {
      final JToggleButton source = (JToggleButton) e.getSource();
      this.sudokuField.setShowVariants(source.isSelected());
    }), gbl);

    panel.add(makeToggleButton("Show errors", e -> {
      final JToggleButton source = (JToggleButton) e.getSource();
      this.sudokuField.setShowErrors(source.isSelected());
    }), gbl);

    panel.add(makeButton("Check solvability",
        e -> SudokuUiFrame.this.sudokuField.setShowSolvability(true)), gbl);

    panel.add(Box.createVerticalStrut(8), gbl);

    panel.add(makeButton("Generate", e -> {
      final JPanel radioButtonPanel = new JPanel();
      radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
      final JRadioButton buttonExtremelyEasy = new JRadioButton("Extremely easy");
      buttonExtremelyEasy.setAlignmentX(JComponent.LEFT_ALIGNMENT);

      final JRadioButton buttonEasy = new JRadioButton("Easy");
      buttonEasy.setAlignmentX(JComponent.LEFT_ALIGNMENT);

      final JRadioButton buttonMedium = new JRadioButton("Medium");
      buttonMedium.setAlignmentX(JComponent.LEFT_ALIGNMENT);

      final JRadioButton buttonDifficult = new JRadioButton("Difficult");
      buttonDifficult.setAlignmentX(JComponent.LEFT_ALIGNMENT);

      final JRadioButton buttonEvil = new JRadioButton("Evil");
      buttonEvil.setAlignmentX(JComponent.LEFT_ALIGNMENT);

      final ButtonGroup group = new ButtonGroup();
      group.add(buttonExtremelyEasy);
      group.add(buttonEasy);
      group.add(buttonMedium);
      group.add(buttonDifficult);
      group.add(buttonEvil);

      buttonMedium.setSelected(true);

      radioButtonPanel.add(buttonExtremelyEasy);
      radioButtonPanel.add(buttonEasy);
      radioButtonPanel.add(buttonMedium);
      radioButtonPanel.add(buttonDifficult);
      radioButtonPanel.add(buttonEvil);

      if (JOptionPane.showConfirmDialog(this, radioButtonPanel, "Select level",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
        final int clues;
        if (buttonExtremelyEasy.isSelected()) {
          clues = 47;
        } else if (buttonEasy.isSelected()) {
          clues = 36;
        } else if (buttonMedium.isSelected()) {
          clues = 32;
        } else if (buttonDifficult.isSelected()) {
          clues = 28;
        } else if (buttonEvil.isSelected()) {
          clues = 22;
        } else {
          throw new Error("Unexpected state");
        }
        final SudokuModel solver = new SudokuModel(3, 3);
        solver.generate(clues);
        this.sudokuField.updateForSolver(solver);
      }
    }), gbl);

    panel.add(makeButton("Try solve", e -> {
      final SudokuModel solver = this.sudokuField.makeSolver();
      if (!solver.findErrors().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Puzzle is not correct one and contains errors!",
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      var variantsForNonEmptyCells = solver.findCurrentVariantsAndSortThem();
      if (81 - variantsForNonEmptyCells.size() < MINIMAL_ALLOWED_NUMBER_OF_CLUES) {
        JOptionPane.showMessageDialog(this,
            "Puzzle can't be solved because number of clues less than " +
                MINIMAL_ALLOWED_NUMBER_OF_CLUES + "!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      final SudokuUiSolvePanel solvePanel = new SudokuUiSolvePanel(this, solver);
      solvePanel.start();
      solvePanel.setVisible(true);
    }), gbl);


    panel.add(Box.createVerticalStrut(16), gbl);

    panel.add(makeButton("Save image", e -> {
      final BufferedImage renderedImage = sudokuField.renderAsImage(432, 432);

      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fileChooser.addChoosableFileFilter(PNG_FILE_FILTER);
      fileChooser.setFileFilter(PNG_FILE_FILTER);

      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File imageFile = fileChooser.getSelectedFile();
        if (!imageFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".png")) {
          imageFile = new File(imageFile.getParentFile(), imageFile.getName() + ".png");
        }
        try {
          ImageIO.write(renderedImage, "png", imageFile);
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, "Can't save image for IO error!", "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }), gbl);

    gbl.weighty = 10000;
    panel.add(Box.createVerticalGlue(), gbl);

    gbl.weighty = 1;
    panel.add(makeButton("About", e -> {
      JOptionPane.showMessageDialog(this,
          "Sudoku dashboard\n\nThe diploma work of Leonid Maznitsa\nv0.0.1\n2023 TalTech ", "About",
          JOptionPane.INFORMATION_MESSAGE);
    }), gbl);

    return panel;
  }

  private void updateFieldForAllowedVariants() {
    final List<SudokuModel.CellVariant> list =
        this.sudokuField.makeSolver().findCurrentVariantsAndSortThem();
    for (final SudokuModel.CellVariant v : list) {
      this.sudokuField.getCell(v.col(), v.row())
          .setCellVariants(Arrays.stream(v.variants()).boxed().collect(
              Collectors.toList()), true);
    }
    this.repaint();
  }
}
