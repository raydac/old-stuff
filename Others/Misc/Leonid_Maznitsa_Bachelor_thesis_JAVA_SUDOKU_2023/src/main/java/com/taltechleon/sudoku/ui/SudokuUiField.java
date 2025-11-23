package com.taltechleon.sudoku.ui;

import com.taltechleon.sudoku.model.SudokuModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SudokuUiField extends JPanel {
  private static final Color COLOR_CELL_ERROR_MARK = new Color(0x60_FF0000, true);
  private static final Color COLOR_OTHER_ERROR_MARK = new Color(0x40_ff7a03, true);
  private static final Color COLOR_SOLVABILITY_ERROR = new Color(0x70_FF0000, true);
  private static final Color COLOR_SOLVABILITY_OK = new Color(0x70_00FF00, true);
  private final SudokuUiCell[][] cells;
  private final List<SudokuModel.Error> errors = new ArrayList<>();
  private boolean showErrors;
  private boolean showVariants;
  private boolean showSolvability;
  private String textSolvability;
  private Color colorSolvability;

  public SudokuUiField() {
    super(new GridLayout(9, 9, 0, 0));
    this.cells = new SudokuUiCell[9][9];
    int cellIndex = 0;
    for (int i = 0; i < 81; i++) {
      final SudokuUiCell cell = new SudokuUiCell(this, cellIndex++);
      final int col = i % 9;
      final int row = i / 9;
      this.add(cell);
      cells[col][row] = cell;
    }
    this.doLayout();
  }

  public boolean isShowSolvability() {
    return this.showSolvability;
  }

  public void setShowSolvability(final boolean value) {
    this.showSolvability = value;
    this.updateSolvabilityState();
    this.repaint();
  }

  public boolean isShowVariants() {
    return this.showVariants;
  }

  public void setShowVariants(final boolean value) {
    this.showVariants = value;
    this.updateShowVariants();
    this.repaint();
  }

  public void updateSolvabilityState() {
    if (this.showSolvability) {
      final SudokuModel model = this.makeSolver();
      if (model.isCorrectlyFilled()) {
        this.textSolvability = "SOLVED";
        this.colorSolvability = COLOR_SOLVABILITY_OK;
      } else if (model.findErrors().isEmpty()) {
        final Set<SudokuModel> foundSolutions = model.solve(2);
        if (foundSolutions.isEmpty()) {
          this.textSolvability = "NO SOLUTIONS";
          this.colorSolvability = COLOR_SOLVABILITY_ERROR;
        } else if (foundSolutions.size() == 1) {
          this.textSolvability = "UNIQUE SOLUTION";
          this.colorSolvability = COLOR_SOLVABILITY_OK;
        } else {
          this.textSolvability = "MULTIPLE SOLUTIONS";
          this.colorSolvability = COLOR_SOLVABILITY_ERROR;
        }
      } else {
        this.textSolvability = "HAS ERRORS";
        this.colorSolvability = COLOR_SOLVABILITY_ERROR;
      }
    }
    this.repaint();
  }

  private void updateShowVariants() {
    if (this.showVariants) {
      final List<SudokuModel.CellVariant> list =
          this.makeSolver().findCurrentVariantsAndSortThem();
      for (final SudokuModel.CellVariant v : list) {
        this.cells[v.col()][v.row()].setCellVariants(
            Arrays.stream(v.variants()).boxed().collect(
                Collectors.toList()), true);
      }
    } else {
      for (int y = 0; y < 9; y++) {
        for (int x = 0; x < 9; x++) {
          this.cells[x][y].setCellVariants(List.of(), false);
          this.cells[x][y].setIndicateComplexity(false);
        }
      }
    }
  }

  public boolean isShowErrors() {
    return this.showErrors;
  }

  public void setShowErrors(final boolean value) {
    this.showErrors = value;
    this.errors.clear();
    if (this.showErrors) {
      this.errors.addAll(this.makeSolver().findErrors());
    }
    this.updateErrorTooltips();
    this.repaint();
  }

  public BufferedImage renderAsImage(final int width, final int height) {
    final BufferedImage bufferedImage =
        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    final Graphics2D gfx = bufferedImage.createGraphics();
    gfx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

    final int cellWidth = width / 9;
    final int cellHeight = height / 9;

    final Rectangle rectangle = new Rectangle(0, 0, cellWidth, cellHeight);

    try {
      for (int y = 0; y < 9; y++) {
        for (int x = 0; x < 9; x++) {
          final SudokuUiCell cell = this.cells[x][y];
          gfx.translate(x * cellWidth, y * cellHeight);
          cell.render(gfx, rectangle);
          gfx.translate(-x * cellWidth, -y * cellHeight);
        }
      }

      if (this.showErrors) {
        this.renderErrors(gfx, cellWidth, cellHeight);
      }

      if (this.showSolvability) {
        this.renderSolvabilityTransparant(gfx, width, height);
      }

    } finally {
      gfx.dispose();
    }

    return bufferedImage;
  }

  private void renderSolvabilityTransparant(final Graphics2D gfx, final int fieldWidth,
                                            final int fieldHeight) {
    if (this.showSolvability && this.textSolvability != null) {
      gfx.setColor(this.colorSolvability);
      final Font font = this.getFont();
      final FontMetrics metrics = gfx.getFontMetrics(font);

      final Rectangle2D stringBounds = metrics.getStringBounds(textSolvability, gfx);
      final double scale = fieldWidth / (stringBounds.getWidth() + 16);
      final Font scaledFont = font.deriveFont(AffineTransform.getScaleInstance(scale, scale));

      gfx.setFont(scaledFont);
      final FontMetrics scaledFontMetrics = gfx.getFontMetrics(scaledFont);
      final Rectangle2D scaledTextRectangle =
          scaledFontMetrics.getStringBounds(this.textSolvability, gfx);

      gfx.drawString(this.textSolvability, (int) (fieldWidth - scaledTextRectangle.getWidth()) / 2,
          (fieldHeight - (scaledFontMetrics.getHeight() - scaledFontMetrics.getMaxAscent() -
              scaledFontMetrics.getDescent())) / 2);
    }
  }

  private void renderErrors(final Graphics2D gfx, final int cellWidth, final int cellHeight) {
    if (this.errors.isEmpty()) {
      return;
    }

    for (final SudokuModel.Error e : this.errors) {
      gfx.setColor(COLOR_OTHER_ERROR_MARK);
      switch (e.getType()) {
        case COLUMN_ERROR: {
          gfx.fillOval(e.getColumn() * cellWidth, 0, cellWidth, 9 * cellHeight);
        }
        break;
        case ROW_ERROR: {
          gfx.fillOval(0, e.getRow() * cellHeight, 9 * cellWidth, cellHeight);
        }
        break;
        case SUBFIELD_ERROR: {
          gfx.fillOval((e.getColumn() / 3) * (cellWidth * 3), (e.getRow() / 3) * (cellHeight * 3),
              3 * cellWidth, 3 * cellHeight);
        }
        break;
      }
      gfx.setColor(COLOR_CELL_ERROR_MARK);
      gfx.fillOval(e.getColumn() * cellWidth, e.getRow() * cellHeight, cellWidth, cellHeight);
    }

  }

  public void clear() {
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        final SudokuUiCell cell = this.cells[x][y];
        cell.setCellValue(0);
      }
    }
    this.updateShowVariants();
  }

  void onValueChangedInCell(final SudokuUiCell cell) {
    this.updateShowVariants();
    this.errors.clear();
    if (this.showErrors) {
      this.errors.addAll(this.makeSolver().findErrors());
    }
    this.updateErrorTooltips();
    this.setShowSolvability(false);
    this.repaint();
  }

  public SudokuModel makeSolver() {
    final SudokuModel sudokuModel = new SudokuModel(3, 3);
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        final int value = this.cells[x][y].getCellValue();
        sudokuModel.setCellValue(x, y, value);
      }
    }
    return sudokuModel;
  }

  public void updateForSolver(final SudokuModel solver) {
    this.clear();
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        final int value = solver.getCellValue(x, y);
        this.cells[x][y].setCellValue(value);
      }
    }

    if (this.showErrors) {
      this.errors.clear();
      this.errors.addAll(solver.findErrors());
    }
    this.updateErrorTooltips();
    this.updateSolvabilityState();

    this.repaint();
  }

  private void updateErrorTooltips() {
    for (int c = 0; c < 9; c++) {
      for (int r = 0; r < 9; r++) {
        this.cells[c][r].setToolTipText(null);
      }
    }
    if (this.showErrors) {
      for (final SudokuModel.Error e : this.errors) {
        final SudokuUiCell cell = this.cells[e.getColumn()][e.getRow()];
        String text = cell.getToolTipText() == null ? "<html>" : cell.getToolTipText();
        if (!"<html>".equals(text)) {
          text += "<br>";
        }
        switch (e.getType()) {
          case COLUMN_ERROR: {
            text += "Duplication in column";
          }
          break;
          case ROW_ERROR: {
            text += "Duplication in row";
          }
          break;
          case SUBFIELD_ERROR: {
            text += "Duplication in sub-field";
          }
          break;
          case NO_CELL_VARIANTS: {
            text += "There is no any variant for the cell";
          }
          break;
        }
        cell.setToolTipText(text);
      }
    }
  }

  @Override
  public void paint(final Graphics g) {
    super.paint(g);
    final Graphics2D gfx = (Graphics2D) g;
    final Rectangle bounds = this.getBounds();

    if (this.showErrors && !this.errors.isEmpty()) {
      this.renderErrors(gfx, bounds.width / 9, bounds.height / 9);
    }

    if (this.showSolvability) {
      this.renderSolvabilityTransparant(gfx, bounds.width, bounds.height);
    }
  }

  public SudokuUiCell getCell(final int col, final int row) {
    return this.cells[col][row];
  }
}
