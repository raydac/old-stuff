package com.taltechleon.sudoku.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class SudokuUiCell extends JComponent {

  private static final Color[] COLORS_COMPLEXITY = new Color[] {
      new Color(0x60000000, true),

      new Color(0x6000ff70, true),
      new Color(0x6000ff13, true),
      new Color(0x607cff00, true),

      new Color(0x60aeff00, true),
      new Color(0x60ecff00, true),
      new Color(0x60ffc500, true),

      new Color(0x60ff6800, true),
      new Color(0x60ff2a00, true),
      new Color(0x60ff0000, true),
  };
  private final List<Integer> cellVariants = new ArrayList<>();
  private final int fieldIndex;
  private final SudokuUiField parentField;
  private int cellValue;
  private boolean indicateComplexity;

  public SudokuUiCell(final SudokuUiField field, final int fieldIndex) {
    super();
    this.parentField = field;
    this.fieldIndex = fieldIndex;

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          if (cellValue >= 9) {
            cellValue = 0;
          } else {
            cellValue++;
          }
          parentField.onValueChangedInCell(SudokuUiCell.this);
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (cellValue <= 0) {
            cellValue = 9;
          } else {
            cellValue--;
          }
          parentField.onValueChangedInCell(SudokuUiCell.this);
          repaint();
        }
      }
    });
  }

  public boolean isIndicateComplexity() {
    return this.indicateComplexity;
  }

  public void setIndicateComplexity(final boolean value) {
    this.indicateComplexity = value;
    this.repaint();
  }

  public void setCellVariants(final List<Integer> variants, final boolean inidcateComplexity) {
    this.cellVariants.clear();
    this.cellVariants.addAll(variants.stream().limit(9).filter(x -> x > 0 && x < 10).toList());
    this.indicateComplexity = inidcateComplexity;
    this.repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(48, 48);
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(8, 8);
  }

  @Override
  protected void paintComponent(final Graphics g) {
    final Rectangle rectangle = this.getBounds();
    final Graphics2D gfx = (Graphics2D) g;
    gfx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

    render(gfx, rectangle);
  }

  void render(final Graphics2D gfx, final Rectangle rectangle) {
    gfx.setColor(Color.WHITE);
    gfx.fillRect(0, 0, rectangle.width, rectangle.height);
    gfx.setStroke(new BasicStroke(1));
    gfx.setColor(Color.LIGHT_GRAY);
    gfx.drawRect(0, 0, rectangle.width, rectangle.height);

    gfx.setColor(Color.DARK_GRAY);

    final int col = this.fieldIndex % 9;
    final int row = this.fieldIndex / 9;
    gfx.setStroke(new BasicStroke(2));
    if (col > 0 && col % 3 == 0) {
      gfx.drawLine(0, 0, 0, rectangle.height);
    }
    if (row > 0 && row % 3 == 0) {
      gfx.drawLine(0, 0, rectangle.width, 0);
    }

    if (this.cellValue == 0) {
      this.drawVariants(gfx, rectangle, this.cellVariants);
    } else {
      this.drawValue(gfx, rectangle, this.cellValue);
    }
  }

  private void drawVariants(final Graphics2D gfx, final Rectangle rectangle,
                            final List<Integer> variants) {
    final String etalonText = "9";
    final Font font = this.getFont();
    final FontMetrics metrics = gfx.getFontMetrics(font);
    final Rectangle2D stringBounds = metrics.getStringBounds(etalonText, gfx);

    final double subCellWidth = rectangle.getWidth() / 3;
    final double subCellHeight = rectangle.getHeight() / 3;

    final double scale =
        Math.min(subCellWidth / stringBounds.getWidth(), subCellHeight / stringBounds.getHeight());

    final Font scaledFont = font.deriveFont(AffineTransform.getScaleInstance(scale, scale));

    gfx.setFont(scaledFont);
    final FontMetrics scaledFontMetrics = gfx.getFontMetrics(scaledFont);
    final Rectangle2D scaledTextRectangle = scaledFontMetrics.getStringBounds(etalonText, gfx);

    gfx.setColor(Color.GRAY);

    int col = 0;
    int row = 0;
    for (final Integer v : variants) {
      final double subCellX = col * subCellWidth;
      final double subCellY = row * subCellHeight;
      gfx.drawString(v.toString(),
          (int) (subCellX + (subCellWidth - scaledTextRectangle.getWidth()) / 2),
          (int) (subCellY + scaledFontMetrics.getHeight() - scaledFontMetrics.getDescent()));

      col++;
      if (col == 3) {
        row++;
        col = 0;
      }
    }

    if (this.indicateComplexity) {
      gfx.setColor(COLORS_COMPLEXITY[this.cellVariants.size()]);
      gfx.fillRect(0, 0, rectangle.width, rectangle.height);
    }

  }

  private void drawValue(final Graphics2D gfx, final Rectangle rectangle, final int value) {
    final String text = Integer.toString(value);
    final Font font = this.getFont();
    final FontMetrics metrics = gfx.getFontMetrics(font);
    final Rectangle2D stringBounds = metrics.getStringBounds(text, gfx);

    final double scale = Math.min(rectangle.getWidth() / stringBounds.getWidth(),
        rectangle.getHeight() / stringBounds.getHeight());

    final Font scaledFont = font.deriveFont(AffineTransform.getScaleInstance(scale, scale));

    gfx.setFont(scaledFont);
    final FontMetrics scaledFontMetrics = gfx.getFontMetrics(scaledFont);
    final Rectangle2D scaledTextRectangle = scaledFontMetrics.getStringBounds(text, gfx);

    gfx.setColor(Color.BLACK);
    gfx.drawString(text, (int) ((rectangle.width - scaledTextRectangle.getWidth()) / 2),
        scaledFontMetrics.getHeight() - scaledFontMetrics.getDescent());
  }

  public int getCellValue() {
    return this.cellValue;
  }

  public void setCellValue(final int cellValue) {
    if (cellValue < 0 || cellValue > 9) {
      throw new IllegalArgumentException("Unexpected value, must be in 0..9");
    }
    this.cellValue = cellValue;
    this.parentField.onValueChangedInCell(this);
    this.repaint();
  }
}
