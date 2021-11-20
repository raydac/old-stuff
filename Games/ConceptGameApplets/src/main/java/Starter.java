public class Starter {

  public static void main(String... args) {
    System.setProperty("sun.java2d.uiScale.enabled","true");
    System.setProperty("sun.java2d.uiScale","2x");
    new AppletViewer();
  }

}
