package net.explorviz.analysis;

import net.explorviz.kiekeradapter.main.KiekerAdapter;

/**
 * Main class for analysis-service.
 */
public final class Main {

  private Main(){}

  public static void main(final String[] args) {
    KiekerAdapter.getInstance().startReader();
  }



}
