package com.taltechleon.sudoku.ui;

import com.taltechleon.sudoku.model.SudokuModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class SudokuUiSolvePanel extends JDialog {

  private static final int RESULT_IMAGE_SIZE = 9 * 36;

  private final SudokuModel baseSolver;

  private final JPanel panelResults;
  private final JProgressBar progressBar;
  private final JButton buttonStopOrClose;
  private final JPanel panelInfo;
  private final JLabel labelFoundSolutions;
  private final JLabel labelTime;
  private final Timer timer;
  private volatile int solutionCounter;
  private volatile long timeStart;
  private volatile long timeEnd;
  private volatile Exception detectedError;
  private SwingWorker<JPanel, SudokuModel> worker;

  public SudokuUiSolvePanel(final JFrame parent, final SudokuModel solver) {
    super(parent, "Solving sudoku puzzle", true, parent.getGraphicsConfiguration());
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        worker.cancel(true);
        SudokuUiSolvePanel.this.dispose();
      }
    });
    this.baseSolver = solver;

    this.panelInfo = new JPanel(new BorderLayout(8, 8));
    this.labelFoundSolutions = new JLabel(" Found solutions: --");
    this.labelTime = new JLabel("...  ");

    this.timer = new Timer(1000, e -> {
      this.updateInfo();
    });

    this.panelInfo.add(this.labelFoundSolutions, BorderLayout.WEST);
    this.panelInfo.add(this.labelTime, BorderLayout.EAST);

    this.panelResults = new JPanel(new GridLayout(1, 0, 16, 16));
    this.progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
    this.progressBar.setIndeterminate(true);

    this.buttonStopOrClose = new JButton("Cancel");
    buttonStopOrClose.addActionListener(e -> {
      if (this.worker != null && !this.worker.isDone()) {
        this.worker.cancel(true);
      }
      this.setVisible(false);
    });

    final JPanel mainPanel = new JPanel(new BorderLayout());

    final JPanel bottomPanel = new JPanel(new GridBagLayout());
    final GridBagConstraints gbc =
        new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
            GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0);
    gbc.weightx = 1000;
    bottomPanel.add(this.progressBar, gbc);
    gbc.weightx = 1;
    bottomPanel.add(buttonStopOrClose, gbc);

    final JScrollPane scrollPane = new JScrollPane(this.panelResults);
    scrollPane.setPreferredSize(new Dimension(RESULT_IMAGE_SIZE * 2 + 48, RESULT_IMAGE_SIZE + 48));

    mainPanel.add(this.panelInfo, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    this.setContentPane(mainPanel);
    this.pack();
    this.setLocationRelativeTo(parent);
  }

  private void updateInfo() {
    this.labelFoundSolutions.setText(" Found solutions: " + this.solutionCounter);
    Duration spentTime = Duration.between(Instant.ofEpochMilli(this.timeStart), Instant.now());
    if (this.timeEnd > 0L) {
      spentTime = Duration.between(Instant.ofEpochMilli(this.timeStart),
          Instant.ofEpochMilli(this.timeEnd));
    }

    this.labelTime.setText(spentTime.toHoursPart() + "h " + spentTime.toMinutesPart() + "m " +
        spentTime.toSecondsPart() + "." + spentTime.toMillisPart() + "s  ");
  }

  public void start() {
    this.timer.start();
    final SudokuUiField field = new SudokuUiField();
    this.worker = new SwingWorker<>() {

      @Override
      protected JPanel doInBackground() {
        try {
          baseSolver.solve(123, this::publish);
        } catch (Exception ex) {
          detectedError = ex;
          ex.printStackTrace();
        } finally {
          timeEnd = System.currentTimeMillis();
          timer.stop();
        }
        return panelResults;
      }

      @Override
      protected void process(List<SudokuModel> chunks) {
        for (final SudokuModel s : chunks) {
          field.updateForSolver(s);
          final BufferedImage image = field.renderAsImage(RESULT_IMAGE_SIZE, RESULT_IMAGE_SIZE);
          solutionCounter++;
          panelResults.add(new JLabel(new ImageIcon(image)));
          panelResults.repaint();
        }
      }
    };

    this.worker.addPropertyChangeListener(p -> {
      if ("state".equals(p.getPropertyName())) {
        final SwingWorker<?, ?> source = (SwingWorker<?, ?>) p.getSource();
        if (source.isDone() || source.isCancelled()) {
          this.progressBar.setVisible(false);
          this.buttonStopOrClose.setText("Close");
          this.doLayout();
          this.repaint();
        }
      }
      this.updateInfo();
    });

    this.worker.execute();
    this.timeStart = System.currentTimeMillis();
    this.timeEnd = -1L;
  }

  public void cancel() {
    if (this.worker != null) {
      this.worker.cancel(true);
    }
  }
}
