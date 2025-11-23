package com.taltechleon.sudoku;

import com.taltechleon.sudoku.model.SudokuModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * <a href="https://www.kaggle.com/datasets/bryanpark/sudoku">Test puzzles from base Kaggle base 10000000 games</a>
 */
@Disabled("Disabled because takes long time")
public class KaggleSetTest {
  private static final List<KaggleSudokuRecord> kaggleSetRecords = new ArrayList<>(1024 * 1024);

  @BeforeAll
  public static void beforeAll() throws Exception {
    System.out.println("Loading Kaggle puzzle set...");
    final long startTime = System.currentTimeMillis();
    try (final InputStream stream = new GZIPInputStream(Objects.requireNonNull(KaggleSetTest.class.getResourceAsStream("/sudoku.csv.gz")))) {
      final BufferedReader reader =
          new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      int counter = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        final String[] split = line.split(",");
        if (split.length != 2) {
          throw new IOException("Unexpectedly read line splitted not to two parts: " + line);
        }
        if (counter != 0) {
          final KaggleSudokuRecord newRecord =
              new KaggleSudokuRecord(split[0].trim(), split[1].trim());
          kaggleSetRecords.add(newRecord);
        }
        counter++;
      }
    }
    System.out.println("Loaded " + kaggleSetRecords.size() + " puzzle(s), spent time " +
        (System.currentTimeMillis() - startTime) + " ms");
  }

  @Test
  void testOverKaggleSet() throws IOException {
    final List<Long> delayPerPuzzle = new ArrayList<>(kaggleSetRecords.size());
    final SudokuModel model = new SudokuModel(3, 3);

    // warm up
    System.out.println("Warm up start");
    kaggleSetRecords.stream().limit(10000).forEach(x -> {
      model.loadFromArray(x.puzzle);
      model.solve(2);
    });
    System.out.println("Warm up end");

    System.out.println("Start test, " + kaggleSetRecords.size() + " records");
    final long timeStart = System.currentTimeMillis();

    int counter = 0;
    for (final KaggleSudokuRecord record : kaggleSetRecords) {
      model.loadFromArray(record.puzzle);
      final long startNano = System.nanoTime();
      final Set<SudokuModel> solutions = model.solve(2);
      delayPerPuzzle.add(System.nanoTime() - startNano);
      Assertions.assertEquals(1, solutions.size(), "Expected only solution");
      Assertions.assertArrayEquals(record.solution,
          solutions.stream().findFirst().orElseThrow().cellsAsArray());
      counter++;
      if (counter % 10000 == 0) {
        System.out.print(".");
      }
    }
    final long spentTime = System.currentTimeMillis() - timeStart;
    System.out.println(
        "\nCompleted, spent time " + spentTime + " ms, approx time per puzzle " +
            ((double) spentTime / (double) kaggleSetRecords.size()) + " ms");
    final File imageFile = new File("kaggle.png");
    ImageIO.write(makeReportImage(800, 600, delayPerPuzzle), "png", imageFile);
    System.out.println("Report file saved as " + imageFile.getAbsolutePath());
  }

  private BufferedImage makeReportImage(final int imageWidth, final int imageHeight, final List<Long> nanoTimes) {
    final long maxTime = nanoTimes.stream().mapToLong(x -> x).max().orElseThrow();
    final long minTime = nanoTimes.stream().mapToLong(x -> x).min().orElseThrow();

    final double heightY = ((double)imageHeight - ((double)imageHeight / 5));

    final double scaleY = heightY / (double)(maxTime - minTime);
    final double scaleX = (double)imageWidth / nanoTimes.size();

    final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
    final Graphics2D gfx = image.createGraphics();
    try {
      gfx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

      gfx.setColor(Color.WHITE);
      gfx.fillRect(0,0, imageWidth, imageHeight);

      final double yy = imageHeight - (imageHeight - heightY) / 2;

      gfx.setColor(Color.ORANGE.darker());
      double lastX = -1.0d;
      double lastY = -1.0d;

      gfx.setStroke(new BasicStroke(0.3f));

      for(int i=0;i<nanoTimes.size();i++){
        final double x = i * scaleX;
        final double y =  yy - nanoTimes.get(i) * scaleY;
        final Shape shape;
        if (lastY < 0.0d || lastX < 0.0d) {
          shape = new Line2D.Double(x,y, x, y);
        } else {
          shape = new Line2D.Double(lastX, lastY, x, y);
        }
        gfx.draw(shape);
        lastX = x;
        lastY = y;
      }

      gfx.setStroke(new BasicStroke(1));

      gfx.setColor(Color.RED);

      final int topLineY = (int) (yy - maxTime * scaleY);
      gfx.drawLine(0, topLineY, imageWidth, topLineY);
      gfx.drawString((maxTime / 1000.0f) + " us", 10, topLineY - 10);

      gfx.setColor(Color.GREEN);
      final int bottomLineY = (int) (yy - minTime * scaleY);
      gfx.drawLine(0, (int)bottomLineY, imageWidth, (int)bottomLineY);
      gfx.drawString((minTime / 1000.0f) + " us", 10, (int)(bottomLineY + 2 + gfx.getFontMetrics().getHeight()));

      final double GRID_VERT_TIME_STEP_NS = Duration.ofNanos(500_000L).toNanos();

      gfx.setColor(Color.GRAY);
      gfx.setStroke(new BasicStroke(0.4f));
      double timeY = minTime - GRID_VERT_TIME_STEP_NS * scaleY;
      while(timeY < maxTime) {
        final double yyy = yy - timeY * scaleY;
        gfx.drawLine(0, (int)yyy, imageWidth, (int)yyy);
        timeY += GRID_VERT_TIME_STEP_NS;
      }

      for(int i=0;i<nanoTimes.size();i+=50_000) {
        final double xxx = i * scaleX;
        gfx.drawLine((int)xxx, topLineY, (int)xxx, bottomLineY);
      }

    } finally {
      gfx.dispose();
    }
    return image;
  }

  private static final class KaggleSudokuRecord {
    final byte[] puzzle;
    final byte[] solution;

    KaggleSudokuRecord(final String puzzle, final String solution) {
      if (puzzle.length() != solution.length()) {
        throw new IllegalArgumentException("Puzzle and solution has different size");
      }
      this.puzzle = new byte[puzzle.length()];
      this.solution = new byte[solution.length()];

      for (int i = 0; i < puzzle.length(); i++) {
        final int p = puzzle.charAt(i) - '0';
        final int s = solution.charAt(i) - '0';
        if (p < 0 || p > 9) {
          throw new IllegalArgumentException(
              "Detected unexpected char for puzzle: " + puzzle.charAt(i));
        }
        if (s < 0 || s > 9) {
          throw new IllegalArgumentException(
              "Detected unexpected char for solution: " + solution.charAt(i));
        }
        this.puzzle[i] = (byte) p;
        this.solution[i] = (byte) s;
      }
    }
  }

}
