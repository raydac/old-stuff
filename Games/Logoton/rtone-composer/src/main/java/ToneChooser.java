import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class ToneChooser extends JPanel {

  private File selectedFile;

  public ToneChooser(final File folder) {
    super();

    final List<File> ottFiles = new ArrayList<File>();
    for (final File f : Objects.requireNonNull(folder.listFiles())) {
      if (f.isFile() && f.getName().toLowerCase(Locale.ENGLISH).endsWith(".ott")) {
        ottFiles.add(f);
      }
    }
    Collections.sort(ottFiles, new Comparator<File>() {
      @Override
      public int compare(File o1, File o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    this.setLayout(new GridLayout(ottFiles.size(),1));

    final ButtonGroup buttonGroup = new ButtonGroup();
    boolean selected = true;
    for (final File ottFile : ottFiles) {
      final JRadioButton fileButton = new JRadioButton(ottFile.getName(), selected);
      if (selected) {
        selectedFile = ottFile;
      }
      buttonGroup.add(fileButton);
      fileButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selectedFile = ottFile;
        }
      });
      this.add(fileButton);
      selected = false;
    }
  }

  public File getSelectedFile() {
    return selectedFile;
  }

  public static File chooseFile(final File folder) {
    final ToneChooser chooserPanel = new ToneChooser(folder);
    final JScrollPane panel = new JScrollPane(chooserPanel);
    panel.setBorder(BorderFactory.createTitledBorder("Choose OTT melody"));
    if (JOptionPane.showConfirmDialog(null, panel, "Choose melody", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
      return chooserPanel.getSelectedFile();
    } else {
      return null;
    }
  }
}
