package schematranslator;

import schematranslator.gui.Application;


public class Main {
  public static void main(String[] args) {
    addSystemUISettings();
    final Thread thread = new Thread(new Application());
    thread.start();
  }

  private static void addSystemUISettings() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", Application.APP_NAME);
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", Application.APP_NAME);
  }
}
