package org.kuleuven.applications;


import javax.swing.JFrame;
import javax.swing.UIManager;

public abstract class Application {
  public Application(ApplicationParameters applicationParameters) {
    try {
      ApplicationFrame applicationFrame = new ApplicationFrame();
      applicationFrame.setApplicationPanel(getApplicationPanel(applicationParameters));
      //applicationFrame.pack();
      applicationFrame.showCentered();
      applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    catch(Exception exception) {

      ExceptionDialog.showException(exception);
      System.exit(1);
    }
  }
  
  protected abstract ApplicationPanel getApplicationPanel(ApplicationParameters applicationParameters) throws Exception;

  protected static void init() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception exception) {
    }
  }
}
