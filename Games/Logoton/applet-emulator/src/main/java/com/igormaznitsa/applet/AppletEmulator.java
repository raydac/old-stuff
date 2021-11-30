package com.igormaznitsa.applet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class AppletEmulator extends Frame {
  private final Applet appletInstance;

  public AppletEmulator(
          final Class<? extends Applet> appletClass,
          final String title,
          final int width,
          final int height,
          final String resourcePath,
          final Function<String, String> parameterProvider
  ) {
    super(title);
    this.setResizable(false);

    final Applet appletInstance;
    try {
      appletInstance = appletClass.getConstructor().newInstance();
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Can't init applet for error!", "Error", JOptionPane.ERROR_MESSAGE);
      throw new Error(ex);
    }
    this.appletInstance = appletInstance;

    appletInstance.setStub(new AppletStub() {
      @Override
      public boolean isActive() {
        return true;
      }

      @Override
      public URL getDocumentBase() {
        return appletClass.getProtectionDomain().getCodeSource().getLocation();
      }

      @Override
      public URL getCodeBase() {
        return appletClass.getResource(resourcePath + '/');
      }

      @Override
      public String getParameter(final String name) {
        return parameterProvider.apply(name.toLowerCase(Locale.ENGLISH));
      }

      @Override
      public AppletContext getAppletContext() {
        return new AppletContext() {
          private final Map<String, InputStream> streams = new ConcurrentHashMap<>();

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
            return appletInstance;
          }

          @Override
          public Enumeration<Applet> getApplets() {
            return Collections.enumeration(Collections.singletonList(appletInstance));
          }

          @Override
          public void showDocument(final URL url) {
            System.out.println("Ask to show document: " + url);
          }

          @Override
          public void showDocument(final URL url, final String target) {
            System.out.println("Ask to show document: " + url + ", target: " + target);
          }

          @Override
          public void showStatus(final String status) {
            System.out.println("Status: " + status);
          }

          @Override
          public void setStream(final String key, final InputStream stream) throws IOException {
            if (stream == null) {
              this.streams.remove(key);
            } else {
              this.streams.put(key, stream);
            }
          }

          @Override
          public InputStream getStream(final String key) {
            return this.streams.get(key);
          }

          @Override
          public Iterator<String> getStreamKeys() {
            return this.streams.keySet().iterator();
          }
        };
      }

      @Override
      public void appletResize(final int width, final int height) {
        SwingUtilities.invokeLater(() -> {
          resizeFrame(width, height);
          AppletEmulator.this.doLayout();
          AppletEmulator.this.revalidate();
          AppletEmulator.this.repaint();
        });
      }
    });

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.out.println("Window closing...");
        System.exit(0);
      }
    });

    this.add(this.appletInstance);
    this.resizeFrame(width, height);

    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.updateComponentTreeUI(this);
      }catch (Exception ex){
        ex.printStackTrace();
      }
    });

    SwingUtilities.invokeLater(() -> {
      try {
        System.out.println("Initing...");
        appletInstance.init();
        System.out.println("Starting...");
        appletInstance.start();
        System.out.println("Started...");
      } catch (Exception ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    });
  }

  private void resizeFrame(final int width, final int height) {
    this.appletInstance.setPreferredSize(new Dimension(width, height));
    this.appletInstance.setSize(new Dimension(width, height));
    this.appletInstance.setMinimumSize(new Dimension(width, height));
    this.pack();
    this.revalidate();
    this.repaint();
  }

}
