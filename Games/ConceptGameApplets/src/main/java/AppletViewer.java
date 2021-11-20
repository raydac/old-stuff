import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;

public class AppletViewer {

  private static final List<AppletItem> APPLETS = Collections.unmodifiableList(Arrays.asList(
          new AppletItem("Shlange", true, Shlange.class, "shlange", 536, 366, new HashMap<String, String>() {{
            put("background", "0A0063");
            put("mouses", "22");
            put("walls", "66");
            put("images", "images/");
          }}),
          new AppletItem("Tetris", true, tetris.class, "tetris", 510, 410, new HashMap<String, String>() {{
            put("background", "0A0063");
            put("images", "images/");
          }}),
          new AppletItem("Torwart", true, torwart.class, "torwart", 590, 460, new HashMap<String, String>() {{
            put("background", "0A0063");
            put("images", "images/");
            put("orientation", "V");
            put("fieldWidth", "30");
            put("fieldHeight", "30");
          }}),
          new AppletItem("SternTangram", false, SternTangram.class, "sterntangram", 500, 280, Collections.emptyMap()),
          new AppletItem("Klad", false, klad.class, "klad", 448, 300, new HashMap<String, String>() {{
            put("background", "008080");
            put("foreground", "FFFFFF");
          }})
  ));

  private static class SelectAppletPanel extends JPanel {

    private AppletItem selected;

    SelectAppletPanel(final List<AppletItem> applets) {
      super(new GridLayout(applets.size(),1));
      final ButtonGroup buttonGroup = new ButtonGroup();

      selected = applets.get(0);

      applets.forEach(a -> {
        JRadioButton appletButton = new JRadioButton(a.name+(a.completed?"":" (non-completed)"), selected == a);
        appletButton.addActionListener(e -> {
          selected = a;
        });
        buttonGroup.add(appletButton);
        this.add(appletButton);
      });
    }

    AppletItem getSelected() {
      return this.selected;
    }
  }

  private static class AppletFrame extends JFrame {

    AppletFrame(final AppletItem appletItem) {
      super(appletItem.name);
      this.setSize(appletItem.width, appletItem.height);

      Applet appletInstance;
      try {
        appletInstance = appletItem.klazz.getConstructor().newInstance();
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Can't init applet for error!", "Error", JOptionPane.ERROR_MESSAGE);
        throw new Error(ex);
      }

      appletInstance.setStub(new AppletStub() {
        @Override
        public boolean isActive() {
          return true;
        }

        @Override
        public URL getDocumentBase() {
          return AppletViewer.class.getResource(appletItem.resourcePath + '/');
        }

        @Override
        public URL getCodeBase() {
          return AppletViewer.class.getResource(appletItem.resourcePath + '/');
        }

        @Override
        public String getParameter(final String name) {
          return appletItem.parameters.get(name.toLowerCase(Locale.ENGLISH));
        }

        @Override
        public AppletContext getAppletContext() {
          return new AppletContext() {
            @Override
            public AudioClip getAudioClip(URL url) {
              throw new UnsupportedOperationException();
            }

            @Override
            public Image getImage(URL url) {
              try {
                return ImageIO.read(url);
              } catch (IOException ex) {
                System.err.println("Can't read image for URL:" + url);
                ex.printStackTrace();
                throw new RuntimeException("Can't load image: " + url, ex);
              }
            }

            @Override
            public Applet getApplet(String name) {
              throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration<Applet> getApplets() {
              throw new UnsupportedOperationException();
            }

            @Override
            public void showDocument(URL url) {
              throw new UnsupportedOperationException();
            }

            @Override
            public void showDocument(URL url, String target) {
              throw new UnsupportedOperationException();
            }

            @Override
            public void showStatus(String status) {
              throw new UnsupportedOperationException();
            }

            @Override
            public void setStream(String key, InputStream stream) throws IOException {
              throw new IOException("Can't set stream: " + key);
            }

            @Override
            public InputStream getStream(String key) {
              throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<String> getStreamKeys() {
              throw new UnsupportedOperationException();
            }
          };
        }

        @Override
        public void appletResize(int width, int height) {

        }
      });

      appletInstance.setSize(appletItem.width, appletItem.height);
      final JPanel panel = new JPanel(new BorderLayout(0, 0));
      panel.setPreferredSize(new Dimension(appletItem.width, appletItem.height));
      panel.setMinimumSize(new Dimension(appletItem.width, appletItem.height));
      panel.add(appletInstance, BorderLayout.CENTER);
      this.setContentPane(panel);

      this.addNotify();

      appletInstance.init();

      this.pack();
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setResizable(false);

      SwingUtilities.invokeLater(appletInstance::start);
    }

  }

  public AppletViewer() {
    SwingUtilities.invokeLater(() -> {
      final SelectAppletPanel selectAppletPanel = new SelectAppletPanel(APPLETS);
      if (JOptionPane.showConfirmDialog(null, selectAppletPanel, "Select applet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
        new AppletFrame(selectAppletPanel.getSelected()).setVisible(true);
      }
    });
  }

  private static class AppletItem {
    final String name;
    final String resourcePath;
    final Class<? extends Applet> klazz;
    final int width;
    final int height;
    final Map<String, String> parameters;
    final boolean completed;

    AppletItem(final String name, final boolean completed, final Class<? extends Applet> klazz, final String resourcePath, final int width, final int height, final Map<String, String> parameters) {
      this.completed = completed;
      this.name = name;
      this.klazz = klazz;
      this.resourcePath = resourcePath;
      this.width = width;
      this.height = height;
      this.parameters = parameters;
    }
  }
}
