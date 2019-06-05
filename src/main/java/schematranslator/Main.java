package schematranslator;

import schematranslator.gui.Application;


public class Main {
  public static void main(String[] args) {
    final Thread thread = new Thread(new Application());
    thread.start();
  }
}
